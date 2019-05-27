package org.javaweb.utils;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class IPLocation implements Serializable {

	private static final long serialVersionUID = -8980702605913442300L;

	@JSONField(name = "country_code")
	private String countryCode;

	@JSONField(name = "country_name")
	private String countryName;

	@JSONField(name = "country_cn_name")
	private String countryCNName;

	@JSONField(name = "region_name")
	private String regionName;

	private String city;

	private String district;

	private String isp;

	private String latitude;

	private String longitude;

	public IPLocation(String countryCode, String countryName, String regionName, String city) {
		this.countryCode = countryCode;
		this.countryName = countryName;
		this.regionName = regionName;
		this.city = city;
	}

	public IPLocation(String countryCode, String countryName, String countryCNName,
	                  String regionName, String city, String district, String isp,
	                  String latitude, String longitude) {

		this.countryCode = countryCode;
		this.countryName = countryName;
		this.countryCNName = countryCNName;
		this.regionName = regionName;
		this.city = city;
		this.district = district;
		this.isp = isp;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountryCNName() {
		return countryCNName;
	}

	public void setCountryCNName(String countryCNName) {
		this.countryCNName = countryCNName;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getIsp() {
		return isp;
	}

	public void setIsp(String isp) {
		this.isp = isp;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

}
