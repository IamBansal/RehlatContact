package com.example.rehlatcontact.api

import com.example.rehlatcontact.model.Contact
import retrofit2.Response
import retrofit2.http.GET

interface MockAPI {

    @GET("contacts")
    suspend fun getContacts() : Response<Contact>

}