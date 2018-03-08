package com.vikanshu.flamechat

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.rengwuxian.materialedittext.MaterialEditText
import com.vikanshu.flamechat.BottomNavigationActivities.AllChatsActivity

class LogInActivity : AppCompatActivity() {

    private var email: MaterialEditText ?= null
    private var password: MaterialEditText ?= null
    private var logInBtn: Button ?= null
    private var firebaseAuth: FirebaseAuth ?= null
    private var progressDialog: ProgressDialog ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        // set toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.title = "Log In"
        toolbar?.setTitleTextColor(resources.getColor(R.color.white))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // adding back arrow in toolbar

        progressDialog = ProgressDialog(this)
        email = findViewById(R.id.email_log_in)
        password = findViewById(R.id.password_log_in)
        logInBtn = findViewById(R.id.logInBtnSignIn)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    // function for login button pressed
    fun logInBtnPressed(v: View){
        val emailText = email?.text.toString()
        val passwordText = password?.text.toString()
        if (TextUtils.isEmpty(emailText)) email?.error = "Field Empty" // check if email field is empty
        if (TextUtils.isEmpty(passwordText)) password?.error = "Field Empty" // check if password is empty
        if (!TextUtils.isEmpty(emailText) && !TextUtils.isEmpty(passwordText)){
            progressDialog?.setMessage("Please Wait.")
            progressDialog?.show()
            firebaseAuth?.signInWithEmailAndPassword(emailText,passwordText)?.addOnCompleteListener {
                if (it.isSuccessful){
                    // user successfully logged in
                    progressDialog?.dismiss()
                    val i = Intent(this, AllChatsActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                    this.finish()
                }else{
                    // error in logging in
                    progressDialog?.dismiss()
                    showToast(it.exception?.message.toString()) // show an error
                }
            }
        }
    }

    // function to show a toast
    private fun showToast(message: String){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }

}
