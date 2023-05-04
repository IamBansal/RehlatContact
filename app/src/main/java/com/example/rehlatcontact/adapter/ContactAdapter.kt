package com.example.rehlatcontact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.rehlatcontact.databinding.ContactItemBinding
import com.example.rehlatcontact.model.ContactItem
import com.google.firebase.database.FirebaseDatabase

class ContactAdapter(private val list: ArrayList<ContactItem>) :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: ContactItem) {
            binding.tvName.text = contact.name
            binding.tvNumber.text = contact.phone
            binding.tvType.text = if (contact.isPersonal) "Personal" else "Business"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ContactItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = list[position]
        holder.bind(contact)
        val phone = contact.phone.substring(contact.phone.length - 10)
        holder.itemView.setOnLongClickListener {
            FirebaseDatabase.getInstance().reference.child("Rehlat").child(phone)
                .removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //Can add some toast
                    } else {
                        Toast.makeText(
                            holder.itemView.context,
                            task.exception?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}