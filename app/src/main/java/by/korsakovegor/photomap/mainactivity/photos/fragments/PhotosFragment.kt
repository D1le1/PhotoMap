package by.korsakovegor.photomap.mainactivity.photos.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import by.korsakovegor.photomap.databinding.FragmentPhotosLayoutBinding
import by.korsakovegor.photomap.mainactivity.photos.adapters.ImageRecyclerAdapter
import by.korsakovegor.photomap.mainactivity.photos.viewmodels.PhotosViewModel
import by.korsakovegor.photomap.models.ImageDtoOut
import by.korsakovegor.photomap.utils.Utils
import com.google.android.material.snackbar.Snackbar

class PhotosFragment() : Fragment() {
    private lateinit var binding: FragmentPhotosLayoutBinding
    private lateinit var viewModel: PhotosViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[PhotosViewModel::class.java]

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
        adapter.setOnItemClickListener(activity as ImageRecyclerAdapter.OnItemClickListener)
        recycler.adapter = adapter
        viewModel.images.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("D1le", "Dest")
    }
}