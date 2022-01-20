package co.harismiftahulhudha.otoklixchallenge.mvvm.views.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.harismiftahulhudha.otoklixchallenge.BuildConfig
import co.harismiftahulhudha.otoklixchallenge.R
import co.harismiftahulhudha.otoklixchallenge.databinding.FragmentPostBinding
import co.harismiftahulhudha.otoklixchallenge.helpers.FormatStringHelper
import co.harismiftahulhudha.otoklixchallenge.mvvm.interfaces.BaseInterface
import co.harismiftahulhudha.otoklixchallenge.mvvm.models.PostModel
import co.harismiftahulhudha.otoklixchallenge.mvvm.viewmodels.PostViewModel
import co.harismiftahulhudha.otoklixchallenge.mvvm.views.activities.FormPostActivity
import co.harismiftahulhudha.otoklixchallenge.mvvm.views.adapters.PostAdapter
import co.harismiftahulhudha.otoklixchallenge.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class PostFragment : BaseFragment(), BaseInterface {

    companion object {
        val CREATE_FORM_POST = "${BuildConfig.APPLICATION_ID}_${PostFragment::class.java.simpleName}_CREATE_FORM_POST"
        val EDIT_FORM_POST = "${BuildConfig.APPLICATION_ID}_${PostFragment::class.java.simpleName}_EDIT_FORM_POST"
    }

    private lateinit var binding: FragmentPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    // ViewModels
    private val viewModel: PostViewModel by viewModels()

    // Components
    private lateinit var adapter: PostAdapter
    @Inject lateinit var formatStringHelper: FormatStringHelper

    // Variables
    var createPostLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            if (data.hasExtra(CREATE_FORM_POST)) {
                val post: PostModel = data.getParcelableExtra(CREATE_FORM_POST)!!
                viewModel.onRequestCreate(post)
            }
        }
    }
    var editPostLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            if (data.hasExtra(EDIT_FORM_POST)) {
                val post: PostModel = data.getParcelableExtra(EDIT_FORM_POST)!!
                viewModel.onRequestEdit(post)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents()
        subscribeListeners()
        subscribeObservers()

        if (!viewModel.isListLoaded) {
            viewModel.listPost()
        }
    }

    override fun initComponents() {
        binding.apply {
            rvPost.layoutManager = LinearLayoutManager(requireContext())
            rvPost.setHasFixedSize(true)
            rvPost.setItemViewCacheSize(20)
            rvPost.itemAnimator = null
            adapter = PostAdapter(
                formatStringHelper,
                onItemClick = { model, position ->
                    viewModel.onNavigateToDetail(model)
                },
                onItemMenuClick = { view, model, position ->
                    viewModel.onShowPopupMenu(view, model, position)
                }
            )
            rvPost.adapter = adapter
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.event.collect { event ->
                    when (event) {
                        PostViewModel.Event.RequestNextPage -> {
                            viewModel.listPost(true)
                        }
                        is PostViewModel.Event.NavigateToDetail -> {
                            val action = PostFragmentDirections.actionPostFragmentToDetailFragment(event.post)
                            mainActivity.navController.navigate(action)
                        }
                        is PostViewModel.Event.ShowPopupMenu -> {
                            val popup = PopupMenu(requireContext(), event.view)
                            popup.menuInflater.inflate(R.menu.post_menu, popup.menu)
                            popup.setOnMenuItemClickListener {
                                when (it.itemId) {
                                    R.id.edit -> {
                                        viewModel.onNavigateToForm(event.post, true)
                                    }
                                    R.id.delete -> {
                                        viewModel.onRequestDelete(event.post)
                                    }
                                }
                                true
                            }
                            popup.show()
                        }
                        is PostViewModel.Event.NavigateToForm -> {
                            if (event.isEdit) {
                                val intent = Intent(requireContext(), FormPostActivity::class.java).putExtra("post", event.post)
                                editPostLauncher.launch(intent)
                            } else {
                                val intent = Intent(requireContext(), FormPostActivity::class.java)
                                createPostLauncher.launch(intent)
                            }
                        }
                        is PostViewModel.Event.RequestDelete -> {
                            viewModel.deletePost(event.post)
                        }
                        is PostViewModel.Event.RequestCreate -> {
                            viewModel.createPost(event.post)
                        }
                        is PostViewModel.Event.RequestEdit -> {
                            viewModel.editPost(event.post)
                        }
                    }
                }
            }
        }
    }

    override fun subscribeListeners() {
        binding.apply {
            rvPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                        val layoutManager = (recyclerView.getLayoutManager() as LinearLayoutManager)
                        val position = layoutManager.findLastVisibleItemPosition()
                        if (!recyclerView.canScrollVertically(1) && position >= 0) {
                            val type = adapter.getItemViewType(position)
                            if (adapter.itemCount > 0) {
                                if (type == PostAdapter.VIEW_LOADING) {
                                    viewModel.onNextPage()
                                }
                            }
                        }
                    }
                }
            })
            refresh.setOnRefreshListener {
                viewModel.listPost(false, true)
            }
            fabCreateData.setOnClickListener {
                viewModel.onNavigateToForm(null, false)
            }
        }
    }

    override fun subscribeObservers() {
        viewModel.observeListPostResponse().removeObservers(viewLifecycleOwner)
        viewModel.observeListPostResponse().observe(viewLifecycleOwner, {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        if (it.data != null) {
                            adapter.submitList(adapter.getModels(it.data, true))
                        }
                    }
                    Resource.Status.SUCCESS -> {
                        binding.apply {
                            if (it.data != null) {
                                if (refresh.isRefreshing) {
                                    refresh.isRefreshing = false
                                }
                                adapter.submitList(adapter.getModels(it.data))
                            }
                        }
                    }
                    Resource.Status.ERROR -> {
                        if (it.data != null) {
                            adapter.submitList(adapter.getModels(it.data))
                        }
                        binding.apply {
                            if (it.errorModel != null) {
                                Toast.makeText(
                                    requireContext(),
                                    it.errorModel!!.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    Resource.Status.UNAUTHORIZED -> {
                        //
                    }
                    Resource.Status.TIMEOUT -> {
                        //
                    }
                }
            }
        })
        viewModel.observeDeletePostResponse().removeObservers(viewLifecycleOwner)
        viewModel.observeDeletePostResponse().observe(viewLifecycleOwner, {
            if (it != null && !viewModel.isDeleted) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        mainActivity.showLoading()
                    }
                    Resource.Status.SUCCESS -> {
                        it.data?.let {
                            adapter.submitList(it)
                            Toast.makeText(requireContext(), "Berhasil menghapus", Toast.LENGTH_SHORT).show()
                        }
                        mainActivity.hideLoading()
                    }
                    Resource.Status.ERROR -> {
                        it.errorModel?.let {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        mainActivity.hideLoading()
                    }
                    Resource.Status.UNAUTHORIZED -> {
                        mainActivity.hideLoading()
                    }
                    Resource.Status.TIMEOUT -> {
                        mainActivity.hideLoading()
                    }
                }
            }
        })
        viewModel.observeCreatedPostResponse().removeObservers(viewLifecycleOwner)
        viewModel.observeCreatedPostResponse().observe(viewLifecycleOwner, {
            if (it != null && !viewModel.isCreated) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        mainActivity.showLoading()
                    }
                    Resource.Status.SUCCESS -> {
                        it.data?.let {
                            adapter.submitList(it)
                            binding.rvPost.scrollToPosition(0)
                            Toast.makeText(requireContext(), "Berhasil membuat", Toast.LENGTH_SHORT).show()
                        }
                        mainActivity.hideLoading()
                    }
                    Resource.Status.ERROR -> {
                        it.errorModel?.let {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        mainActivity.hideLoading()
                    }
                    Resource.Status.UNAUTHORIZED -> {
                        mainActivity.hideLoading()
                    }
                    Resource.Status.TIMEOUT -> {
                        mainActivity.hideLoading()
                    }
                }
            }
        })
        viewModel.observeEditPostResponse().removeObservers(viewLifecycleOwner)
        viewModel.observeEditPostResponse().observe(viewLifecycleOwner, {
            if (it != null && !viewModel.isEdited) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        mainActivity.showLoading()
                    }
                    Resource.Status.SUCCESS -> {
                        it.data?.let {
                            adapter.submitList(it)
                            Toast.makeText(requireContext(), "Berhasil mengedit", Toast.LENGTH_SHORT).show()
                        }
                        mainActivity.hideLoading()
                    }
                    Resource.Status.ERROR -> {
                        it.errorModel?.let {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        mainActivity.hideLoading()
                    }
                    Resource.Status.UNAUTHORIZED -> {
                        mainActivity.hideLoading()
                    }
                    Resource.Status.TIMEOUT -> {
                        mainActivity.hideLoading()
                    }
                }
            }
        })
    }
}