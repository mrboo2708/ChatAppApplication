package com.example.chatappapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatappapplication.R
import kotlinx.coroutines.flow.flow
import java.util.concurrent.Flow

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


}