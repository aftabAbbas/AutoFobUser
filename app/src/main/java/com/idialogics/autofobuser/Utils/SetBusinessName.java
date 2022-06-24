package com.idialogics.autofobuser.Utils;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class SetBusinessName {

    private static SetBusinessName instance;


    public static SetBusinessName with(Context context, TextView textView) {

        SharedPref sh = new SharedPref(context);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.ID, sh.getString(Constants.ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful() && Objects.requireNonNull(task.getResult()).size() > 0) {

                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);


                            String businessName = documentSnapshot.getString(Constants.BUSINESS_NAME);

                            sh.putString(Constants.BUSINESS_NAME, businessName);

                            textView.setText(businessName);


                        }

                    }
                }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());


        if (instance == null)
            instance = new SetBusinessName();
        return instance;

    }

}
