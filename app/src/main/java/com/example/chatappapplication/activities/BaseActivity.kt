package com.example.chatappapplication.activities


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chatappapplication.utilities.Constants
import com.example.chatappapplication.utilities.PreferenceManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

open class BaseActivity : AppCompatActivity(){

    private lateinit var documentRefe : DocumentReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferenceManager : PreferenceManager = PreferenceManager(applicationContext)
        val database : FirebaseFirestore = Firebase.firestore
        documentRefe = database.collection(Constants.KEY_COLLECTION_USER)
            .document(preferenceManager.getString(Constants.KEY_USER_ID).toString())
    }

    override fun onPause() {
        super.onPause()
        documentRefe.update(Constants.KEY_AVAILABILITY,0)
    }

    override fun onResume() {
        super.onResume()
        documentRefe.update(Constants.KEY_AVAILABILITY,1)
    }


}