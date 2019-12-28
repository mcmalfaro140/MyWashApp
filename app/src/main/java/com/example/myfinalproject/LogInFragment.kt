package com.example.myfinalproject


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.myfinalproject.databinding.FragmentLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class LogInFragment : Fragment() {

    private  lateinit var binding: FragmentLogInBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_log_in, container, false )
        //hides progress bar
        binding.progressBar.setVisibility(View.GONE)


        //login btn listener
        binding.login.setOnClickListener {
            binding.progressBar.setVisibility(View.VISIBLE)
            if(!checkEmpty()){
                binding.progressBar.setVisibility(View.GONE)
                binding.authText.setVisibility(View.VISIBLE)
                binding.authText.text = "Enter email and password"
            }else{
                if (binding.switch1.isChecked){
                    logInUser(binding.email.text.toString(), binding.password, binding.authText, binding.progressBar)

                }else{
                    logInProvider(binding.email.text.toString(), binding.password, binding.authText, binding.progressBar)
                }
            }
        }
        //Signup btn listener
        binding.signupbtn.setOnClickListener {view: View ->
            if (binding.switch1.isChecked){
                view.findNavController().navigate(R.id.action_logInFragment_to_userSignUpFragment)
            }else{
                view.findNavController().navigate(R.id.action_logInFragment_to_providerSignUpFragment)
            }
        }

        binding.forgotPass.setOnClickListener {view: View ->
            view.findNavController().navigate(R.id.action_logInFragment_to_forgotPasswordFragment)
        }

        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.switch1.text = "switch to ProviderModel"
                binding.mainText.setText(R.string.user)
            } else {
                binding.switch1.text = "Switch to User"
                binding.mainText.setText(R.string.provider)
            }
        }

        return binding.root
    }

    //TODO: check values email and pass before sending the request
    //Function that will handle the log in
    private fun logInUser(user: String, pass: EditText, test: TextView, rotatingBar: ProgressBar){
        db.collection("userType")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.get("Email").toString().equals(user)) {
                        var temp = document.get("type").toString()
                        var docId = document.id
                        if (temp.equals("provider")) {
                            rotatingBar.setVisibility(View.GONE)
                            test.text = "Not register USER"
                        } else {

                            val docRef = db.collection("userType").document(docId)
                            db.runTransaction { transaction ->
                                transaction.update(docRef, "current", "user")
                            }

                            auth.signInWithEmailAndPassword(user, pass.text.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        rotatingBar.setVisibility(View.GONE)
                                        val intent = Intent(getActivity(), MainActivity::class.java)
                                        getActivity()?.startActivity(intent)
                                    } else {
                                        rotatingBar.setVisibility(View.GONE)
                                        test.text = "Log in failed"
                                    }
                                }
                        }

                    }
                }
            }
    }

    //TODO: check values email and pass before sending the request
    //Function that will handle the log in
    private fun logInProvider(user: String, pass: EditText, test: TextView, rotatingBar: ProgressBar){

        db.collection("userType")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.get("Email").toString().equals(user)) {
                        var temp = document.get("type").toString()
                        var docId = document.id
                        if (temp.equals("user")) {
                            rotatingBar.setVisibility(View.GONE)
                            test.text = "Not a PROVIDER"
                        } else {

                            val docRef = db.collection("userType").document(docId)
                            db.runTransaction { transaction ->
                                transaction.update(docRef, "current", "provider")
                            }

                            auth.signInWithEmailAndPassword(user, pass.text.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        rotatingBar.setVisibility(View.GONE)
                                        val intent =
                                            Intent(getActivity(), ProvidersMainActivity::class.java)
                                        getActivity()?.startActivity(intent)
                                    } else {
                                        rotatingBar.setVisibility(View.GONE)
                                        test.text = "Log in failed"
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
        }else if(binding.password.text.isEmpty()){
            return false
        }
        return true
    }



}
