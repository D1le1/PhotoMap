package by.korsakovegor.photomap.mainactivity.photos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import by.korsakovegor.photomap.databinding.FragmentPhotosLayoutBinding
import by.korsakovegor.photomap.mainactivity.photos.adapters.RecyclerAdapter
import by.korsakovegor.photomap.models.ImageDtoOut

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
        viewModel.getImages()

        val recycler = binding.recyclerView
        val layoutManager = GridLayoutManager(context, 3)
        recycler.layoutManager = layoutManager

        viewModel.images.observe(viewLifecycleOwner){
            val images = it.reversed() as ArrayList<ImageDtoOut>
            val adapter = RecyclerAdapter(images)
            adapter.setOnItemClickListener(activity as RecyclerAdapter.OnItemClickListener)
            recycler.adapter = adapter
        }
    }
}