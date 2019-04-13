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

    @Path("/checkUser/{username}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String checkUserifAlreadyExists(@PathParam("username") String username)
	{
		
		try {
			Util fU = new Util(); 
			InputStream serviceAccount = new FileInputStream(fU.getServiceAccountFile());
			GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
			FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).build();
			FirebaseApp.initializeApp(options);	
			
		}
		
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		try {
			Firestore db = FirestoreClient.getFirestore();
			DocumentReference docRef = db.collection("users").document(username);
			ApiFuture<DocumentSnapshot> result = docRef.get();
			DocumentSnapshot documentSnapshot = result.get();
			
			if(documentSnapshot!=null) {
				User user_details = documentSnapshot.toObject(User.class);
				if(user_details!=null) {
					return "invalid"; 
				}
			}	
		}
		
		catch(Exception e) {
			System.out.println(e.getMessage());
		}		
		return "valid";
	}
    
    @Path("/checkEmail/{email}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String checkEmailifAlreadyExists(@PathParam("email") String email)
	{
		
		try {
			Util fU = new Util();
			InputStream serviceAccount = new FileInputStream(fU.getServiceAccountFile());
			GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
			FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).build();
			FirebaseApp.initializeApp(options);	
			
		}
		
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		try {
			Firestore db = FirestoreClient.getFirestore();
			CollectionReference users = db.collection("users");
			Query query = users.whereEqualTo("email",email);

			ApiFuture<QuerySnapshot> querySnapshot= query.get();
			
			if(querySnapshot.get().getDocuments().isEmpty()) {
				return "valid";
			}			
		}
		
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		return "invalid";
	}
    
    
    @Path("/storeToken")
    @POST
    @Consumes("application/json")
    public String storeToken(User user){
    
    	try {
			Util fU = new Util();
			InputStream serviceAccount = new FileInputStream(fU.getServiceAccountFile());
			GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
			FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).build();
			FirebaseApp.initializeApp(options);		
		}
		
		catch(Exception e){
			System.out.println(e.getMessage());
		}
    	Firestore db = FirestoreClient.getFirestore();
    	DocumentReference docRef = db.collection("users").document(user.getUsername());
    	ApiFuture<WriteResult> future = docRef.update("token",user.getToken());
    	
    	try {
			WriteResult result = future.get();
			System.out.println("Result : " + result);
			return "success"; 
    	} catch (InterruptedException e) {
			e.printStackTrace();
    	} catch (ExecutionException e) {
			e.printStackTrace();
		}
    	return "fail";
    }
    
    
    
}
