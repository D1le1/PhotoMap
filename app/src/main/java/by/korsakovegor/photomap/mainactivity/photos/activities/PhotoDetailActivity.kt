package by.korsakovegor.photomap.mainactivity.photos.activities

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import by.korsakovegor.photomap.R
import by.korsakovegor.photomap.databinding.DetailPhotoLayoutBinding

class PhotoDetailActivity:AppCompatActivity() {
    private lateinit var binding: DetailPhotoLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailPhotoLayoutBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.sendButton.setOnClickListener{
            val anim = AnimationUtils.loadAnimation(this, R.anim.button_state)
            it.startAnimation(anim)

        }
    }
}

//https://junior.balinasoft.com/images/uploaded/2023/11/9/u479r7m2sglm25tonachio89vylfk05a.png