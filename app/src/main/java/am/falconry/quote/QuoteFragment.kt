package am.falconry.quote

import am.falconry.R
import am.falconry.client.ClientFragmentArgs
import am.falconry.database.FalconryDatabase
import am.falconry.database.client.ClientRepository
import am.falconry.database.quote.QuoteRepository
import am.falconry.databinding.FragmentQuoteBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController


class QuoteFragment : Fragment() {

    private lateinit var binding: FragmentQuoteBinding
    private lateinit var viewModel: QuoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application

        val arguments = ClientFragmentArgs.fromBundle(requireArguments())

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quote, container, false)
        val database = FalconryDatabase.getInstance(application)
        val clientRepository = ClientRepository(database)
        val quoteRepository = QuoteRepository(database)
        val viewModelFactory = QuoteViewModelFactory(arguments.clientId, clientRepository, quoteRepository, application)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(QuoteViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.topAppBar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        setupClientsDropDown()
        setupClientLocationsDropDown()
        setupLocations()

        viewModel.navigateToQuoteList.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController()
                    .navigate(QuoteFragmentDirections.actionQuoteFragmentToHomeViewPagerFragment())
                viewModel.doneNavigatingToQuoteList()
            }
        })

        return binding.root
    }

    private fun setupClientsDropDown() {
//        val adapter = ClientAdapter(
//            requireContext(),
//            R.layout.dropdown_menu_popup_item,
//            mutableListOf()
//        )
//
//        binding.clientSelect.setAdapter(adapter)
//
//        viewModel.clients.observe(viewLifecycleOwner, Observer {
//            adapter.addAll(it)
//        })
//
//        binding.clientSelect.onItemClickListener = OnItemClickListener { parent, _, position, id ->
//            val client = parent.getItemAtPosition(position) as Client
//            viewModel.onSelectClient(client)
//        }

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            mutableListOf<String>()
        )

        binding.clientSelect.setAdapter(adapter)

        viewModel.clientNames.observe(viewLifecycleOwner, Observer {
            adapter.addAll(it)
        })

        binding.clientSelect.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, id ->
            val clientName = parent.getItemAtPosition(position).toString()
            viewModel.onSelectClient(clientName)
        }
    }

    private fun setupClientLocationsDropDown() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            mutableListOf<String>()
        )

        binding.locationSelect.setAdapter(adapter)

        viewModel.locationNames.observe(viewLifecycleOwner, Observer {
            adapter.addAll(it)
        })

        binding.locationSelect.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val locationName = parent.getItemAtPosition(position).toString()
            viewModel.onSelectLocation(locationName)
        }
    }

    private fun setupLocations() {
        val adapter = QuoteLocationsAdapter(
            QuoteLocationOptionClickListener { locationId, checked ->
                viewModel.updateTrappingOption(locationId, checked)
            }, QuoteLocationOptionClickListener { locationId, checked ->
                viewModel.updateScaringOption(locationId, checked)
            })
        binding.locationsList.adapter = adapter

        viewModel.quoteLocations.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })
    }
}
