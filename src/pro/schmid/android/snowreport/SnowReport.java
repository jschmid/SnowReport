package pro.schmid.android.snowreport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pro.schmid.android.snowreport.model.Resort;
import pro.schmid.android.snowreport.view.ResortAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

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
		
		launchLoadResorts();
	}

	private void launchLoadResorts() {
		String loadingText = getResources().getString(R.string.load_resorts);
		final ProgressDialog pd = ProgressDialog.show(this, "", loadingText, true);
		final AlertDialog.Builder b = new AlertDialog.Builder(this);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					loadResorts();
				} catch (ResortsRetrievalException e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							b
							.setMessage(R.string.resorts_retrieval_error)
							.setPositiveButton("OK", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
							})
							.show();
						}
					});
				}
				pd.dismiss();
			}
		}).start();
	}

	private void loadResorts() throws ResortsRetrievalException {
		List<Resort> resorts = getResorts();
		
		sortResorts(resorts);

		final ListView resortsList = (ListView) findViewById(R.id.resorts_list);
		
		resortsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				Resort r = (Resort)resortsList.getItemAtPosition(position);
				
				Log.d(SnowReport.class.toString(), r.getName());
			}
		});
		
		final BaseAdapter a = new ResortAdapter(this, R.layout.resort_item, resorts);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				resortsList.setAdapter(a);
			}
		});
	}

	private List<Resort> getResorts() throws ResortsRetrievalException {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		String locale = getBaseContext().getResources().getConfiguration().locale.getLanguage();
		String region = prefs.getString("region", "000");
		
		String url = "http://snow.myswitzerland.com/suisse?lang=" + locale;
		
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			throw new ResortsRetrievalException();
		}

		String sid = doc.select("#sid").attr("value");
		String oid = doc.select("#oid").attr("value");

		Log.i(SnowReport.class.toString(), sid);
		Log.i(SnowReport.class.toString(), oid);

		try {
			doc = Jsoup.connect("http://snow.myswitzerland.com/servlet/services?object=SearchModel&command=search&jspPath=/jsp/mySwitzerland&jspSearchResultsFile=StationListAjax.jsp&sid=" + sid + "&regionId=" + region + "&oid=" + oid + "&top30=false&ski=true&snowboard=false&crosscountry=false&tobogganing=false&hiking=false&emoSearch=SB&isWispoStation=true&jspPath=%2Fjsp%2FmySwitzerland&jspSearchResultsFile=StationListAjax.jsp&checkAvailability=true&sortAttribute=&AdminSearchTerms=&_=").get();
		} catch (IOException e) {
			throw new ResortsRetrievalException();
		}

		Elements headers = doc.select("th");

		for(Element el : headers) {
			Log.i(SnowReport.class.toString(), el.text());
		}

		Elements resortsElements = doc.select("tr td");

		List<Resort> resorts = new ArrayList<Resort>();
		Resort tmpResort = new Resort();

		int i = 0;
		for(Element el : resortsElements) {
			Log.i(SnowReport.class.toString(), el.text());

			switch(i++ % 7) {
			case 0:
				tmpResort.setName(el.text());
				tmpResort.setUrl(el.select("a").first().attr("abs:href"));
				break;
			case 1:
				tmpResort.setSlopes(el.text());
				break;
			case 2:
				tmpResort.setArtificialSnow(el.text());
				break;
			case 3:
				tmpResort.setSnowState(el.text());
				break;
			case 4:
				tmpResort.setSlopesState(el.text());
				break;
			case 5:
				tmpResort.setSlopesToResort(el.text());
				break;
			case 6:
				tmpResort.setLastUpdate(el.text());
				resorts.add(tmpResort);
				tmpResort = new Resort();
				break;
			}
		}

		return resorts;
	}

	private void sortResorts(List<Resort> resorts) {
		Collections.sort(resorts, new Comparator<Resort>() {
			@Override
			public int compare(Resort r1, Resort r2) {
				return r1.getName().compareTo(r2.getName());
			}
		});
	}
}