package ryebread761.lwinrecents;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Not currently using normal android preferences for the settings activity,
 * as it seemed this would be a little easier.
 */

@SuppressLint({ "ShowToast", "WorldReadableFiles" })
@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
    private static final String PREF_NAME = "LWIRPrefs";
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        toast = Toast.makeText(this, "Changes set!", Toast.LENGTH_LONG);

        CheckBox co = (CheckBox) findViewById(R.id.custom_opacity);
		SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_WORLD_READABLE);
        co.setChecked(prefs.getBoolean("custom_opacity", false));


        TextView opacityText = (TextView) findViewById(R.id.background_opacity_text);
        opacityText.setText(getResources().getString(R.string.background_opacity) +
                Integer.toString(prefs.getInt("custom_opacity_value", 0)));
        SeekBar opacityBar = (SeekBar) findViewById(R.id.background_opacity);
        opacityBar.setProgress(prefs.getInt("custom_opacity_value", 0));
        opacityBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int opacity = 0;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
				SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_WORLD_READABLE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("custom_opacity_value", opacity);
                editor.commit();
                toast.show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                opacity = progress;
                TextView opacityText = (TextView) findViewById(R.id.background_opacity_text);
                opacityText.setText(getResources().getString(R.string.background_opacity) +
                        opacity);
            }
        });
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()) {
            case R.id.custom_opacity:
				SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_WORLD_READABLE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("custom_opacity", checked);
                editor.commit();
                toast.show();
                break;
        }
    }
}
