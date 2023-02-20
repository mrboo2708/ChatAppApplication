package com.example.chatappapplication.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.chatappapplication.databinding.ActivitySignUpBinding
import com.example.chatappapplication.utilities.Constants
import com.example.chatappapplication.utilities.ConvertStuff
import com.example.chatappapplication.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.regex.Pattern
import kotlin.math.log

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var imageEncoded : String? = null
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
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

        binding.layoutImage.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.flags =Intent.FLAG_GRANT_READ_URI_PERMISSION
            pickImage.launch(intent)
        }
    }

    private fun showToast(message:String){
        Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
    }

    private fun signUp(){
        loading(true)
        val database = Firebase.firestore
        var user = hashMapOf(
            Constants.KEY_NAME to binding.inputName.text.toString(),
            Constants.KEY_EMAIL to binding.inputEmail.text.toString(),
            Constants.KEY_PASSWORD to binding.inputPassword.text.toString(),
            Constants.KEY_IMAGE to imageEncoded
        )
        database.collection(Constants.KEY_COLLECTION_USER)
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("log", "DocumentSnapshot added with ID: ${documentReference.id}")
                loading(false)
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true)
                preferenceManager.putString(Constants.KEY_USER_ID,documentReference.id)
                preferenceManager.putString(Constants.KEY_NAME,binding.inputName.text.toString())
                imageEncoded?.let { preferenceManager.putString(Constants.KEY_IMAGE, it) }
                val intent = Intent(applicationContext,MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.w("log", "Error adding document", e)
                loading(false)
                e.message?.let { showToast(it) }
            }

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
        }else if(binding.inputPassword.text.toString() != binding.inputConfirmPassword.text.toString()){
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

    // TODO: need fix with Activity Result API
    private final val pickImage : ActivityResultLauncher<Intent>  =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                if (it.data != null) {
                    var imageUri: Uri? = it.data!!.data
                    try {
                        val inputStream =
                            imageUri?.let { it1 -> contentResolver.openInputStream(it1) }
                        var bitmap = BitmapFactory.decodeStream(inputStream)
                        binding.imageProfile.setImageBitmap(bitmap)
                        binding.textAddImage.visibility = View.GONE
                        imageEncoded = bitmap.encode()
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    //extend func for bitmap
    fun Bitmap.encode(): String{
        var previewWidth = 150
        var previewHeight = this.height * previewWidth / this.width
        val previewBitmap = Bitmap.createScaledBitmap(this,previewWidth,previewHeight,false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream)
        val bytes : ByteArray = byteArrayOutputStream.toByteArray()
        return  android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
    }


}


