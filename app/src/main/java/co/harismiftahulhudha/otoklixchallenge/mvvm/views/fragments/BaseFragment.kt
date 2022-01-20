package co.harismiftahulhudha.otoklixchallenge.mvvm.views.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import co.harismiftahulhudha.otoklixchallenge.mvvm.views.activities.MainActivity

abstract class BaseFragment: Fragment() {
    lateinit var mainActivity: MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = activity as MainActivity
    }
}