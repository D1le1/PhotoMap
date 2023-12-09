package by.korsakovegor.photomap.mainactivity.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import by.korsakovegor.photomap.databinding.FragmentMapLayoutBinding
import by.korsakovegor.photomap.databinding.FragmentPhotosLayoutBinding
import by.korsakovegor.photomap.mainactivity.photos.adapters.RecyclerAdapter

class PhotosFragment : Fragment() {
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

        val recycler = binding.recyclerView
        val layoutManager = GridLayoutManager(context, 3)
        recycler.layoutManager = layoutManager

        val images = arrayListOf(
            "asdasdasdasd",
            "asdasdasdasd",
            "asdasdasdasd",
            "asdasdasdasd",
            "asdasdasdasd",
            "asdasdasdasd",
            "asdasdasdasd",
            "asdasdasdasd",
            "asdasdasdasd",
            "asdasdsad"
        )

        val adapter = RecyclerAdapter(images)
        recycler.adapter = adapter
    }
}