package ua.pp.seregamakarov.searchvideo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PrefActivity extends PreferenceActivity {
	//private Button btnClearRecent;
	private final int DIALOG_CLEAR = 1;
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
                Log.i("PrefActibity","OnPreferenceCLick() = " + key);
                if(key.equals("pref_clear_recent")){
                    //showDialog(DIALOG_CLEAR);
                    AlertDialog.Builder adb = new AlertDialog.Builder(PrefActivity.this);
                    //AlertDialog.Builder adb = new AlertDialog.Builder(this);

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
	
	/*protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_CLEAR) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);

			adb.setTitle("Clear suggestions");
			adb.setMessage("Delete all?");
			adb.setIcon(android.R.drawable.ic_dialog_alert);
			adb.setPositiveButton(R.string.yes, myClickListener);
			adb.setNeutralButton(R.string.cancel, myClickListener);

			return adb.create();
		}
		return super.onCreateDialog(id);
	}*/
	
	/*android.content.DialogInterface.OnClickListener myClickListener = new android.content.DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case Dialog.BUTTON_POSITIVE:
				suggestions.clearHistory();
				break;
			case Dialog.BUTTON_NEUTRAL:
				break;
			}
		}
	};*/



    /*class ClearRecentAlertDialog extends Dialog {
        private Context ctx;
        public ClearRecentAlertDialog(Context context) {
            super(context);
            ctx = context;
        }

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder adb = new AlertDialog.Builder(ctx);
            //AlertDialog.Builder adb = new AlertDialog.Builder(this);

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
            // Create the AlertDialog object and return it
            return adb.create();
        }
    */
}
