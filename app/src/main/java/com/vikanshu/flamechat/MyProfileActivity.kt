package com.vikanshu.flamechat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_my_profile.*

class MyProfileActivity : AppCompatActivity() {

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
    }
}
