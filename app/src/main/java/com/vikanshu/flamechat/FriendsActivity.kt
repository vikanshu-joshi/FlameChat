package com.vikanshu.flamechat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_friends.*

class FriendsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        overridePendingTransition(0,0)
        bottom_navigation?.selectedItemId = R.id.friends
        bottom_navigation?.setOnNavigationItemSelectedListener {
            when (it.itemId){
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
