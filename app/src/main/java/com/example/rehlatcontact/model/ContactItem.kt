package com.example.rehlatcontact.model

data class ContactItem(
    val id: String,
    var isPersonal: Boolean,
    val name: String,
    val phone: String
) {
    constructor() : this("", true, "", "")
}