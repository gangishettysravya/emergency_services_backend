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
	
	
	}
	
	
	
	
}
