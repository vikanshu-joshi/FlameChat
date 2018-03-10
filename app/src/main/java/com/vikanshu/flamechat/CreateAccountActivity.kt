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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.rengwuxian.materialedittext.MaterialEditText
import com.vikanshu.flamechat.BottomNavigationActivities.AllChatsActivity

class CreateAccountActivity : AppCompatActivity() {

    private var username: MaterialEditText ?= null
    private var email: MaterialEditText ?= null
    private var password: MaterialEditText ?= null
    private var createBtn: Button ?= null
    private var firebaseAuth: FirebaseAuth ?= null
    private var firebaseDatabase: DatabaseReference ?= null
    private var progressDialog: ProgressDialog ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        // set toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.title = "Create an Account"
        toolbar?.setTitleTextColor(resources.getColor(R.color.white))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // adding back arrow in toolbar

        username = findViewById(R.id.username_create_account)
        email = findViewById(R.id.email_create_account)
        password = findViewById(R.id.password_create_account)
        createBtn = findViewById(R.id.createBtnCreateAccount)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance().reference
        progressDialog = ProgressDialog(this)
        progressDialog?.setCancelable(false)
    }

    // function for create account button pressed
    fun createBtnPressed(v: View){
        val usernameText = username?.text.toString()
        val emailText = email?.text.toString()
        val passwordText = password?.text.toString()
        if (TextUtils.isEmpty(usernameText)) username?.error = "Field Empty" // if username field empty
        if (TextUtils.isEmpty(passwordText)) password?.error = "Field Empty" // if password field empty
        if (TextUtils.isEmpty(emailText)) email?.error = "Field Empty" // if email field empty
        if (!TextUtils.isEmpty(usernameText) && !TextUtils.isEmpty(passwordText) && !TextUtils.isEmpty(emailText)){
            progressDialog?.setMessage("Please wait while we do the work for you.")
            progressDialog?.show()
            createAccount(usernameText,emailText,passwordText)
        }

    }

    // function to create an account
    private fun createAccount(name: String,mail: String,pass: String){
        firebaseAuth?.createUserWithEmailAndPassword(mail,pass)?.addOnCompleteListener {
            if (it.isSuccessful){
                // user created
                // now save data in database
                val usermap = HashMap<String,String>()
                val token = FirebaseInstanceId.getInstance().token
                usermap["username"] = name;usermap["email"] = mail;usermap["password"] = pass
                usermap["image"] = "default";usermap["status"] = "I am new to FlameChat"
                usermap["token"] = token.toString()
                firebaseDatabase?.child("USERS")?.child(firebaseAuth?.uid)?.setValue(usermap)?.addOnCompleteListener {
                    if (it.isSuccessful){
                        // successful in saving data
                        progressDialog?.dismiss()
                        val i = Intent(this, AllChatsActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                        this.finish()
                    }else{
                        // unsuccessful in saving data
                        // now delete the created user
                        progressDialog?.dismiss()
                        firebaseAuth?.currentUser?.delete()
                    }
                }
            }else{
                // error in creating account
                progressDialog?.dismiss()
                showToast(it.exception?.message.toString())
            }
        }
    }

    // function to show a toast
    private fun showToast(message: String){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }
}
