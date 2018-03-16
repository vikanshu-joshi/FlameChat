package com.vikanshu.flamechat.BottomNavigationActivities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_my_profile.*
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.vikanshu.flamechat.BottomNavigationActivities.AllChatsActivity.Static.friendsList
import com.vikanshu.flamechat.R
import com.vikanshu.flamechat.SplashActivity


class MyProfileActivity : AppCompatActivity() {

    private var profileImage: CircleImageView? = null
    private var username: TextView? = null
    private var status: TextView? = null
    private var tick: ImageView? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseUser: FirebaseUser? = null
    private var firebaseDatabase: DatabaseReference? = null
    private var firebaseStorage: StorageReference? = null
    private var alertBox: AlertDialog.Builder? = null
    private var progressDialog: ProgressDialog? = null
    private val databaseListener = object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot?) {
            username?.text = p0?.child("username")?.value.toString()
            status?.text = p0?.child("status")?.value.toString()
            val image = p0?.child("image")?.value.toString()
            if (image == "default") profileImage?.setImageResource(R.drawable.default_avatar)
            else Picasso.with(this@MyProfileActivity).load(Uri.parse(image))
                    .networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.loading)
                    .into(profileImage,object : Callback{
                        override fun onSuccess() {}
                        override fun onError() {
                            Picasso.with(this@MyProfileActivity).load(Uri.parse(image)).placeholder(R.drawable.loading)
                                    .into(profileImage,object : Callback{
                                        override fun onError() {
                                            profileImage?.setImageResource(R.drawable.default_avatar)
                                            showToast("error in loading profile image")
                                        }
                                        override fun onSuccess() {}
                                    })
                        }
                    })
        }

        override fun onCancelled(p0: DatabaseError?) {
            // in case we are unable to fetch data then warn user that saved values are loaded
            showToast("Network error. Showing offline saved values")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        overridePendingTransition(0, 0)
        bottom_navigation?.selectedItemId = R.id.profile
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
                R.id.requests -> {
                    startActivity(Intent(this, RequestsActivity::class.java))
                    this.finish()
                    true
                }
                else -> {
                    false
                }
            }
        }

        profileImage = findViewById(R.id.profileImageMyProfile)
        username = findViewById(R.id.usernameMyProfile)
        status = findViewById(R.id.statusMyProfile)
        tick = findViewById(R.id.tickImage)
        progressDialog = ProgressDialog(this)
        progressDialog?.setCancelable(false)
        alertBox = AlertDialog.Builder(this)
        // firebase variables
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth?.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance().reference.child("USERS")?.child(firebaseUser?.uid)
        firebaseDatabase?.keepSynced(true)
        firebaseStorage = FirebaseStorage.getInstance().reference.child("profile_images")
        firebaseDatabase?.addValueEventListener(databaseListener)
        firebaseUser?.reload()
        if (firebaseUser!!.isEmailVerified) tick?.setImageResource(R.drawable.green_tick)
        else tick?.setImageResource(R.drawable.red_tick)
    }

    // function for change status pressed
    fun changeStatus(v: View) {
        if (isNetworkAvailable()){
            val view = layoutInflater.inflate(R.layout.single_edittext_alert, null)
            val input = view.findViewById<EditText>(R.id.single_input)
            input.setText(status?.text)
            input.setSelection(input.length())
            alertBox?.setView(view)
            alertBox?.setMessage("Enter New Status")
            alertBox?.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            alertBox?.setPositiveButton("Update") { dialog, _ ->
                if (TextUtils.isEmpty(input.text.toString())) {
                    dialog.dismiss()
                } else {
                    progressDialog?.setMessage("Please Wait. Updating ......")
                    progressDialog?.show()
                    firebaseDatabase?.child("status")?.setValue(input?.text.toString())?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            dialog.dismiss()
                            progressDialog?.dismiss()
                            status?.text = input.text.toString()
                            showToast("Status updates successfully")
                        } else {
                            dialog.dismiss()
                            progressDialog?.dismiss()
                            showToast(it.exception?.message.toString())
                        }
                    }
                }
            }
            alertBox?.show()
        }else if(!isNetworkAvailable()){
            showToast("No Internet Connection")
        }
    }

    // function if tick image is clicked
    fun tickTouched(v: View) {
        if (firebaseUser!!.isEmailVerified) showToast("Your email is verified")
        else {
            showToast("Your email is not verified")
            val alert = AlertDialog.Builder(this)
            alert.setMessage("Send an email verification link ?")
            alert.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            alert.setPositiveButton("Send") { dialog, _ ->
                firebaseUser?.sendEmailVerification()?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast("verification link sent")
                    } else {
                        showToast("unable to send verification link")
                    }
                }
            }
            alert.show()
        }
    }

    // function for change profile image
    fun changeProfileImage(v: View) {
        if (firebaseUser!!.isEmailVerified) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "select image from"), 2102)
        } else {
            showToast("You need to verify your email first")
        }
    }

    // function to log out
    fun logout(v: View){
        FirebaseDatabase.getInstance().reference.child("ONLINE_STATUS").child(firebaseUser?.uid)
                ?.setValue(ServerValue.TIMESTAMP)
        firebaseAuth?.signOut()
        friendsList.clear()
        showToast("You have logged out successfully")
        startActivity(Intent(this, SplashActivity::class.java))
        this.finish()
    }

    override fun onPause() {
        firebaseDatabase?.removeEventListener(databaseListener)
        super.onPause()
    }

    // function to show a toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // function to update profile image
    private fun updateImage(imageUri: Uri){
        progressDialog?.setMessage("Please Wait. Updating Profile.")
        progressDialog?.show()
        if (isNetworkAvailable()) {
            firebaseStorage?.child(firebaseUser?.uid + ".jpeg")?.putFile(imageUri)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val downloadUri = it.result.downloadUrl
                    firebaseDatabase?.child("image")?.setValue(downloadUri.toString())?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            progressDialog?.dismiss()
                            Picasso.with(this@MyProfileActivity).load(downloadUri)
                                    .networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.loading)
                                    .into(profileImage,object : Callback{
                                        override fun onSuccess() {}
                                        override fun onError() {
                                            Picasso.with(this@MyProfileActivity).load(downloadUri).placeholder(R.drawable.loading)
                                                    .into(profileImage,object : Callback{
                                                        override fun onError() {
                                                            profileImage?.setImageResource(R.drawable.default_avatar)
                                                            showToast("error in loading profile image")
                                                        }
                                                        override fun onSuccess() {}
                                                    })
                                        }
                                    })
                            showToast("profile image updated successfully.")
                        } else {
                            progressDialog?.dismiss()
                            showToast(it.exception?.message.toString())
                        }
                    }
                } else {
                    progressDialog?.dismiss()
                    showToast(it.exception?.message.toString())
                }
            }
        } else {
            progressDialog?.dismiss()
            showToast("No Internet Connection")
        }
    }

    //this function checks whether there is Internet Access or not
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2102) {
            CropImage.activity(data?.data).setAspectRatio(1, 1)
                    .setCropShape(CropImageView.CropShape.OVAL).start(this@MyProfileActivity)
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                updateImage(resultUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                showToast(error.message.toString())
            }
        }
    }

}
