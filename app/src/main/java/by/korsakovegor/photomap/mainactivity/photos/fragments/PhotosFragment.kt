package by.korsakovegor.photomap.mainactivity.photos.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import by.korsakovegor.photomap.R
import by.korsakovegor.photomap.databinding.FragmentPhotosLayoutBinding
import by.korsakovegor.photomap.mainactivity.photos.adapters.RecyclerAdapter
import by.korsakovegor.photomap.mainactivity.photos.viewmodels.PhotosViewModel
import by.korsakovegor.photomap.models.ImageDtoOut
import by.korsakovegor.photomap.utils.Utils
import com.google.android.material.snackbar.Snackbar
import java.util.Date

class PhotosFragment() : Fragment() {
    private lateinit var binding: FragmentPhotosLayoutBinding

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

        val viewModel = ViewModelProvider(this)[PhotosViewModel::class.java]
        if (Utils.isInternetAvailable(context))
            viewModel.getImages()
        else
            Snackbar.make(view, "Turn On Internet Please", Snackbar.LENGTH_SHORT).show()

        val recycler = binding.recyclerView
        val layoutManager = GridLayoutManager(context, 3)
        recycler.layoutManager = layoutManager

        val images = ArrayList<ImageDtoOut>()
        val adapter = RecyclerAdapter(images)
        adapter.setOnItemClickListener(activity as RecyclerAdapter.OnItemClickListener)
        recycler.adapter = adapter
        viewModel.images.observe(viewLifecycleOwner) {
            val start = images.size
            images.clear()
            images.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }
}