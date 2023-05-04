package com.example.rehlatcontact.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rehlatcontact.R
import com.example.rehlatcontact.adapter.ContactAdapter
import com.example.rehlatcontact.databinding.FragmentHomeBinding
import com.example.rehlatcontact.model.ContactItem
import com.example.rehlatcontact.respository.Repository
import com.example.rehlatcontact.utils.Resource
import com.example.rehlatcontact.viewmodel.ContactViewModel
import com.example.rehlatcontact.viewmodel.ViewModelProviderFactory
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: ContactViewModel
    private lateinit var list: ArrayList<ContactItem>
    private lateinit var contactAdapter: ContactAdapter

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        list.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list = ArrayList()
        val repository = Repository()
        val factory = ViewModelProviderFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ContactViewModel::class.java]

        contactAdapter = ContactAdapter(list)
        binding.rvContacts.adapter = contactAdapter
        binding.rvContacts.layoutManager = LinearLayoutManager(requireContext())

        list.clear()
        contactAdapter.notifyDataSetChanged()

        FirebaseDatabase.getInstance().reference.child("Rehlat")
            .addChildEventListener(object :
                ChildEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val item = snapshot.getValue(ContactItem()::class.java)
                    list.add(item!!)
                    Log.d("list", list.size.toString())
                    contactAdapter.notifyDataSetChanged()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                @SuppressLint("NotifyDataSetChanged")
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val item = snapshot.getValue(ContactItem()::class.java)
                    list.remove(item!!)
                    contactAdapter.notifyDataSetChanged()
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {}
            })
        getMenuItems()

        getAllContacts()

        binding.btnAddContact.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addContactFragment)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }

    private fun getMenuItems() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.popup_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.business -> {
                        showBusinessContacts()
                        true
                    }
                    R.id.personal -> {
                        showPersonalContacts()
                        true
                    }
                    R.id.all -> {
                        setupRecyclerView(list)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllContacts() {

        viewModel.getContacts()
        viewModel.contact.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    list.removeAll(response.data!!)
                    list.addAll(response.data)
                    contactAdapter.notifyDataSetChanged()
                    Log.d("success", list[0].name)
                }
                is Resource.Error -> {
                    Log.d("error", response.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("loading", "in loading state")
                }
            }
        }
    }

    private fun showBusinessContacts() {
        val listNew = ArrayList<ContactItem>()
        for (item in list) {
            if (!item.isPersonal) {
                listNew.add(item)
            }
        }
        setupRecyclerView(listNew)
    }

    private fun showPersonalContacts() {
        val listNew = ArrayList<ContactItem>()
        for (item in list) {
            if (item.isPersonal) {
                listNew.add(item)
            }
        }
        setupRecyclerView(listNew)
    }

    private fun setupRecyclerView(list: java.util.ArrayList<ContactItem>) {
        binding.rvContacts.apply {
            contactAdapter = ContactAdapter(list)
            adapter = contactAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }


}