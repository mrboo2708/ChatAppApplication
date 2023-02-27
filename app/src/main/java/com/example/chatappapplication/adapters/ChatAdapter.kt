package com.example.chatappapplication.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappapplication.databinding.ItemContainerReceivedMessageBinding
import com.example.chatappapplication.databinding.ItemContainerSentMessageBinding
import com.example.chatappapplication.databinding.ItemContainerUserBinding
import com.example.chatappapplication.models.ChatMessage
import com.example.chatappapplication.models.MessageType
import com.example.chatappapplication.models.User

class TestException(message:String): Exception(message)

class ChatAdapter(
     val chatMessage: List<ChatMessage>,  val imageProfile: Bitmap,
     val senderId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val VIEW_TYPE_SENT = 1
    val VIEW_TYPE_RECEIVED = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (val item = viewType) {
            VIEW_TYPE_SENT -> SentMessageViewHolder(
                ItemContainerSentMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
            )
            VIEW_TYPE_RECEIVED -> ReceivedMessageViewHolder(
                ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            else -> {
                throw TestException("Error")
            }
        }
    }

    override fun getItemCount(): Int {
        return chatMessage.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(getItemViewType(position)== VIEW_TYPE_SENT){
             (holder as SentMessageViewHolder).bind(chatMessage.get(position))
        }
        else {
            (holder as ReceivedMessageViewHolder).bind(chatMessage.get(position),imageProfile)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (chatMessage.get(position).senderId.equals(senderId)) {
            return VIEW_TYPE_SENT
        } else {
            return VIEW_TYPE_RECEIVED
        }
    }


    inner class SentMessageViewHolder(private val itemBinding: ItemContainerSentMessageBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {


        fun bind(chatMessage: ChatMessage) {
            itemBinding.textMessage.text = chatMessage.message
            itemBinding.textDateTime.text = chatMessage.dateTime

        }

    }

    inner class ReceivedMessageViewHolder(private val itemBinding: ItemContainerReceivedMessageBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(chatMessage: ChatMessage, receiverProfileImage: Bitmap) {
            itemBinding.textMessage.text = chatMessage.message
            itemBinding.textDateTime.text = chatMessage.dateTime
            itemBinding.imageProfile.setImageBitmap(receiverProfileImage)

        }

    }


}