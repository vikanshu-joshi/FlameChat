package com.vikanshu.flamechat

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.vikanshu.flamechat.BottomNavigationActivities.AllChatsActivity.Static.allUsers
import com.vikanshu.flamechat.BottomNavigationActivities.AllChatsActivity.Static.friendsList

class UserProfileActivity : AppCompatActivity() {

    private lateinit var mUsername: String
    private lateinit var mStatus: String
    private lateinit var mImage: String
    private lateinit var mUid: String
    private var frndStatus = "N"
    private var sendReqBtn: ConstraintLayout? = null
    private var usernameTextView: TextView? = null
    private var statusTextView: TextView? = null
    private var profileImageView: ImageView? = null
    private var frndStatusText: TextView ?= null
    private var frndStatusImage: ImageView ?= null
    private var firebaseDatabse: DatabaseReference ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        val extras = intent.extras
        mUsername = extras["username"].toString()
        mStatus = extras["status"].toString()
        mImage = extras["image"].toString()
        mUid = extras["uid"].toString()



        firebaseDatabse = FirebaseDatabase.getInstance().reference.child("FRIENDS")

        usernameTextView = findViewById(R.id.usernameUserProfile)
        statusTextView = findViewById(R.id.statusUserProfile)
        profileImageView = findViewById(R.id.imageUserProfile)
        sendReqBtn = findViewById(R.id.sendRequestButton)
        frndStatusText = findViewById(R.id.frndStatusTextView)
        frndStatusImage = findViewById(R.id.frndStatusImageView)

        for (i in friendsList){
            if (i.uid == mUid && i.type == "S")
                frndStatus = "S"
            else if (i.uid == mUid && i.type == "R")
                frndStatus = "R"
            else if (i.uid == mUid && i.type == "F")
                frndStatus = "F"
        }

        setFriendshipStatus()

        usernameTextView?.text = mUsername
        statusTextView?.text = mStatus
        if (mImage != "default")
            Picasso.with(this).load(Uri.parse(mImage)).placeholder(R.drawable.loading).into(profileImageView)
        else
            profileImageView?.setImageResource(R.drawable.default_avatar)
    }

    //this function checks whether there is Internet Access or not
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    // function for send req clicked
    fun sendReq(v: View){
        if (frndStatus == "N" && mUid != FirebaseAuth.getInstance().uid){
            firebaseDatabse?.child(FirebaseAuth.getInstance().uid)?.child(mUid)?.setValue("S")?.addOnCompleteListener {
                if (it.isSuccessful){
                    firebaseDatabse?.child(mUid)?.child(FirebaseAuth.getInstance().uid)?.setValue("R")?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            frndStatus = "S"
                            showToast("Request Sent")
                            setFriendshipStatus()
                        }else{
                            showToast(it.exception?.message.toString())
                        }
                    }
                }else{
                    showToast(it.exception?.message.toString())
                }
            }
        }
        else if (frndStatus == "S") showToast("Request already sent")
        else if (frndStatus == "R") showToast("Request received")
        else if (frndStatus == "F") showToast("Already Friends")
    }

    // function to set the request button
    private fun setFriendshipStatus(){
        if (mUid == FirebaseAuth.getInstance().uid){
            sendReqBtn?.visibility = View.INVISIBLE
            sendReqBtn?.isClickable = false
        }
        when (frndStatus) {
            "R" -> {
                frndStatusImage?.setImageResource(R.drawable.accept)
                frndStatusText?.text = "Accept Request"
                sendReqBtn?.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            }
            "S" -> {
                frndStatusImage?.setImageResource(R.drawable.request_sent)
                frndStatusText?.text = "Request Sent"
                sendReqBtn?.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            }
            "F" -> {
                frndStatusImage?.setImageResource(R.drawable.friends_icon)
                frndStatusText?.text = "Friends"
                sendReqBtn?.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
            }
            "N" -> {
                frndStatusImage?.setImageResource(R.drawable.send_request)
                frndStatusText?.text = "Send Request"
                sendReqBtn?.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            }
        }
    }

    // function to show a toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
