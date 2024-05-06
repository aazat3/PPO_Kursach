package com.example.ppo_kursach

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ppo_kursach.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = Firebase.database.reference
//        val dealId =

//        supportFragmentManager.beginTransaction()
//            .add(R.id.main_fragment, DealFragment())
//            .commit()

        findViewById<BottomNavigationView>(R.id.bottomNav).setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
//                    loadFragment(HomeFragment())
                    true
                }
                R.id.message -> {
//                    loadFragment(ChatFragment())
                    true
                }
                R.id.settings -> {
//                    loadFragment(SettingFragment())
                    true
                }

                else -> {
                    true
                }
            }
        }
    }
}