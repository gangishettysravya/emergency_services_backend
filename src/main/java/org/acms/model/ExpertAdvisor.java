package org.acms.model;

public class ExpertAdvisor extends User{
	
	private String serviceCategory;
	private int connections;
	
	public ExpertAdvisor(){
		
	}
	
	public ExpertAdvisor(String username, String password, String email,String contact_number, String user_category,String service_category,int connections){
		
		super(username,password,email,contact_number,user_category);
		setServiceCategory(service_category);
		setConnections(connections);
	}
	
	public void setConnections(int connections){
		this.connections = connections;
	}
	
	public int getConnections() {
		return this.connections;
	}
	
	public void setServiceCategory(String serviceCategory) {
		this.serviceCategory = serviceCategory;
	}
	
	public String getServiceCategory() {
		return this.serviceCategory;
	}
	
}
