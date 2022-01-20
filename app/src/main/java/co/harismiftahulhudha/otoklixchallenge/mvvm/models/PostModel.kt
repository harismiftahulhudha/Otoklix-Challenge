package co.harismiftahulhudha.otoklixchallenge.mvvm.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "posts")
@Parcelize
data class PostModel(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    var title: String = "",
    var content: String = "",
    @SerializedName("published_at")
    val publishedAt: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
): Parcelable
