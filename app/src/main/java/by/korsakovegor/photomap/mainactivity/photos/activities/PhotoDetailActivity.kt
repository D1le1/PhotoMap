package by.korsakovegor.photomap.mainactivity.photos.activities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.korsakovegor.photomap.R
import by.korsakovegor.photomap.databinding.DetailPhotoLayoutBinding
import by.korsakovegor.photomap.mainactivity.photos.adapters.CommentsRecyclerAdapter
import by.korsakovegor.photomap.mainactivity.photos.viewmodels.PhotosViewModel
import by.korsakovegor.photomap.models.CommentDtoIn
import by.korsakovegor.photomap.models.CommentDtoOut
import by.korsakovegor.photomap.models.ImageDtoOut
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Date

class PhotoDetailActivity : AppCompatActivity(), CommentsRecyclerAdapter.OnCommentLongClickListener {
    private lateinit var binding: DetailPhotoLayoutBinding
    private var image: ImageDtoOut? = null
    private lateinit var viewModel: PhotosViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailPhotoLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[PhotosViewModel::class.java]

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        loadImageData()
        viewModel.getComments(image?.id)

        val recycler = binding.recyclerView
        val layoutManager = LinearLayoutManager(this)
        recycler.layoutManager = layoutManager

        val adapter = CommentsRecyclerAdapter()
        adapter.setOnCommentLongClick(this)
        recycler.adapter = adapter

        viewModel.comments.observe(this) {
            adapter.updateData(it)
        }

        viewModel.comment.observe(this) {
            adapter.addItem(it)
        }

        binding.sendButton.setOnClickListener {
            val anim = AnimationUtils.loadAnimation(this, R.anim.button_state)
            it.startAnimation(anim)

            viewModel.sendComment(CommentDtoIn(binding.commentEditText.text.toString()), image?.id)
        }

        val date = Date()
        val time = date.time / 1000
        CoroutineScope(Dispatchers.IO).launch {
            val base = drawableToBase64(this@PhotoDetailActivity, R.drawable.image2)
//            uploadImageToServer(base, time, 20.1, 20.1)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun loadImageData() {
        image = intent.getSerializableExtra("image", ImageDtoOut::class.java)
        Picasso.get().load(image?.url).into(binding.photo)
        binding.time.text = image?.time
    }

    fun uploadImageToServer(base64Image: String, date: Long, lat: Double, lng: Double) {
        val jsonBody = JSONObject()
            .put("base64Image", base64Image)
            .put("date", date)
            .put("lat", lat)
            .put("lng", lng)
            .toString()

        val mediaType = "application/json;charset=UTF-8".toMediaType()
        val requestBody = jsonBody.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://junior.balinasoft.com/api/image")
            .post(requestBody)
            .addHeader("accept", "application/json;charset=UTF-8")
            .addHeader(
                "Access-Token",
                "6U5f0Hvae8OgyKpfWdnnW7l3Euvdw0TlFrlztYubaVZ59J4tKIfjd23MaAbWseXP"
            )
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Обработка ошибок
                Log.d("D1le", e.printStackTrace().toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("D1le", response.body?.string().toString())
            }
        })
    }

    fun drawableToBase64(context: Context?, drawableId: Int): String {
        val drawable = context?.getDrawable(drawableId)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val imageBytes = outputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    override fun onCommentLongClick(v: View, commentDtoOut: CommentDtoOut) {
        Log.d("D1le", "Long click: ${commentDtoOut.text} imageid: ${image?.id}")
    }
}
