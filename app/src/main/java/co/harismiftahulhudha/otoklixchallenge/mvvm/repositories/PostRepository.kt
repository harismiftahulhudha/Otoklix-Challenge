package co.harismiftahulhudha.otoklixchallenge.mvvm.repositories

import androidx.lifecycle.LiveData
import co.harismiftahulhudha.otoklixchallenge.apis.UtilsApi
import co.harismiftahulhudha.otoklixchallenge.database.dao.PostDao
import co.harismiftahulhudha.otoklixchallenge.mvvm.models.PostModel
import co.harismiftahulhudha.otoklixchallenge.utils.ApiResponse
import co.harismiftahulhudha.otoklixchallenge.utils.AppExecutors
import co.harismiftahulhudha.otoklixchallenge.utils.NetworkBoundResource
import co.harismiftahulhudha.otoklixchallenge.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class PostRepository @Inject constructor(
    private var postDao: PostDao
): CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    @Inject
    lateinit var utilsApi: UtilsApi

    fun list(page: Int): LiveData<Resource<MutableList<PostModel>>> {
        return object: NetworkBoundResource<MutableList<PostModel>, MutableList<PostModel>>(AppExecutors.instance!!) {
            override fun saveCallResult(item: MutableList<PostModel>) {
                item.forEach {
                    postDao.insert(it)
                }
            }

            override fun shouldFetch(data: MutableList<PostModel>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<MutableList<PostModel>> {
                return postDao.list(page)
            }

            override fun createCall(): LiveData<ApiResponse<MutableList<PostModel>>> {
                return utilsApi.postApiService().list()
            }

        }.getAsLiveData()
    }

    fun show(id: Long): LiveData<Resource<PostModel>> {
        return object: NetworkBoundResource<PostModel, PostModel>(AppExecutors.instance!!) {
            override fun saveCallResult(item: PostModel) {
                postDao.insert(item)
            }

            override fun shouldFetch(data: PostModel?): Boolean {
                return false
            }

            override fun loadFromDb(): LiveData<PostModel> {
                return postDao.show(id)
            }

            override fun createCall(): LiveData<ApiResponse<PostModel>> {
                return utilsApi.postApiService().show(id)
            }

        }.getAsLiveData()
    }

    fun create(title: String, content: String, page: Int): LiveData<Resource<MutableList<PostModel>>> {
        return object: NetworkBoundResource<MutableList<PostModel>, PostModel>(AppExecutors.instance!!) {
            override fun saveCallResult(item: PostModel) {
                postDao.insert(item)
            }

            override fun shouldFetch(data: MutableList<PostModel>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<MutableList<PostModel>> {
                return postDao.list(page)
            }

            override fun createCall(): LiveData<ApiResponse<PostModel>> {
                return utilsApi.postApiService().create(title, content)
            }

        }.getAsLiveData()
    }

    fun update(id: Long, title: String, content: String, page: Int): LiveData<Resource<MutableList<PostModel>>> {
        return object: NetworkBoundResource<MutableList<PostModel>, PostModel>(AppExecutors.instance!!) {
            override fun saveCallResult(item: PostModel) {
                postDao.insert(item)
            }

            override fun shouldFetch(data: MutableList<PostModel>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<MutableList<PostModel>> {
                return postDao.list(page)
            }

            override fun createCall(): LiveData<ApiResponse<PostModel>> {
                return utilsApi.postApiService().update(id, title, content)
            }

        }.getAsLiveData()
    }

    fun delete(id: Long, page: Int): LiveData<Resource<MutableList<PostModel>>> {
        return object: NetworkBoundResource<MutableList<PostModel>, PostModel>(AppExecutors.instance!!) {
            override fun saveCallResult(item: PostModel) {
                postDao.delete(id)
            }

            override fun shouldFetch(data: MutableList<PostModel>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<MutableList<PostModel>> {
                return postDao.list(page)
            }

            override fun createCall(): LiveData<ApiResponse<PostModel>> {
                return utilsApi.postApiService().delete(id)
            }

        }.getAsLiveData()
    }
}