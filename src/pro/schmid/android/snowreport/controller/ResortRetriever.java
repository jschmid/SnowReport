package pro.schmid.android.snowreport.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pro.schmid.android.snowreport.R;
import pro.schmid.android.snowreport.ResortsRetrievalException;
import pro.schmid.android.snowreport.model.Resort;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;

public class ResortRetriever extends AsyncTask<String, Void, Resort> {

	private ProgressDialog pd;
	private Activity activity;
	
	private static final Pattern p = Pattern.compile("([^:]*:(.*))");
	
	public ResortRetriever(Activity act) {
		this.activity = act;
	}

	@Override
	protected Resort doInBackground(String ... params) {
		
		if(params.length != 1 || params[0] == null)
			return null;

		Resort r = null;
		try {
			r = getResort(params[0]);
		} catch (ResortsRetrievalException e) {
			return null;
		}

		return r;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		String loadingText = activity.getResources().getString(R.string.load_resort);
		pd = ProgressDialog.show(activity, "", loadingText, true);
	}

	@Override
	protected void onPostExecute(Resort result) {
		super.onPostExecute(result);

		if(result != null) {
			placeWebcam(result);
			placeInfos(result);

			pd.dismiss();
		} else {
			pd.dismiss();
			
			new AlertDialog.Builder(activity)
			.setMessage(R.string.resort_retrieval_error)
			.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					activity.finish();
				}
			})
			.show();
		}
	}

	private void placeWebcam(final Resort result) {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		if(!prefs.getBoolean("show_webcam", true))
			return;
		
		final String webcamUrl = result.getWebcamUrl();
		
		if(webcamUrl == null)
			return;
		
		if(!Pattern.matches(".*jpg$", webcamUrl))
			return;
		
		final ImageView img = (ImageView) activity.findViewById(R.id.webcamView);

		if(img == null)
			return;
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				final Bitmap b = downloadFile(webcamUrl);
				
				if(b != null) {
					img.post(new Runnable() {
	
						@Override
						public void run() {
							img.setImageBitmap(b);
						}
					});
				}
			}
		}).start();
	}

	private void placeInfos(Resort r) {
		putText(R.id.slopes, r.getSlopesKm());
		putText(R.id.artificialSnow, r.getArtificialSnow());
		putText(R.id.snowState, r.getSnowState());
		putText(R.id.slopesState, r.getSlopesState());
		putText(R.id.slopesToResort, r.getSlopesToResort());
		putText(R.id.bottomAltitude, r.getBottomAltitude());
		putText(R.id.nbInstallations, r.getNbInstallations());
		putText(R.id.startTime, r.getStartTime());
		putText(R.id.stopTime, r.getStopTime());
		putText(R.id.phone, r.getPhone());
		putText(R.id.lastUpdate, r.getLastUpdate());
	}
	
	private void putText(int id, String text) {
 		TextView tv = (TextView) activity.findViewById(id);
		if (tv != null && text != null) {
			tv.setText(text);
		}
	}

	private Resort getResort(String url) throws ResortsRetrievalException {

		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			throw new ResortsRetrievalException();
		}

		return fillResortWithContent(doc);
	}

	private Resort fillResortWithContent(Document doc) {
		
		Resort r = new Resort();

		fillResortWithUpdate(r, doc);
		fillResortWithWebcam(r, doc);
		fillResortWithSki(r, doc);
		fillResortWithInfo(r, doc);
		
		return r;
	}

	private void fillResortWithUpdate(Resort r, Document doc) {
		Elements span = doc.select("#aktDate");
		String update = span.first().text();
		
		if(update == null)
			return;
		
		update = p.matcher(update).replaceFirst("$2");
		
		r.setLastUpdate(update);
	}

	private void fillResortWithWebcam(Resort r, Document doc) {
		Elements webcamUrlEls = doc.select("#flashcontent a:contains(webcam)");
		
		if(webcamUrlEls == null)
			return;
		
		Element webcamUrlEl = webcamUrlEls.first();
		
		if(webcamUrlEl == null)
			return;
		
		String webcamUrlS = webcamUrlEl.attr("abs:href");
		
		if(webcamUrlS == null)
			return;
		
		r.setWebcamUrl(webcamUrlS);
	}

	private void fillResortWithSki(Resort r, Document doc) {
		Elements tds = doc.select("#ski + div.chapter td");
		
		if(tds == null)
			return;

		r.setSlopesKm(tds.get(0).text());
		r.setArtificialSnow(tds.get(2).text());
		r.setSlopesState(tds.get(4).text());
		r.setSnowState(tds.get(6).text());
		r.setSlopesToResort(tds.get(8).text());
		r.setBottomAltitude(tds.get(10).text());
		r.setStartTime(tds.get(11).text());
		r.setStopTime(tds.get(13).text());
		r.setNbInstallations(tds.get(14).text());
		r.setPhone(tds.get(15).text());
	}

	private void fillResortWithInfo(Resort r, Document doc) {
		// TODO
	}

	private Bitmap downloadFile(String fileUrl) {

		URL myFileUrl = null;          
		try {
			myFileUrl= new URL(fileUrl);
		} catch (MalformedURLException e) {
			return null;
		}
		
		try {
			HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();

			return BitmapFactory.decodeStream(is);

		} catch (IOException e) {
			return null;
		}
	}

}
