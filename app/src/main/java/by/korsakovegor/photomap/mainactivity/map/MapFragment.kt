package by.korsakovegor.photomap.mainactivity.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.korsakovegor.photomap.databinding.ActivityMainBinding
import by.korsakovegor.photomap.databinding.FragmentMapLayoutBinding

class MapFragment: Fragment() {
    private lateinit var binding: FragmentMapLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }
}