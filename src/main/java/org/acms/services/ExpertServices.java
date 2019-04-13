package org.acms.services;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.acms.model.ExpertAdvisor;
import org.acms.model.User;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Path("/expert")
public class ExpertServices {
	
	@Path("/getDetails/{username}")
	@GET
	@Produces("application/json")
	public ExpertAdvisor getDetails(@PathParam("username") String username) throws Exception{
		
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
		
		ExpertAdvisor expert_details = null;
		
		if(documentSnapshot!=null) {
			expert_details = documentSnapshot.toObject(ExpertAdvisor.class);
			System.out.println("User-Details - " + expert_details.toString());
			return expert_details;
			
		}
		return null;
	}
	
	
	
	
}
