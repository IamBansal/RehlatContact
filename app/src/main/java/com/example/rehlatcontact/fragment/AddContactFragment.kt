@file:Suppress("DEPRECATION")

package com.example.rehlatcontact.fragment

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.rehlatcontact.databinding.FragmentAddContactBinding
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class AddContactFragment : Fragment() {

    private lateinit var binding: FragmentAddContactBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

        binding.btnAddContact.setOnClickListener {
            addContactToDB()
        }

    }

    private fun addContactToDB() {

        val name = binding.etName.text.trim().toString()
        val number = binding.etPhone.text.trim().toString()
        val code = binding.etContactCode.selectedCountryCode.trim()

        val isPersonal = if (binding.radioBusiness.isChecked){
            false
        } else if (binding.radioPersonal.isChecked){
            true
        } else {
            Toast.makeText(requireContext(), "Select the type first", Toast.LENGTH_SHORT).show()
            return
        }
        if (name.isEmpty() || number.isEmpty()){
            Toast.makeText(requireContext(), "Fill the fields first", Toast.LENGTH_SHORT).show()
            return
        }
        if (number.length != 10){
            Toast.makeText(requireContext(), "Enter correct number", Toast.LENGTH_SHORT).show()
            return
        }

        val progressBar = ProgressDialog(requireContext())
        progressBar.setMessage("Adding up the contact..")
        progressBar.show()

        val random = UUID.randomUUID().toString()
        val map = HashMap<String, Any>()
        map["id"] = random
        map["isPersonal"] = isPersonal
        map["name"] = name
        map["phone"] = "+$code-$number"

        FirebaseDatabase.getInstance().reference.child("Rehlat").child(number)
            .updateChildren(map.toMutableMap()).addOnCompleteListener {
                progressBar.dismiss()
            if (it.isSuccessful) {
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), it.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }

    }

}