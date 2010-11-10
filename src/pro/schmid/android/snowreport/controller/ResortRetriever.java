package pro.schmid.android.snowreport.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import pro.schmid.android.snowreport.R;
import pro.schmid.android.snowreport.ResortsRetrievalException;
import pro.schmid.android.snowreport.model.Resort;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ResortRetriever extends AsyncTask<String, Void, Resort> {

	private ProgressDialog pd;
	private Activity activity;

	public ResortRetriever(Activity act) {
		this.activity = act;
	}

	@Override
	protected Resort doInBackground(String ... params) {

		Log.d(ResortRetriever.class.toString(), "Downloading: " + params[0]);

		Resort r = null;
		try {
			r = getResort(params[0]);
		} catch (ResortsRetrievalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

		placeWebcam(result);

		pd.dismiss();
	}

	private void placeWebcam(final Resort result) {
		final String webcamUrl = result.getWebcamUrl();
		
		if(!Pattern.matches(".*jpg$", webcamUrl))
			return;
		
		final ImageView img = (ImageView) activity.findViewById(R.id.webcamView);

		new Thread(new Runnable() {

			@Override
			public void run() {
				final Bitmap b = downloadFile(webcamUrl);

				img.post(new Runnable() {

					@Override
					public void run() {
						img.setImageBitmap(b);
					}
				});
			}
		}).start();
	}

	private Resort getResort(String url) throws ResortsRetrievalException {

		Resort r = new Resort();

		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			throw new ResortsRetrievalException();
		}

		Elements webcamUrle = doc.select("#flashcontent");
		Log.d(ResortRetriever.class.toString(), webcamUrle.first().html());
		webcamUrle = webcamUrle.select("a:contains(webcam)");

		String webcamUrls = webcamUrle.first().attr("abs:href");
		r.setWebcamUrl(webcamUrls);

		Log.d(ResortRetriever.class.toString(), "Got webcam: " + webcamUrls);

		return r;
	}

	private Bitmap downloadFile(String fileUrl) {

		URL myFileUrl = null;          
		try {
			myFileUrl= new URL(fileUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();

			return BitmapFactory.decodeStream(is);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
