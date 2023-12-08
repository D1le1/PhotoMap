package by.korsakovegor.photomap.authactivity.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.korsakovegor.photomap.authactivity.fragments.LoginFragment
import by.korsakovegor.photomap.authactivity.fragments.RegisterFragment

class ViewPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position)
        {
            0 -> LoginFragment()
            else -> RegisterFragment()
        }
    }

}