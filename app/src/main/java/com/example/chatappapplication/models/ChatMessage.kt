package com.example.chatappapplication.models

import java.util.Date

data class ChatMessage(
    var senderId: String, var receiverId: String,
    var message: String, var dateTime: String, var dateObject: Date?
){
    constructor() : this("","","","",null)
}
