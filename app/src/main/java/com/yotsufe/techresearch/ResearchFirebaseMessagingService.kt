package com.yotsufe.techresearch

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class ResearchFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("###", "token: $token")
    }

}
