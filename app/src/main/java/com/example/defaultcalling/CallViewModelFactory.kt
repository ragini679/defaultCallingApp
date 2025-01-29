package com.example.defaultcalling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CallViewModelFactory(private val repository: CallRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CallViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CallViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
