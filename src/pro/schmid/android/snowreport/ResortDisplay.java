package pro.schmid.android.snowreport;

import pro.schmid.android.snowreport.model.Resort;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ResortDisplay extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resort_display);
		
		Bundle bundle  = this.getIntent().getExtras();
		
		String url = bundle.getString("url");
		
		TextView t1 = (TextView) findViewById(R.id.resort_url);
		
		t1.setText(url);
		
		Resort r = (Resort)bundle.getSerializable("resort");
		
		TextView t2 = (TextView) findViewById(R.id.resort_name);
		
		t2.setText(r.getName());
	}

}
