package com.example.myfinalproject


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.myfinalproject.Adapter.ProvidersOrdersAdapter
import com.example.myfinalproject.Adapter.UsersOrdersAdapter
import com.example.myfinalproject.databinding.FragmentProvidersOrdersBinding
import com.example.myfinalproject.databinding.FragmentUsersOrderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_providers_orders.*
import kotlinx.android.synthetic.main.fragment_users_order.*


class ProvidersOrdersFragment : Fragment() {


    var  db = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentProvidersOrdersBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_providers_orders, container, false)


        var arrayList = ArrayList<String>()

        //for the database

        var current = FirebaseAuth.getInstance().currentUser?.email
        //for the database to get the order that pertain to the current provider
        var ref = db.collection("orders").whereEqualTo("provider", current.toString())

        ref.get().addOnSuccessListener { documents ->
            for (doc in documents) {
                var str = "${doc.get("username")}-${doc.get("phone")}-${doc.get("location")}-${doc.get("date")}-${doc.get("type")}-${doc.get("OrderStatus")}-${doc.get("id")}"
                arrayList.add(str)
            }

            //This displays the RecyclerView with the custom row view
            val adapter = ProvidersOrdersAdapter(context!!, arrayList)
            //conects the list to the adapter
            providers_orders_listView.adapter = adapter

        }

        // Inflate the layout for this fragment
        return binding.root
    }





}
