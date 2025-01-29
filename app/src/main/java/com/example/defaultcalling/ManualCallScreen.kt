package com.example.defaultcalling

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun ManualCallScreen() {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }

    val callPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            makeCall(context, phoneNumber.text)
        } else {
            Toast.makeText(context, "Call permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Enter Phone Number", style = MaterialTheme.typography.headlineMedium)

        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            singleLine = true
        )

        Button(
            onClick = {
                if (phoneNumber.text.isNotEmpty()) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        makeCall(context, phoneNumber.text)
                    } else {
                        callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                    }
                } else {
                    Toast.makeText(context, "Enter a valid number", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text("Call")
        }
    }
}

private fun makeCall(context: Context, phoneNumber: String) {
    val callIntent = Intent(Intent.ACTION_CALL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    context.startActivity(callIntent)
}
