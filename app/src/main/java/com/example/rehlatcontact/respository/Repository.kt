package com.example.rehlatcontact.respository

import com.example.rehlatcontact.api.RetrofitInstance

class Repository {

    suspend fun getContacts() = RetrofitInstance.api.getContacts()

}