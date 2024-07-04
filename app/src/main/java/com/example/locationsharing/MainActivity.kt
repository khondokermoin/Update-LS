package com.example.locationsharing

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthenticationViewModel
    private lateinit var fireStoreViewModel: FireStoreViewModel
    private lateinit var userAdapter: UserAdapter
    private lateinit var recyclerViewUser: RecyclerView
    private lateinit var locationBtn: FloatingActionButton

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navview: NavigationView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private lateinit var locationViewModel: LocationViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getLocation()
            } else {
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        navview = findViewById(R.id.navview)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navview.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.profile ->{
                    startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                    drawerLayout.closeDrawers()
                }

                R.id.logout ->{
                    Firebase.auth.signOut()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                    drawerLayout.closeDrawers()
                }

            }
            true
        }
        locationBtn = findViewById(R.id.locationBtn)

        locationBtn.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        authViewModel = ViewModelProvider(this).get(AuthenticationViewModel::class.java)
        fireStoreViewModel = ViewModelProvider(this).get(FireStoreViewModel::class.java)

        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationViewModel.initializeFusedLocationClient(fusedLocationClient)

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ) {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            getLocation()
        }
        recyclerViewUser = findViewById(R.id.userRV)
        fireStoreViewModel = ViewModelProvider(this).get(FireStoreViewModel::class.java)

        userAdapter = UserAdapter(emptyList())
        recyclerViewUser.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        fetchUser()

    }
    private fun fetchUser() {
        fireStoreViewModel.getAllUser { userList->
            userAdapter.updateData(userList)
        }
    }
    fun getLocation(){
        locationViewModel.getLastLocation { location->
            authViewModel.getCurrentUserId()?.let { userId ->
                fireStoreViewModel.updateUserLocation(userId, location)
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            true
        }
        else super.onOptionsItemSelected(item)
    }
}