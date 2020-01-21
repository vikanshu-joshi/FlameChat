package com.vikanshu.flamechat.FirebaseFiles

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService


class MyFirebaseInstanceIDService: FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        FirebaseDatabase.getInstance().reference.child("USERS").child(FirebaseAuth.getInstance().uid)
                .child("token").setValue(FirebaseInstanceId.getInstance().token)
    }

}