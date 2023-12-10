package by.korsakovegor.photomap.mainactivity

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import by.korsakovegor.photomap.R
import android.os.Bundle
import kotlin.random.Random
import android.util.Base64
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
import androidx.fragment.app.FragmentActivity
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
import java.io.ByteArrayOutputStream
import java.util.Date


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewModel: MainViewModel
    private lateinit var fragmentTransaction: FragmentTransaction
    private var user: SignUserOutDto? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("user", SignUserOutDto::class.java)
        }else
            intent.getSerializableExtra("user") as SignUserOutDto

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

        binding.fab.setOnClickListener {
            val base64 = drawableToBase64(this, R.drawable.image2)
            val date = Date()
            viewModel.uploadImageToServer(
                base64, date.time / 1000, Random.nextDouble(
                    20.0, 51.0
                ), Random.nextDouble(
                    20.0, 51.0
                ),
                user?.token ?: ""
            )
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }else if(supportFragmentManager.findFragmentById(R.id.fragment_container) is MapFragment) {
                    viewModel.setFragment(PhotosFragment(user))
                    binding.navigationView.setCheckedItem(R.id.photos)
                }
                else {
                    finish()
                }
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.photos -> viewModel.setFragment(PhotosFragment(user))
            R.id.map -> viewModel.setFragment(MapFragment(user))
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openFragment(fragment: Fragment) {
        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    private fun drawableToBase64(context: Context?, drawableId: Int): String {
        val drawable = context?.getDrawable(drawableId)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val imageBytes = outputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

}