package com.example.myfinalproject


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.example.myfinalproject.databinding.FragmentUserSettingsBinding


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


/**
 * A simple [Fragment] subclass.
 */
class UserSettingsFragment : Fragment() {
    private lateinit var binding: FragmentUserSettingsBinding
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
            updateUsers(user)

        }


        return binding.root

    }

    private fun updateUsers(updateUser: HashMap<String, String>) {
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

                if (found) {
                    db.collection("Users").document(userIndex).set(updateUser)
                        .addOnSuccessListener {
                            val docRef = db.collection("userType").document(userIndex)
                            db.runTransaction { transaction ->
                                transaction.update(docRef, "type", "master")
                                transaction.update(docRef, "current", "user")
                            }.addOnSuccessListener { task ->
                                val intent = Intent(getActivity(), MainActivity::class.java)
                                getActivity()?.startActivity(intent)
                                activity?.finish()
                            }


                        }


                }


            }
    }
}