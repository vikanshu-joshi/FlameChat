package com.vikanshu.flamechat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_friend.*

class AddFriendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)
        overridePendingTransition(0,0)
        bottom_navigation?.selectedItemId = R.id.add_friend
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
                R.id.requests -> {
                    startActivity(Intent(this,RequestsActivity::class.java))
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
