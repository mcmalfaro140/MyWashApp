package com.example.myfinalproject


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.myfinalproject.databinding.FragmentProvidersSettingsBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 */
class ProvidersSettingsFragment : Fragment() {

    private  lateinit var binding: FragmentProvidersSettingsBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_settings, container, false)
        binding.userEmail.text = FirebaseAuth.getInstance().currentUser?.email.toString()

        binding.UpdateBtn.setOnClickListener {
            val user = hashMapOf(
                "Name" to binding.nameUpdateText.text.toString(),
                "Email" to binding.userEmail.text.toString(),
                "Phone" to binding.phoneUpdateText.text.toString(),
                "Address" to binding.addressUpdateText.text.toString(),
                "City" to binding.cityUpdateText.text.toString(),
                "State" to binding.stateUpdateText.text.toString(),
                "Zip" to binding.zipUpdateText.text.toString()
            )
            updateProviders(user)

        }


        return binding.root

    }

}

    private fun updateProviders(newUser:HashMap<String,String>)
    {

    }


