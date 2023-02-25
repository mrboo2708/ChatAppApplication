package com.example.chatappapplication.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatappapplication.R
import com.example.chatappapplication.databinding.ActivityMainBinding
import com.example.chatappapplication.utilities.Constants
import com.example.chatappapplication.utilities.PreferenceManager
import android.util.Base64
import android.widget.Toast
import com.google.common.primitives.Bytes
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import java.lang.reflect.Field
import java.util.Objects

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        loadUserDetail()
        getToken()
        setListeners()
    }

    private fun setListeners(){
        binding.imageSignOut.setOnClickListener{
            signOut()
        }
    }

    private fun loadUserDetail() {
        showToast(preferenceManager.getString(Constants.KEY_NAME).toString())
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME).toString())
        var byte : ByteArray = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE).toString(), Base64.DEFAULT)
        var bitmap = BitmapFactory.decodeByteArray(byte, 0, byte.size)
        binding.imageProfile.setImageBitmap(bitmap)
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener(this::updateToken)
    }

    private fun updateToken(token: String) {
        val database = Firebase.firestore
        val documentReference =
            database.collection(Constants.KEY_COLLECTION_USER).document(preferenceManager.getString(Constants.KEY_USER_ID)
                .toString())

        documentReference.update(Constants.KEY_FCM_TOKEN, token).addOnSuccessListener {
            showToast("Token update successfully")
        }?.addOnFailureListener {
            showToast("Unable to update token")
        }
    }

    private fun signOut(){
        showToast("Signing out...")
        val database = Firebase.firestore
        val documentReference = database.collection(Constants.KEY_COLLECTION_USER).document(
            preferenceManager.getString(Constants.KEY_USER_ID).toString()
        )
        val update = HashMap<String,Any>()
        update.put(Constants.KEY_FCM_TOKEN,FieldValue.delete())
        documentReference.update(update)
            .addOnSuccessListener {
                preferenceManager.clear()
                startActivity(Intent(applicationContext,SignInActivity::class.java))
            }.addOnFailureListener{
                showToast("Unable to sign out")
            }
    }


}