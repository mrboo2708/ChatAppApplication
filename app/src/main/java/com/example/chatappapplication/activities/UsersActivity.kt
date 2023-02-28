package com.example.chatappapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.chatappapplication.R
import com.example.chatappapplication.adapters.UsersAdapter
import com.example.chatappapplication.databinding.ActivityMainBinding
import com.example.chatappapplication.databinding.ActivityUsersBinding
import com.example.chatappapplication.listener.UserListener
import com.example.chatappapplication.utilities.Constants
import com.example.chatappapplication.utilities.PreferenceManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.chatappapplication.models.User
import com.google.firebase.firestore.QueryDocumentSnapshot

class UsersActivity : BaseActivity(),UserListener {
    private lateinit var binding: ActivityUsersBinding
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        setListener()
        getUser()
    }
    private fun setListener(){
        binding.imageBack.setOnClickListener{
            onBackPressed()
        }
    }

    private fun showErrorMessage(){
        binding.textErrorMessage.text = String.format("%s","No user available")
        binding.textErrorMessage.visibility = View.VISIBLE
    }

    private fun getUser(){
        loading(true)
        val database = Firebase.firestore
        database.collection(Constants.KEY_COLLECTION_USER).get().addOnCompleteListener{
            loading(false)
            var currentId = preferenceManager.getString(Constants.KEY_USER_ID)
            if(it.isSuccessful && it.result != null){
                val users :MutableList<User> = arrayListOf()
                for(query : QueryDocumentSnapshot in it.result){
                    if(currentId.equals(query.id)){
                        continue
                    }
                    var user = User()
                    user.name = query.getString(Constants.KEY_NAME).toString()
                    user.email = query.getString(Constants.KEY_EMAIL).toString()
                    user.image = query.getString(Constants.KEY_IMAGE).toString()
                    user.token = query.getString(Constants.KEY_FCM_TOKEN).toString()
                    user.id = query.id
                    users.add(user)
                }
                if(users.size > 0){
                    val userAdapter : UsersAdapter = UsersAdapter(users,this)
                    binding.userRecyclerView.adapter = userAdapter
                    binding.userRecyclerView.visibility = View.VISIBLE
                }
                else{
                    showErrorMessage()
                }
            }else {
                showErrorMessage()
            }
        }
    }



    private fun loading(isLoading: Boolean){
        if(isLoading){
            binding.processBar.visibility = View.VISIBLE
        }
        else{
            binding.processBar.visibility = View.INVISIBLE
        }
    }

    override fun onUserClicked(user: User) {
        val intent = Intent(applicationContext,ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER,user)
        startActivity(intent)
        finish()
    }
}