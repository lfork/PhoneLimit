package com.lfork.phonelimitadvanced.main.settings

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.data.getSettingsIndexTipsSwitch
import com.lfork.phonelimitadvanced.data.saveSettingsIndexTipsSwitch
import kotlinx.android.synthetic.main.my_frag.view.*

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var viewModel: MyViewModel

    private var root:View?=null;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.my_frag, container, false)
            root!!.switch_index_tips.isChecked = context?.getSettingsIndexTipsSwitch()?:true

            root!!.switch_index_tips.setOnCheckedChangeListener { buttonView, isChecked ->
                context?.saveSettingsIndexTipsSwitch(isChecked)
            }
        }


        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
