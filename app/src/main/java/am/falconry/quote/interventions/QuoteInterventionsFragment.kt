package am.falconry.quote.interventions

import am.falconry.R
import am.falconry.database.FalconryDatabase
import am.falconry.database.quote.QuoteRepository
import am.falconry.databinding.FragmentQuoteInterventionsBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController

class QuoteInterventionsFragment : Fragment() {

    private lateinit var viewModel: QuoteInterventionsViewModel
    private lateinit var binding: FragmentQuoteInterventionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application

        val arguments = QuoteInterventionsFragmentArgs.fromBundle(requireArguments())

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quote_interventions, container, false)
        val quoteRepository = QuoteRepository(FalconryDatabase.getInstance(application))
        val viewModelFactory = QuoteInterventionsViewModelFactory(arguments.params.quoteId, arguments.params.date, quoteRepository)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(QuoteInterventionsViewModel::class.java)

        binding.topAppBar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupInterventionPointsList()

        return binding.root
    }

    private fun setupInterventionPointsList() {
        val adapter = QuoteInterventionsAdapter()
        binding.interventionsList.adapter = adapter

        viewModel.interventions.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })
    }
}
