package by.korsakovegor.photomap.mainactivity.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.korsakovegor.photomap.databinding.FragmentMapLayoutBinding
import by.korsakovegor.photomap.databinding.FragmentPhotosLayoutBinding

class PhotosFragment: Fragment() {
    private lateinit var binding: FragmentPhotosLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotosLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }
}