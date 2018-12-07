package il.co.shmulman.www.kotlinreadtempsensor_v2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ConnectWifi.setOnClickListener() {
            ConnectionStatus.text = "Loading sensors ..."

            // Local axillary variables initiation
            var dataLoaded200 = false
            var dataLoaded201 = false


            // Read sensors data via HTML file thread
            doAsync{
                val response200 = URL("http://10.100.102.200/").readText()
                dataLoaded200 = true
                uiThread {
                    printOutput(response200,"200")
                }
                ConnectionStatus.text = printDataLoadedStatus(dataLoaded200,dataLoaded201)
            }

            doAsync {
                val response201 = URL("http://10.100.102.201/").readText()
                dataLoaded201 = true
                uiThread {
                    printOutput(response201,"201")
                }
                ConnectionStatus.text = printDataLoadedStatus(dataLoaded200,dataLoaded201)
            }

            // Wait for 10 sec. to read two sensors
            ConnectionStatus.text = "Searching for sensors ..."

            // Print the result, whether the HTML file was read
            ConnectionStatus.text = printDataLoadedStatus(dataLoaded200,dataLoaded201)
        }
    }

    private fun printOutput(response : String, sensorNumber : String) {
        val timeAndDate = response.substringAfter("<h1>").substringBefore("<br>")
        val temperature = response.substringAfter("Temp").substringBefore(" C ")
        DataOutput.append("$sensorNumber: ${timeAndDate.dropLast(1)} Temperature: $temperature\n")
        Temperature_Final.text = temperature + "\u00B0"
    }

    private fun printDataLoadedStatus(status1:Boolean,status2:Boolean):String {

        return when(status1){
            true    -> {
                if (status2) "Sensors 200 and 201 are loaded"
                else "Sensor 200 is loaded 201 is NOT"
            }
            false -> {
                if (status2) "Sensor 201 is loaded 200 is NOT"
                else "Can NOT read sensors 200 and 201"
            }
        }
    }
}
