package pro.schmid.android.snowreport.model;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class Resort implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8269349132946891090L;
	private String id;
	private String name;
	private String slopes;
	private String artificialSnow;
	private String snowState;
	private String slopesState;
	private String slopesToResort;
	private String lastUpdate;
	private String url;
	private String webcamUrl;
	
	private final static Pattern resortUrlMatcher = Pattern.compile("^(.*)/(.+)/(res([0-9]+).html)$");
	
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
		Matcher matcher = resortUrlMatcher.matcher(url);
		if(matcher.find()) {
			this.id = matcher.replaceFirst("$4");
			Log.d(Resort.class.toString(), "Resort ID found: " + id);
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
		url = resortUrlMatcher.matcher(url).replaceAll("$1/SnowReport/$3");
		this.url = url;
		setIdFromUrl(url);
	}
	/**
	 * @return the webcamUrl
	 */
	public String getWebcamUrl() {
		return webcamUrl;
	}
	/**
	 * @param webcamUrl the webcamUrl to set
	 */
	public void setWebcamUrl(String webcamUrl) {
		this.webcamUrl = webcamUrl;
	}
	
	
}
