package com.obolus.openremo.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.obolus.openremo.R
import com.obolus.openremo.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val apiKey : String = activity?.getPreferences(Context.MODE_PRIVATE)?.getString(getString(R.string.api_key), "").toString()
        binding.settingApiKey.text = Editable.Factory.getInstance().newEditable(apiKey)

        binding.seekBar2.progress = (activity?.getPreferences(Context.MODE_PRIVATE)?.getInt(getString(R.string.setting_temp), 26) ?: 26) - 16

        binding.settingApiKey.doAfterTextChanged {
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            if (sharedPref != null) {
                with(sharedPref.edit()) {
                    putString(getString(R.string.api_key), binding.settingApiKey.text.toString())
                    println("setting apikey to ${binding.settingApiKey.text}")
                    apply()
                }
            }
        }

        binding.seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                if (sharedPref != null) {
                    with(sharedPref.edit()) {
                        val temp : Int = seekBar.progress + 16
                        putInt(getString(R.string.setting_temp), temp)
                        println("setting temp to $temp")
                        apply()
                    }
                }
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
