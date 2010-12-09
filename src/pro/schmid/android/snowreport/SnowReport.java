package pro.schmid.android.snowreport;

import pro.schmid.android.snowreport.controller.ResortsRetriever;
import pro.schmid.android.snowreport.model.Resort;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class SnowReport extends Activity {

	private Boolean reloadResorts = true;
	private SharedPreferences prefs;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		setContentView(R.layout.snowreport);

		setCallbacks();
	}

	private void setCallbacks() {
		EditText et = (EditText) findViewById(R.id.searchText);

		if(et != null) {
			et.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

				@Override
				public void afterTextChanged(Editable s) {
					filter(s.toString());
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.search_menu_button:
			launchSearch();
			return true;
			
		case R.id.refresh_button:
			reloadResorts = true;
			refresh();
			return true;

		case R.id.pref_button:
			reloadResorts = true;
			showHideSearch(false);
			Intent i = new Intent(getBaseContext(), Preferences.class);
			startActivity(i);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void refresh() {
		if(!reloadResorts)
			return;

		reloadResorts = false;

		new ResortsRetriever(this).execute();
	}

	@Override
	public boolean onSearchRequested() {

		launchSearch();

		return false;
	}
	
	private void launchSearch() {
		
		String region = prefs.getString("region", "000");

		if (Constants.favoritesKey.equals(region)) {
			Toast.makeText(this, R.string.search_favorites, Toast.LENGTH_LONG).show();
			return;
		}

		LinearLayout l = (LinearLayout) findViewById(R.id.searchWrapper);
		if(l.getVisibility() == View.VISIBLE) {
			showHideSearch(false);
		} else {
			showHideSearch(true);
		}
	}
	
	private void showHideSearch(boolean show) {
		EditText e = (EditText) findViewById(R.id.searchText);
		LinearLayout l = (LinearLayout) findViewById(R.id.searchWrapper);
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		if(show) {
			l.setVisibility(View.VISIBLE);
			e.requestFocus();
			imm.showSoftInput(e, 0);
		} else {
			imm.hideSoftInputFromWindow(e.getWindowToken(), 0);
			e.clearFocus();
			l.setVisibility(View.GONE);
			filter("");
		}
	}

	private void filter(String s) {
		Log.d(SnowReport.class.toString(), "Searching: <" + s.toString() + ">");

		ListView resortsList = (ListView) findViewById(R.id.resorts_list);

		@SuppressWarnings("unchecked")
		ArrayAdapter<Resort> a = (ArrayAdapter<Resort>) resortsList.getAdapter();
		a.getFilter().filter(s.toString());
	}
}