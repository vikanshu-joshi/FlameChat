package com.vikanshu.flamechat

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.squareup.picasso.Picasso
import com.vikanshu.flamechat.R.id.image_single_user_view
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_add_friend.*
import kotlinx.android.synthetic.main.single_user_list.view.*
import android.view.LayoutInflater
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class AddFriendActivity : AppCompatActivity() {

    private val query = FirebaseDatabase.getInstance()
            .reference.child("USERS").limitToLast(Int.MAX_VALUE)
    val options = FirebaseRecyclerOptions.Builder<UsersData>().setQuery(query,UsersData::class.java).build()
    private val adapter = object : FirebaseRecyclerAdapter<UsersData, usersListViewHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): usersListViewHolder {
            // Create a new instance of the ViewHolder, in this case we are using a custom
            // layout called R.layout.message for each item
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.single_user_list, parent, false)

            return usersListViewHolder(view,this@AddFriendActivity)
        }

        override fun onBindViewHolder(holder: usersListViewHolder, position: Int, model: UsersData) {

            holder.setName(model.getusername())
            holder.setStatus(model.getstatus())
            holder.setImage(model.getimage())

        }
    }

    private var usersList: RecyclerView? = null
    private var firebaseDatabase: DatabaseReference ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.title = "All Users"
        toolbar?.setTitleTextColor(resources.getColor(R.color.white))
        setSupportActionBar(toolbar)
        overridePendingTransition(0, 0)
        bottom_navigation?.selectedItemId = R.id.add_friend
        bottom_navigation?.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.friends -> {
                    startActivity(Intent(this, FriendsActivity::class.java))
                    this.finish()
                    true
                }
                R.id.chats -> {
                    startActivity(Intent(this, AllChatsActivity::class.java))
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
                else -> {
                    false
                }
            }
        }
        firebaseDatabase = FirebaseDatabase.getInstance().reference.child("USERS").child(FirebaseAuth.getInstance().uid)
        usersList = findViewById(R.id.allUsersRecylerView)
        usersList?.setHasFixedSize(true)
        usersList?.layoutManager = LinearLayoutManager(this)
        usersList?.adapter = adapter

    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }
    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
    class usersListViewHolder(itemView: View, var context: Context) : RecyclerView.ViewHolder(itemView) {
        private val imageView = itemView.findViewById<CircleImageView>(image_single_user_view)
        fun setName(name: String) {
            itemView.username_single_user_view.text = name
        }

        fun setStatus(status: String) {
            itemView.status_single_user_view.text = status
        }

        fun setImage(image: String) {
            if (image == "default") imageView.setImageResource(R.drawable.default_avatar)
            else Picasso.with(context).load(Uri.parse(image)).placeholder(R.drawable.loading).into(imageView)
        }

    }
}
