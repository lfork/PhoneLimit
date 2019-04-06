package com.lfork.phonelimit.main.settings

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.lfork.phonelimit.R
import com.lfork.phonelimit.data.getMainMenuVisibility
import com.lfork.phonelimit.data.getSettingsIndexTipsSwitch
import com.lfork.phonelimit.data.saveMainMenuVisibility
import com.lfork.phonelimit.data.saveSettingsIndexTipsSwitch
import kotlinx.android.synthetic.main.settings_frag.*

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var viewModel: MyViewModel

    private var root: View? = null;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.settings_frag, container, false)
        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switch_index_tips.isChecked = context?.getSettingsIndexTipsSwitch() ?: true

        switch_index_tips.setOnCheckedChangeListener { buttonView, isChecked ->
            context?.saveSettingsIndexTipsSwitch(isChecked)
        }

        switch_main_menu.isChecked = context?.getMainMenuVisibility() ?: true

        switch_main_menu.setOnCheckedChangeListener { buttonView, isChecked ->
            context?.saveMainMenuVisibility(isChecked)
            activity?.invalidateOptionsMenu()
        }

        btn_bg_set.setOnClickListener {
            BgSettingActivity.startBackgroundSelectActivity(context!!)
        }

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
