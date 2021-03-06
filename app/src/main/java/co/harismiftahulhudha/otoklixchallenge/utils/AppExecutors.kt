package co.harismiftahulhudha.otoklixchallenge.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors {
    private val mDiskIO: Executor = Executors.newSingleThreadExecutor()
    private val mMainThreadExecutor: Executor = MainThreadExecutor()

    fun diskIO(): Executor {
        return mDiskIO
    }

    fun mainThread(): Executor {
        return mMainThreadExecutor
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(
            Looper.getMainLooper()
        )

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    companion object {
        var instance: AppExecutors? = null
            get() {
                if (field == null) {
                    field = AppExecutors()
                }
                return field
            }
            private set
    }
}