package com.example.defaultcalling.incomingcalls

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat


class IncomingCallActivity:ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val phoneNumber = intent.getStringExtra("PHONE_NUMBER")
        setContent {
            IncomingCallScreen(
                phoneNumber = phoneNumber ?: "Unknown",
                onAccept = {
                    Log.d("IncomingCallActivity", "Accept button clicked")
                    checkAndAnswerCall()  },
                onReject = {
                    Log.d("IncomingCallActivity", "Reject button clicked")
                    rejectCall() }
            )
        }
    }

    private fun checkAndAnswerCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                answerCall()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ANSWER_PHONE_CALLS)
            }
        } else {
            Toast.makeText(this, "Auto-answer not supported on Android below Oreo", Toast.LENGTH_SHORT).show()
        }
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                answerCall()
            } else {
                Toast.makeText(this, "Permission denied. Cannot answer call.", Toast.LENGTH_SHORT).show()
            }
        }

    companion object {
        fun showIncomingCallUI(context: Context, PhoneNumber: String) {
            val intent = Intent(context, IncomingCallActivity::class.java).apply {
                putExtra("PHONE_NUMBER", PhoneNumber)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    @Composable
    fun IncomingCallScreen(phoneNumber: String, onAccept: () -> Unit, onReject: () -> Unit) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Incoming Call from: $phoneNumber",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Accept")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onReject,
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                    ) {
                        Text("Reject")
                    }
                }
            }
        }
    }
    private fun answerCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED) {
                telecomManager.acceptRingingCall()
            } else {
                Toast.makeText(this, "Permission required to answer call", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun rejectCall() {
        Toast.makeText(this, "Call rejected (Feature not available in Android 9+)", Toast.LENGTH_SHORT).show()
        finish() // Close the activity
    }
}