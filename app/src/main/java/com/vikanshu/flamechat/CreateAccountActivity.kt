package com.vikanshu.flamechat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar

class CreateAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        // set toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.title = "Create an Account"
        toolbar?.setTitleTextColor(resources.getColor(R.color.white))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // adding back arrow in toolbar
    }
}
