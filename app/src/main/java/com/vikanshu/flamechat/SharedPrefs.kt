package com.vikanshu.flamechat

import android.content.Context

class SharedPrefs(ctx: Context) {

    var prefs = ctx.getSharedPreferences("MY_PREFS",Context.MODE_PRIVATE)
    var myName = "name"
    var myImage = "image"
    var myStatus = "status"

    fun getName(): String {return  prefs.getString(myName,"")}
    fun getImage(): String {return  prefs.getString(myImage,"default")}
    fun getStatus(): String {return prefs.getString(myStatus,"I am new to FlameChat")}

    fun setData(list: ArrayList<String>){
        val editor = prefs.edit()
        editor.putString(myName,list[0])
        editor.putString(myImage,list[1])
        editor.putString(myStatus,list[2])
        editor.apply()
    }

    fun setName(value: String) {val editor = prefs.edit();editor.putString(myName,value);editor.apply()}
    fun setImage(value: String) {val editor = prefs.edit();editor.putString(myImage,value);editor.apply()}
    fun setStatus(value: String) {val editor = prefs.edit();editor.putString(myStatus,value);editor.apply()}

}