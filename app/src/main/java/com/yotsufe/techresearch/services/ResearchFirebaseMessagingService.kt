package com.yotsufe.techresearch.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class ResearchFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("###", "token: $token")
    }

}
