package org.acms.model;

public class ServiceProvider extends User{
	
	private double latitude;
	private double longitude;
	private String serviceCategory;
	
	public ServiceProvider(){
		super();
	}
	
	public ServiceProvider(String username, String password, String email,String contact_number, String user_category,double latitude,double longitude,String service_category){
		
		super(username,password,email,contact_number,user_category);
		setLatitude(latitude);
		setLongitude(longitude);
		setServiceCategory(service_category);
		
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

	public String getServiceCategory() {
		return serviceCategory;
	}

	public void setServiceCategory(String service_category) {
		this.serviceCategory = service_category;
	}
	
	
	
	

}
