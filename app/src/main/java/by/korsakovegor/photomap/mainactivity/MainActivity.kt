package by.korsakovegor.photomap.mainactivity

import android.app.ActivityOptions
import android.content.Intent
import by.korsakovegor.photomap.R
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import by.korsakovegor.photomap.databinding.ActivityMainBinding
import by.korsakovegor.photomap.mainactivity.map.MapFragment
import by.korsakovegor.photomap.mainactivity.photos.adapters.ImageRecyclerAdapter
import by.korsakovegor.photomap.mainactivity.photos.fragments.PhotoDetailFragment
import by.korsakovegor.photomap.mainactivity.photos.fragments.PhotosFragment
import by.korsakovegor.photomap.mainactivity.photos.viewmodels.MainViewModel
import by.korsakovegor.photomap.mainactivity.photos.viewmodels.PhotosViewModel
import by.korsakovegor.photomap.models.ImageDtoOut
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ImageRecyclerAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewModel: MainViewModel

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

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.currentFragment.observe(this){
            openFragment(it)
        }

        binding.navigationView.setNavigationItemSelectedListener(this)
        openFragment(PhotosFragment())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.photos -> viewModel.setFragment(PhotosFragment())
            R.id.map -> viewModel.setFragment(MapFragment())
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    @RequiresApi(34)
    override fun onImageClick(v: View, image: ImageDtoOut) {
        val anim = AnimationUtils.loadAnimation(this, R.anim.button_state)
        v.startAnimation(anim)
        CoroutineScope(Dispatchers.Main).launch {
            delay(170)
            val intent = Intent(this@MainActivity, PhotoDetailActivity::class.java)
            intent.putExtra("image", image)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this@MainActivity,
                v.findViewById(R.id.photoImage),
                "imageTrans"
            )
            startActivity(intent, options.toBundle())
        }
    }


}