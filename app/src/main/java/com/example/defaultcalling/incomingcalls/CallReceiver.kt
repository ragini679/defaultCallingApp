package com.example.defaultcalling.incomingcalls

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

class CallReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action==TelephonyManager.ACTION_PHONE_STATE_CHANGED){
           val state=intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            when(state){
                TelephonyManager.EXTRA_STATE_RINGING->{
                    val incomingNumber=intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                    Log.d("CallReceiver","Incoming call from: $incomingNumber")
                    IncomingCallActivity.showIncomingCallUI(context,incomingNumber?:"Unknown")
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK->{
                    Log.d("CallReceiver", "Call answered")
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    Log.d("CallReceiver", "Call ended or rejected")
                }
            }
        }
    }
}