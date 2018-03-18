package com.vikanshu.flamechat

class ChatData {

    
    private var mtext = ""
    private var mFrom = ""
    private var mTime: Long = 0


    constructor(){}

    constructor(text: String,from: String,time: Long){
        this.mtext = text
        this.mFrom = from
        this.mTime = time
    }

    fun gettext(): String {return this.mtext}
    fun getfrom(): String {return this.mFrom}
    fun gettime(): Long {return this.mTime}

    fun settext(text: String){ this.mtext = text }
    fun setfrom(from: String){ this.mFrom = from }
    fun settime(time: Long){ this.mTime = time }
    
}