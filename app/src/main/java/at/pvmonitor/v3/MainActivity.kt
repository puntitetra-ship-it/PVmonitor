package at.pvmonitor.v3

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = TextView(this).apply {
            text = "PV Monitor\n\nApp läuft erfolgreich ✓"
            textSize = 24f
            setPadding(40, 80, 40, 40)
        }

        setContentView(text)
    }
}
