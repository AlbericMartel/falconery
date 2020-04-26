package am.falconry

import am.falconry.databinding.FragmentViewPagerBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.adapter = PagerAdapter(this)

        // Set the icon and text for each tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
        }.attach()

        (activity as AppCompatActivity).setSupportActionBar(binding.topAppBar)

        return binding.root
    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            CLIENTS_PAGE_INDEX -> R.drawable.ic_people_black_24dp
            QUOTES_PAGE_INDEX -> R.drawable.ic_assignment_black_24dp
            else -> throw IndexOutOfBoundsException()
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            CLIENTS_PAGE_INDEX -> getString(R.string.clientsScreen)
            QUOTES_PAGE_INDEX -> getString(R.string.quotesScreen)
            else -> null
        }
    }
}