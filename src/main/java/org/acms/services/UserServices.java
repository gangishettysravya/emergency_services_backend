package org.acms.services;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acms.model.ServiceProvider;
import org.acms.model.User;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Path("/user")
public class UserServices{

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
    
    @Path("/login")
    @POST
    @Consumes("application/json")
    public String login(User user)throws Exception{
    	
    	if(user==null) {
    		System.out.println("User is null");
    		return null;
    	}
    	    	
    	try {
    		Util fU = new Util();
    		String serviceAccountFile = fU.getServiceAccountFile(); 
			InputStream serviceAccount = new FileInputStream(serviceAccountFile);
			GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
			FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).build();
			FirebaseApp.initializeApp(options);	
		}
		
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		Firestore db = FirestoreClient.getFirestore();
		
		DocumentReference docRef = db.collection("users").document(user.getUsername());
		ApiFuture<DocumentSnapshot> result = docRef.get();
		DocumentSnapshot documentSnapshot = result.get();
		
		User user_details = null;
		
		if(documentSnapshot!=null) {
			user_details = documentSnapshot.toObject(User.class);
			if(user_details!=null)
			if(user_details.getPassword().equals(user.getPassword())) {
				System.out.println("User-Details - " + user_details.toString());
				return user_details.getUserCategory();
			}
			else {
				return "fail";
			}
		}
		return "fail";
     }
    
    
    
}
