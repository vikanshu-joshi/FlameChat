package com.vikanshu.flamechat

import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.chat_message_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {

    private var username = ""
    private var image = ""
    private var uid = ""
    private var setName: TextView? = null
    private var setOnlineStatus: TextView? = null
    private var setImage: CircleImageView? = null
    private var database: DatabaseReference? = null
    private var onlineStatus: DatabaseReference? = null
    private var onlineLight: ImageView? = null
    private var myUid = ""
    private var msgEditText: EditText? = null
    private var message: String? = null
    private var chats_list: RecyclerView? = null
    private var adapter: FirebaseRecyclerAdapter<ChatData, MessageListViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setName = findViewById(R.id.nameChatScreen)
        setImage = findViewById(R.id.userImageChatScreen)
        setOnlineStatus = findViewById(R.id.onlineStatusChatScreen)
        onlineLight = findViewById(R.id.onlineLightChatScreen)
        msgEditText = findViewById(R.id.messageChatScreen)
        chats_list = findViewById(R.id.messages_list)

        val extras = intent.extras
        uid = extras["uid"].toString()
        username = extras["username"].toString()
        image = extras["image"].toString()
        myUid = FirebaseAuth.getInstance().uid.toString()
        database = FirebaseDatabase.getInstance().reference.child("MESSAGES")
        onlineStatus = FirebaseDatabase.getInstance().reference.child("ONLINE_STATUS").child(uid)
        onlineStatus?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                setOnlineStatus?.text = "offline"
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0?.value == "online") {
                    onlineLight?.setImageResource(R.drawable.ic_online);setOnlineStatus?.text = "online"
                } else {
                    onlineLight?.setImageResource(R.drawable.ic_offline)
                    val lastSeen = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                    setOnlineStatus?.text = "Last seen " + lastSeen.format(Date(p0?.value as Long)).toString()
                }
            }
        })

        setName?.text = username
        if (image == "default") setImage?.setImageResource(R.drawable.default_avatar)
        else Picasso.with(this@ChatActivity).load(Uri.parse(image)).placeholder(R.drawable.loading).into(setImage)

        val query = FirebaseDatabase.getInstance().reference.child("MESSAGES")
                .child(myUid).child(uid).limitToLast(Int.MAX_VALUE).orderByChild("time")

        val options = FirebaseRecyclerOptions.Builder<ChatData>()
                .setQuery(query, ChatData::class.java).build()

        adapter = object : FirebaseRecyclerAdapter<ChatData, MessageListViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListViewHolder {

                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.chat_message_layout, parent, false)
                return MessageListViewHolder(view, this@ChatActivity)
            }

            override fun onBindViewHolder(holder: MessageListViewHolder, position: Int, model: ChatData) {
                if (model.getfrom() == "me") {
                    holder.setMyMsg(model.gettext())
                } else if (model.getfrom() == "him") {
                    holder.setHisMsg(model.gettext())
                }
            }
        }
        chats_list?.setHasFixedSize(true)
        val manager = LinearLayoutManager(this@ChatActivity)
        manager.stackFromEnd = true
        chats_list?.layoutManager = manager
        chats_list?.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

    fun backTop(v: View) {
        this.finish()
        super.onBackPressed()
    }

    fun sendMessageBtn(v: View) {
        message = msgEditText?.text.toString()
        if (message != null && message != "") {
            val msgMap = HashMap<String, Any>()
            msgMap["from"] = "him"
            msgMap["time"] = ServerValue.TIMESTAMP
            msgMap["text"] = message as Any
            val msgId = ('a'..'z').randomString(5)
            database?.child(uid)?.child(myUid)?.child(msgId)?.setValue(msgMap)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    msgMap["from"] = "me"
                    database?.child(myUid)?.child(uid)?.child(msgId)?.setValue(msgMap)?.addOnFailureListener {
                        Toast.makeText(this, "Unable to send message", Toast.LENGTH_LONG).show()
                    }
                    msgEditText?.text?.clear()
                } else {
                    Toast.makeText(this, "Unable to send message", Toast.LENGTH_LONG).show()
                }
            }


        }
    }

    private fun ClosedRange<Char>.randomString(length: Int) =
            (1..length).map { (Random().nextInt(endInclusive.toInt() - start.toInt()) + start.toInt()).toChar() }
                    .joinToString("")

    class MessageListViewHolder(itemView: View, var context: Context) : RecyclerView.ViewHolder(itemView) {

        fun setMyMsg(msg: String) {
            itemView?.message_me?.text = msg
            itemView?.message_me?.visibility = View.VISIBLE
            itemView?.message_him?.visibility = View.INVISIBLE

        }

        fun setHisMsg(msg: String) {
            itemView?.message_him?.text = msg
            itemView?.message_him?.visibility = View.VISIBLE
            itemView?.message_me?.visibility = View.INVISIBLE
        }


    }

}