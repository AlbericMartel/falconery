package am.falconry.client

import am.falconry.R
import am.falconry.database.FalconryDatabase
import am.falconry.database.client.ClientRepository
import am.falconry.database.quote.QuoteRepository
import am.falconry.databinding.FragmentClientBinding
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

class ClientFragment : Fragment() {

    private lateinit var viewModel: ClientViewModel
    private lateinit var binding: FragmentClientBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application

        val arguments = ClientFragmentArgs.fromBundle(requireArguments())

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_client, container, false)
        val clientRepository = ClientRepository(FalconryDatabase.getInstance(application))
        val quoteRepository = QuoteRepository(FalconryDatabase.getInstance(application))
        val viewModelFactory = ClientViewModelFactory(arguments.clientId, clientRepository, quoteRepository)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ClientViewModel::class.java)

        binding.topAppBar.setNavigationOnClickListener { view ->
            view.findNavController().navigate(ClientFragmentDirections.actionClientFragmentToHomeViewPagerFragment())
        }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupInterventionZonesList()

        viewModel.goToClientList.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController()
                    .navigate(ClientFragmentDirections.actionClientFragmentToHomeViewPagerFragment())
                viewModel.doneGoToClientList()
            }
        })

        return binding.root
    }

    private fun setupInterventionZonesList() {
        val adapter = ClientInterventionZonesAdapter(
            InterventionZoneClickListener { interventionZoneId ->
                viewModel.goToInterventionZone(interventionZoneId)
            },
            InterventionZoneClickListener { interventionZoneId ->
                viewModel.createNewQuote(interventionZoneId)
            })
        binding.interventionZonesList.adapter = adapter

        viewModel.loadedInterventionZones.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })

        viewModel.goToInterventionZone.observe(viewLifecycleOwner, Observer { interventionZoneId ->
            interventionZoneId?.let {
                this.findNavController()
                    .navigate(ClientFragmentDirections.actionClientFragmentToClientInterventionZoneFragment(it))
                viewModel.doneGoToInterventionZone()
            }
        })
    }
}
