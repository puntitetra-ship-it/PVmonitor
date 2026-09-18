package at.pvmonitor.v3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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

    var settings by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PV Monitor") },
                actions = {
                    TextButton(
                        onClick = { settings = !settings }
                    ) {
                        Text(
                            if (settings) "Dashboard"
                            else "Einstellungen"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (settings) {

                Text(
                    "Geräte",
                    fontWeight = FontWeight.Bold
                )

                Text("Zendure SolarFlow 2400 AC+")
                Text("Shelly Pro 3EM")
                Text("Enphase IQ Gateway")

                Text(
                    "Geräteverbindung wird als Nächstes eingerichtet."
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
                    title = "Netz",
                    value = "0 W"
                )

                PowerCard(
                    title = "Batterie",
                    value = "0 W",
                    extra = "-- %"
                )

                HorizontalDivider()

                Text("Zendure: nicht verbunden")
                Text("Shelly 3EM: nicht verbunden")
                Text("Enphase: nicht verbunden")

                Text(
                    "PV Monitor V3",
                    style = MaterialTheme.typography.bodySmall
                )
            }
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
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            if (extra != null) {
                Text(extra)
            }
        }
    }
}
