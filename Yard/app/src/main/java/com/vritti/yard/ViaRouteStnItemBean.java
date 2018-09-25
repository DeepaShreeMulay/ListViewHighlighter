package com.vritti.yard;

public class ViaRouteStnItemBean {
	String RouteStationId ;String TimeTableId ;String StationMasterId;
	String InstallationId ;String ETATime ;	String stationName;

	public String getRouteStationId() {
		return RouteStationId;
	}

	public void setRouteStationId(String routeStationId) {
		RouteStationId = routeStationId;
	}

	public String getTimeTableId() {
		return TimeTableId;
	}

	public void setTimeTableId(String timeTableId) {
		TimeTableId = timeTableId;
	}

	public String getStationMasterId() {
		return StationMasterId;
	}

	public void setStationMasterId(String stationMasterId) {
		StationMasterId = stationMasterId;
	}

	public String getInstallationId() {
		return InstallationId;
	}

	public void setInstallationId(String installationId) {
		InstallationId = installationId;
	}

	public String getETATime() {
		return ETATime;
	}

	public void setETATime(String ETATime) {
		this.ETATime = ETATime;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

}
