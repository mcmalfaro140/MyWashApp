package com.example.myfinalproject


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.myfinalproject.databinding.FragmentSingUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ProviderSignUpFragment : Fragment() {

    private  lateinit var binding: FragmentSingUpBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sing_up, container, false )
        binding.iconImg.setImageResource(R.drawable.provider_icon)
        binding.progressBar.setVisibility(View.GONE)

        binding.continueBtn.setOnClickListener {
            binding.progressBar.setVisibility(View.VISIBLE)
            if(!checkEmpty()){
                binding.progressBar.setVisibility(View.GONE)
                binding.errorTxt.setVisibility(View.VISIBLE)
                binding.errorTxt.text = "Enter email and password"
            }else{
                signUp(binding.email.text.toString(),binding.newPass.text.toString(), it)
            }

        }

        return binding.root
    }

    private fun signUp(email:String, password: String, view: View)
    {
        var found = false
        var count = 0
        var master = false

        db.collection("userType")
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    count++
                    if (document.get("Email").toString().equals(email)) {
                        var temp = document.get("type").toString()
                        if (temp.equals("provider")) {
                            found = true
                        } else if(temp.equals("master")){
                            found = true
                            master = true
                        }
                    }
                }

                if (found || master) {
                    binding.progressBar.setVisibility(View.GONE)
                    binding.errorTxt.setVisibility(View.VISIBLE)
                    binding.errorTxt.text = "You are already a PROVIDER"
                }else if (!found && count === documents.size()) {

                    //try creating a new user
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                view.findNavController().navigate(R.id.action_providerSignUpFragment_to_providerSignUpInfoSignUpInfot)
                            }else{
                                //if user exist... try logging in
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            view.findNavController().navigate(R.id.action_providerSignUpFragment_to_providerSignUpInfoSignUpInfot)
                                        } else {
                                            //else sign up fail
                                            binding.progressBar.setVisibility(View.GONE)
                                            binding.errorTxt.setVisibility(View.VISIBLE)
                                        }
                                    }
                            }
                        }
                }
            }
    }

    private fun checkEmpty():Boolean{
        if(binding.email.text.isEmpty()){
            return false
        }else if(binding.newPass.text.isEmpty()){
            return false
        }
        return true
    }
}