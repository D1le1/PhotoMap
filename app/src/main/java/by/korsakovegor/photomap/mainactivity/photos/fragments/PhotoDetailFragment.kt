package by.korsakovegor.photomap.mainactivity.photos.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import by.korsakovegor.photomap.R
import by.korsakovegor.photomap.databinding.DetailPhotoLayoutBinding

class PhotoDetailFragment:Fragment() {
    private lateinit var binding: DetailPhotoLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DetailPhotoLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}

//https://junior.balinasoft.com/images/uploaded/2023/11/9/u479r7m2sglm25tonachio89vylfk05a.png