package com.example.defaultcalling

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.defaultcalling.ui.theme.DefaultcallingTheme

class MainActivity : ComponentActivity() {

    private val PERMISSION_REQUEST_CODE = 101
    private lateinit var callViewModel: CallViewModel // Declare it as lateinit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create the repository
        val repository = CallRepository(this ) // Ensure CallRepository is properly implemented

        // Create the custom ViewModel factory
        val factory = CallViewModelFactory(repository)

        // Initialize the ViewModel with the custom factory
        callViewModel = ViewModelProvider(this, factory).get(CallViewModel::class.java)
        setContent {
            DefaultcallingTheme {
                MainContent()
            }
        }
    }

    @Composable
    fun MainContent() {
        val hasPermissions = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            if (checkPermissions()) {
                hasPermissions.value = true
            } else {
                requestPermissions()
            }
        }

        if (hasPermissions.value) {
            HomeScreen(viewModel = callViewModel)
        } else {
            ShowPermissionRequestMessage()
        }
    }

    private fun checkPermissions(): Boolean {
        val callLogPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
        val contactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        return callLogPermission == PackageManager.PERMISSION_GRANTED &&
                contactsPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS),
            PERMISSION_REQUEST_CODE
        )
    }

    @Composable
    fun ShowPermissionRequestMessage() {
        Toast.makeText(this, " permissions to continue", Toast.LENGTH_LONG).show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions as Array<String>, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Reload the composable to show HomeScreen
                setContent {
                    DefaultcallingTheme {
                        MainContent()
                    }
                }
            } else {
                Toast.makeText(this, "Permissions denied. Cannot proceed.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
