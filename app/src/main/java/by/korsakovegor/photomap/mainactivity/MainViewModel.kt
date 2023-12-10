package by.korsakovegor.photomap.mainactivity

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.korsakovegor.photomap.models.CommentDtoOut

class MainViewModel: ViewModel() {
    private val _currentFragment = MutableLiveData<Fragment>()
    val currentFragment: LiveData<Fragment> get() = _currentFragment

    fun setFragment(fragment: Fragment){
        _currentFragment.postValue(fragment)
    }
}