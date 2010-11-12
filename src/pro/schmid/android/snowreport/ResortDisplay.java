package pro.schmid.android.snowreport;

import pro.schmid.android.snowreport.controller.ResortRetriever;
import pro.schmid.android.snowreport.model.Resort;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TextView;

public class ResortDisplay extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resort_display);
		
		Bundle bundle  = this.getIntent().getExtras();
		
		Resort r = (Resort)bundle.getSerializable("resort");
		
		TextView t = (TextView) findViewById(R.id.resort_main_name);
		
		t.setText(r.getName());
		
		new ResortRetriever(this).execute(r.getUrl());
	}
	
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }

}
