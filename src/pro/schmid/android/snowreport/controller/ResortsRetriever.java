package pro.schmid.android.snowreport.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pro.schmid.android.snowreport.Constants;
import pro.schmid.android.snowreport.R;
import pro.schmid.android.snowreport.ResortsRetrievalException;
import pro.schmid.android.snowreport.SnowReport;
import pro.schmid.android.snowreport.model.Resort;
import pro.schmid.android.snowreport.view.ResortAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class ResortsRetriever extends AsyncTask<Void, Void, List<Resort>> {
	
	private String locale;
	private ProgressDialog pd;
	private Activity activity;
	private SharedPreferences prefs;
	private int timeout;
	private ResortAdapter adapter;
	
	public ResortsRetriever(Activity act, ResortAdapter adapter) {
		this.activity = act;
		this.adapter = adapter;
	}
	
	@Override
	protected List<Resort> doInBackground(Void ... params) {
			List<Resort> resorts;
			try {
				resorts = getResorts();
			} catch (ResortsRetrievalException e) {
				return null;
			}
			
			sortResorts(resorts);
			
			return resorts;
	}
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(List<Resort> resorts) {
		super.onPostExecute(resorts);
		
		if(resorts != null) {
			adapter.addResorts(resorts);
			adapter.notifyDataSetChanged();
			
		} else {
			
			new AlertDialog.Builder(activity)
			.setMessage(R.string.resorts_retrieval_error)
			.setPositiveButton(R.string.retry, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new ResortsRetriever(activity, adapter).execute();
				}
			})
			.setNegativeButton(R.string.cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					activity.finish();
				}
			})
			.show();
		}

		pd.dismiss();
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		locale = activity.getResources().getConfiguration().locale.getLanguage();
		timeout = Integer.parseInt(prefs.getString("timeout", "10000"));
		
		String loadingText = activity.getResources().getString(R.string.load_resorts);
		pd = ProgressDialog.show(activity, "", loadingText, true);
	}
	
	
	private List<Resort> getResorts() throws ResortsRetrievalException {
		
		String region = prefs.getString("region", "000");
		
		if(Constants.favoritesKey.equals(region)) {
			region = "000";
		}
		
		String url = "http://snow.myswitzerland.com/suisse?lang=" + locale;
		
		Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(timeout).get();
		} catch (IOException e) {
			throw new ResortsRetrievalException();
		}

		String sid = doc.select("#sid").attr("value");
		sid = URLEncoder.encode(sid);
		String oid = doc.select("#oid").attr("value");

		Log.i(SnowReport.class.toString(), sid);
		Log.i(SnowReport.class.toString(), oid);

		String mainUrl = "http://snow.myswitzerland.com/servlet/services?object=SearchModel&command=search&jspPath=/jsp/mySwitzerland&jspSearchResultsFile=StationListAjax.jsp&sid=" + sid + "&regionId=" + region + "&oid=" + oid + "&top30=false&ski=true&snowboard=false&crosscountry=false&tobogganing=false&hiking=false&emoSearch=SB&isWispoStation=true&jspPath=%2Fjsp%2FmySwitzerland&jspSearchResultsFile=StationListAjax.jsp&checkAvailability=true&sortAttribute=&AdminSearchTerms=&_=";
		
		try {
			doc = Jsoup.connect(mainUrl).timeout(timeout).get();
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

			switch(i++ % 7) {
			case 0:
				tmpResort = new Resort();
				tmpResort.setName(el.text());
				try {
					// TODO Do this better
					tmpResort.setUrl(el.select("a").first().attr("abs:href"), locale);
				} catch(NullPointerException e) {
					// TODO
				}
				break;
			case 1:
				tmpResort.setSlopesKm(el.text());
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
