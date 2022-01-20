package co.harismiftahulhudha.otoklixchallenge.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import co.harismiftahulhudha.otoklixchallenge.BuildConfig
import co.harismiftahulhudha.otoklixchallenge.mvvm.models.PostModel

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(articleModel: PostModel)

    @Query("DELETE FROM posts WHERE id = :id")
    fun delete(id: Long)

    @Query("SELECT * FROM posts ORDER BY id DESC LIMIT (:page * ${BuildConfig.LIMIT})")
    fun list(page: Int): LiveData<MutableList<PostModel>>

    @Query("SELECT * FROM posts WHERE id = :id")
    fun show(id: Long): LiveData<PostModel>

    @Query("SELECT * FROM posts ORDER BY id DESC LIMIT 1")
    fun last(): LiveData<PostModel>
 }