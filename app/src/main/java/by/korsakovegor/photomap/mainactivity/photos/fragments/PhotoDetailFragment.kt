//package by.korsakovegor.photomap.mainactivity.photos.fragments
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.drawable.BitmapDrawable
//import android.os.Build
//import android.os.Bundle
//import android.util.Base64
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.animation.AnimationUtils
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.LinearLayoutManager
//import by.korsakovegor.photomap.R
//import by.korsakovegor.photomap.databinding.DetailPhotoLayoutBinding
//import by.korsakovegor.photomap.mainactivity.photos.adapters.CommentsRecyclerAdapter
//import by.korsakovegor.photomap.mainactivity.photos.viewmodels.PhotosViewModel
//import by.korsakovegor.photomap.models.ImageDtoOut
//import com.squareup.picasso.Picasso
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import okhttp3.Call
//import okhttp3.Callback
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody
//import okhttp3.Response
//import org.json.JSONObject
//import java.io.ByteArrayOutputStream
//import java.io.IOException
//import java.util.Date
//
//class PhotoDetailFragment : Fragment() {
//    companion object {
//        fun getInstance(args: Bundle?): PhotoDetailFragment {
//            val photoDetailFragment = PhotoDetailFragment()
//            photoDetailFragment.arguments = args
//            return photoDetailFragment
//        }
//    }
//
//    private lateinit var binding: DetailPhotoLayoutBinding
//    private var image: ImageDtoOut? = null
//    private lateinit var viewModel: PhotosViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = DetailPhotoLayoutBinding.inflate(inflater, container, false)
//        viewModel = ViewModelProvider(this)[PhotosViewModel::class.java]
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        loadImageData()
//        viewModel.getComments(image?.id)
//
//
//
//        val recycler = binding.recyclerView
//        val layoutManager = LinearLayoutManager(context)
//        recycler.layoutManager = layoutManager
//
//        val adapter = CommentsRecyclerAdapter()
//        recycler.adapter = adapter
//
//        viewModel.comments.observe(viewLifecycleOwner){
//            if (it != null) {
//                adapter.updateData(it)
//            }
//        }
//    }
//
//    private fun loadImageData() {
//        image = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            arguments?.getSerializable("image", ImageDtoOut::class.java)
//        }else{
//            arguments?.getSerializable("image") as ImageDtoOut
//        }
//        Picasso.get().load(image?.url).into(binding.photo)
//        binding.time.text = image?.time
//    }
//}
