package com.vikanshu.flamechat

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatActivity : AppCompatActivity() {

    private var username = ""
    private var image = ""
    private var uid = ""
    private var setName: TextView ?= null
    private var setOnlineStatus: TextView ?= null
    private var setImage: CircleImageView ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val extras = intent.extras
        uid = extras["uid"].toString()
        username = extras["username"].toString()
        image = extras["image"].toString()

        setName = findViewById(R.id.nameChatScreen)
        setImage = findViewById(R.id.userImageChatScreen)
        setOnlineStatus = findViewById(R.id.onlineStatusChatScreen)

        setName?.text = username
        if (image == "default") setImage?.setImageResource(R.drawable.default_avatar)
        else Picasso.with(this@ChatActivity).load(Uri.parse(image)).placeholder(R.drawable.loading).into(setImage)


    }


    fun backTop(v: View){
        this.finish()
        super.onBackPressed()
    }

}
