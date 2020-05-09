package am.falconry.client.interventionzone

import am.falconry.R
import am.falconry.database.FalconryDatabase
import am.falconry.database.client.ClientRepository
import am.falconry.databinding.FragmentClientInterventionZoneBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

class ClientInterventionZoneFragment : Fragment() {

    private lateinit var viewModel: ClientInterventionZoneViewModel
    private lateinit var binding: FragmentClientInterventionZoneBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application

        val arguments = ClientInterventionZoneFragmentArgs.fromBundle(requireArguments())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_intervention_zone, container, false)
        val repository = ClientRepository(FalconryDatabase.getInstance(application))
        val viewModelFactory = ClientInterventionZoneViewModelFactory(arguments.interventionZoneParams, repository)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ClientInterventionZoneViewModel::class.java)

        binding.topAppBar.setNavigationOnClickListener { goToClientFragment(arguments.interventionZoneParams.clientId) }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupInterventionPointObserver(viewModel, binding)

        viewModel.goToClient.observe(viewLifecycleOwner, Observer {
            it?.let {
                goToClientFragment(it)
            }
        })

        return binding.root
    }

    private fun goToClientFragment(clientId: Long) {
        this.findNavController().navigate(ClientInterventionZoneFragmentDirections.actionClientInterventionZoneFragmentToClientFragment().setClientId(clientId))
        viewModel.doneGoToClient()
    }

    private fun setupInterventionPointObserver(viewModel: ClientInterventionZoneViewModel, binding: FragmentClientInterventionZoneBinding) {
        val adapter = ClientInterventionPointAdapter()
        binding.interventionPointsList.adapter = adapter

        viewModel.loadedInterventionPoints.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.interventionPoints.value = it.toMutableList()
            }
        })

        viewModel.interventionPoints.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })
    }
}
