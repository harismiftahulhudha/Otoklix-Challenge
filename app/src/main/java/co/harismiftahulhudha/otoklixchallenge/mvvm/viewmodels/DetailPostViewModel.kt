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
class DetailPostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val channel = Channel<Event>()
    val event = channel.receiveAsFlow()

    var post = state.get<PostModel>("post")!!

    fun onSetData() = viewModelScope.launch {
        channel.send(Event.SetData)
    }

    sealed class Event {
        object SetData: Event()
    }
}