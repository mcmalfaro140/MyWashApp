package com.example.myfinalproject

import android.content.Intent
import com.example.myfinalproject.databinding.FragmentSignUpInfoBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class UserSignUpInfot : Fragment() {

    private  lateinit var binding: FragmentSignUpInfoBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up_info, container, false )

        binding.userEmail.text = FirebaseAuth.getInstance().currentUser?.email.toString()
        binding.progressBar.setVisibility(View.GONE)
        binding.basicFrame.setVisibility(View.GONE)
        binding.deluxFrame.setVisibility(View.GONE)
        binding.extraFrame.setVisibility(View.GONE)
        binding.addDelux.setVisibility(View.GONE)
        binding.addExtra.setVisibility(View.GONE)


        binding.registerBtn.setOnClickListener {
            binding.progressBar.setVisibility(View.VISIBLE)
            if(!checkEmpty()){
                binding.progressBar.setVisibility(View.GONE)
                binding.errorTxt.setVisibility(View.VISIBLE)
                binding.errorTxt.text = "Answer all questions"
            }else {
                if (!checkZipCode(binding.zipditText.text.toString())) {
                    binding.progressBar.setVisibility(View.GONE)
                    binding.errorTxt.setVisibility(View.VISIBLE)
                    binding.errorTxt.text = "Invalid ZIP code"
                } else {
                    //creates new user before adding to database
                    val user = hashMapOf(
                        "Name" to  binding.nameeditText.text.toString(),
                        "Email" to binding.userEmail.text.toString(),
                        "Phone" to binding.phoneeditText.text.toString(),
                        "Address" to binding.addresseditText.text.toString(),
                        "City" to binding.cityeditText.text.toString(),
                        "State" to binding.stateditText.text.toString(),
                        "Zip" to binding.zipditText.text.toString()
                    )
                    register(user)
                }
            }
        }


        return binding.root
    }

    private fun register(newUser:HashMap<String,String>) {

        var currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        var found = false;
        var userIndex = ""
        var count = 0

        db.collection("userType")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    count++
                    if (document.get("Email").toString().equals(currentUserEmail)) {
                        found = true
                        userIndex = document.id
                    }
                }

                if(found) {
                    db.collection("Users")
                        .add(newUser)
                        .addOnSuccessListener {
                            val docRef = db.collection("userType").document(userIndex)
                            db.runTransaction { transaction ->
                                transaction.update(docRef, "type", "master")
                                transaction.update(docRef, "current", "user")
                            }.addOnSuccessListener { _ ->
                                val intent = Intent(getActivity(), MainActivity::class.java)
                                getActivity()?.startActivity(intent)
                                activity?.finish()
                            }.addOnFailureListener { _ ->
                                binding.progressBar.setVisibility(View.GONE)
                                binding.errorTxt.setVisibility(View.VISIBLE)
                            }
                        }

                }else if(!found && count == documents.size()){
                    //creates new user type
                    val newUserType = hashMapOf(
                        "Email" to currentUserEmail,
                        "current" to "user",
                        "type" to "user"
                    )

                    db.collection("Users")
                        .add(newUser)
                        .addOnSuccessListener {
                            db.collection("userType")
                                .add(newUserType)
                                .addOnSuccessListener {
                                    //binding.help.text = "inside new user"
                                    val intent = Intent (getActivity(), MainActivity::class.java)
                                    getActivity()?.startActivity(intent)
                                    activity?.finish()
                                }
                                .addOnFailureListener {
                                    binding.progressBar.setVisibility(View.GONE)
                                    binding.errorTxt.setVisibility(View.VISIBLE)
                                }
                        }
                        .addOnFailureListener {
                            binding.progressBar.setVisibility(View.GONE)
                            binding.errorTxt.setVisibility(View.VISIBLE)
                        }
                }

            }

    }

    private fun checkZipCode(zip:String):Boolean{
        try {
            val temp = zip.toInt()
        }catch (e: NumberFormatException){
            return false
        }
        return true
    }

    private fun checkEmpty():Boolean{
        if(binding.nameeditText.text.isEmpty()){
            return false
        }else if(binding.phoneeditText.text.isEmpty()){
            return false
        }else if(binding.addresseditText.text.isEmpty()){
            return false
        }else if(binding.cityeditText.text.isEmpty()){
            return false
        }else if(binding.stateditText.text.isEmpty()){
            return false
        }else if(binding.zipditText.text.isEmpty()){
            return false
        }
        return true
    }





}