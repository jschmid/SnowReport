package pro.schmid.android.snowreport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SnowReport extends Activity {
	
	private Boolean reloadResorts = true;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.snowreport);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
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