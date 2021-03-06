package com.example.myfinalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.myfinalproject.databinding.ActivityProvidersMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.nav_header_provider.view.*

class ProvidersMainActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityProvidersMainBinding
    //for the drawer menu
    private lateinit var drawerLayout: DrawerLayout


    //Database instance
    //private lateinit var  pFirebaseDatabase: FirebaseDatabase




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        // val myRef = database.getReference("message")

        //  myRef.setValue("Hello, World!")


        // Inflate the layout for this fragment
        binding = DataBindingUtil.setContentView(this, R.layout.activity_providers_main )

        //initialize from the activity main binding
        drawerLayout = binding.drawerLayout
        val navController = this.findNavController(R.id.myNavHostFragment_provider)



        //for the up button and include the drawerlayout as the third component
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)


        // prevent nav gesture if not on start destination
        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, args: Bundle? ->
            if (nd.id == nc.graph.startDestination) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }


        //set the NavigationUI to now about the navigation  view
        NavigationUI.setupWithNavController(binding.navView, navController)
        var navView = binding.navView
        val navHeaderText  = navView.getHeaderView(0)
        navHeaderText.navHeaderTextProv.text = FirebaseAuth.getInstance().currentUser?.email.toString()


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment_provider)
        //so the up button replaces the menu button when we get to the first fragment
        return NavigationUI.navigateUp( navController, drawerLayout)

    }
}
