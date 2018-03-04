package com.vikanshu.flamechat

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_my_profile.*

class MyProfileActivity : AppCompatActivity() {

    private var profileImage: CircleImageView ?= null
    private var username: TextView ?= null
    private var status: TextView ?= null
    private var tick: ImageView?= null
    private var firebaseAuth: FirebaseAuth ?= null
    private var firebaseUser: FirebaseUser ?= null
    private var firebaseDatabase: DatabaseReference ?= null
    private var firebaseStorage: StorageReference ?= null
    private var alertBox: AlertDialog.Builder ?= null
    private var progressDialog: ProgressDialog ?= null
    private val databaseListener = object : ValueEventListener{
        override fun onDataChange(p0: DataSnapshot?) {
            username?.text = p0?.child("username")?.value.toString()
            status?.text = p0?.child("status")?.value.toString()
            val image = p0?.child("image")?.value.toString()
            if (image == "default") profileImage?.setImageResource(R.drawable.default_avatar)
            else Picasso.with(this@MyProfileActivity).load(Uri.parse(image))
                    .placeholder(R.drawable.default_avatar).into(profileImage)
        }
        override fun onCancelled(p0: DatabaseError?) {
            // in case we are unable to fetch data then warn user that saved values are loaded
            showToast("Network error. Showing offline saved values")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        overridePendingTransition(0,0)
        bottom_navigation?.selectedItemId = R.id.profile
        bottom_navigation?.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.friends -> {
                    startActivity(Intent(this,FriendsActivity::class.java))
                    this.finish()
                    true
                }
                R.id.chats -> {
                    startActivity(Intent(this,AllChatsActivity::class.java))
                    this.finish()
                    true
                }
                R.id.add_friend -> {
                    startActivity(Intent(this,AddFriendActivity::class.java))
                    this.finish()
                    true
                }
                R.id.requests -> {
                    startActivity(Intent(this,RequestsActivity::class.java))
                    this.finish()
                    true
                }
                else -> {false}
            }
        }

        profileImage = findViewById(R.id.profileImageMyProfile)
        username = findViewById(R.id.usernameMyProfile)
        status = findViewById(R.id.statusMyProfile)
        tick = findViewById(R.id.tickImage)
        progressDialog = ProgressDialog(this)
        progressDialog?.setCancelable(false)
        alertBox = AlertDialog.Builder(this)

        // firebase variables
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth?.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance().reference.child("USERS")?.child(firebaseUser?.uid)
        firebaseStorage = FirebaseStorage.getInstance().reference.child("profile_images")
        firebaseDatabase?.addValueEventListener(databaseListener)
    }

    // function for change status pressed
    fun changeStatus(v: View){

    }

    override fun onPause() {
        firebaseDatabase?.removeEventListener(databaseListener)
        super.onPause()
    }

    // function to show a toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}
