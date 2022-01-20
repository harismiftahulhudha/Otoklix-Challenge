package co.harismiftahulhudha.otoklixchallenge.mvvm.viewmodels

import androidx.lifecycle.*
import co.harismiftahulhudha.otoklixchallenge.mvvm.models.PostModel
import co.harismiftahulhudha.otoklixchallenge.mvvm.repositories.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormPostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val channel = Channel<Event>()
    val event = channel.receiveAsFlow()

    var inputTitle = state.get<String>("inputTitle") ?: ""
        set(value) {
            field = value
            state.set("inputTitle", value)
        }

    var inputContent = state.get<String>("inputContent") ?: ""
        set(value) {
            field = value
            state.set("inputContent", value)
        }

    var post = state.get<PostModel>("post")
        set(value) {
            field = value
            state.set("post", value)
        }

    fun onRequestSave() = viewModelScope.launch {
        channel.send(Event.RequestSave)
    }

    fun onChangeInput() = viewModelScope.launch {
        if (inputTitle.isNotEmpty() && inputContent.isNotEmpty()) {
            channel.send(Event.EnableBtnSubmit)
        } else {
            channel.send(Event.DisableBtnSubmit)
        }
    }

    sealed class Event {
        object RequestSave: Event()
        object EnableBtnSubmit: Event()
        object DisableBtnSubmit: Event()
    }
}