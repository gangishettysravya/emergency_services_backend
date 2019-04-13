package org.acms.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.acms.model.Location;

public class Util{	
	
	String serviceAccountFile = "/home/sravya/git/emergency-services/src/main/java/org/acms/resources/serviceaccount.json";
	
	public Util() {
	
	}
	
	public String getServiceAccountFile() {
		return this.serviceAccountFile;
	}
	
	public Location[] getBoundings(Location present_location,double distance){
		
		double earth_radius = 6371; //km
		double latitude = present_location.getLatitude();
		double longitude = present_location.getLongitude();
			
		//angular radius of query circle
		double angular_radius = distance/earth_radius;
		
		System.out.println(angular_radius);
		
		double min_lat = latitude - Math.toDegrees(angular_radius);
		double max_lat = latitude + Math.toDegrees(angular_radius);
		
		//double min_long = lon - Math.toDegrees(distance/R/Math.cos(Math.toRadians(lat)));
        //double max_long = lon + Math.toDegrees(distance/R/Math.cos(Math.toRadians(lat)));
				
		double deltaLon = Math.asin(Math.sin(angular_radius)/Math.cos(angular_radius));
		double min_long = longitude - Math.toDegrees(deltaLon);
		double max_long = longitude + Math.toDegrees(deltaLon);
		
		System.out.println("min-latitude : " + min_lat + " max_latitude : " + max_lat);
		System.out.println("min_longitude : " + min_long + "max_longitude : "+max_long);
		
		Location [] locations = new Location[2];
		locations[0] = new Location(min_lat,min_long);
		locations[1] = new Location(max_lat,max_long);
		
		return locations;
	}
	
	public void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation){
		try
            {
                    OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
                    int read = 0;
                    byte[] bytes = new byte[1024];

                    out = new FileOutputStream(new File(uploadedFileLocation));
                    while ((read = uploadedInputStream.read(bytes)) != -1)
                    {
                            out.write(bytes, 0, read);
                    }
                    out.flush();
                    out.close();
            }catch (IOException e)
            {

                    e.printStackTrace();
            }

    }
	
}
