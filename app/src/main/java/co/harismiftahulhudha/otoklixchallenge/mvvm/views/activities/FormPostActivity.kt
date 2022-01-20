package co.harismiftahulhudha.otoklixchallenge.mvvm.views.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import co.harismiftahulhudha.otoklixchallenge.R
import co.harismiftahulhudha.otoklixchallenge.databinding.ActivityFormPostBinding
import co.harismiftahulhudha.otoklixchallenge.mvvm.interfaces.BaseInterface
import co.harismiftahulhudha.otoklixchallenge.mvvm.models.PostModel
import co.harismiftahulhudha.otoklixchallenge.mvvm.viewmodels.FormPostViewModel
import co.harismiftahulhudha.otoklixchallenge.mvvm.views.fragments.PostFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FormPostActivity : AppCompatActivity(), BaseInterface {

    private lateinit var binding: ActivityFormPostBinding

    private val viewModel: FormPostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
        subscribeListeners()
        subscribeObservers()
    }

    override fun initComponents() {
        binding.apply {
            viewModel.post?.let {
                txtTitle.text = resources.getString(R.string.edit_post)
                viewModel.inputTitle = it.title
                viewModel.inputContent = it.content
            } ?: kotlin.run {
                txtTitle.text = resources.getString(R.string.buat_post)
            }
            if (!viewModel.inputTitle.equals("")) {
                inputTitle.setText(viewModel.inputTitle)
            }
            if (!viewModel.inputContent.equals("")) {
                inputContent.setText(viewModel.inputContent)
            }
            viewModel.onChangeInput()
            lifecycleScope.launchWhenStarted {
                viewModel.event.collect { event ->
                    when(event) {
                        FormPostViewModel.Event.RequestSave -> {
                            viewModel.post?.let {
                                val intent = Intent()
                                val post = it
                                post.title = viewModel.inputTitle
                                post.content = viewModel.inputContent
                                intent.putExtra(PostFragment.EDIT_FORM_POST, post)
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            } ?: kotlin.run {
                                val intent = Intent()
                                val post = PostModel(-1L)
                                post.title = viewModel.inputTitle
                                post.content = viewModel.inputContent
                                intent.putExtra(PostFragment.CREATE_FORM_POST, post)
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }
                        }
                        FormPostViewModel.Event.DisableBtnSubmit -> {
                            btnSubmit.isEnabled = false
                            btnSubmit.background = ContextCompat.getDrawable(this@FormPostActivity, R.drawable.bg_btn_disable_gray)
                        }
                        FormPostViewModel.Event.EnableBtnSubmit -> {
                            btnSubmit.isEnabled = true
                            btnSubmit.background = ContextCompat.getDrawable(this@FormPostActivity, R.drawable.bg_btn_active_gray)
                        }
                    }
                }
            }
        }
    }

    override fun subscribeListeners() {
        binding.apply {
            inputTitle.addTextChangedListener {
                viewModel.inputTitle = inputTitle.text.toString().trim()
                viewModel.onChangeInput()
            }
            inputContent.addTextChangedListener {
                viewModel.inputContent = inputContent.text.toString().trim()
                viewModel.onChangeInput()
            }
            btnSubmit.setOnClickListener {
                viewModel.onRequestSave()
            }
        }
    }

    override fun subscribeObservers() {
        //
    }
}