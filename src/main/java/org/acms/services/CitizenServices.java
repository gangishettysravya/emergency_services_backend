package org.acms.services;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acms.model.User;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.acms.model.ExpertAdvisor;
import org.acms.model.Location;
import org.acms.model.Request;
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
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;

@Path("/citizen")
public class CitizenServices{

	//Done by Manisha
	@Path("/signup")
	@POST
	@Consumes("application/json")
	public String registerCitizen(User user)throws Exception{
	
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
		DocumentReference docRef = db.collection("users").document(user.getUsername());
		ApiFuture<WriteResult> result = docRef.set(user);
		System.out.println("Update time : " + result.get().getUpdateTime());
		
		if(result.isDone()) {
    		return "success";
    	}
    	
    	else {
    		return "fail";
    	}
	}
	
	@Path("/getDetails/{username}")
	@GET
	@Produces("application/json")
	public User getDetails(@PathParam("username") String username) throws Exception{
		
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
		
		User user_details = null;
		
		if(documentSnapshot!=null) {
			user_details = documentSnapshot.toObject(User.class);
			System.out.println("User-Details - " + user_details.toString());
			return user_details;
			
		}
		return null;
	}
	
	@Path("/getNearestServices")
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public List<ServiceProvider> getAllServicesNearBy(Location location){

		Util util = new Util();
		double distance = location.getDistance();
		Location [] bounds = util.getBoundings(location,distance);
		
		List<ServiceProvider> serviceProviders = new ArrayList<ServiceProvider>();
		
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
		CollectionReference users = db.collection("users");
		
		Query query = users.whereEqualTo("userCategory","Emergency Service");
		query = query.whereGreaterThanOrEqualTo("latitude",bounds[0].getLatitude()).whereLessThanOrEqualTo("latitude",bounds[1].getLatitude());
		
		ApiFuture<QuerySnapshot> querySnapshot = query.get();		
		
		try {
			
			for(DocumentSnapshot document : querySnapshot.get().getDocuments()) {
				ServiceProvider s = document.toObject(ServiceProvider.class);
				if(s.getLongitude() >= bounds[0].getLongitude() && s.getLongitude() <= bounds[1].getLongitude()) {
					serviceProviders.add(s);
				}
			}
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
				
		return serviceProviders;
		
	}
	
	@Path("/sendRequest")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public String sendRequest(
			@FormDataParam("username") String username,
			@FormDataParam("serviceCategory") String serviceCategory,
			@FormDataParam("latitude") double latitude,
			@FormDataParam("longitude") double longitude,
			@FormDataParam("distance") double distance,
			@FormDataParam("description") String description,
			@FormDataParam("image")InputStream uploadedInputStream,
			@FormDataParam("image")FormDataContentDisposition fileDetail){
			
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
		
		String status = "New";
		Request request = new Request(username,serviceCategory,description,latitude,longitude,status);
		Location [] bounds = util.getBoundings(new Location(latitude,longitude),distance);
		
		List<String> serviceProviderUsernames = new ArrayList<String>();;
		List<String> serviceProviderTokens = new ArrayList<String>();;
		
		CollectionReference users = db.collection("users");
		
		Query query = users.whereEqualTo("userCategory","Emergency Service").whereEqualTo("serviceCategory",serviceCategory);
		query = query.whereGreaterThanOrEqualTo("latitude",bounds[0].getLatitude()).whereLessThanOrEqualTo("latitude",bounds[1].getLatitude());
		
		ApiFuture<QuerySnapshot> querySnapshot = query.get();		
		
		try {	
			for(DocumentSnapshot document : querySnapshot.get().getDocuments()) {
				ServiceProvider s = document.toObject(ServiceProvider.class);
				if(s.getLongitude() >= bounds[0].getLongitude() && s.getLongitude() <= bounds[1].getLongitude()) {
						if(s.getToken()!=null) {
							serviceProviderUsernames.add(s.getUsername());
							serviceProviderTokens.add(s.getToken());
						}
				}
			}
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return "fail";
		}
		
		if(serviceProviderUsernames.isEmpty()) {
			System.out.println("No services Available");
			return "fail";
		}
				
		request.setServiceProviders(serviceProviderUsernames);
		String expert_name = "";
		String expert_token="";
		
		//Assign an expert randomly to this request.
		Query assign_expert = users.whereEqualTo("userCategory", "Expert").whereEqualTo("serviceCategory",request.getServiceCategory()).orderBy("connections");
		ApiFuture<QuerySnapshot> querySnapshot1 = assign_expert.get();
		try {	
			DocumentSnapshot document = querySnapshot1.get().getDocuments().get(0);
			ExpertAdvisor expert = document.toObject(ExpertAdvisor.class);
			if(expert!=null) {
				expert_name = expert.getUsername();
				expert_token = expert.getToken();
				final DocumentReference docRef_expert = db.collection("users").document(expert_name);
				
				ApiFuture<Void> transaction =
					    db.runTransaction(
					        new Transaction.Function<Void>() {
					          @Override
					          public Void updateCallback(Transaction transaction) throws Exception {
					            
					        	// retrieve document and decrement population field
					            DocumentSnapshot snapshot = transaction.get(docRef_expert).get();
					            int old_connections = (int) snapshot.get("connections");
					            transaction.update(docRef_expert, "connections", old_connections + 1);
					            System.out.println("Requests : " + old_connections);
					            return null;
					          }
					      });	
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return "fail";
		}
		
		request.setExpert(expert_name);
		
		String docId = null;
		CollectionReference requests = db.collection("requests"); 
		ApiFuture<DocumentReference> addedDocRef = requests.add(request);
		try {
			docId = addedDocRef.get().getId();
			System.out.println("Added document with ID: " + docId);	
		} catch (InterruptedException | ExecutionException e1) {
			e1.printStackTrace();
			return "fail";
		}
		
		if(docId==null) {
			return "fail";
		}
		
		Bucket bucket = StorageClient.getInstance().bucket("default-a9400.appspot.com");
		
		try {
			Blob blob = bucket.create(docId,uploadedInputStream);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			return "fail";
		}
	
		sendNotificationsToServices(serviceProviderTokens,username);
		if(expert_token!=null) {
			if(!expert_token.equals(""))
				sendNotificationToExpert(expert_token,username);
		}
		
		return "success";
	}
	
	private void sendNotificationToExpert(String token,String citizenUsername){
		
		MulticastMessage message = MulticastMessage.builder().putData("title", "Service Request")
				.putData("click_action","expert_notification_open_intent")
				.putData("body","Recieved from "+ citizenUsername)
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
	
	private void sendNotificationsToServices(List<String> tokens,String citizenUsername){
	
		MulticastMessage message = MulticastMessage.builder().putData("title", "Service Request")
				.putData("click_action","service_provider_notification_open_intent")
				.putData("body","Recieved from "+ citizenUsername)
				.addAllTokens(tokens)
			    .build();
		
		BatchResponse response = null;
		
		try {
			response = FirebaseMessaging.getInstance().sendMulticast(message);
		} catch (FirebaseMessagingException e) {
			e.printStackTrace();
		}
		System.out.println(response.getSuccessCount() + "Notifications were sent successfully");	
	}
	
	@Path("/getAllRequests/{username}")
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
		
		Query query = users.whereEqualTo("citizen",username);
		ApiFuture<QuerySnapshot> querySnapshot = query.get();		
		
		try {
			
			for(DocumentSnapshot document : querySnapshot.get().getDocuments()) {
				String requestId = document.getId();
				RequestWithId request = document.toObject(RequestWithId.class);
				if(request!=null) {
					if(request.getAcceptedBy()==null)
						request.setAcceptedBy("");
					request.setRequestId(requestId);
					requests.add(request);
				}
			}
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return requests;
	}
	
}
