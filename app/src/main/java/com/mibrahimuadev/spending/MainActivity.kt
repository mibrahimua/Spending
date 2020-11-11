package com.mibrahimuadev.spending

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mibrahimuadev.spending.databinding.ActivityMainBinding
import com.mibrahimuadev.spending.home.HomeFragmentDirections
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
//    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.i("MainActivity", "MainActivity Created")

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        /**
         * NavigationUI.setupActionBarWithNavController() berguna untuk menambahkan
         * tombol panah di toolbar
         */
//        NavigationUI.setupActionBarWithNavController(this,navController)

//        appBarConfiguration = AppBarConfiguration(
//            setOf(R.id.homeFragment, R.id.pemasukanFragment)
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        setupActionBarWithNavController(navController)

        binding.bottomNavigationView.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onStop() {
        super.onStop()
        Log.i("MainActivity", "MainActivty stopped")
    }

    override fun onPause() {
        super.onPause()
        Log.i("MainActivity", "MainActivty Paused")
    }

    override fun onResume() {
        super.onResume()
        Log.i("MainActivity", "MainActivty Resumed")
    }

    override fun onStart() {
        super.onStart()
        Log.i("MainActivity", "MainActivty started")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("MainActivity", "MainActivty destroyed")
    }
}