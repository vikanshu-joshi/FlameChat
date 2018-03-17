package com.vikanshu.flamechat

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

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
    private var msgEditText: EditText ?= null
    private var message: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setName = findViewById(R.id.nameChatScreen)
        setImage = findViewById(R.id.userImageChatScreen)
        setOnlineStatus = findViewById(R.id.onlineStatusChatScreen)
        onlineLight = findViewById(R.id.onlineLightChatScreen)
        msgEditText = findViewById(R.id.messageChatScreen)

        val extras = intent.extras
        uid = extras["uid"].toString()
        username = extras["username"].toString()
        image = extras["image"].toString()
        myUid = FirebaseAuth.getInstance().uid.toString()
        database = FirebaseDatabase.getInstance().reference.child("MESSAGES")
        onlineStatus = FirebaseDatabase.getInstance().reference.child("ONLINE_STATUS").child(uid)
        onlineStatus?.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                setOnlineStatus?.text = "offline"
            }
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

    fun sendMessageBtn(v: View){
        message = msgEditText?.text.toString()
        if (message != null && message != ""){
            val msgMap = HashMap<String,Any>()
            msgMap["from"] = "him"
            msgMap["time"] = ServerValue.TIMESTAMP
            msgMap["text"] = message as Any
            val msgId = ('a'..'z').randomString(5)
            database?.child(uid)?.child(myUid)?.child(msgId)?.setValue(msgMap)?.addOnCompleteListener {
                if (it.isSuccessful){
                    msgMap["from"] = "me"
                    database?.child(myUid)?.child(uid)?.child(msgId)?.setValue(msgMap)?.addOnFailureListener {
                        Toast.makeText(this,"Unable to send message",Toast.LENGTH_LONG).show()
                    }
                    msgEditText?.text?.clear()
                }else{
                    Toast.makeText(this,"Unable to send message",Toast.LENGTH_LONG).show()
                }
            }


        }
    }

    private fun ClosedRange<Char>.randomString(length: Int) =
            (1..length).map { (Random().nextInt(endInclusive.toInt() - start.toInt()) + start.toInt()).toChar() }
                    .joinToString("")

}
