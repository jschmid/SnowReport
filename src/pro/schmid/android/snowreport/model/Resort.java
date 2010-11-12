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
	
	// Found on homepage
	private String name;
	private String slopesKm;
	private String artificialSnow;
	private String snowState;
	private String slopesState;
	private String slopesToResort;
	private String lastUpdate;
	private String url;
	
	// Found on specific page
	private String webcamUrl;
	private String bottomAltitude;
	private String nbInstallations;
	private String startTime;
	private String stopTime;
	private String phone;
	
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
	public String getSlopesKm() {
		return slopesKm;
	}
	public void setSlopesKm(String slopes) {
		this.slopesKm = slopes;
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

	/**
	 * @return the bottomAltitude
	 */
	public String getBottomAltitude() {
		return bottomAltitude;
	}
	/**
	 * @param bottomAltitude the bottomAltitude to set
	 */
	public void setBottomAltitude(String bottomAltitude) {
		this.bottomAltitude = bottomAltitude;
	}
	/**
	 * @return the nbInstallations
	 */
	public String getNbInstallations() {
		return nbInstallations;
	}
	/**
	 * @param nbInstallations the nbInstallations to set
	 */
	public void setNbInstallations(String nbInstallations) {
		this.nbInstallations = nbInstallations;
	}
	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the stopTime
	 */
	public String getStopTime() {
		return stopTime;
	}
	/**
	 * @param stopTime the stopTime to set
	 */
	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
