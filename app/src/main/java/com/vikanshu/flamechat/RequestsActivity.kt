package com.vikanshu.flamechat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_requests.*

class RequestsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests)
        overridePendingTransition(0,0)
        bottom_navigation?.selectedItemId = R.id.requests
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
                R.id.profile -> {
                    startActivity(Intent(this,MyProfileActivity::class.java))
                    this.finish()
                    true
                }
                else -> {false}
            }
        }
    }
}
