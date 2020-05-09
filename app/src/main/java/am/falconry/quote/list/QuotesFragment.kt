package am.falconry.quote.list

import am.falconry.R
import am.falconry.database.FalconryDatabase
import am.falconry.database.quote.QuoteRepository
import am.falconry.databinding.FragmentQuotesBinding
import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController

class QuotesFragment : Fragment() {

    private lateinit var viewModel: QuotesViewModel
    private lateinit var binding: FragmentQuotesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application

        val arguments = QuotesFragmentArgs.fromBundle(requireArguments())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quotes, container, false)
        viewModel = setupViewModel(application, arguments.clientId)

        binding.topAppBar.setNavigationOnClickListener { view ->
            view.findNavController().navigate(QuotesFragmentDirections.actionQuotesFragmentToClientFragment().setClientId(arguments.clientId))
        }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupQuotesList(binding)

        return binding.root
    }

    private fun setupViewModel(application: Application, clientId: Long): QuotesViewModel {
        val dataSource = FalconryDatabase.getInstance(application)
        val viewModelFactory = QuotesViewModelFactory(QuoteRepository(dataSource), clientId)
        return ViewModelProviders.of(this, viewModelFactory).get(QuotesViewModel::class.java)
    }

    private fun setupQuotesList(binding: FragmentQuotesBinding) {
        val adapter = QuoteAdapter(QuoteClickListener { quoteId ->
            viewModel.onQuoteClicked(quoteId)
        })
        binding.quoteList.adapter = adapter

        viewModel.quotes.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.quotes = it
            }
        })
    }
}
