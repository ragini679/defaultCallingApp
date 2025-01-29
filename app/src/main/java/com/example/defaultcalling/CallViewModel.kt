package com.example.defaultcalling

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.defaultcalling.dataModels.CallLogs
import com.example.defaultcalling.dataModels.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CallViewModel(private val repository: CallRepository):ViewModel() {
    private val _callLogs=MutableLiveData<List<CallLogs>>()
    val callLogs:LiveData<List<CallLogs>> =_callLogs

    private val _contacts=MutableLiveData<List<Contact>>()
    val contacts:LiveData<List<Contact>> =_contacts

    fun fetchCallLogs() {
        viewModelScope.launch(Dispatchers.IO) {
            val logs = repository.getCallLogs()
            withContext(Dispatchers.Main) {
                _callLogs.value = logs
            }
        }
    }

    fun fetchContacts() {
        viewModelScope.launch(Dispatchers.IO) {
            val contactsList = repository.getContacts()
            withContext(Dispatchers.Main) {
                _contacts.value = contactsList
            }
        }
    }

}