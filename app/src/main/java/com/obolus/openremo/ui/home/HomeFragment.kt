package com.obolus.openremo.ui.home

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.obolus.openremo.databinding.FragmentHomeBinding
import com.github.kittinunf.result.Result
import com.obolus.openremo.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask


var temps = floatArrayOf()
var humidities = intArrayOf()
var timer: Timer? = null

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    private val lightId = "c9d0be99-ffa8-4253-848b-8eb131a894db"
    private val airconId = "928f2a54-f1bc-4a75-930c-1fedc5d662c7"
    private val url = "https://api.nature.global"
    private var token = ""

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var db: AppDatabase? = null
    private var measurementDao: MeasurementDao? = null

    private fun aBitEarlier(): Long {
        return System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30L * 5L)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })

        binding.progressBar4.visibility = View.GONE

        var temp = 26

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        if (sharedPref != null) {
            token = sharedPref.getString(getString(R.string.api_key), "").toString()
            println("found token in pref: '${token}'")
            temp = sharedPref.getInt(getString(R.string.setting_temp), 26)
            println("found temp setting in pref: '${temp}'")
        }
        if (token.isEmpty()) {
            println("Token is empty")
            textView.text = "Please set token"
        }

        Thread.sleep(1000)
        db = AppDatabase.getAppDataBase(context = requireContext())
        measurementDao = db?.measurmentDao()

        binding.buttonhot.setOnClickListener {
            textView.text = "Sending command (T=${temp})"
            binding.progressBar4.visibility = View.VISIBLE
            Fuel.post(
                "$url/1/appliances/$airconId/aircon_settings",
                listOf(
                    "appliance" to airconId,
                    "operation_mode" to "cool",
                    "temperature" to temp.toString()
                )
            ).authentication()
                .bearer(token)
                .responseString { result2 ->
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
        binding.buttoncold.setOnClickListener {
            textView.text = "Sending command (T=${temp})"
            binding.progressBar4.visibility = View.VISIBLE
            Fuel.post(
                "$url/1/appliances/$airconId/aircon_settings",
                listOf(
                    "appliance" to airconId,
                    "operation_mode" to "warm",
                    "temperature" to temp.toString()
                )
            ).authentication()
                .bearer(token)
                .responseString { result2 ->
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
        binding.buttonacoff.setOnClickListener {
            textView.text = "Sending command AC off"
            binding.progressBar4.visibility = View.VISIBLE
            Fuel.post(
                "$url/1/appliances/$airconId/aircon_settings",
                listOf(
                    "appliance" to airconId,
                    "button" to "power-off"
                )
            ).authentication()
                .bearer(token)
                .responseString { result2 ->
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

        binding.button10.setOnClickListener {
            textView.text = "Sending command"
            binding.progressBar4.visibility = View.VISIBLE
            Fuel.post(
                "$url/1/appliances/$lightId/light",
                listOf("appliance" to lightId, "button" to "onoff")
            )
                .authentication()
                .bearer(token)
                .responseString { result2 ->
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

        binding.button11.setOnClickListener {
            textView.text = "Sending command"
            binding.progressBar4.visibility = View.VISIBLE
            Fuel.post(
                "$url/1/appliances/$lightId/light",
                listOf("appliance" to lightId, "button" to "onoff")
            )
                .authentication()
                .bearer(token)
                .responseString { result2 ->
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

        binding.button21.setOnClickListener {
            textView.text = "Sending command"
            binding.progressBar4.visibility = View.VISIBLE

            Fuel.post(
                "$url/1/appliances/$lightId/light",
                listOf("appliance" to lightId, "button" to "night")
            )
                .authentication()
                .bearer(token)
                .responseString { result2 ->
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

        val times: MutableList<String> = arrayListOf()
        measurementDao?.let {
            val latest = measurementsToLists(it.getLatest(aBitEarlier()))
            temps = latest.temps
            humidities = latest.humidities
            for (time in latest.times) {
                times.add(DateFormat.format("hh:mm", time).toString())
            }
        }

        val aaChartView = root.findViewById<AAChartView>(R.id.aa_chart_view)
        val aaChartModel: AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .backgroundColor("#333333")
            .axesTextColor("#ffffff")
            .yAxisTitle("°C/%")
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Temperature")
                        .data(arrayOf(temps)),
                    AASeriesElement()
                        .name("Humidity")
                        .data(arrayOf(humidities))
                )
            )

        //Draw chart (at this moment without data
        aaChartView.aa_drawChartWithChartModel(aaChartModel)
        aaChartView.aa_hideTheSeriesElementContent(1)

        //Schedule data collection
        try {
            timer?.let {
                it.cancel() // no super safe i guess
                it.purge()
                timer = null
                println("Old timer task removed")
            }
        } catch (e: Exception) {
            println(e)
        }
        timer = Timer("pollTemp", true)
        timer?.let {
            it.scheduleAtFixedRate(
                timerTask {
                    measurementDao?.let { updateMeasurements(it) }
                },
                0,
                TimeUnit.SECONDS.toMillis(600)
            )
        }
        return root
    }

    private fun updateMeasurements(measurementDao : MeasurementDao) {
        println("Running update task")


        Fuel.get("$url/1/devices")
            .authentication()
            .bearer(token)
            .responseString { result ->
                notify("Updating", true)
                when (result) {
                    is Result.Success -> {
                        val devicesJSON = JSONArray(result.get())
                        val events = (devicesJSON[0] as JSONObject)["newest_events"] as JSONObject
                        val temperature: Float? =
                            ((events["te"] as JSONObject)["val"]).toString().toFloatOrNull()
                        val humidity: Int? =
                            (events["hu"] as JSONObject)["val"].toString().toIntOrNull()

                        if (temperature != null && humidity != null) {
                            //temps = appendWithLimit(temps, temperature)
                            //humidities = appendWithLimit(humidities, humidity)
                            measurementDao.insert(Measurement(System.currentTimeMillis(),
                                temperature, humidity))
                            println("Saved temp and humid lists")
                        }

                        notify("Read temp $temperature", false)
                        redraw()
                    }
                    is Result.Failure -> {
                        println(result.toString())
                        notify("Failed get device", false)
                    }
                }
            }
    }

    private fun notify(text: String?, taskRunning: Boolean) {
        try {
            val textView: TextView = binding.textHome
            textView.text = text
            binding.progressBar4.visibility = if(taskRunning) View.VISIBLE else View.GONE
        } catch (e: Exception) {
            println("Did not update screen because Home not focussed: $e")
        }
    }

    private fun redraw() {
        try {
            val root: View = binding.root

            val times: MutableList<String> = arrayListOf()
            measurementDao?.let {
                val latest = measurementsToLists(it.getLatest(aBitEarlier()))
                temps = latest.temps
                humidities = latest.humidities
                for (time in latest.times) {
                    times.add(DateFormat.format("hh:mm", time).toString())
                }
            }

            val aaChartView = root.findViewById<AAChartView>(R.id.aa_chart_view)
            aaChartView.aa_refreshChartWithChartModel(AAChartModel()
                .chartType(AAChartType.Line)
                .backgroundColor("#333333")
                .axesTextColor("#ffffff")
                .yAxisTitle("°C/%")
                .categories(times.toTypedArray())
                .series(
                    arrayOf(
                        AASeriesElement()
                            .name("Temperature")
                            .data(temps.toTypedArray() as Array<Any>),
                        AASeriesElement()
                            .name("Humidity")
                            .data(humidities.toTypedArray() as Array<Any>)
                    ))

                )
            // or use aaChartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray for just data
        } catch (e: Exception) {
            println("Did not update screen because Home not focussed: $e")
            return
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}