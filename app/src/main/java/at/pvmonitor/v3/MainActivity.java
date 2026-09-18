package at.pvmonitor.v3;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView text = new TextView(this);
        text.setText("PV Monitor\n\nApp läuft erfolgreich ✓");
        text.setTextSize(24);
        text.setPadding(40, 80, 40, 40);

        setContentView(text);
    }
}
