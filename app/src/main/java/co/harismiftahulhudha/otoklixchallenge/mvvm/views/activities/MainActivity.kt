package co.harismiftahulhudha.otoklixchallenge.mvvm.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import co.harismiftahulhudha.otoklixchallenge.R
import co.harismiftahulhudha.otoklixchallenge.databinding.ActivityMainBinding
import co.harismiftahulhudha.otoklixchallenge.helpers.LoadingDialogHelper
import co.harismiftahulhudha.otoklixchallenge.mvvm.interfaces.BaseInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), BaseInterface {

    lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    private lateinit var loading: LoadingDialogHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
        subscribeListeners()
        subscribeObservers()
    }

    override fun initComponents() {
        loading = LoadingDialogHelper(this)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.mainHostFragment) as NavHostFragment
        navController = navHostFragment.findNavController()
    }

    override fun subscribeListeners() {
        //
    }

    override fun subscribeObservers() {
        //
    }

    fun showLoading() {
        loading.show(null, null)
    }

    fun hideLoading() {
        loading.hide()
    }
}