package com.vikanshu.flamechat

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private var username = ""
    private var image = ""
    private var uid = ""
    private var setName: TextView ?= null
    private var setOnlineStatus: TextView ?= null
    private var setImage: CircleImageView ?= null
    private var database: DatabaseReference ?= null
    private var onlineStatus: DatabaseReference ?= null
    private var onlineLight: ImageView?= null
    private var myUid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setName = findViewById(R.id.nameChatScreen)
        setImage = findViewById(R.id.userImageChatScreen)
        setOnlineStatus = findViewById(R.id.onlineStatusChatScreen)
        onlineLight = findViewById(R.id.onlineLightChatScreen)

        val extras = intent.extras
        uid = extras["uid"].toString()
        username = extras["username"].toString()
        image = extras["image"].toString()
        myUid = FirebaseAuth.getInstance().uid.toString()
        database = FirebaseDatabase.getInstance().reference.child("MESSAGES").child(myUid).child(uid)
        onlineStatus = FirebaseDatabase.getInstance().reference.child("ONLINE_STATUS").child(uid)
        onlineStatus?.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(p0: DataSnapshot?) {
                if (p0?.value == "online") { onlineLight?.setImageResource(R.drawable.ic_online);setOnlineStatus?.text = "online"}
                else {
                    onlineLight?.setImageResource(R.drawable.ic_offline)
                    val lastSeen = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                    setOnlineStatus?.text = "Last seen "+ lastSeen.format(Date(p0?.value as Long)).toString()
                }
            }
        })

        setName?.text = username
        if (image == "default") setImage?.setImageResource(R.drawable.default_avatar)
        else Picasso.with(this@ChatActivity).load(Uri.parse(image)).placeholder(R.drawable.loading).into(setImage)


    }


    fun backTop(v: View){
        this.finish()
        super.onBackPressed()
    }

    fun sendMessageBtn(v: View){}

}
