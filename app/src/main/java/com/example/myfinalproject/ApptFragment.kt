package com.example.myfinalproject

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.example.myfinalproject.databinding.ApptFragmentBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.appt_fragment.*
import kotlinx.android.synthetic.main.activity_main.*

import java.text.SimpleDateFormat
import java.util.*

class ApptFragment : Fragment() {



    private  lateinit var binding: ApptFragmentBinding
    var cal = Calendar.getInstance()
    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.appt_fragment, container, false )
        var strPrice = arguments?.getString("price").toString()
        binding.progressBarOrder.setVisibility(View.GONE)
        binding.errorTxtOrder.setVisibility(View.GONE)
        binding.orderProviderName?.text = arguments?.getString("name")
        binding.provPrice.text = "$ ${strPrice}"
        var price = strPrice.toDouble()
        var totalPrice = price + 3

        lateinit  var email : String
           email     = arguments?.getString("Email").toString()//saves the email of the provider
        Log.i("pro", email)


        //saving data
        var providername :String = binding.orderProviderName.text.toString()
        Log.i("name" , providername)
        binding.finalprice.text = "$ ${totalPrice}"//final price
        binding.totalOrderPrice.text = "Price: $ ${strPrice}"//total order


        // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        binding.datePicker.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(context!!,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })

        binding.button.setOnClickListener {
            binding.progressBarOrder.setVisibility(View.VISIBLE)
            if(errorCheck(binding.date.toString(),binding.editTextAddr.text.toString())){
                binding.progressBarOrder.setVisibility(View.GONE)
                binding.errorTxtOrder.setVisibility(View.VISIBLE)
            }else{
                placeOrder("10/20/20", binding.editTextAddr.text.toString(), email , providername);
            }

        }


        return binding.root
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        binding.date.text = sdf.format(cal.getTime())
    }

    private fun placeOrder(date:String, location: String,  provEmail:String, provName:String) {
        var currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        var username = ""
        var phoneUser = ""

        binding.errorTxtOrder.setVisibility(View.GONE)
        db.collection("Users")
            .whereEqualTo("Email", currentUserEmail)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    username = document.get("Name").toString()
                    phoneUser = document.get("Phone").toString()

                }

                //creates new order
                val newOrder = hashMapOf(
                    "OrderStatus" to "Pending",
                    "date" to date,
                    "location" to location,
                    "phone" to phoneUser,
                    "provider" to provEmail,
                    "providername" to provName,
                    "type" to "Basic",
                    "user" to currentUserEmail,
                    "username" to username
                )

                db.collection("orders")
                    .add(newOrder)
                    .addOnSuccessListener {
                        binding.progressBarOrder.setVisibility(View.GONE)
                        binding.errorTxtOrder.setVisibility(View.VISIBLE)
                        var id = it.id.toString()
                        var bundle = bundleOf("orderId" to id)
                        view?.findNavController()?.navigate(R.id.action_apptFragment_to_doneFragment, bundle)
                    }
                    .addOnFailureListener {

                    }

            }

    }

    private fun errorCheck(date: String, addr:String) : Boolean{
        if(date.isEmpty() or addr.isEmpty()){
            return true
        }
        return false
    }

}

