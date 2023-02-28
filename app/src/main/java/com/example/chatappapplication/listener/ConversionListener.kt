package com.example.chatappapplication.listener

import com.example.chatappapplication.models.User

interface ConversionListener {
    fun onConversionClicked(user : User)
}