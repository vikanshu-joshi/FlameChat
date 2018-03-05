package com.vikanshu.flamechat.BottomNavigationActivities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.vikanshu.flamechat.R
import kotlinx.android.synthetic.main.activity_all_chats.*

class AllChatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_chats)
        overridePendingTransition(0,0)
        bottom_navigation?.selectedItemId = R.id.chats
        bottom_navigation?.setOnNavigationItemSelectedListener{
            when (it.itemId){
                R.id.friends -> {
                    startActivity(Intent(this, FriendsActivity::class.java))
                    this.finish()
                    true
                }
                R.id.add_friend -> {
                    startActivity(Intent(this, AddFriendActivity::class.java))
                    this.finish()
                    true
                }
                R.id.requests -> {
                    startActivity(Intent(this, RequestsActivity::class.java))
                    this.finish()
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, MyProfileActivity::class.java))
                    this.finish()
                    true
                }
                else -> {false}
            }
        }
    }
}
