package com.yotsufe.techresearch.inappmessaging

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.inappmessaging.FirebaseInAppMessagingDisplay
import com.google.firebase.inappmessaging.FirebaseInAppMessagingDisplayCallbacks
import com.google.firebase.inappmessaging.model.InAppMessage
import com.google.firebase.inappmessaging.model.MessageType

class CustomMessagingDisplayComponent(private val context: Context) : FirebaseInAppMessagingDisplay {
    override fun displayMessage(
        inAppMessage: InAppMessage,
        callbacks: FirebaseInAppMessagingDisplayCallbacks
    ) {
        Log.d("####", "displayMessage")
        callbacks.impressionDetected()
        when (inAppMessage.messageType) {
            MessageType.CARD -> {
                Log.d("####", "CARD")
            }
            MessageType.MODAL -> {
                Log.d("####", "MODAL")
                showModal(inAppMessage)
            }
            MessageType.IMAGE_ONLY -> {
                Log.d("####", "IMAGE_ONLY")
            }
            MessageType.BANNER -> {
                Log.d("####", "BANNER")
            }
            else -> {
            }
        }
    }

    fun showModal(inAppMessage: InAppMessage) {
        Log.d("####", inAppMessage.title!!.text!!)
        Log.d("####", inAppMessage.body!!.text!!)
        Log.d("####", inAppMessage.imageData!!.imageUrl)
        Log.d("####", inAppMessage.title.toString())
        Log.d("####", inAppMessage.title.toString())
        Toast.makeText(context, inAppMessage.data!!["title"], Toast.LENGTH_LONG).show()
    }
}
