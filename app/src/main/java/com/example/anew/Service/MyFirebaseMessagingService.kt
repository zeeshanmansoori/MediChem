package com.example.anew.Service

import android.util.Log
import com.example.anew.ui.intialSetup.ADMIN_REF
import com.example.anew.ui.intialSetup.IS_USER
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.paperdb.Paper


class MyFirebaseMessagingService: FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("mymessage", token)

        if (Firebase.auth.currentUser!=null) {
            if (Paper.book().read(IS_USER, false)) {

            } else {
                FirebaseFirestore.getInstance().collection(ADMIN_REF)
                    .document("zohanshaikh.az1@gmail.com").update(
                    "fcm_id", token
                )
            }
        }


    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("mymessage","message has recieved from functions")
    }
}