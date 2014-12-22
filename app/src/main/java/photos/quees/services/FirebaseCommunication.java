package photos.quees.services;

import com.firebase.client.Firebase;

public class FirebaseCommunication {
    private String baseRefUrl = "https://blazing-torch-4836.firebaseio.com/";

    public FirebaseCommunication() {
    }

    public Firebase getBaseRef() {
        return new Firebase(baseRefUrl);
    }

    public Firebase getRef(String child) {
       return getBaseRef().child(child);
    }
}
