package org.acms.model;

public class User {
	
	 private String username;
	 private String password;
     private String email;
     private String contactNumber;
     private String userCategory;
     private String token;
      
     public User() {
     	 
     }
     
     public User(String username, String password, String email, String contact_number, String category) {
 	    
		  setUsername(username);
		  setPassword(password);
		  setEmail(email);
		  setContactNumber(contact_number);
		  setUserCategory(category);
	}
	 
	 
	 public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getUserCategory() {
		return userCategory;
	}
	
	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}
	
	public String getContactNumber() {
		return contactNumber;
	}
	
	public String getToken(){
		return this.token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}

}
