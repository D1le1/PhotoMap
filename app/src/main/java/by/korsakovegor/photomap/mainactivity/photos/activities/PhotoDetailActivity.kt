package by.korsakovegor.photomap.mainactivity.photos.activities

import android.os.Build
import android.os.Bundle
import android.os.VibratorManager
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.korsakovegor.photomap.R
import by.korsakovegor.photomap.databinding.DetailPhotoLayoutBinding
import by.korsakovegor.photomap.mainactivity.photos.adapters.CommentsRecyclerAdapter
import by.korsakovegor.photomap.mainactivity.photos.viewmodels.PhotosViewModel
import by.korsakovegor.photomap.models.CommentDtoIn
import by.korsakovegor.photomap.models.CommentDtoOut
import by.korsakovegor.photomap.models.ImageDtoOut
import by.korsakovegor.photomap.models.SignUserOutDto
import by.korsakovegor.photomap.utils.MainDb
import by.korsakovegor.photomap.utils.Utils
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoDetailActivity : AppCompatActivity(),
    CommentsRecyclerAdapter.OnCommentLongClickListener {
    private lateinit var binding: DetailPhotoLayoutBinding
    private var image: ImageDtoOut? = null
    private lateinit var viewModel: PhotosViewModel
    private var page: Int = 0
    private var isOnBottom = false
    private lateinit var db: MainDb


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailPhotoLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = MainDb.getInstance(this)

        val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("user", SignUserOutDto::class.java)
        } else
            intent.getSerializableExtra("user") as SignUserOutDto


        viewModel = ViewModelProvider(this)[PhotosViewModel::class.java]
        viewModel.setUserToken(user?.token ?: "")

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        loadImageData()
        if (Utils.isInternetAvailable(this)) {
            page = 0
            viewModel.getComments(image?.id, page)
            binding.swipeRefreshLayout.isRefreshing = true
        }

        val recycler = binding.recyclerView
        val layoutManager = LinearLayoutManager(this)
        recycler.layoutManager = layoutManager

        val adapter = CommentsRecyclerAdapter()
        adapter.setOnCommentLongClick(this)
        recycler.adapter = adapter

        viewModel.comments.observe(this) {
            binding.swipeRefreshLayout.isRefreshing = false
            if (it != null) {
                if (it.size > 0) {
                    if (page == 0)
                        adapter.updateData(it)
                    else
                        adapter.addData(it)
                    page++
                    isOnBottom = false
                }
                CoroutineScope(Dispatchers.IO).launch {
                    db.getCommentsDao().insertNewComments(it)
                }
            }
        }

        viewModel.comment.observe(this) {
            binding.swipeRefreshLayout.isRefreshing = false
            if (it != null) {
                adapter.addItem(it)
                binding.commentEditText.text.clear()
                CoroutineScope(Dispatchers.IO).launch {
                    db.getCommentsDao().insertNewComment(it)
                }
            }
        }

        viewModel.error.observe(this) {
            binding.swipeRefreshLayout.isRefreshing = false
            if (it.isEmpty()) {
                Utils.showConnectionAlertDialog(this)
            } else {
                Utils.showAlertDialog(this, "Error", it)
            }
        }

        viewModel.deletedItem.observe(this) {
            val comment = adapter.deleteItem(it)
            CoroutineScope(Dispatchers.IO).launch{
                db.getCommentsDao().deleteComment(comment)
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.sendButton.setOnClickListener {
            val anim = AnimationUtils.loadAnimation(this, R.anim.button_state)
            it.startAnimation(anim)

            if (binding.commentEditText.text.isEmpty())
                binding.commentEditText.error = "Comment may not be empty"
            else
                if (Utils.isInternetAvailable(this)) {
                    binding.swipeRefreshLayout.isRefreshing = true
                    viewModel.sendComment(
                        CommentDtoIn(binding.commentEditText.text.toString()),
                        image?.id
                    )
                } else
                    Utils.showConnectionAlertDialog(this)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (Utils.isInternetAvailable(this)) {
                page = 0
                viewModel.getComments(image?.id, page)
            } else {
                Utils.showConnectionAlertDialog(this)
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recycler.canScrollVertically(1) && !isOnBottom) {
                    isOnBottom = true
                    if (Utils.isInternetAvailable(this@PhotoDetailActivity)) {
                        binding.swipeRefreshLayout.isRefreshing = true
                        viewModel.getComments(image?.id, page)
                    } else
                        isOnBottom = false
                }
            }
        })
    }

    private fun loadImageData() {
        image = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("image", ImageDtoOut::class.java)
        } else
            intent.getSerializableExtra("image") as ImageDtoOut
        Picasso.get().load(image?.url).into(binding.photo)
        binding.time.text = Utils.getFormattedDateTime(image?.date ?: 0)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCommentLongClick(comment: CommentDtoOut, pos: Int) {
        val vib = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
        Utils.doVibrate(vib)

        Utils.showAlertDialog(this, "Delete Alert", "Are you sure you want to delete comment?")
        { _, _ ->
            if (Utils.isInternetAvailable(this)) {
                viewModel.deleteComment(comment, image?.id, pos)
                binding.swipeRefreshLayout.isRefreshing = true
            } else {
                Utils.showConnectionAlertDialog(this)
            }
        }
    }
}
