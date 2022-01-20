package co.harismiftahulhudha.otoklixchallenge.mvvm.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ErrorModel(
    val status: Int,
    val error: String? = null,
    val message: String? = null
): Parcelable