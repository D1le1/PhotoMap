package by.korsakovegor.photomap.mainactivity.photos.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import by.korsakovegor.photomap.R
import by.korsakovegor.photomap.databinding.DetailPhotoLayoutBinding
import by.korsakovegor.photomap.models.ImageDtoOut
import com.squareup.picasso.Picasso

class PhotoDetailFragment:Fragment() {
    companion object{
        fun getInstance(args: Bundle?): PhotoDetailFragment {
            val photoDetailFragment = PhotoDetailFragment()
            photoDetailFragment.arguments = args
            return photoDetailFragment
        }
    }

    private lateinit var binding: DetailPhotoLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DetailPhotoLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val image = arguments?.getSerializable("image", ImageDtoOut::class.java)

        Picasso.get().load(image?.url).into(binding.photo)
        binding.time.text = image?.time
    }
}

//https://junior.balinasoft.com/images/uploaded/2023/11/9/u479r7m2sglm25tonachio89vylfk05a.png