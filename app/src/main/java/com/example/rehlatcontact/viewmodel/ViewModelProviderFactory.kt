package com.example.rehlatcontact.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rehlatcontact.respository.Repository

class ViewModelProviderFactory(
    private val repository: Repository
) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContactViewModel(repository) as T
    }

}