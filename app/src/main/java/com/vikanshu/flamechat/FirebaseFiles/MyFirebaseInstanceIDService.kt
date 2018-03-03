package com.vikanshu.flamechat.FirebaseFiles

import com.google.firebase.iid.FirebaseInstanceIdService


class MyFirebaseInstanceIDService: FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
    }

}