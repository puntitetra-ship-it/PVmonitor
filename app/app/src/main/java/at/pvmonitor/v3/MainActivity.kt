package at.pvmonitor.v3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max

data class PvState(
    val pvW: Double = 0.0,
    val gridW: Double = 0.0,
    val houseW: Double = 0.0,
    val batteryPct: Int? = null,
    val batteryW: Double = 0.0,
    val zendureOnline: Boolean = false,
    val shellyOnline: Boolean = false,
    val enphaseOnline: Boolean = false,
    val message: String = "Bereit"
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                PVMonitorApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PVMonitorApp() {

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

    var state by remember {
        mutableStateOf(PvState())
    }

    var settings by remember {
        mutableStateOf(false)
    }

    val client = remember {
        OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .build()
    }

    suspend fun refresh() = withContext(Dispatchers.IO) {

        var pv = 0.0
        var grid = 0.0
        var battery = 0.0
        var soc:
