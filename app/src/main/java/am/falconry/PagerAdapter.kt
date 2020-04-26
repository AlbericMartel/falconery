package am.falconry

import am.falconry.clientlist.ClientsFragment
import am.falconry.quotelist.QuotesFragment
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

const val CLIENTS_PAGE_INDEX = 0
const val QUOTES_PAGE_INDEX = 1

class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        CLIENTS_PAGE_INDEX to { ClientsFragment() },
        QUOTES_PAGE_INDEX to { QuotesFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}