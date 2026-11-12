package com.example.marketplace_app.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * One place that knows the Firebase "addresses" (nodes) used by the whole app.
 * Both the buyer and the seller read and write the SAME database through here.
 *
 *   Users/{uid}                 -> User
 *   Products/{productId}        -> Product   (created by sellers)
 *   Carts/{buyerUid}/{productId}-> CartItem  (one cart per buyer)
 *   Orders/{orderId}            -> Order     (created by buyers, read by sellers)
 */
public class FirebaseManager {

    //Using constants prevents typo bugs
    public static final String NODE_USERS = "Users";
    public static final String NODE_PRODUCTS = "Products";
    public static final String NODE_CARTS = "Carts";
    public static final String NODE_ORDERS = "Orders";

    private static FirebaseManager instance;

    private final FirebaseAuth auth;
    private final DatabaseReference root;

    private FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        DatabaseReference tempRoot;
        try {
            tempRoot = FirebaseDatabase.getInstance().getReference();
        } catch (Exception e) {
            //if falls out then we call directry firebase
            tempRoot = FirebaseDatabase.getInstance("https://marketplace-app-55594-default-rtdb.firebaseio.com").getReference();
        }
        root = tempRoot;
    }

    // Singleton: the whole app shares one FirebaseManager
    // If it doesn't exist yet, it creates it once and reuses it everywhere. This saves phone RAM and memory.
    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }


    //create all helper functions
    public FirebaseAuth getAuth() {
        return auth;
    }

    public String getCurrentUid() {
        FirebaseUser user = auth.getCurrentUser();
        return user == null ? null : user.getUid();
        /** uid of the logged-in user, or null if nobody is logged in. */
    }

    public void logout() {
        auth.signOut();
    }

    public DatabaseReference rootRef() {
        return root;
    }

    public DatabaseReference usersRef() {
        return root.child(NODE_USERS);
    }

    public DatabaseReference productsRef() {
        return root.child(NODE_PRODUCTS);
    }

    public DatabaseReference cartsRef() {
        return root.child(NODE_CARTS);
    }

    public DatabaseReference ordersRef() {
        return root.child(NODE_ORDERS);
    }
}
