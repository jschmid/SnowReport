package pro.schmid.android.snowreport.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class Resort {
	private String id;
	private String name;
	private String slopes;
	private String artificialSnow;
	private String snowState;
	private String slopesState;
	private String slopesToResort;
	private String lastUpdate;
	private String url;
	

	private final static Pattern p1 = Pattern.compile("res([0-9]+).html$");
	private final static Pattern p2 = Pattern.compile("[0-9]+");
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	public void setIdFromUrl(String url) {
		Matcher m1 = p1.matcher(url);
		
		if(m1.find()) {
			String tmp = m1.group();
			
			Matcher m2 = p2.matcher(tmp);
			
			if(m2.find()) {
				this.id = m2.group();
				Log.d(Resort.class.toString(), "Resort ID found: " + id);
			}
			
		} else {
			Log.d(Resort.class.toString(), "Could not find the resort ID with " + url);
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSlopes() {
		return slopes;
	}
	public void setSlopes(String slopes) {
		this.slopes = slopes;
	}
	public String getArtificialSnow() {
		return artificialSnow;
	}
	public void setArtificialSnow(String artificialSnow) {
		this.artificialSnow = artificialSnow;
	}
	public String getSnowState() {
		return snowState;
	}
	public void setSnowState(String snowState) {
		this.snowState = snowState;
	}
	public String getSlopesState() {
		return slopesState;
	}
	public void setSlopesState(String slopesState) {
		this.slopesState = slopesState;
	}
	public String getSlopesToResort() {
		return slopesToResort;
	}
	public void setSlopesToResort(String slopesToResort) {
		this.slopesToResort = slopesToResort;
	}
	public String getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
		setIdFromUrl(url);
	}
	
	
}
