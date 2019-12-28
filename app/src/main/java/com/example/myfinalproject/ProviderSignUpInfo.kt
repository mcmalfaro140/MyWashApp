package com.example.myfinalproject

import android.content.Intent
import com.example.myfinalproject.databinding.FragmentSignUpInfoBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ProviderSignUpInfoSignUpInfot : Fragment() {

    private  lateinit var binding: FragmentSignUpInfoBinding
    private lateinit var auth: FirebaseAuth
    // Access a Cloud Firestore instance from your Activity
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up_info, container, false )
        binding.iconImg.setImageResource(R.drawable.provider_icon)
        binding.userEmail.text = FirebaseAuth.getInstance().currentUser?.email.toString()
        binding.progressBar.setVisibility(View.GONE)
        binding.basicFrame.setVisibility(View.VISIBLE)
        binding.deluxFrame.setVisibility(View.GONE)
        binding.extraFrame.setVisibility(View.GONE)
        binding.addDelux.setVisibility(View.VISIBLE)
        binding.addExtra.setVisibility(View.GONE)
        var basicPrice = ""
        var basicDesc = ""
        var deluxPrice = ""
        var deluxDesc = ""
        var extraPrice = ""
        var extraDesc = ""


        binding.addDelux.setOnClickListener {
            binding.basicErrorTxt.setVisibility(View.GONE)
            var testResult = checkInputCarwashtype(binding.basicDescEdit.text.toString(),binding.basicPriceEdit.text.toString())
            if(testResult.equals("")){

                basicDesc = binding.basicDescEdit.text.toString()
                basicPrice = binding.basicPriceEdit.text.toString()

                binding.deluxFrame.setVisibility(View.VISIBLE)
                binding.addExtra.setVisibility(View.VISIBLE)
                binding.addDelux.setVisibility(View.GONE)
            }else{
                binding.basicErrorTxt.setVisibility(View.VISIBLE)
                binding.basicErrorTxt.text = testResult
            }

        }

        binding.addExtra.setOnClickListener {
            binding.deluxErrorTxt.setVisibility(View.GONE)
            var testResult = checkInputCarwashtype(binding.deluxDescEdit.text.toString(),binding.deluxPriceEdit.text.toString())
            if(testResult.equals("")){

                deluxDesc = binding.deluxDescEdit.text.toString()
                deluxPrice = binding.deluxPriceEdit.text.toString()

                binding.extraFrame.setVisibility(View.VISIBLE)
                binding.addExtra.setVisibility(View.GONE)
            }else{
                binding.deluxErrorTxt.setVisibility(View.VISIBLE)
                binding.basicErrorTxt.text = testResult
            }

        }

        binding.registerBtn.setOnClickListener {
            binding.progressBar.setVisibility(View.VISIBLE)
            binding.extraErrorTxt.setVisibility(View.GONE)

            var testResult = checkInputCarwashtype(binding.extraDescEdit.text.toString(),binding.extraPriceEdit.text.toString())
            var basicTestResult = checkInputCarwashtype(binding.basicDescEdit.text.toString(),binding.basicPriceEdit.text.toString())
            var deluxTestResult = checkInputCarwashtype(binding.deluxDescEdit.text.toString(),binding.deluxPriceEdit.text.toString())


            if(!checkEmpty()){
                binding.progressBar.setVisibility(View.GONE)
                binding.errorTxt.setVisibility(View.VISIBLE)
                binding.errorTxt.text = "Answer all questions"
            }else{
                if(!checkZipCode(binding.zipditText.text.toString())){
                    binding.progressBar.setVisibility(View.GONE)
                    binding.errorTxt.setVisibility(View.VISIBLE)
                    binding.errorTxt.text = "Enter a valid ZIP code"
                }else{
                    if (!basicTestResult.equals("")){
                        binding.progressBar.setVisibility(View.GONE)
                        binding.extraErrorTxt.setVisibility(View.VISIBLE)
                        binding.basicErrorTxt.text = basicTestResult
                    }else{

                        basicDesc = binding.basicDescEdit.text.toString()
                        basicPrice = binding.basicPriceEdit.text.toString()

                        if(binding.deluxFrame.isVisible){
                            if(!deluxTestResult.equals("")){
                                binding.progressBar.setVisibility(View.GONE)
                                binding.extraErrorTxt.setVisibility(View.VISIBLE)
                                binding.basicErrorTxt.text = deluxTestResult
                            }else{

                                deluxDesc = binding.deluxDescEdit.text.toString()
                                deluxPrice = binding.deluxPriceEdit.text.toString()
                            }
                        }

                        if(binding.extraFrame.isVisible){
                            if(!testResult.equals("")){
                                binding.progressBar.setVisibility(View.GONE)
                                binding.extraErrorTxt.setVisibility(View.VISIBLE)
                                binding.basicErrorTxt.text = testResult
                            }else{
                                extraDesc = binding.extraDescEdit.text.toString()
                                extraPrice = binding.extraPriceEdit.text.toString()
                            }
                        }

                        //creates new user before adding to database
                        val provider = hashMapOf(
                            "Name" to  binding.nameeditText.text.toString(),
                            "Email" to binding.userEmail.text.toString(),
                            "Phone" to binding.phoneeditText.text.toString(),
                            "Address" to binding.addresseditText.text.toString(),
                            "City" to binding.cityeditText.text.toString(),
                            "State" to binding.stateditText.text.toString(),
                            "Zip" to binding.zipditText.text.toString().toInt(),
                            "basic" to basicDesc,
                            "basicPrice" to basicPrice,
                            "delux" to deluxDesc,
                            "deluxPrice" to deluxPrice,
                            "extra" to extraDesc,
                            "extraPrice" to extraPrice
                        )

                        register(provider)
                    }
                }

            }
        }


        return binding.root
    }

    private fun register(newUser: HashMap<String, Any> ) {

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
                    db.collection("Providers")
                        .add(newUser)
                        .addOnSuccessListener {
                            val docRef = db.collection("userType").document(userIndex)
                            db.runTransaction { transaction ->
                                transaction.update(docRef, "type", "master")
                                transaction.update(docRef, "current", "provider")
                            }.addOnSuccessListener { task ->
                                val intent = Intent(getActivity(), ProvidersMainActivity::class.java)
                                getActivity()?.startActivity(intent)
                                activity?.finish()
                            }.addOnFailureListener { e ->
                                binding.progressBar.setVisibility(View.GONE)
                                binding.errorTxt.setVisibility(View.VISIBLE)
                            }
                        }

                }else if(!found && count === documents.size()){
                    //creates new user type
                    val newUserType = hashMapOf(
                        "Email" to currentUserEmail,
                        "current" to "provider",
                        "type" to "provider"
                    )

                    db.collection("Providers")
                        .add(newUser)
                        .addOnSuccessListener {
                            db.collection("userType")
                                .add(newUserType)
                                .addOnSuccessListener {
                                    //binding.help.text = "inside new user"
                                    val intent = Intent (getActivity(), ProvidersMainActivity::class.java)
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

    private fun checkInputCarwashtype(des:String, price:String): String{

        if(des.isEmpty()){
            return "Enter a description."
        }else if(price.isEmpty()){
            return "Enter a price"
        }else if(price.isNotEmpty()){
            try {
                val temp = price.toDouble()
            }catch (e: NumberFormatException){
                return "Enter a valid number for price"
            }
        }
        return ""
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