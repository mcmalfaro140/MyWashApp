package com.example.myfinalproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.system.exitProcess

class SplashActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private var REQUEST_PERMISSIONS_REQUEST_CODE: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //hiding title bar of this activity
        window.requestFeature(Window.FEATURE_NO_TITLE)
        //making this activity full screen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.splash_background)

        auth = FirebaseAuth.getInstance()

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this,"The app needs location permition in order to work properly.", Toast.LENGTH_LONG)

                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)

            }
        } else {
            // Permission has already been granted
            start()
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    start()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this,"The app needs location permition in order to work properly.", Toast.LENGTH_LONG)
                    this.finish()
                    exitProcess(0)
                }
                return
            }
        }
    }


    private fun start(){
        //if user has logged in then it will be redirect to the main activity
        if(auth.currentUser != null){
            var currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
            Log.i("misael info", currentUserEmail.toString())
            db.collection("userType")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.get("Email").toString().equals(currentUserEmail)) {
                            var temp = document.get("current").toString()
                            if (temp.equals("user")) {
                                startActivity(Intent(this, MainActivity::class.java))
                                //finish this activity
                                finish()
                            } else {
                                startActivity(Intent(this, ProvidersMainActivity::class.java))
                                //finish this activity
                                finish()
                            }

                        }

                    }


                }

        }else{
            //1second splash time
            Handler().postDelayed({
                //start main activity
                startActivity(Intent(this, AuthenticationActivity::class.java))
                //finish this activity
                finish()
            },1000)
        }

    }
}
