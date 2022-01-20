package co.harismiftahulhudha.otoklixchallenge.mvvm.viewmodels

import android.view.View
import androidx.lifecycle.*
import co.harismiftahulhudha.otoklixchallenge.mvvm.models.PostModel
import co.harismiftahulhudha.otoklixchallenge.mvvm.repositories.PostRepository
import co.harismiftahulhudha.otoklixchallenge.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val channel = Channel<Event>()
    val event = channel.receiveAsFlow()

    var page = state.get<Int>("page") ?: 1
        set(value) {
            field = value
            state.set("page", value)
        }
    var isListLoaded = state.get<Boolean>("isListLoaded") ?: false
        set(value) {
            field = value
            state.set("isListLoaded", value)
        }
    var isDeleted = state.get<Boolean>("isDeleted") ?: false
        set(value) {
            field = value
            state.set("isDeleted", value)
        }
    var isCreated = state.get<Boolean>("isCreated") ?: false
        set(value) {
            field = value
            state.set("isCreated", value)
        }
    var isEdited = state.get<Boolean>("isEdited") ?: false
        set(value) {
            field = value
            state.set("isEdited", value)
        }

    private var listPostResponse: MediatorLiveData<Resource<MutableList<PostModel>>> = MediatorLiveData()

    fun observeListPostResponse() = listPostResponse as LiveData<Resource<MutableList<PostModel>>>

    fun listPost(isNextPage: Boolean = false, isRefresh: Boolean = false) {
        if (isRefresh) {
            page = 1
        } else if (isNextPage) {
            page++
        }
        val source = postRepository.list(page)
        listPostResponse.addSource(source) {
            if (it != null) {
                listPostResponse.value = it
                if (it.status != Resource.Status.LOADING) {
                    listPostResponse.removeSource(source)
                    isListLoaded = true
                }
            } else {
                listPostResponse.removeSource(source)
            }
        }
    }

    private var deletePostResponse: MediatorLiveData<Resource<MutableList<PostModel>>> = MediatorLiveData()

    fun observeDeletePostResponse() = deletePostResponse as LiveData<Resource<MutableList<PostModel>>>

    fun deletePost(post: PostModel) {
        isDeleted = false
        val source = postRepository.delete(post.id, page)
        deletePostResponse.addSource(source) {
            if (it != null) {
                deletePostResponse.value = it
                if (it.status != Resource.Status.LOADING) {
                    deletePostResponse.removeSource(source)
                    isDeleted = true
                }
            } else {
                deletePostResponse.removeSource(source)
            }
        }
    }

    private var createPostResponse: MediatorLiveData<Resource<MutableList<PostModel>>> = MediatorLiveData()

    fun observeCreatedPostResponse() = createPostResponse as LiveData<Resource<MutableList<PostModel>>>

    fun createPost(post: PostModel) {
        isCreated = false
        val source = postRepository.create(post.title, post.title, page)
        createPostResponse.addSource(source) {
            if (it != null) {
                createPostResponse.value = it
                if (it.status != Resource.Status.LOADING) {
                    createPostResponse.removeSource(source)
                    isCreated = true
                }
            } else {
                createPostResponse.removeSource(source)
            }
        }
    }

    private var editPostResponse: MediatorLiveData<Resource<MutableList<PostModel>>> = MediatorLiveData()

    fun observeEditPostResponse() = editPostResponse as LiveData<Resource<MutableList<PostModel>>>

    fun editPost(post: PostModel) {
        isEdited = false
        val source = postRepository.update(post.id, post.title, post.content, page)
        editPostResponse.addSource(source) {
            if (it != null) {
                editPostResponse.value = it
                if (it.status != Resource.Status.LOADING) {
                    editPostResponse.removeSource(source)
                    isEdited = true
                }
            } else {
                editPostResponse.removeSource(source)
            }
        }
    }

    fun onNavigateToDetail(post: PostModel) = viewModelScope.launch {
        channel.send(Event.NavigateToDetail(post))
    }

    fun onNavigateToForm(post: PostModel?, isEdit: Boolean) = viewModelScope.launch {
        channel.send(Event.NavigateToForm(post, isEdit))
    }

    fun onShowPopupMenu(view: View, post: PostModel, position: Int) = viewModelScope.launch {
        channel.send(Event.ShowPopupMenu(view, post, position))
    }

    fun onRequestDelete(post: PostModel) = viewModelScope.launch {
        channel.send(Event.RequestDelete(post))
    }

    fun onRequestEdit(post: PostModel) = viewModelScope.launch {
        channel.send(Event.RequestEdit(post))
    }

    fun onRequestCreate(post: PostModel) = viewModelScope.launch {
        channel.send(Event.RequestCreate(post))
    }

    fun onNextPage() = viewModelScope.launch {
        channel.send(Event.RequestNextPage)
    }

    sealed class Event {
        data class NavigateToDetail(val post: PostModel): Event()
        data class NavigateToForm(val post: PostModel?, val isEdit: Boolean): Event()
        data class ShowPopupMenu(val view: View, val post: PostModel, val position: Int): Event()
        data class RequestCreate(val post: PostModel): Event()
        data class RequestEdit(val post: PostModel): Event()
        data class RequestDelete(val post: PostModel): Event()
        object RequestNextPage: Event()
    }
}