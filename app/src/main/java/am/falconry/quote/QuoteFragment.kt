package am.falconry.quote

import am.falconry.R
import am.falconry.client.ClientFragmentArgs
import am.falconry.database.FalconryDatabase
import am.falconry.database.quote.QuoteRepository
import am.falconry.databinding.FragmentQuoteBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

class QuoteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application

        val arguments = ClientFragmentArgs.fromBundle(requireArguments())

        val binding: FragmentQuoteBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_quote, container, false)
        val repository = QuoteRepository(FalconryDatabase.getInstance(application))
        val viewModelFactory = QuoteViewModelFactory(arguments.clientId, repository, application)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(QuoteViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.topAppBar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        viewModel.navigateToQuoteList.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController()
                    .navigate(QuoteFragmentDirections.actionQuoteFragmentToHomeViewPagerFragment())
                viewModel.doneNavigatingToQuoteList()
            }
        })

        return binding.root
    }
}
