package com.example.chatappapplication.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappapplication.databinding.ItemContainerRecentConversationBinding
import com.example.chatappapplication.listener.ConversionListener
import com.example.chatappapplication.models.ChatMessage
import com.example.chatappapplication.models.User

class RecentConvertionAdapter(val chatMessage: List<ChatMessage>,val conversionListener: ConversionListener) :
    RecyclerView.Adapter<RecentConvertionAdapter.ConversionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        return ConversionViewHolder(ItemContainerRecentConversationBinding.inflate(
            LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return chatMessage.size
    }

    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        holder.bind(chatMessage.get(position))
    }


    private fun getConversationImage(encodedImage: String): Bitmap {
        var bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return  BitmapFactory.decodeByteArray(bytes,0,bytes.size)
    }

    inner class ConversionViewHolder(private val itembinding : ItemContainerRecentConversationBinding) : RecyclerView.ViewHolder(
        itembinding.root
    ) {
        fun bind(chatMessage: ChatMessage){
            itembinding.imageProfile.setImageBitmap(getConversationImage(chatMessage.conversationImage))
            itembinding.textName.text = chatMessage.conversationName
            itembinding.textRecentMessage.text = chatMessage.message
            itembinding.root.setOnClickListener{
                var user : User = User()
                user.id = chatMessage.conversationId
                user.name = chatMessage.conversationName
                user.image = chatMessage.conversationImage
                conversionListener.onConversionClicked(user)
            }
        }
    }



}