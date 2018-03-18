package com.vikanshu.flamechat

class UsersData{


    private var mName: String = ""
    private var mStatus: String = ""
    private var mImage: String = ""


    constructor(){}

    constructor(username: String,status: String,image: String){
        this.mName = username
        this.mStatus = status
        this.mImage = image
    }

    fun getusername(): String {return this.mName}
    fun getstatus(): String {return this.mStatus}
    fun getimage(): String {return this.mImage}

    fun setusername(name: String){ this.mName = name }
    fun setstatus(status: String){ this.mStatus = status }
    fun setimage(image: String){ this.mImage = image }

}