package co.harismiftahulhudha.otoklixchallenge.mvvm.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import co.harismiftahulhudha.otoklixchallenge.databinding.FragmentDetailPostBinding
import co.harismiftahulhudha.otoklixchallenge.helpers.FormatStringHelper
import co.harismiftahulhudha.otoklixchallenge.mvvm.interfaces.BaseInterface
import co.harismiftahulhudha.otoklixchallenge.mvvm.viewmodels.DetailPostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : BaseFragment(), BaseInterface {

    private lateinit var binding: FragmentDetailPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    // ViewModels
    private val viewModel: DetailPostViewModel by viewModels()

    // Components
    @Inject
    lateinit var formatStringHelper: FormatStringHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents()
        subscribeListeners()
        subscribeObservers()

        viewModel.onSetData()
    }

    override fun initComponents() {
        binding.apply {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.event.collect { event ->
                    when (event) {
                        DetailPostViewModel.Event.SetData -> {
                            txtTitle.text = viewModel.post.title
                            txtContent.text = viewModel.post.content
                            txtPublishedAt.text = formatStringHelper.covertTimeToText(viewModel.post.publishedAt)
                        }
                    }
                }
            }
        }
    }

    override fun subscribeListeners() {
        //
    }

    override fun subscribeObservers() {
        //
    }
}