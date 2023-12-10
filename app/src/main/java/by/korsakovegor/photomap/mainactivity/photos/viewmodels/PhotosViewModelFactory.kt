package by.korsakovegor.photomap.mainactivity.photos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.korsakovegor.photomap.models.SignUserOutDto

class PhotosViewModelFactory(private val user: SignUserOutDto) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotosViewModel::class.java)) {
            return PhotosViewModel(user) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}