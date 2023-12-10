package by.korsakovegor.photomap.mainactivity.photos.fragments

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibratorManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import by.korsakovegor.photomap.R
import by.korsakovegor.photomap.databinding.FragmentPhotosLayoutBinding
import by.korsakovegor.photomap.mainactivity.photos.activities.PhotoDetailActivity
import by.korsakovegor.photomap.mainactivity.photos.adapters.ImageRecyclerAdapter
import by.korsakovegor.photomap.mainactivity.photos.viewmodels.PhotosViewModel
import by.korsakovegor.photomap.mainactivity.photos.viewmodels.PhotosViewModelFactory
import by.korsakovegor.photomap.models.ImageDtoOut
import by.korsakovegor.photomap.models.SignUserOutDto
import by.korsakovegor.photomap.utils.Utils
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PhotosFragment(private val user: SignUserOutDto?) : Fragment(),
    ImageRecyclerAdapter.OnImageClickListener,
    ImageRecyclerAdapter.OnImageLongClickListener {

    private lateinit var binding: FragmentPhotosLayoutBinding
    private lateinit var viewModel: PhotosViewModel
    private var longClicked = false
    private var isOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModeFactory = PhotosViewModelFactory(user!!)
        viewModel = ViewModelProvider(this, viewModeFactory)[PhotosViewModel::class.java]
        if (Utils.isInternetAvailable(context))
            viewModel.getImages()
        else
            view?.let { Snackbar.make(it, "Turn On Internet Please", Snackbar.LENGTH_SHORT).show() }
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

        val recycler = binding.recyclerView
        val layoutManager = GridLayoutManager(context, 3)
        recycler.layoutManager = layoutManager

        val adapter = ImageRecyclerAdapter()
        adapter.setOnItemClickListener(this)
        adapter.setOnItemLongClickListener(this)
        recycler.adapter = adapter
        viewModel.images.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
        viewModel.deletedItem.observe(viewLifecycleOwner) {
            adapter.deleteItem(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("D1le", "Dest")
    }

    override fun onImageClick(v: View, image: ImageDtoOut) {
        if (!longClicked && !isOpen) {
            isOpen = true
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
                isOpen = false
            }
        }
        longClicked = false
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onImageLongClick(image: ImageDtoOut, pos: Int) {
        longClicked = true
        val vib =
            activity?.getSystemService(AppCompatActivity.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        Utils.doVibrate(vib)

        Utils.showAlertDialog(
            requireContext(),
            "Delete Alert",
            "Are you sure you want to delete this image?"
        )
        { _, _ ->
            viewModel.deleteImage(image, pos)
        }

    }

}