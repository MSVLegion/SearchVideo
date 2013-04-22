package ua.pp.seregamakarov.searchvideo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PrefActivity extends PreferenceActivity {
	private SearchRecentSuggestions suggestions;
    private Preference myPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
        myPreference = findPreference("pref_clear_recent");
        myPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                String key = preference.getKey();
                if(key.equals("pref_clear_recent")){
                    AlertDialog.Builder adb = new AlertDialog.Builder(PrefActivity.this);
                    adb.setTitle("Clear suggestions");
                    adb.setMessage("Delete all?");
                    adb.setIcon(android.R.drawable.ic_dialog_alert);
                    adb.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            suggestions.clearHistory();
                        }
                    });
                    adb.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    adb.show();
                    return true;
                }
                return false;
            }
        });
		setContentView(R.layout.pref_layout);
        Log.i("PrefActibity","OnCreate() ");
		suggestions = new SearchRecentSuggestions(this,
                SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
	}

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
    {
        super.onPreferenceTreeClick(preferenceScreen, preference);
        if (preference!=null)
            if (preference instanceof PreferenceScreen)
                if (((PreferenceScreen)preference).getDialog()!=null)
                    ((PreferenceScreen)preference).getDialog().getWindow().getDecorView().setBackgroundDrawable(this.getWindow().getDecorView().getBackground().getConstantState().newDrawable());
        return false;
    }
}
