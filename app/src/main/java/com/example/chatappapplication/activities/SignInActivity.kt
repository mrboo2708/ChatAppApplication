package com.example.chatappapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.chatappapplication.databinding.ActivitySignInBinding
import com.example.chatappapplication.utilities.Constants
import com.example.chatappapplication.utilities.PreferenceManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.FileNotFoundException

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(applicationContext)
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()


    }

    private fun setListeners() {
        binding.textCreateNewAccount.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
        }
        binding.buttonSignIn.setOnClickListener {
            if (isValidSignInDetail()) {
                signIn()
            }
        }

    }

    private fun signIn() {
        loading(true)
        val database = Firebase.firestore
        database.collection(Constants.KEY_COLLECTION_USER)
            .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.text.toString())
            .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.text.toString())
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null && it.result.documents.size > 0) {
                    val documentSnapshot = it.result.documents.get(0)
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                    preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.id)
                    documentSnapshot.getString(Constants.KEY_NAME)
                        ?.let { it1 -> preferenceManager.putString(Constants.KEY_NAME, it1) }
                    documentSnapshot.getString(Constants.KEY_IMAGE)
                        ?.let { it1 -> preferenceManager.putString(Constants.KEY_IMAGE, it1) }
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    loading(false)
                    showToast("Unable to sign in")
                }
            }
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.buttonSignIn.visibility = View.INVISIBLE
            binding.processBar.visibility = View.VISIBLE
        } else {
            binding.processBar.visibility = View.INVISIBLE
            binding.buttonSignIn.visibility = View.VISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun isValidSignInDetail(): Boolean {
        if (binding.inputEmail.text.toString().trim().isEmpty()) {
            showToast("Enter email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()) {
            showToast("Email not valid")
            return false
        } else if (binding.inputPassword.text.toString().trim().isEmpty()) {
            showToast("Enter password")
            return false
        } else {
            return true
        }
    }

}