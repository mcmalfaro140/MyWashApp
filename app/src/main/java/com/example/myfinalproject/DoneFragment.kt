package com.example.myfinalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigator
import androidx.navigation.findNavController
import com.example.myfinalproject.databinding.DoneFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore

class DoneFragment : Fragment() {
    private  lateinit var binding: DoneFragmentBinding
    var  db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.done_fragment, container, false )
        var id = arguments?.getString("orderId").toString()

        val docRef = db.collection("orders").document(id)
        db.runTransaction { transaction ->
            transaction.update(docRef, "id", id)
        }

        binding.confirId.text = id
        binding.btnDone.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_doneFragment_to_mapFragment)

        }
        return binding.root
    }

}