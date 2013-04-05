package ua.pp.seregamakarov.searchvideo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.SearchRecentSuggestions;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PrefActivity extends PreferenceActivity {
	private Button btnClearRecent;
	private final int DIALOG_CLEAR = 1;
	private SearchRecentSuggestions suggestions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		setContentView(R.layout.pref_layout);
		btnClearRecent = (Button) findViewById(R.id.btnClearRecent);
		
		btnClearRecent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_CLEAR);
			}
		});
		
		suggestions = new SearchRecentSuggestions(this,
                SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
	}
	
	protected Dialog onCreateDialog(int id) {
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
	}
	
	android.content.DialogInterface.OnClickListener myClickListener = new android.content.DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case Dialog.BUTTON_POSITIVE:
				suggestions.clearHistory();
				break;
			case Dialog.BUTTON_NEUTRAL:
				break;
			}
		}
	};
}
