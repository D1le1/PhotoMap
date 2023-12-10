package by.korsakovegor.photomap.mainactivity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import by.korsakovegor.photomap.R
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import by.korsakovegor.photomap.databinding.ActivityMainBinding
import by.korsakovegor.photomap.mainactivity.map.MapFragment
import by.korsakovegor.photomap.mainactivity.photos.activities.PhotoDetailActivity
import by.korsakovegor.photomap.mainactivity.photos.adapters.ImageRecyclerAdapter
import by.korsakovegor.photomap.mainactivity.photos.fragments.PhotosFragment
import by.korsakovegor.photomap.models.ImageDtoOut
import by.korsakovegor.photomap.models.SignUserOutDto
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewModel: MainViewModel
    private var user: SignUserOutDto? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.getSerializableExtra("user", SignUserOutDto::class.java)
        binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.usernameText).text =
            user?.login

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.currentFragment.observe(this) {
            openFragment(it)
        }

        binding.navigationView.setNavigationItemSelectedListener(this)
        openFragment(PhotosFragment(user))

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.photos -> viewModel.setFragment(PhotosFragment(user))
            R.id.map -> viewModel.setFragment(MapFragment())
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

}