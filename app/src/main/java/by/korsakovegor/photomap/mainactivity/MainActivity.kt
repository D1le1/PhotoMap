package by.korsakovegor.photomap.mainactivity

import android.content.Context
import by.korsakovegor.photomap.R
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import by.korsakovegor.photomap.databinding.ActivityMainBinding
import by.korsakovegor.photomap.mainactivity.map.MapFragment
import by.korsakovegor.photomap.mainactivity.photos.adapters.RecyclerAdapter
import by.korsakovegor.photomap.mainactivity.photos.fragments.PhotoDetailFragment
import by.korsakovegor.photomap.mainactivity.photos.fragments.PhotosFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    RecyclerAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


        binding.navigationView.setNavigationItemSelectedListener(this)
        openFragment(PhotosFragment(), false)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.photos -> openFragment(PhotosFragment(), false)
            R.id.map -> openFragment(MapFragment(), false)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        else if (!toggle.isDrawerIndicatorEnabled) {
            toggle.isDrawerIndicatorEnabled = true
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            toggle.syncState()
            binding.fab.show()
            super.onBackPressed()
        } else
            super.onBackPressed()
    }

    private fun openFragment(fragment: Fragment, flag: Boolean) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        if (flag) {
            fragmentTransaction.addToBackStack("")
            toggle.isDrawerIndicatorEnabled = false
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            binding.fab.hide()
            toggle.setToolbarNavigationClickListener {
                onBackPressed()
            }
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        fragmentTransaction.commit()
    }

    override fun onClick(v: View) {
        val anim = AnimationUtils.loadAnimation(this, R.anim.button_state)
        v.startAnimation(anim)
        openFragment(PhotoDetailFragment(), true)
    }


}