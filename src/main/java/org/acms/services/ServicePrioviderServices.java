package org.acms.services;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.acms.model.RequestWithId;
import org.acms.model.ServiceProvider;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.Transaction;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.util.concurrent.ExecutionException;
import org.acms.model.User;
import org.acms.model.Request;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;


@Path("/serviceProvider")
public class ServicePrioviderServices{
	
	//Done by Manisha
	@Path("/signup")
	@POST
	@Consumes("application/json")
	public String registerServiceProvider(ServiceProvider serviceProvider)throws Exception{
	
		try {
			Util fU = new Util(); 
			InputStream serviceAccount = new FileInputStream(fU.getServiceAccountFile());
			GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
			FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).build();
			FirebaseApp.initializeApp(options);	
		}
		
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		Firestore db = FirestoreClient.getFirestore();
		DocumentReference docRef = db.collection("users").document(serviceProvider.getUsername());
		ApiFuture<WriteResult> result = docRef.set(serviceProvider);
		System.out.println("Update time : " + result.get().getUpdateTime());
		
		if(result.isDone()) {
			DocumentReference docReference = db.collection("users").document(serviceProvider.getUsername());
			return "success";
    	}
    	
    	else {
    		return "fail";
    	}
	}
	
	@Path("/getDetails/{username}")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public ServiceProvider getDetails(@PathParam("username") String username) throws Exception{
		
		try {
			Util fU = new Util(); 
			InputStream serviceAccount = new FileInputStream(fU.getServiceAccountFile());
			GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
			FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).build();
			FirebaseApp.initializeApp(options);	
		}
		
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		Firestore db = FirestoreClient.getFirestore();
		DocumentReference docRef = db.collection("users").document(username);
		ApiFuture<DocumentSnapshot> result = docRef.get();
		DocumentSnapshot documentSnapshot = result.get();
		
		ServiceProvider service_provider_details = null;
		
		if(documentSnapshot!=null) {
			service_provider_details = documentSnapshot.toObject(ServiceProvider.class);
			System.out.println("User-Details - " + service_provider_details.toString());
			return service_provider_details;
			
		}
		return null;
	}
	
	
	@Path("/newRequests/{username}")
	@GET
	@Produces("application/json")
	public List<RequestWithId> getnewRequests(@PathParam("username") String username){
		
		Util util = new Util();
		
		try {
			InputStream serviceAccount = new FileInputStream(util.getServiceAccountFile());
			GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
			FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).setStorageBucket("default-a9400.appspot.com").build();
			FirebaseApp.initializeApp(options);	
		}
		
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		List<RequestWithId> requests = new ArrayList<RequestWithId>();
		
		Firestore db = FirestoreClient.getFirestore();
		CollectionReference users = db.collection("requests");
		
		Query query = users.whereEqualTo("status", "New").whereArrayContains("serviceProviders",username);
		ApiFuture<QuerySnapshot> querySnapshot = query.get();		
		
		try {
			
			for(DocumentSnapshot document : querySnapshot.get().getDocuments()) {
				String requestId = document.getId();
				RequestWithId request = document.toObject(RequestWithId.class);
				if(request!=null) {
					request.setRequestId(requestId);
					requests.add(request);
				}
			}
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		return requests;
	}
	
	@Path("/pastRequests/{username}")
	@GET
	@Produces("application/json")
	public List<RequestWithId> getPastRequests(@PathParam("username") String username){
		
		Util util = new Util();
		
		try {
			InputStream serviceAccount = new FileInputStream(util.getServiceAccountFile());
			GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
			FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).setStorageBucket("default-a9400.appspot.com").build();
			FirebaseApp.initializeApp(options);	
		}
		
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		List<RequestWithId> requests = new ArrayList<RequestWithId>();
		
		Firestore db = FirestoreClient.getFirestore();
		CollectionReference users = db.collection("requests");
		
		Query query = users.whereEqualTo("status", "Completed").whereEqualTo("acceptedBy",username);
		ApiFuture<QuerySnapshot> querySnapshot = query.get();		
		
		try {
			
			for(DocumentSnapshot document : querySnapshot.get().getDocuments()) {
				String requestId = document.getId();
				RequestWithId request = document.toObject(RequestWithId.class);
				if(request!=null) {
					request.setRequestId(requestId);
					requests.add(request);
				}
			}
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		
		Query query1 = users.whereEqualTo("status", "Ongoing").whereEqualTo("acceptedBy",username);
		ApiFuture<QuerySnapshot> query1Snapshot = query1.get();
         try {
			
			for(DocumentSnapshot document : query1Snapshot.get().getDocuments()) {
				String requestId = document.getId();
				RequestWithId request = document.toObject(RequestWithId.class);
				if(request!=null) {
					request.setRequestId(requestId);
					requests.add(request);
				}
			}
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return requests;
	}
 
	@Path("/acceptRequest")
	@POST
	@Consumes("application/json")
	public String getaccepted(RequestWithId request) throws Exception{
        
		Util util = new Util();
		try {
			InputStream serviceAccount = new FileInputStream(util.getServiceAccountFile());
			GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
			FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).setStorageBucket("default-a9400.appspot.com").build();
			FirebaseApp.initializeApp(options);	
		}
		
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		Firestore db = FirestoreClient.getFirestore();
		DocumentReference docRef = db.collection("requests").document(request.getRequestId());
		
		ApiFuture<DocumentSnapshot> result = docRef.get();
		DocumentSnapshot documentSnapshot = result.get();
		
		Request request_details = null;
		User user=null;
		if(documentSnapshot!=null) {
			request_details = documentSnapshot.toObject(Request.class);
			if(request_details.getStatus().equals("New"))
			{
				ApiFuture<WriteResult> future = docRef.update("acceptedBy",request.getAcceptedBy(),"status","Ongoing");
		    	DocumentReference docRef1 = db.collection("users").document(request.getCitizen());
		    	ApiFuture<DocumentSnapshot> result1 = docRef1.get();
				DocumentSnapshot documentSnapshot1 = result1.get();
				user=documentSnapshot1.toObject(User.class);
				notifycitizen(request.getAcceptedBy(),  user.getToken(),"Request Confirmed");
				return "success";
			}
		}
		return "fail";
	}
	
	private void notifycitizen(String serviceUsername, String token,String title){
		MulticastMessage message = MulticastMessage.builder().putData("title", title)
				.putData("click_action","citizen_notification_open_intent")
				.putData("body","Recieved from "+ serviceUsername)
				.addToken(token)
			    .build();
		
		BatchResponse response = null;
		
		try {
			response = FirebaseMessaging.getInstance().sendMulticast(message);
		} catch (FirebaseMessagingException e) {
			e.printStackTrace();
		}
		System.out.println(response.getSuccessCount() + "Notifications were sent successfully");	
	}
	
	@Path("/rejectRequest")
	@POST
	@Consumes("application/json")
	public String rejectRequest(RequestWithId request){
		
		Util util = new Util();
		
		try {
			InputStream serviceAccount = new FileInputStream(util.getServiceAccountFile());
			GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
			FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).setStorageBucket("default-a9400.appspot.com").build();
			FirebaseApp.initializeApp(options);	
		}
		
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		Firestore db = FirestoreClient.getFirestore();
		DocumentReference docRef = db.collection("requests").document(request.getRequestId());
		
		ApiFuture<WriteResult> future = docRef.update("serviceProviders",FieldValue.arrayRemove(request.getAcceptedBy()));
		
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
	
	@Path("/completedRequest")
	@POST
	@Consumes("application/json")
	public String completedRequest(RequestWithId request){
		
		Util util = new Util();
		
		try {
			InputStream serviceAccount = new FileInputStream(util.getServiceAccountFile());
			GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
			FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).setStorageBucket("default-a9400.appspot.com").build();
			FirebaseApp.initializeApp(options);	
		}
		
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		Firestore db = FirestoreClient.getFirestore();
		DocumentReference docRef = db.collection("requests").document(request.getRequestId());
		
		ApiFuture<WriteResult> future = docRef.update("status","Completed");
		
		try {
			WriteResult result = future.get();
			System.out.println("Result : " + result);
			
			DocumentReference docRef1 = db.collection("users").document(request.getCitizen());
	    	ApiFuture<DocumentSnapshot> result1 = docRef1.get();
			DocumentSnapshot documentSnapshot1 = result1.get();
			User citizen=documentSnapshot1.toObject(User.class);
			notifycitizen(request.getAcceptedBy(), citizen.getToken(), "Request Completed");
			if(request.getExpert()!=null && !request.getExpert().equals("")) {
				final DocumentReference docRef_expert = db.collection("users").document(request.getExpert());
				ApiFuture<Void> transaction =
					    db.runTransaction(
					        new Transaction.Function<Void>() {
					          @Override
					          public Void updateCallback(Transaction transaction) throws Exception {
					            
					        	// retrieve document and decrement field
					            DocumentSnapshot snapshot = transaction.get(docRef_expert).get();
					            int old_connections = (int) snapshot.get("connections");
					            transaction.update(docRef_expert, "connections", old_connections - 1);
					            return null;
					          }
					      });
			}
			return "success";
			
    	} catch (InterruptedException e) {
			e.printStackTrace();
    	} catch (ExecutionException e) {
			e.printStackTrace();
		}
    	return "fail";
	}
	
}
