package com.example.marketplace_app.firebase;

import com.example.marketplace_app.models.User;

public class UserRepository {
    //The UI doesn't talk directly to Firebase.
    //This is a class responsible for data access.

    private final FirebaseManager firebase = FirebaseManager.getInstance();

    /** Create / overwrite Users/{uid}. Used by Sign up. */
    public void saveUser(User user, DataCallback<Void> callback) {
        firebase.usersRef().child(user.getUid()).setValue(user)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /** Read Users/{uid} once. The user is null if the record does not exist. */
    public void getUser(String uid, DataCallback<User> callback) {
        firebase.usersRef().child(uid).get()
                .addOnSuccessListener(snapshot -> callback.onSuccess(snapshot.getValue(User.class)))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
