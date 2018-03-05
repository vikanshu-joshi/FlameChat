package com.vikanshu.flamechat

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class UserProfileActivity : AppCompatActivity() {

    private lateinit var username: String
    private lateinit var status: String
    private lateinit var image: String
    private lateinit var uid: String
    private var usernameTextView: TextView ?= null
    private var statusTextView: TextView ?= null
    private var profileImageView: ImageView ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        val extras = intent.extras
        username = extras["username"].toString()
        status = extras["status"].toString()
        image = extras["image"].toString()
        uid = extras["uid"].toString()

        usernameTextView = findViewById(R.id.usernameUserProfile)
        statusTextView = findViewById(R.id.statusUserProfile)
        profileImageView = findViewById(R.id.imageUserProfile)

        usernameTextView?.text = username
        statusTextView?.text  = status
        if (image != "default")
            Picasso.with(this).load(Uri.parse(image)).placeholder(R.drawable.loading).into(profileImageView)
        else
            profileImageView?.setImageResource(R.drawable.default_avatar)
    }
}
