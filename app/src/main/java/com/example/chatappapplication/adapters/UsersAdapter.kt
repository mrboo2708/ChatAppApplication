package com.example.chatappapplication.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappapplication.databinding.ItemContainerUserBinding
import com.example.chatappapplication.models.User


class UsersAdapter(private val userList: List<User>) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemBinding = ItemContainerUserBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user : User = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = userList.size


    class UserViewHolder(private val itemBinding: ItemContainerUserBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
    {
        fun bind(user : User){
            itemBinding.textName.setText(user.name)
            itemBinding.textEmail.setText(user.email)
            itemBinding.imageProfile.setImageBitmap(getUserImage(user.image))
        }

        private fun getUserImage(encodedImage: String): Bitmap{
            var bytes: ByteArray =Base64.decode(encodedImage,Base64.DEFAULT)
            return  BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        }

    }





}