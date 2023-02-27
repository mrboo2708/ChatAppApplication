package com.example.chatappapplication.listener

import com.example.chatappapplication.models.User

interface UserListener {
    fun onUserClicked(user : User)
}