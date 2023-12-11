package by.korsakovegor.photomap.mainactivity.photos.fragments

import android.Manifest
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.VibratorManager
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import by.korsakovegor.photomap.R
import by.korsakovegor.photomap.databinding.FragmentPhotosLayoutBinding
import by.korsakovegor.photomap.mainactivity.photos.activities.PhotoDetailActivity
import by.korsakovegor.photomap.mainactivity.photos.adapters.ImageRecyclerAdapter
import by.korsakovegor.photomap.mainactivity.photos.viewmodels.PhotosViewModel
import by.korsakovegor.photomap.models.ImageDtoIn
import by.korsakovegor.photomap.models.ImageDtoOut
import by.korsakovegor.photomap.models.SignUserOutDto
import by.korsakovegor.photomap.utils.MainDb
import by.korsakovegor.photomap.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Date

class PhotosFragment(private val user: SignUserOutDto? = null) : Fragment(),
    ImageRecyclerAdapter.OnImageClickListener,
    ImageRecyclerAdapter.OnImageLongClickListener {

    private lateinit var binding: FragmentPhotosLayoutBinding
    private lateinit var viewModel: PhotosViewModel
    private var isLongClicked = false
    private var isPictureOpening = false
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var currentFile: File
    private var page: Int = 0
    private var isOnBottom = false
    private lateinit var db: MainDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = 0
        viewModel = ViewModelProvider(this)[PhotosViewModel::class.java]
        viewModel.setUserToken(user?.token ?: "")
        db = MainDb.getInstance(requireContext())
        if (Utils.isInternetAvailable(requireContext())) {
            Log.d("D1le", user?.token.toString())
            viewModel.getImages(page)
        } else {
            Utils.showConnectionAlertDialog(requireContext())
        }

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    processCapturedPhoto()
                } else binding.swipeRefreshLayout.isRefreshing = false
            }

        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {}.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotosLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Utils.isInternetAvailable(requireContext()))
            binding.swipeRefreshLayout.isRefreshing = true

        val recycler = binding.recyclerView
        val layoutManager = GridLayoutManager(context, 3)
        recycler.layoutManager = layoutManager

        val adapter = ImageRecyclerAdapter()
        adapter.setOnItemClickListener(this)
        adapter.setOnItemLongClickListener(this)
        recycler.adapter = adapter
        viewModel.images.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshing = false
            if (it != null) {
                if (it.size > 0) {
                    if (page == 0) {
                        adapter.updateData(it)
                    } else {
                        adapter.addData(it)
                    }
                    page++
                    isOnBottom = false
                    CoroutineScope(Dispatchers.IO).launch {
                        db.getImagesDao().insertNewImages(it)
                    }
                }
            }
            if (adapter.itemCount > 0)
                binding.noImages.visibility = View.GONE
        }
        viewModel.image.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.addItem(it)
                binding.swipeRefreshLayout.isRefreshing = false
                binding.noImages.visibility = View.GONE
                CoroutineScope(Dispatchers.IO).launch {
                    db.getImagesDao().insertNewImage(it)
                }
            }
        }
        viewModel.deletedItem.observe(viewLifecycleOwner) {
            val image = adapter.deleteItem(it)
            CoroutineScope(Dispatchers.IO).launch {
                db.getImagesDao().deleteImage(image)
            }
            if (adapter.itemCount == 0)
                binding.noImages.visibility = View.VISIBLE
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.error.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshing = false
            if (it.isEmpty()) {
                Utils.showConnectionAlertDialog(requireContext())
            } else {
                Utils.showAlertDialog(requireContext(), "Error", it)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (Utils.isInternetAvailable(requireContext())) {
                page = 0
                viewModel.getImages(page)
            } else {
                Utils.showConnectionAlertDialog(requireContext())
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        binding.fab.setOnClickListener {
            binding.swipeRefreshLayout.isRefreshing = true
            dispatchTakePictureIntent()
        }

        recycler.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recycler.canScrollVertically(1) && !isOnBottom) {
                    if (Utils.isInternetAvailable(requireContext())) {
                        isOnBottom = true
                        viewModel.getImages(page)
                    } else
                        isOnBottom = false
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("D1le", "Dest")
    }

    override fun onImageClick(v: View, image: ImageDtoOut) {
        if (!isLongClicked && !isPictureOpening) {
            isPictureOpening = true
            val anim = AnimationUtils.loadAnimation(activity, R.anim.button_state)
            v.startAnimation(anim)
            CoroutineScope(Dispatchers.Main).launch {
                delay(170)
                val intent = Intent(activity, PhotoDetailActivity::class.java)
                intent.putExtra("image", image)
                intent.putExtra("user", user)
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    activity,
                    v.findViewById(R.id.photoImage),
                    "imageTrans"
                )
                startActivity(intent, options.toBundle())
                isPictureOpening = false
            }
        }
        isLongClicked = false
    }


    override fun onImageLongClick(image: ImageDtoOut, pos: Int) {
        isLongClicked = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vib =
                activity?.getSystemService(AppCompatActivity.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            Utils.doVibrate(vib)
        } else {

        }

        Utils.showAlertDialog(
            requireContext(),
            "Delete Alert",
            "Are you sure you want to delete this image?"
        )
        { _, _ ->
            if (Utils.isInternetAvailable(requireContext())) {
                viewModel.deleteImage(image, pos)
                binding.swipeRefreshLayout.isRefreshing = true
            } else
                Utils.showConnectionAlertDialog(requireContext())
        }

    }

    private fun createImageFile(): File {
        val fileName = "photo.jpg"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, null, storageDir)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = createImageFile()
        val photoURI =
            FileProvider.getUriForFile(
                requireContext(),
                "by.korsakovegor.photomap.fileprovider",
                file
            )
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        currentFile = file
        if (requireActivity().packageManager?.let { takePictureIntent.resolveActivity(it) } != null) {
            resultLauncher.launch(takePictureIntent)
        }
    }

    private fun processCapturedPhoto() {
        val file = currentFile
        if (file.exists()) {
            Glide.with(this)
                .asBitmap()
                .load(file)
                .apply(RequestOptions().override(720, 1280))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {

                        getCurrentLocation { latLng ->
                            if (latLng != null) {

                                val outputStream = ByteArrayOutputStream()
                                resource.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                                val imageBytes = outputStream.toByteArray()
                                val base64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                                val image =
                                    ImageDtoIn(base64, Date(), latLng.latitude, latLng.longitude)
                                viewModel.sendImage(
                                    image, user?.token ?: ""
                                )
                                file.delete()
                            }
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }


    private fun getCurrentLocation(callback: (LatLng?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Utils.showAlertDialog(requireContext(), "Error", "Give necessary permissions")

            binding.swipeRefreshLayout.isRefreshing = false
            callback(null)
            return
        }

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            }
        ).addOnSuccessListener { location: Location? ->
            if (location == null) {
                Utils.showAlertDialog(requireContext(), "Error", "Can't get location")
                binding.swipeRefreshLayout.isRefreshing = false
                callback(null)
            } else {
                val latLng = LatLng(location.latitude, location.longitude)
                callback(latLng)
            }
        }
    }
}