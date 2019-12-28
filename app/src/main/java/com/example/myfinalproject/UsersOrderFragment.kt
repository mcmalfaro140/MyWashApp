package com.example.myfinalproject



import android.os.Bundle
//import android.support.widget.DefaultItemAnimator
//import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.myfinalproject.Adapter.UsersOrdersAdapter
import com.example.myfinalproject.databinding.FragmentUsersOrderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_users_order.*

/**
 * A simple [Fragment] subclass.
 */
class UsersOrderFragment : Fragment() {


    var  db = FirebaseFirestore.getInstance()

   // private lateinit var listView : ListView
   // private lateinit var orderData: ArrayList<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentUsersOrderBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_users_order, container, false)



        var arrayList = ArrayList<String>()

        var current = FirebaseAuth.getInstance().currentUser?.email
        //for the database
        var ref = db.collection("orders").whereEqualTo("user", current.toString())


        ref.get().addOnSuccessListener { documents ->
            for (doc in documents) {
                var str = "${doc.get("providername")}-${doc.get("phone")}-${doc.get("location")}-${doc.get("date")}-${doc.get("type")}-${doc.get("OrderStatus")}-${doc.get("id")}"
                arrayList.add(str)
            }

            //This displays the RecyclerView with the custom row view
            val adapter = UsersOrdersAdapter(context!!, arrayList)
            //conects the list to the adapter
            user_orders_listView.adapter = adapter

        }
        return binding.root
    }




}
