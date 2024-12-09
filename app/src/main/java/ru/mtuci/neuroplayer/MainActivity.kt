package ru.mtuci.neuroplayer

import android.Manifest
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.loader.content.CursorLoader
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import ru.mtuci.neuroplayer.databinding.ActivityMainBinding
import ru.mtuci.neuroplayer.utils.SongManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var requestPermission: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        requestPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = binding.navView

        val navFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        navController = navFragment.navController


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val rootTabs = setOf(
            R.id.navigation_transformer, R.id.navigation_library
        )

        appBarConfiguration = AppBarConfiguration(
            rootTabs
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        NavigationUI.setupWithNavController(navView, navController, saveState = false)

        CursorLoader(this)


//        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        SongManager.destroy()
    }
}