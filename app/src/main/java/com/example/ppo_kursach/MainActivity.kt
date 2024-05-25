package com.example.ppo_kursach

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
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

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.gfgNavigation_host_gfgFragment) as NavHostFragment
        val navController = navHostFragment.navController
        firebaseDatabase = Firebase.database.reference

        findViewById<BottomNavigationView>(R.id.bottomNav).setOnItemSelectedListener {
            when (it.itemId) {
                R.id.to_deal -> {
                    navController.navigate(R.id.dealFragment)
                    true
                }
                R.id.to_decoration -> {
                    navController.navigate(R.id.decorationFragment)
                    true
                }
                R.id.to_statistic -> {
                    navController.navigate(R.id.statisticFragment)
                    true
                }
                R.id.to_user -> {
                    navController.navigate(R.id.userFragment)
                    true
                }
                else -> {
                    true
                }
            }
        }
    }
}