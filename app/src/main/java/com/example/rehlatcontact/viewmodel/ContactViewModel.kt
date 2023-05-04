package com.example.rehlatcontact.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rehlatcontact.model.Contact
import com.example.rehlatcontact.respository.Repository
import com.example.rehlatcontact.utils.Resource
import kotlinx.coroutines.launch
import java.io.IOException

class ContactViewModel(private val repository: Repository) : ViewModel() {

    val contact: MutableLiveData<Resource<Contact>> = MutableLiveData()

    fun getContacts() = viewModelScope.launch {
        getSafeContacts()
    }

    private suspend fun getSafeContacts() {
        contact.postValue(Resource.Loading())
        try {
            val response = repository.getContacts()
            Log.d("response", response.toString())
            if (response.isSuccessful) {
                response.body()?.let {
                    contact.postValue(Resource.Success(it))
                }
            } else {
                contact.postValue(Resource.Error(response.message()))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> contact.postValue(Resource.Error("Network failure"))
                else -> contact.postValue(Resource.Error(t.message.toString()))
            }
        }
    }

}