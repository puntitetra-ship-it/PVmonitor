package at.pvmonitor.v3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.max

data class PvState(
    val pv: Double = 0.0,
    val house: Double = 0.0,
    val grid: Double = 0.0,
    val battery: Double = 0.0,
    val soc: Int? = null,
    val zendure: Boolean = false,
    val shelly: Boolean = false,
    val enphase: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                PVMonitor()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PVMonitor() {

    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = context.getSharedPreferences("pvmonitor", 0)

    var zendureIp by remember {
        mutableStateOf(prefs.getString("zendure", "") ?: "")
    }

    var shellyIp by remember {
        mutableStateOf(prefs.getString("shelly", "") ?: "")
    }

    var enphaseIp by remember {
        mutableStateOf(prefs.getString("enphase", "") ?: "")
    }

    var settings by remember { mutableStateOf(false) }
    var data by remember { mutableStateOf(PvState()) }

    val client = remember { OkHttpClient() }

    LaunchedEffect(zendureIp, shellyIp, enphaseIp) {

        while (true) {

            var pv = 0.0
            var grid = 0.0
            var battery = 0.0
            var soc: Int? = null

            var zOK = false
            var sOK = false
            var eOK = false

            if (zendureIp.isNotBlank()) {
                try {
                    val json = getJson(
                        client,
                        "http://$zendureIp/properties/report"
                    )

                    val p =
                        json.optJSONObject("properties") ?: json

                    soc = p.optInt("electricLevel")

                    battery =
                        p.optDouble("outputPackPower", 0.0) -
                        p.optDouble("packInputPower", 0.0)

                    zOK = true
                } catch (_: Exception) {}
            }

            if (shellyIp.isNotBlank()) {
                try {
                    val json = getJson(
                        client,
                        "http://$shellyIp/rpc/EM.GetStatus?id=0"
                    )

                    grid =
                        json.optDouble("total_act_power", 0.0)

                    sOK = true
                } catch (_: Exception) {}
            }

            if (enphaseIp.isNotBlank()) {
                try {
                    val json = getJson(
                        client,
                        "http://$enphaseIp/production.json"
                    )

                    val production =
                        json.optJSONArray("production")

                    if (production != null) {
                        for (i in 0 until production.length()) {
                            val item =
                                production.getJSONObject(i)

                            pv = max(
                                pv,
                                item.optDouble("wNow", 0.0)
                            )
                        }
                    }

                    eOK = true
                } catch (_: Exception) {}
            }

            val house =
                max(0.0, pv + grid - battery)

            data = PvState(
                pv,
                house,
                grid,
                battery,
                soc,
                zOK,
                sOK,
                eOK
            )

            delay(5000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PV Monitor") },
                actions = {
                    TextButton(
                        onClick = { settings = !settings }
                    ) {
                        Text(
                            if (settings)
                                "Dashboard"
                            else
                                "Einstellungen"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScroll
