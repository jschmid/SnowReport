package pro.schmid.android.snowreport;

import pro.schmid.android.snowreport.controller.ResortsRetriever;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class SnowReport extends Activity {
	
	private Boolean reloadResorts = true;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.snowreport);
		
		setCallbacks();
		
	}
	
	private void setCallbacks() {
		EditText et = (EditText) findViewById(R.id.search);
		
		if(et != null) {
			et.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {
					
					Log.d(SnowReport.class.toString(), "Searching: <" + s.toString() + ">");
					
					ListView resortsList = (ListView) findViewById(R.id.resorts_list);
					
					ArrayAdapter a = (ArrayAdapter) resortsList.getAdapter();
					
					a.getFilter().filter(s.toString());
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
	    case R.id.refresh_button:
	    	reloadResorts = true;
	    	refresh();
	    	return true;
	    	
	    case R.id.pref_button:
	    	reloadResorts = true;
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
}