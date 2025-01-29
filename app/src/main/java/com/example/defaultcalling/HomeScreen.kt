

package com.example.defaultcalling

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.defaultcalling.dataModels.CallLogs
import com.example.defaultcalling.dataModels.Contact
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: CallViewModel) {
    val callLogs by viewModel.callLogs.observeAsState(emptyList())
    val contacts by viewModel.contacts.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.fetchCallLogs()
        viewModel.fetchContacts()
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Contacts", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,style = MaterialTheme.typography.headlineLarge) })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Use the padding provided by Scaffold
                .padding(5.dp) // Add additional padding as needed
        ) {
            Button(onClick = { showDialog=true}, modifier = Modifier.padding(vertical = 8.dp)) {
                Text("Dial New Number")
            }
            if(showDialog){
               CallDialog(onDismiss={showDialog=false})
            }
                LazyColumn {
                    items(contacts) { contact ->
                        ContactItem( contact,callLogs, context = LocalContext.current)
                }
            }

        }
    }
}

@Composable
fun CallDialog(onDismiss: () -> Unit) {
Dialog(onDismissRequest={onDismiss()}){
    Surface(shape = MaterialTheme.shapes.medium,
        tonalElevation = 8.dp) {
       Column(modifier = Modifier.padding(16.dp),
           verticalArrangement = Arrangement.spacedBy(8.dp)
       ) {
           Text("Enter Phone Number", style = MaterialTheme.typography.headlineMedium)
           ManualCallScreen()
           Spacer(modifier = Modifier.height(10.dp))
       }
    }
}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactItem(contact: Contact, callLogs: List<CallLogs>,context: Context) {
    // Track if the contact is expanded
     var isExpanded by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val callPermissionLauncher= rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()
    ){isGranted->
        if(isGranted){
            makeCall(context,contact.phoneNumber)
        }
        else{
            Toast.makeText(context, "Call permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Find the most recent call log for this contact
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
        ){
            val recentCall=callLogs
                .filter { it.number==contact.phoneNumber }
                .sortedByDescending { it.date }
                .firstOrNull()
            if(recentCall!=null){
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                ) {
                    Text("Call History for ${contact.name}", style = MaterialTheme.typography.headlineSmall)
                    LazyColumn {
                        items(callLogs.filter { it.number==contact.phoneNumber }){log->
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text("Type: ${log.type}", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "Duration: ${formatDuration(log.duration)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "Date: ${formatDate(log.date)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
            else{
                Text(
                    "No call history available for ${contact.name}.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
    val recentCall=callLogs
        .filter { it.number==contact.phoneNumber }
        .sortedByDescending { it.date }
        .firstOrNull()

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp)) {
        Card( shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp), // FIXED
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
                contentAlignment = Alignment.Center ){
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_account_circle_24), // Dummy image
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .padding(4.dp)
                    )
                    Text(text = contact.name,style=MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(if(isExpanded) "▼" else "▶",style=MaterialTheme.typography.bodyLarge)
                }
            }

        }

        // If expanded, show more details
        if(isExpanded){
            Spacer(modifier = Modifier.height(8.dp))
            // show the phone number
            Text("Phone: ${contact.phoneNumber}", style =MaterialTheme.typography.bodySmall)
            // Show the most recent call log, if any
            recentCall?.let {
                // Show the recent call type (Incoming/Outgoing) and its duration
                val callType =it.type

                // Format duration as "X min Y sec" if duration is available
                val durationInSeconds = it.duration.toIntOrNull() ?: 0
                val minutes = durationInSeconds / 60
                val seconds = durationInSeconds % 60
                val formattedDuration = "${minutes}min ${seconds}sec"

                Text("Recent Call: $callType, $formattedDuration", style = MaterialTheme.typography.bodySmall)
            } ?: Text("No recent calls", style = MaterialTheme.typography.bodySmall)
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
                 horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    if(ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
                       makeCall(context,contact.phoneNumber)
                    }
                    else{
                        callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                    }
                     }
                ) {
                    Text("Call")
                }
                Button(onClick = {showBottomSheet=true }) {
                    Text("History")
                }
            }
        }
    }
}
fun formatDuration(duration: String): String {
    val durationInSeconds = duration.toIntOrNull() ?: 0
    val minutes = durationInSeconds / 60
    val seconds = durationInSeconds % 60
    return "${minutes}min ${seconds}sec"
}

// Utility function to format timestamp into human-readable date
fun formatDate(timestamp: String): String {
    val date = Date(timestamp.toLongOrNull() ?: 0L)
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return formatter.format(date)
}
// Function to make a call
private fun makeCall(context: Context, phoneNumber: String) {
    val callIntent = Intent(Intent.ACTION_CALL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    context.startActivity(callIntent)
}

