package com.vikanshu.flamechat.BottomNavigationActivities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jenzz.appstate.AppStateListener
import com.jenzz.appstate.AppStateMonitor
import com.vikanshu.flamechat.AllUsersData
import com.vikanshu.flamechat.BottomNavigationActivities.AllChatsActivity.Static.appStateMonitorListener
import com.vikanshu.flamechat.BottomNavigationActivities.AllChatsActivity.Static.friendsListener
import com.vikanshu.flamechat.BottomNavigationActivities.AllChatsActivity.Static.listener
import com.vikanshu.flamechat.FriendsData
import com.vikanshu.flamechat.R
import kotlinx.android.synthetic.main.activity_all_chats.*
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.squareup.picasso.Picasso
import com.vikanshu.flamechat.ChatActivity
import kotlinx.android.synthetic.main.chat_user_layout.view.*


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
                if (FirebaseAuth.getInstance().currentUser != null)
                    FirebaseDatabase.getInstance().reference.child("ONLINE_STATUS")
                            .child(FirebaseAuth.getInstance().uid).setValue("online")
            }
            override fun onAppDidEnterBackground() {
                if (FirebaseAuth.getInstance().currentUser != null)
                    FirebaseDatabase.getInstance().reference.child("ONLINE_STATUS")
                            .child(FirebaseAuth.getInstance().uid).setValue(ServerValue.TIMESTAMP)
            }
        }
    }
    private var appState: AppStateMonitor ?= null
    private var chatRecycler: RecyclerView ?= null
    val query = FirebaseDatabase.getInstance()
            .reference
            .child("CHATS")
            .child(FirebaseAuth.getInstance().currentUser?.uid)
            .limitToLast(Integer.MAX_VALUE)
    val options = FirebaseRecyclerOptions.Builder<Chat>()
            .setQuery(query, Chat::class.java)
            .build()
    val adapter = object : FirebaseRecyclerAdapter<Chat, ChatHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
            // Create a new instance of the ViewHolder, in this case we are using a custom
            // layout called R.layout.message for each item
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_user_layout, parent, false)
            return ChatHolder(view,this@AllChatsActivity)
        }

        override fun onBindViewHolder(holder: ChatHolder, position: Int, model: Chat) {
            holder.setName(model.getname())
            holder.setLast(model.getlast())
            holder.setImage(model.getimage())
            holder.content_holder?.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("username",model.getname())
                bundle.putString("image",model.getimage())
                bundle.putString("uid",model.getuid())
                val intent = Intent(this@AllChatsActivity,ChatActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }
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
        chatRecycler = findViewById(R.id.chats_recyler_view)
        chatRecycler?.setHasFixedSize(true)
        chatRecycler?.layoutManager = LinearLayoutManager(this@AllChatsActivity)
        chatRecycler?.adapter = adapter
    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
        val usersDataFirebase = FirebaseDatabase.getInstance().reference.child("USERS")
        val friendsDataFirebase = FirebaseDatabase.getInstance().reference
                .child("FRIENDS")?.child(FirebaseAuth.getInstance().uid)
        usersDataFirebase.addChildEventListener(listener)
        friendsDataFirebase?.addChildEventListener(friendsListener)
    }
    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
    class ChatHolder(itemView: View, var context: Context) : RecyclerView.ViewHolder(itemView) {
        private val imageView = itemView.findViewById<ImageView>(R.id.image_chat_view)
        val content_holder = itemView.findViewById<ConstraintLayout>(R.id.chat_main)
        fun setName(name: String) {
            itemView.name_chat_view.text = name
        }
        fun setLast(status: String) {
            itemView.last_chat_view.text = status
        }
        fun setImage(image: String) {
            if (image == "default") imageView.setImageResource(R.drawable.default_avatar)
            else Picasso.with(context).load(Uri.parse(image)).placeholder(R.drawable.loading).into(imageView)
        }
    }
    class Chat{
        private var name = ""
        private var image = ""
        private var last = ""
        private var uid = ""


        constructor(){}

        constructor(mname: String,mimage: String,mlast: String,muid: String){
            this.name = mname
            this.image = mimage
            this.last = mlast
            this.uid = muid
        }

        fun getname(): String {return this.name}
        fun getimage(): String {return this.image}
        fun getlast(): String {return this.last}
        fun getuid(): String {return this.uid}

        fun setname(mname: String){ this.name = mname }
        fun setimage(mimage: String){ this.image = mimage }
        fun setlast(mlast: String){ this.last = mlast }
        fun setuid(muid: String){ this.uid = muid }
    }
}
