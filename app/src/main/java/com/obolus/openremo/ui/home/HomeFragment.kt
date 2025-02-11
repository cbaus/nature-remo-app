package com.obolus.openremo.ui.home

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.math.MathUtils.clamp
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
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

var temps = floatArrayOf()
var humidities = intArrayOf()
var timer: Timer? = null
var lastRunTask = 0L
val updateFreqInS = 600L

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    private var url: String = ""
    private var token: String = ""
    private var lightId: String = ""
    private var light2Id: String = ""
    private var airconId: String = ""

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var db: AppDatabase? = null
    private var measurementDao: MeasurementDao? = null

    private fun aBitEarlier(): Long {
        return System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24L)
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
        url = getString(R.string.api_url)

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
            lightId = sharedPref.getString(getString(R.string.setting_light_id),"").toString()
            light2Id = sharedPref.getString(getString(R.string.setting_light2_id),"").toString()
            airconId = sharedPref.getString(getString(R.string.setting_aircon_id),"").toString()
        }
        if (token.isEmpty()) {
            println("Token is empty")
            textView.text = "Please set token"
        }
        if (lightId.isEmpty()) {
            println("Light id is empty")
            textView.text = "Please set light id"
        }
        if (light2Id.isEmpty()) {
            println("Light2 id is empty")
            textView.text = "Please set light 2 id"
        }
        if (airconId.isEmpty()) {
            println("AC id is empty")
            textView.text = "Please set aircon id"
        }

        db = AppDatabase.getAppDataBase(context = requireContext())
        measurementDao = db?.measurmentDao()

        binding.buttonhot.setOnClickListener {
            textView.text = "Sending cool down to T=${temp}"
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
            textView.text = "Sending heat up T=${temp}"
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

        binding.buttonDayL1.setOnClickListener {
            sendLightCommand("on", lightId)
        }

        binding.buttonOffL1.setOnClickListener {
            sendLightCommand("off", lightId)
        }

        binding.buttonNightL1.setOnClickListener {
            sendLightCommand("night", lightId)
        }

        binding.buttonDayL2.setOnClickListener {
            sendLightCommand("on", light2Id)
        }

        binding.buttonOffL2.setOnClickListener {
            sendLightCommand("off", light2Id)
        }

        binding.buttonNightL2.setOnClickListener {
            sendLightCommand("night", light2Id)
        }

        val times: MutableList<String> = arrayListOf()
        measurementDao?.let {
            val latest = measurementsToLists(it.getLatest(aBitEarlier()))
            temps = latest.temps
            humidities = latest.humidities
            for (time in latest.times) {
                times.add(DateFormat.format("HH:mm", time).toString())
            }
        }

        val aaChartView = root.findViewById<AAChartView>(R.id.aa_chart_view)
        val aaChartModel: AAChartModel = AAChartModel()
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
                )
            )

        //Draw chart (at this moment without data
        aaChartView.aa_drawChartWithChartModel(aaChartModel)

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
        val delay = TimeUnit.SECONDS.toMillis(updateFreqInS) - clamp(System.currentTimeMillis() - lastRunTask,
            0L, TimeUnit.SECONDS.toMillis(updateFreqInS))
        println("Scheduling task with delay of ${delay}ms")
        timer = Timer("pollTemp", true)
        timer?.let { it ->
            it.scheduleAtFixedRate(
                timerTask {
                    measurementDao?.let { updateMeasurements(it) }
                },
                delay,
                TimeUnit.SECONDS.toMillis(updateFreqInS)
            )
        }
        return root
    }

    private fun sendLightCommand(buttonCmd: String, id : String) {
        notify("Sending command", true)
        binding.progressBar4.visibility = View.VISIBLE
        Fuel.post(
            "$url/1/appliances/$id/light",
            listOf("appliance" to id, "button" to buttonCmd)
        )
            .authentication()
            .bearer(token)
            .responseString { result2 ->
                when (result2) {
                    is Result.Success -> {
                        val appliances = result2.get()

                        println("asd $appliances")
                        notify("Done", false)
                    }
                    is Result.Failure -> {
                        println(result2.toString())
                        notify("Failure Get Appliance", false)
                    }
                }
                binding.progressBar4.visibility = View.GONE
            }
    }

    private fun updateMeasurements(measurementDao : MeasurementDao) {
        val ranRecently = lastRunTask > System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(updateFreqInS - 10L)
        println("Running update task (already updated in this period? $ranRecently, last ran $lastRunTask > ${System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(updateFreqInS - 10L)})")

        if (ranRecently)
            return

        lastRunTask = System.currentTimeMillis()

        Fuel.get("$url/1/devices")
            .authentication()
            .bearer(token)
            .responseString { result ->
                notify("Updating", true)
                when (result) {
                    is Result.Success -> {
                        val devicesJSON = JSONArray(result.get())
                        val events = devicesJSON.getJSONObject(0).getJSONObject("newest_events")
                        val temperature: Float? =
                            events.getJSONObject("te").get("val").toString().toFloatOrNull()
                        val humidity: Int? =
                            events.getJSONObject("hu").get("val").toString().toIntOrNull()

                        if (temperature != null && humidity != null) {
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
                    times.add(DateFormat.format("HH:mm", time).toString())
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