package com.vikanshu.flamechat.BottomNavigationActivities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.vikanshu.flamechat.AllUsersData
import com.vikanshu.flamechat.BottomNavigationActivities.AllChatsActivity.Static.allUsers
import com.vikanshu.flamechat.BottomNavigationActivities.AllChatsActivity.Static.friendsList
import com.vikanshu.flamechat.R
import com.vikanshu.flamechat.UserProfileActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_requests.*

class RequestsActivity : AppCompatActivity() {

    private var requests = ArrayList<String>()
    private var final_requests = ArrayList<AllUsersData>()
    private var req_list: ListView ?= null
    private var adapter: AdapterSearch ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests)
        overridePendingTransition(0, 0)
        bottom_navigation?.selectedItemId = R.id.requests
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
                R.id.add_friend -> {
                    startActivity(Intent(this, AddFriendActivity::class.java))
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

        final_requests.clear()
        requests.clear()

        for (i in friendsList) {
            if (i.type == "R") {
                requests.add(i.uid)
            }
        }
        for (i in requests){
            for (j in allUsers){
                if (i == j.uid) {
                    final_requests.add(j)
                    adapter?.notifyDataSetChanged()
                }
            }
        }
        req_list = findViewById(R.id.requests_list)
        req_list?.divider = null
        adapter = AdapterSearch(this,final_requests)
        req_list?.adapter = adapter
        if (final_requests.isEmpty()){
            req_list?.visibility = View.INVISIBLE
            no_req?.visibility = View.VISIBLE
        }
        else{
            req_list?.visibility = View.VISIBLE
            no_req?.visibility = View.INVISIBLE
            req_list?.setOnItemClickListener { parent, view, position, id ->
                val intent = Intent(this,UserProfileActivity::class.java)
                val bundle = Bundle()
                bundle.putString("username",final_requests[position].name)
                bundle.putString("image",final_requests[position].image)
                bundle.putString("status",final_requests[position].status)
                bundle.putString("uid",final_requests[position].uid)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

    private class AdapterSearch(val context: Context, private val list: ArrayList<AllUsersData>): BaseAdapter(){

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val itemView: View
            var holder = AddViewHolder()
            if (convertView == null){
                itemView = LayoutInflater.from(context).inflate(R.layout.single_user_list,null)
                holder.image = itemView.findViewById(R.id.image_single_user_view)
                holder.name = itemView.findViewById(R.id.username_single_user_view)
                holder.status = itemView.findViewById(R.id.status_single_user_view)
                itemView.tag = holder
            }else{
                holder = convertView.tag as AddViewHolder
                itemView = convertView
            }
            val category = list[position]
            if (category.image == "default") holder.image?.setImageResource(R.drawable.default_avatar)
            else Picasso.with(context).load(Uri.parse(category.image)).placeholder(R.drawable.loading).into(holder.image)
            holder.name?.text = category.name
            holder.status?.text = category.name
            return itemView
        }

        override fun getItem(position: Int): Any {
            return list[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return list.count()
        }

        private class AddViewHolder{
            var image: CircleImageView?= null
            var name: TextView?= null
            var status: TextView ?= null
        }
    }

}
