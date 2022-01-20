package co.harismiftahulhudha.otoklixchallenge.utils

import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

private const val TAG = "NetworkBoundResource"

abstract class NetworkBoundResource<CacheObject, RequestObject> @MainThread constructor(private val appExecutors: AppExecutors) {

    private var results = MediatorLiveData<Resource<CacheObject>>()

    init {
        init()
    }

    private fun init() {
        // update LiveData for loading status
        results.value = Resource.loading(null)

        // observe LiveData source from local db
        val dbSource = loadFromDb()
        results.addSource(dbSource) {
            results.removeSource(dbSource)
            if (shouldFetch(it)) {
                // get data from the network
                fetchFromNetwork(dbSource)
            } else {
                results.addSource(dbSource) {
                    setValue(Resource.success(it))
                }
            }
        }.runCatching {
            //
        }.exceptionOrNull()
    }

    /**
     * 1) observe local db
     * 2) if <condition/> query the network
     * 3) stop observing the local db
     * 4) insert new data into local db
     * 5) begin observing local db again to see the refreshed data from network
     * @param dbSource
     */
    private fun fetchFromNetwork(dbSource: LiveData<CacheObject>) {

        // update LiveData for loading status
        results.addSource(dbSource) { cacheObject: CacheObject ->
            setValue(Resource.loading(cacheObject))
        }

        val apiResponse: LiveData<ApiResponse<RequestObject>> = createCall()

        results.addSource(apiResponse) {
            results.removeSource(dbSource)
            results.removeSource(apiResponse)

            if (it is ApiSuccessResponse) {
                appExecutors.diskIO().execute {
                    saveCallResult(processResponse(it as ApiSuccessResponse<CacheObject>) as RequestObject)
                    appExecutors.mainThread().execute {
                        results.addSource(loadFromDb()) { cacheObject: CacheObject ->
                            setValue(Resource.success(cacheObject))
                        }
                    }
                }
            } else if (it is ApiEmptyResponse) {
                appExecutors.mainThread().execute {
                    results.addSource(loadFromDb()) {
                        setValue(Resource.success(it))
                    }
                }
            } else if (it is ApiErrorResponse) {
                results.addSource(dbSource) { cacheObject: CacheObject ->
                    setValue(Resource.error(cacheObject, it.errorMessage, it.code, it.errorModel))
                }
            } else if (it is ApiTimeoutResponse) {
                results.addSource(dbSource) { cacheObject: CacheObject ->
                    setValue(Resource.timeout(cacheObject, it.errorMessage))
                }
            } else if (it is ApiUnAuthorizedResponse) {
                results.addSource(dbSource) { cacheObject: CacheObject ->
                    setValue(Resource.unauthorized(cacheObject, it.errorMessage, it.code))
                }
            }
        }
    }

    @WorkerThread
    private fun processResponse(response: ApiSuccessResponse<CacheObject>): CacheObject {
        return response.body
    }

    @MainThread
    private fun setValue(newValue: Resource<CacheObject>) {

        if (results.value != newValue) {
            results.value = newValue
        }
    }

    protected open fun onFetchFailed() {}

    // Called to save the result of the API response into the database.
    @WorkerThread
    protected abstract fun saveCallResult(@NonNull item: RequestObject)

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract fun shouldFetch(@Nullable data: CacheObject?): Boolean

    // Called to get the cached data from the database.
    @MainThread
    protected abstract fun loadFromDb(): LiveData<CacheObject>

    // Called to create the API call.
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestObject>>

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    fun getAsLiveData(): LiveData<Resource<CacheObject>> = results
}