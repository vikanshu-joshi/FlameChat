package com.vikanshu.flamechat.BottomNavigationActivities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.jenzz.appstate.AppState
import com.jenzz.appstate.AppStateListener
import com.jenzz.appstate.AppStateMonitor
import com.vikanshu.flamechat.AllUsersData
import com.vikanshu.flamechat.BottomNavigationActivities.AllChatsActivity.Static.appStateMonitorListener
import com.vikanshu.flamechat.BottomNavigationActivities.AllChatsActivity.Static.friendsListener
import com.vikanshu.flamechat.BottomNavigationActivities.AllChatsActivity.Static.listener
import com.vikanshu.flamechat.FriendsData
import com.vikanshu.flamechat.R
import kotlinx.android.synthetic.main.activity_all_chats.*

class AllChatsActivity : AppCompatActivity() {

    object Static{
        var allUsers = ArrayList<AllUsersData>()
        var friendsList = ArrayList<FriendsData>()
        val friendsListener = object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                val uid = p0?.key.toString()
                val type = p0?.value.toString()
                for (i in friendsList)
                    if (i.uid == uid) i.type = type
            }
            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                val uid = p0?.key.toString()
                val type = p0?.value.toString()
                friendsList.add(FriendsData(uid,type))
            }
            override fun onChildRemoved(p0: DataSnapshot?) {}
        }
        val listener = object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
            }
            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {

            }
            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                val uid = p0?.key.toString()
                val usermap = p0?.value as HashMap<String,String>
                allUsers.add(AllUsersData(usermap["username"].toString(),usermap["email"].toString(),usermap["status"].toString(),
                        usermap["image"].toString(),usermap["password"].toString(),uid))
            }
            override fun onChildRemoved(p0: DataSnapshot?) {
            }
        }
        val appStateMonitorListener = object : AppStateListener {
            override fun onAppDidEnterForeground() {
                FirebaseDatabase.getInstance().reference.child("USERS").child(FirebaseAuth.getInstance().uid)
                        .child("online").setValue(true)
            }

            override fun onAppDidEnterBackground() {
                FirebaseDatabase.getInstance().reference.child("USERS").child(FirebaseAuth.getInstance().uid)
                        .child("online").setValue(false)
            }
        }
    }

    private var appState: AppStateMonitor ?= null

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
        appState = AppStateMonitor.create(application)
        appState?.addListener(appStateMonitorListener)
        appState?.start()
    }
    override fun onStart() {
        super.onStart()
        val usersDataFirebase = FirebaseDatabase.getInstance().reference.child("USERS")
        val friendsDataFirebase = FirebaseDatabase.getInstance().reference
                .child("FRIENDS")?.child(FirebaseAuth.getInstance().uid)
        usersDataFirebase.addChildEventListener(listener)
        friendsDataFirebase?.addChildEventListener(friendsListener)
    }
}
