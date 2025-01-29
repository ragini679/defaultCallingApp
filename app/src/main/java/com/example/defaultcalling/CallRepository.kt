package com.example.defaultcalling

import android.content.Context
import android.provider.CallLog
import android.provider.ContactsContract
import com.example.defaultcalling.dataModels.CallLogs
import com.example.defaultcalling.dataModels.Contact
import java.util.UUID

class CallRepository(private val context: Context) {

    // Fetches call logs from the device
    fun getCallLogs(): List<CallLogs> {
        val callLogs = mutableListOf<CallLogs>()
        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            "${CallLog.Calls.DATE} DESC" // Ensure proper sorting
        )

        cursor?.use {
            while (it.moveToNext()) {
                val number = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.NUMBER)) ?: "Unknown"
                val type = when (it.getInt(it.getColumnIndexOrThrow(CallLog.Calls.TYPE))) {
                    CallLog.Calls.INCOMING_TYPE -> "Incoming"
                    CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                    CallLog.Calls.MISSED_TYPE -> "Missed"
                    else -> "Unknown"
                }
                val date = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.DATE)) ?: "Unknown"
                val duration = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.DURATION)) ?: "0"

                callLogs.add(
                    CallLogs(
                        UUID.randomUUID().toString(),
                        number,
                         type,
                       date,
                       duration
                    )
                )
            }
        }
        return callLogs
    }

    // Fetches contacts from the device
    fun getContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)) ?: "Unknown"
                val number = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)) ?: "Unknown"

                contacts.add(
                    Contact(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        phoneNumber = number
                    )
                )
            }
        }
        // Deduplicate contacts by phone number
        val uniqueContacts = contacts.groupBy { it.phoneNumber }
            .map { (_, contactList) -> contactList.first() }

        return uniqueContacts
    }
}