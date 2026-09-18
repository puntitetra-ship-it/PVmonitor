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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                PvMonitorApp()
            }
        }
    }
}

@Composable
fun PvMonitorApp() {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("pvmonitor", 0)

    var settings by remember { mutableStateOf(false) }

    var shellyIp by remember {
        mutableStateOf(prefs.getString("shelly_ip", "") ?: "")
    }

    var gridPower by remember { mutableStateOf(0.0) }
    var shellyOnline by remember { mutableStateOf(false) }

    LaunchedEffect(shellyIp) {

        while (true) {

            if (shellyIp.isNotBlank()) {

                try {

                    val value = readShellyPower(shellyIp)

                    gridPower = value
                    shellyOnline = true

                } catch (e: Exception) {

                    shellyOnline = false
                }
            }

            delay(5000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                "PV Monitor",
                style = MaterialTheme.typography.headlineLarge
            )

            TextButton(
                onClick = { settings = !settings }
            ) {

                Text(
                    if (settings) "Dashboard"
                    else "Einstellungen"
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        if (settings) {

            Text(
                "Geräte",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Shelly Pro 3EM",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = shellyIp,
                onValueChange = { shellyIp = it },
                label = {
                    Text("Shelly IP-Adresse")
                },
                placeholder = {
                    Text("z.B. 192.168.1.50")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = {

                    prefs.edit()
                        .putString("shelly_ip", shellyIp.trim())
                        .apply()

                    settings = false
                }
            ) {

                Text("Speichern")
            }

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                if (shellyOnline)
                    "Shelly: verbunden"
                else
                    "Shelly: nicht verbunden"
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text("Zendure SolarFlow 2400 AC+")
            Text("Enphase IQ Gateway")

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Zendure und Enphase richten wir anschließend ein.",
                style = MaterialTheme.typography.bodyMedium
            )

        } else {

            PowerCard(
                title = "PV-Erzeugung",
                value = "0 W"
            )

            PowerCard(
                title = "Hausverbrauch",
                value = "0 W"
            )

            PowerCard(
                title =
                    if (gridPower >= 0)
                        "Netzbezug"
                    else
                        "Einspeisung",

                value = "${abs(gridPower).toInt()} W"
            )

            PowerCard(
                title = "Batterie",
                value = "0 W",
                extra = "-- %"
            )

            HorizontalDivider()

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Shelly 3EM: " +
                    if (shellyOnline)
                        "verbunden"
                    else
                        "nicht verbunden"
            )

            Text("Zendure: nicht verbunden")
            Text("Enphase: nicht verbunden")

            Spacer(modifier = Modifier.height(20.dp))

            Text("PV Monitor V3")
        }
    }
}

@Composable
fun PowerCard(
    title: String,
    value: String,
    extra: String? = null
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical
