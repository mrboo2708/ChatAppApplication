package com.example.chatappapplication.models
import java.io.Serializable

data class User(var name: String,var image : String, var email : String, val token : String) : Serializable{


}