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
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.result.Result
import com.obolus.openremo.R
import com.obolus.openremo.databinding.FragmentDashboardBinding
import org.json.JSONArray

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var url: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        url = getString(R.string.api_url)

        var apiKey: String = activity?.getPreferences(Context.MODE_PRIVATE)
            ?.getString(getString(R.string.api_key), "").toString()
        binding.settingApiKey.text = Editable.Factory.getInstance().newEditable(apiKey)
        binding.settingAirconId.text = activity?.getPreferences(Context.MODE_PRIVATE)
            ?.getString(getString(R.string.setting_aircon_id), "undefined").toString()
        binding.settingLightId.text = activity?.getPreferences(Context.MODE_PRIVATE)
            ?.getString(getString(R.string.setting_light_id), "undefined").toString()

        binding.seekBar2.progress = (activity?.getPreferences(Context.MODE_PRIVATE)
            ?.getInt(getString(R.string.setting_temp), 26) ?: 26) - 16

        binding.settingApiKey.doAfterTextChanged {
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            if (sharedPref != null) {
                with(sharedPref.edit()) {
                    putString(getString(R.string.api_key), binding.settingApiKey.text.toString())
                    println("setting apikey to ${binding.settingApiKey.text}")
                    apiKey = binding.settingApiKey.text.toString()
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
                        val temp: Int = seekBar.progress + 16
                        putInt(getString(R.string.setting_temp), temp)
                        println("setting temp to $temp")
                        apply()
                    }
                }
            }
        })

        binding.button.setOnClickListener {
            Fuel.get(
                "$url/1/appliances/",
            ).authentication()
                .bearer(apiKey)
                .responseString { result2 ->
                    when (result2) {
                        is Result.Success -> {
                            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                            val appliances = JSONArray(result2.get())
                            for (i in 0 until appliances.length()) {
                                val appliance = appliances.getJSONObject(i)
                                val type = appliance["type"]
                                if (type == "AC") {
                                    println("found AC")
                                    val id = appliance.get("id").toString()
                                    if (sharedPref != null) {
                                        with(sharedPref.edit()) {
                                            putString(
                                                getString(R.string.setting_aircon_id),
                                                id
                                            )
                                            apply()
                                            binding.settingAirconId.text = id
                                        }
                                    }
                                } else if (type == "LIGHT") {
                                    println("found light")
                                    val id = appliance.get("id").toString()
                                    if (sharedPref != null) {
                                        with(sharedPref.edit()) {
                                            putString(
                                                getString(R.string.setting_light_id),
                                                id
                                            )
                                            apply()
                                            binding.settingLightId.text = id
                                        }
                                    }
                                }
                            }
                        }

                        is Result.Failure -> {
                            println("Failed to get appliances $result2")
                        }
                    }
                }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
