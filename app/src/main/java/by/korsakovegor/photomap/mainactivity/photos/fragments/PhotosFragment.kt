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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PhotosFragment(private val user: SignUserOutDto? = null) : Fragment(),
    ImageRecyclerAdapter.OnImageClickListener,
    ImageRecyclerAdapter.OnImageLongClickListener {

    private lateinit var binding: FragmentPhotosLayoutBinding
    private lateinit var viewModel: PhotosViewModel
    private var isLongClicked = false
    private var isPictureOpening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModeFactory = PhotosViewModelFactory(user!!)
        viewModel = ViewModelProvider(this, viewModeFactory)[PhotosViewModel::class.java]
        if (Utils.isInternetAvailable(context))
        viewModel = ViewModelProvider(this)[PhotosViewModel::class.java]
        if (Utils.isInternetAvailable(requireContext())) {
            viewModel.getImages()
        } else
            Utils.showConnectionAlertDialog(requireContext())
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
            adapter.updateData(it)
            if (it.size > 0)
                binding.noImages.visibility = View.GONE
        }
        viewModel.deletedItem.observe(viewLifecycleOwner) {
            adapter.deleteItem(it)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.error.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshing = false
            Utils.showConnectionAlertDialog(requireContext())
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (Utils.isInternetAvailable(requireContext()))
                viewModel.getImages()
            else {
                Utils.showConnectionAlertDialog(requireContext())
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
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

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onImageLongClick(image: ImageDtoOut, pos: Int) {
        isLongClicked = true
        val vib =
            activity?.getSystemService(AppCompatActivity.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        Utils.doVibrate(vib)

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

}