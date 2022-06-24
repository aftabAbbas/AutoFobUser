package com.idialogics.autofobuser.Utils;

import android.app.Application;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class App extends Application {

    DatabaseReference dataRef;
    SharedPref sh;

    @Override
    public void onCreate() {
        super.onCreate();
        initUI();


        dataRef.child(Constants.MANUFECTURER).keepSynced(true);
        dataRef.child(Constants.MODEL).keepSynced(true);
        dataRef.child(Constants.PRODUCTS).keepSynced(true);
        dataRef.child(Constants.YEAR).keepSynced(true);
        dataRef.child(sh.getString(Constants.ID)).child(Constants.CART).keepSynced(true);
        dataRef.child(Constants.KEY_COLLECTION_USERS)
                .child(sh.getString(Constants.ID)).child(Constants.NOTIFICATION).keepSynced(true);
    }

    private void initUI() {

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        dataRef = FirebaseDatabase.getInstance().getReference();
        sh = new SharedPref(this);

    }
}
