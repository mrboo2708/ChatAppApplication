package com.example.chatappapplication.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.chatappapplication.adapters.ChatAdapter
import com.example.chatappapplication.databinding.ActivityChatBinding
import com.example.chatappapplication.models.ChatMessage
import com.example.chatappapplication.models.User
import com.example.chatappapplication.utilities.Constants
import com.example.chatappapplication.utilities.PreferenceManager
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatBinding
    private lateinit var receiverUser : User
    private  var chatMessage :ArrayList<ChatMessage> = arrayListOf()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var preferenceManager : PreferenceManager
    private lateinit var database : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()
        loadReceiverDetails()
        init()
        listenMessages()

    }
    private fun init(){
        preferenceManager = PreferenceManager(applicationContext)
        chatAdapter = ChatAdapter(chatMessage,getBitMapFromEncodedString(receiverUser.image),
            preferenceManager.getString(Constants.KEY_USER_ID).toString()
        )
        binding.chatRecyclerView.adapter = chatAdapter
        database = Firebase.firestore
    }

    private fun sendMessage(){
        val message : HashMap<String, Any> = hashMapOf()
        message[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_USER_ID).toString()
        message[Constants.KEY_RECEIVER_ID] = receiverUser.id
        message[Constants.KEY_MESSAGE] = binding.inputMessage.text.toString()
        message[Constants.KEY_TIMESTAMP] = Date()
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message)
        binding.inputMessage.text = null
    }

    private fun getBitMapFromEncodedString(encodedImage : String) : Bitmap {
        val byte : ByteArray = Base64.decode(encodedImage,Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byte,0,byte.size)
    }


    private fun loadReceiverDetails(){
        receiverUser = intent.getSerializableExtra(Constants.KEY_USER) as User
        binding.textName.text = receiverUser.name


    }

    private fun listenMessages(){
        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
            .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverUser.id)
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID,receiverUser.id)
            .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }

    private final var eventListener : EventListener<QuerySnapshot> =
            EventListener { value: QuerySnapshot?, error ->
            if (error != null) {
                return@EventListener
            }
            if (value != null) {
                val count :Int = chatMessage.size
                Log.d("error", count.toString())
                for(document: DocumentChange in value.documentChanges){
                    if(document.type == DocumentChange.Type.ADDED){
                        val chatMessages = ChatMessage()
                        chatMessages.senderId = document.document.getString(Constants.KEY_SENDER_ID).toString()
                        chatMessages.receiverId = document.document.getString(Constants.KEY_RECEIVER_ID).toString()
                        chatMessages.message = document.document.getString(Constants.KEY_MESSAGE).toString()
                        chatMessages.dateTime = getReadableDateTime(document.document.getDate(Constants.KEY_TIMESTAMP)!!)
                        chatMessages.dateObject = document.document.getDate(Constants.KEY_TIMESTAMP)
                        chatMessage.add(chatMessages)
                    }

                }
                chatMessage.sortWith(Comparator { obj1, obj2 -> obj1.dateTime.compareTo(obj2.dateTime) })
                if(count == 0){
                    chatAdapter.notifyDataSetChanged()
                    Log.d("error", "notify")
                }
                else {
                    chatAdapter.notifyItemRangeInserted(chatMessage.size,chatMessage.size)
                    binding.chatRecyclerView.smoothScrollToPosition(chatMessage.size - 1)
                    Log.d("error", "smooth")
                }
                binding.chatRecyclerView.visibility = View.VISIBLE
            }
            binding.processBar.visibility = View.GONE
        }


    private fun setListener(){
        binding.imageBack.setOnClickListener{
            onBackPressed()
        }
        binding.layoutSend.setOnClickListener{
            sendMessage()
        }
    }

    private fun getReadableDateTime(date : Date): String{
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a",Locale.getDefault()).format(date)
    }
}