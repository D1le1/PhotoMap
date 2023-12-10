package by.korsakovegor.photomap.authactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.korsakovegor.photomap.R
import by.korsakovegor.photomap.authactivity.adapters.ViewPagerAdapter
import by.korsakovegor.photomap.authactivity.fragments.RegisterFragment
import by.korsakovegor.photomap.databinding.ActivityAuthBinding
import com.google.android.material.tabs.TabLayoutMediator

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = binding.viewPager
        val viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter

        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            tab.text = getTitles(pos)
        }.attach()
    }

    private fun getTitles(pos: Int): String {
        return if(pos == 0) "Login" else "Register"
    }
}