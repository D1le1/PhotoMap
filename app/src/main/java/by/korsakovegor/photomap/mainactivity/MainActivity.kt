package by.korsakovegor.photomap.mainactivity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import by.korsakovegor.photomap.R
import by.korsakovegor.photomap.databinding.ActivityMainBinding
import by.korsakovegor.photomap.mainactivity.map.MapFragment
import by.korsakovegor.photomap.mainactivity.photos.fragments.PhotosFragment
import by.korsakovegor.photomap.mainactivity.photos.viewmodels.PhotosViewModel
import by.korsakovegor.photomap.models.ImageDtoIn
import by.korsakovegor.photomap.models.SignUserOutDto
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.navigation.NavigationView
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Date


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewModel: MainViewModel
    private var user: SignUserOutDto? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("user", SignUserOutDto::class.java)
        } else
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


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else if (supportFragmentManager.findFragmentById(R.id.fragment_container) is MapFragment) {
                    viewModel.setFragment(PhotosFragment(user))
                    binding.navigationView.setCheckedItem(R.id.photos)
                } else {
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
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyPhoto.jpg")
        val photoURI = FileProvider.getUriForFile(this, "by.korsakovegor.photomap.fileprovider", file)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        if (this.packageManager?.let { takePictureIntent.resolveActivity(it) } != null) {
            resultLauncher.launch(takePictureIntent)
        }
    }

    private fun processCapturedPhoto() {
        val file = File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyPhoto.jpg")
        val photoURI = FileProvider.getUriForFile(this, "by.korsakovegor.photomap.fileprovider", file)
        Glide.with(this)
            .asBitmap()
            .load(photoURI)
            .apply(RequestOptions().override(720, 1280))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val outputStream = ByteArrayOutputStream()
                    resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    val imageBytes = outputStream.toByteArray()
                    val base64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)

                    val image = ImageDtoIn(
                        base64, Date(), 0.0, 0.0
                    )
                    val viewModel = ViewModelProvider(this@MainActivity)[PhotosViewModel::class.java]
                    viewModel.sendImage(image, user?.token ?: "")

                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }


}