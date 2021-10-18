package com.obolus.openremo.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.obolus.openremo.R
import com.obolus.openremo.databinding.FragmentHomeBinding
import com.github.kittinunf.result.Result
import com.google.gson.Gson

val gson = Gson()

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        binding.progressBar4.visibility = View.GONE

        val light_id = "c9d0be99-ffa8-4253-848b-8eb131a894db"
        val aircon_id = "928f2a54-f1bc-4a75-930c-1fedc5d662c7"
        val url = "https://api.nature.global"
        var token: String = ""
        var temp = 26

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        if (sharedPref != null) {
            token = sharedPref.getString(getString(R.string.api_key), "").toString()
            println("found token in pref: '${token}'")
            temp = sharedPref.getInt(getString(R.string.setting_temp), 26).toInt()
            println("found temp in pref: '${temp}'")
        }
        if (token.isEmpty()) {
            println("Token is empty")
            textView.text = "Please set token"
        }

        binding.buttonhot.setOnClickListener() {
            textView.text = "Sending command (T=${temp})"
            binding.progressBar4.visibility = View.VISIBLE
            Fuel.post(
                "$url/1/appliances/$aircon_id/aircon_settings",
                listOf(
                    "appliance" to aircon_id,
                    "operation_mode" to "cool",
                    "temperature" to temp.toString()
                )
            ).authentication()
                .bearer(token)
                .responseString() { result2 ->
                    when (result2) {
                        is Result.Success -> {
                            val appliances = result2.get()

                            println("asd $appliances")
                            textView.text = "Done"
                        }
                        is Result.Failure -> {
                            println(result2.toString())
                            textView.text = "Failure Get Appliance"
                        }
                    }
                    binding.progressBar4.visibility = View.GONE
                }
        }
        binding.buttoncold.setOnClickListener() {
            textView.text = "Sending command (T=${temp})"
            binding.progressBar4.visibility = View.VISIBLE
            Fuel.post(
                "$url/1/appliances/$aircon_id/aircon_settings",
                listOf(
                    "appliance" to aircon_id,
                    "operation_mode" to "warm",
                    "temperature" to temp.toString()
                )
            ).authentication()
                .bearer(token)
                .responseString() { result2 ->
                    when (result2) {
                        is Result.Success -> {
                            val appliances = result2.get()

                            println("asd $appliances")
                            textView.text = "Done"
                        }
                        is Result.Failure -> {
                            println(result2.toString())
                            textView.text = "Failure Get Appliance"
                        }
                    }
                    binding.progressBar4.visibility = View.GONE
                }
        }
        binding.buttonacoff.setOnClickListener() {
            textView.text = "Sending command AC off"
            binding.progressBar4.visibility = View.VISIBLE
            Fuel.post(
                "$url/1/appliances/$aircon_id/aircon_settings",
                listOf(
                    "appliance" to aircon_id,
                    "button" to "power-off"
                )
            ).authentication()
                .bearer(token)
                .responseString() { result2 ->
                    when (result2) {
                        is Result.Success -> {
                            val appliances = result2.get()

                            println("asd $appliances")
                            textView.text = "Done"
                        }
                        is Result.Failure -> {
                            println(result2.toString())
                            textView.text = "Failure Get Appliance"
                        }
                    }
                    binding.progressBar4.visibility = View.GONE
                }
        }

        binding.button10.setOnClickListener() {
            textView.text = "Sending command"
            binding.progressBar4.visibility = View.VISIBLE
            Fuel.post(
                "$url/1/appliances/$light_id/light",
                listOf("appliance" to light_id, "button" to "onoff")
            )
                .authentication()
                .bearer(token)
                .responseString() { result2 ->
                    when (result2) {
                        is Result.Success -> {
                            val appliances = result2.get()

                            println("asd $appliances")
                            textView.text = "Done"
                        }
                        is Result.Failure -> {
                            println(result2.toString())
                            textView.text = "Failure Get Appliance"
                        }
                    }
                    binding.progressBar4.visibility = View.GONE
                }
        }

        binding.button11.setOnClickListener() {
            textView.text = "Sending command"
            binding.progressBar4.visibility = View.VISIBLE
            Fuel.post(
                "$url/1/appliances/$light_id/light",
                listOf("appliance" to light_id, "button" to "onoff")
            )
                .authentication()
                .bearer(token)
                .responseString() { result2 ->
                    when (result2) {
                        is Result.Success -> {
                            val appliances = result2.get()

                            println("asd $appliances")
                            textView.text = "Done"
                        }
                        is Result.Failure -> {
                            println(result2.toString())
                            textView.text = "Failure Get Appliance"
                        }
                    }
                    binding.progressBar4.visibility = View.GONE
                }
        }

        binding.button21.setOnClickListener() {
            textView.text = "Sending command"
            binding.progressBar4.visibility = View.VISIBLE


/*
            Fuel.get("$url/1/appliances")
                .authentication()
                .bearer(token)
                .responseString() { result ->
                    when(result) {
                        is Result.Success -> {
                            val devices = result.get()
                            println("asdev $devices")

                            textView.text = "Got device"

                        }
                        is Result.Failure -> {
                            textView.text = result.toString()
                            binding.progressBar4.setProgress(1, false)
                            textView.text = "Failure Get Device"
                        }
                    }
*/

            Fuel.post(
                "$url/1/appliances/$light_id/light",
                listOf("appliance" to light_id, "button" to "night")
            )
                .authentication()
                .bearer(token)
                .responseString() { result2 ->
                    when (result2) {
                        is Result.Success -> {
                            val appliances = result2.get()

                            println("asd $appliances")
                            textView.text = "Done"
                        }
                        is Result.Failure -> {
                            println(result2.toString())
                            textView.text = "Failure Get Appliance"
                        }
                    }
                    binding.progressBar4.visibility = View.GONE
                }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}