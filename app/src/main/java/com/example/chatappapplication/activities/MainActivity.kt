package com.example.chatappapplication.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatappapplication.adapters.RecentConvertionAdapter
import com.example.chatappapplication.databinding.ActivityMainBinding
import com.example.chatappapplication.listener.ConversionListener
import com.example.chatappapplication.models.ChatMessage
import com.example.chatappapplication.models.User
import com.example.chatappapplication.utilities.Constants
import com.example.chatappapplication.utilities.PreferenceManager
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), ConversionListener{
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var conversations : MutableList<ChatMessage>
    private lateinit var recentConversationAdapter : RecentConvertionAdapter
    private lateinit var database : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        init()
        loadUserDetail()
        getToken()
        setListeners()
        listenConversation()
    }

    private fun init(){
        conversations = mutableListOf()
        recentConversationAdapter = RecentConvertionAdapter(conversations,this)
        binding.conversationRecyclerView.adapter = recentConversationAdapter
        database = Firebase.firestore

    }

    private fun setListeners(){
        binding.imageSignOut.setOnClickListener{
            signOut()
        }
        binding.fabNewChat.setOnClickListener{
            startActivity(Intent(applicationContext,UsersActivity::class.java))
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

    private fun listenConversation(){
        database.collection(Constants.KEY_COLLECTION_CONVERSATION)
            .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CONVERSATION)
            .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }

    private final var eventListener : EventListener<QuerySnapshot> =
        EventListener { value: QuerySnapshot?, error ->
            if(error != null){
                return@EventListener
            }
            if(value != null){
                for (document : DocumentChange  in value.documentChanges){
                    if(document.type == DocumentChange.Type.ADDED){
                        var senderId : String = document.document.getString(Constants.KEY_SENDER_ID).toString()
                        var receiverId : String = document.document.getString(Constants.KEY_RECEIVER_ID).toString()
                        val chatMessage : ChatMessage = ChatMessage()
                        chatMessage.senderId = senderId
                        chatMessage.receiverId = receiverId
                        if(preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)){
                            chatMessage.conversationImage = document.document.getString(Constants.KEY_RECEIVER_IMAGE).toString()
                            chatMessage.conversationName = document.document.getString(Constants.KEY_RECEIVER_NAME).toString()
                            chatMessage.conversationId = document.document.getString(Constants.KEY_RECEIVER_ID).toString()

                        }else{
                            chatMessage.conversationImage = document.document.getString(Constants.KEY_SENDER_IMAGE).toString()
                            chatMessage.conversationName = document.document.getString(Constants.KEY_SENDER_NAME).toString()
                            chatMessage.conversationId = document.document.getString(Constants.KEY_SENDER_ID).toString()
                        }
                        chatMessage.message = document.document.getString(Constants.KEY_LAST_MESSAGE).toString()
                        chatMessage.dateObject = document.document.getDate(Constants.KEY_TIMESTAMP)
                        conversations.add(chatMessage)

                    }else if(document.type == DocumentChange.Type.MODIFIED) {

                        for (i in 0 until  conversations.size){
                            val senderId = document.document.getString(Constants.KEY_SENDER_ID).toString()
                            val receiverId = document.document.getString(Constants.KEY_RECEIVER_ID).toString()
                            if(conversations.get(i).senderId.equals(senderId) &&
                                    conversations.get(i).receiverId.equals(receiverId)){
                                conversations.get(i).message = document.document.getString(Constants.KEY_LAST_MESSAGE).toString()
                                conversations.get(i).dateObject = document.document.getDate(Constants.KEY_TIMESTAMP)
                                break

                            }
                        }
                    }
                }
                conversations.sortWith(Comparator { obj1, obj2 -> obj1.dateObject!!.compareTo(obj2.dateObject) })
                recentConversationAdapter.notifyDataSetChanged()
                binding.conversationRecyclerView.smoothScrollToPosition(0)
                binding.conversationRecyclerView.visibility = View.VISIBLE
                binding.processBar.visibility = View.GONE
            }
        }


    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener(this::updateToken)
    }

    private fun updateToken(token: String) {
        val database = Firebase.firestore
        val documentReference =
            database.collection(Constants.KEY_COLLECTION_USER).document(preferenceManager.getString(Constants.KEY_USER_ID)
                .toString())

        documentReference.update(Constants.KEY_FCM_TOKEN, token).addOnFailureListener {
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

    override fun onConversionClicked(user: User) {
        val intent : Intent = Intent(applicationContext,ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER,user)
        startActivity(intent)
    }


}