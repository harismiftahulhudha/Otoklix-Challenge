package co.harismiftahulhudha.otoklixchallenge.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import co.harismiftahulhudha.otoklixchallenge.database.dao.PostDao
import co.harismiftahulhudha.otoklixchallenge.di.ApplicationScope
import co.harismiftahulhudha.otoklixchallenge.mvvm.models.PostModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

private const val TAG = "AppDatabase"

@Database(
    entities = [PostModel::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao

    class Callback @Inject constructor(
        private val database: Provider<AppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            applicationScope.launch {
//                generateDataUserTesting(userDao)
            }
        }
    }
}