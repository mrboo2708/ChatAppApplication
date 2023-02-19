package com.example.chatappapplication.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.chatappapplication.databinding.ActivitySignUpBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var imageEncoded : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()

    }

    private fun setListeners(){
        binding.textSignIn.setOnClickListener{
            onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    finish()
                }
            })
        }

        binding.buttonSignUp.setOnClickListener{
            if(isValidSignUpDetail()){
                signUp()
            }
        }
    }

    private fun showToast(message:String){
        Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
    }

    private fun signUp(){

    }

    private fun isValidSignUpDetail() : Boolean{
        if(imageEncoded == null){
            showToast("Select profile image")
            return false
        }else if(binding.inputName.text.toString().trim().isEmpty()){
            showToast("Enter name")
            return false
        }else if(binding.inputEmail.text.toString().trim().isEmpty()){
            showToast("Enter email")
            return false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()){
            showToast("Enter valid email!")
            return false
        }else if(binding.inputPassword.text.toString().trim().isEmpty()){
            showToast("Enter password")
            return false
        }else if(binding.inputConfirmPassword.text.toString().trim().isEmpty()){
            showToast("Confirm your password")
            return false
        }else if(!binding.inputPassword.text.toString().equals(binding.inputConfirmPassword.text.toString())){
            showToast("Password and confirm password must equals")
            return false
        }else {
            return true
        }

    }

    private fun loading(isLoading : Boolean){
        if(isLoading){
            binding.buttonSignUp.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }
        else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.buttonSignUp.visibility = View.VISIBLE
        }
    }



}


