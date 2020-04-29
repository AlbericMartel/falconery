package am.falconry.quotelist

import am.falconry.HomeViewPagerFragmentDirections
import am.falconry.R
import am.falconry.database.FalconryDatabase
import am.falconry.database.quote.QuoteRepository
import am.falconry.databinding.FragmentQuotesBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

class QuotesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application

        val binding: FragmentQuotesBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_quotes, container, false)
        val dataSource = FalconryDatabase.getInstance(application)
        val viewModelFactory = QuotesViewModelFactory(
            QuoteRepository(dataSource), application)
        val viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(QuotesViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = QuotesAdapter(QuoteClickListener { quoteId ->
            viewModel.onQuoteClicked(quoteId)
        })
        binding.quoteList.adapter = adapter

        viewModel.quotes.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.quotes = it
            }
        })

        viewModel.navigateToQuote.observe(viewLifecycleOwner, Observer { quoteId ->
            quoteId?.let {
                this.findNavController().navigate(
                    HomeViewPagerFragmentDirections.actionHomeViewPagerFragmentToQuoteFragment()
                )
                viewModel.onQuoteDetailNavigated()
            }
        })

        return binding.root
    }
}
