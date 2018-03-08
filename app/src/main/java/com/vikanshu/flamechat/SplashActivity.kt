package com.vikanshu.flamechat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.vikanshu.flamechat.BottomNavigationActivities.AllChatsActivity

class SplashActivity : AppCompatActivity() {

    private val permissions = arrayOf(Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        overridePendingTransition(0,0)
        firebaseUser = FirebaseAuth.getInstance()?.currentUser
        if (!hasAllPermissions(this, *permissions)) // if permissions not granted then ask for them
            ActivityCompat.requestPermissions(this, permissions, 21023)
        else // if permissions granted then move forward
            move()
    }

    // function for result of asked permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            21023 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    move()
                } else {
                    showToast("Please grant all the permissions to continue")
                    this.finish()
                }
            }
        }
    }

    // function for have account button pressed
    fun alreadyHaveAccountPressed(v: View) {
        // move to log in activity
        startActivity(Intent(this, LogInActivity::class.java))
        this.finish()
    }

    // function for need account pressed
    fun needAccountPressed(v: View) {
        // move to create account activity
        startActivity(Intent(this, CreateAccountActivity::class.java))
        this.finish()
    }

    // function to move or stay
    private fun move() {
        if (firebaseUser != null) {
            // move to main screen
            val i = Intent(this, AllChatsActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
            Handler().postDelayed({this.finish()},1500)
        }
    }

    // function to show a toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // function to check whether all permissions are granted
    private fun hasAllPermissions(context: Context, vararg permissions: String): Boolean {
        for (i in permissions) {
            val res = context.checkCallingOrSelfPermission(i)
            if (res == PackageManager.PERMISSION_DENIED)
                return false
        }
        return true
    }
}
