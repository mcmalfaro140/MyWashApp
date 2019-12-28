package com.example.myfinalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.myfinalproject.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.maps.SupportMapFragment
import androidx.databinding.adapters.NumberPickerBindingAdapter.setValue
import com.google.firebase.database.DatabaseReference
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.nav_header_provider.view.*
import kotlinx.android.synthetic.main.nav_header_user.view.*
import com.google.android.gms.maps.OnMapReadyCallback


class MainActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityMainBinding
    //for the drawer menu
    private lateinit var drawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout for this fragment
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main )

        //initialize from the activity main binding
        drawerLayout = binding.drawerLayout
        val navController = this.findNavController(R.id.myNavHostFragment)




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
        navHeaderText.navHeaderTextUser.text = FirebaseAuth.getInstance().currentUser?.email.toString()



    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        //so the up button replaces the menu button when we get to the first fragment
        return NavigationUI.navigateUp( navController, drawerLayout)

    }
}


