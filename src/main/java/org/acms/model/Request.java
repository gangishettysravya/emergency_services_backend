package org.acms.model;

import java.util.List;

public class Request {
	
	String citizen;
	String serviceCategory;
	double latitude;
	double longitude;
	String acceptedBy;
	List<String> serviceProviders;
	String description;
	String status;
	String expert;
	
	public Request(){
		
	}
	
	public Request(String citizen,String serviceCategory,String description,double latitude, double longitude,String Status){
		setCitizen(citizen);
		setServiceCategory(serviceCategory);
		setLatitude(latitude);
		setLongitude(longitude);
		setDescription(description);
		setStatus(Status);
	}
	
	public String getCitizen() {
		return citizen;
	}

	public void setCitizen(String citizenUsername) {
		this.citizen = citizenUsername;
	}

	public String getServiceCategory() {
		return serviceCategory;
	}

	public void setServiceCategory(String serviceCategory) {
		this.serviceCategory = serviceCategory;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public List<String> getServiceProviders() {
		return serviceProviders;
	}

	public void setServiceProviders(List<String> serviceProviders) {
		this.serviceProviders = serviceProviders;
	}
	
	public String getAcceptedBy() {
		return acceptedBy;
	}

	public void setAcceptedBy(String acceptedBy) {
		this.acceptedBy = acceptedBy;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status){
		this.status = status;
	}
	
	
	public String getExpert() {
		return expert;
	}

	public void setExpert(String expert){
		this.expert = expert;
	}

}
