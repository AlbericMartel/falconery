package am.falconry.client

import am.falconry.R
import am.falconry.database.FalconryDatabase
import am.falconry.database.client.ClientRepository
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
        val repository = ClientRepository(FalconryDatabase.getInstance(application))
        val viewModelFactory = ClientViewModelFactory(arguments.clientId, repository)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ClientViewModel::class.java)

        binding.topAppBar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        binding.lifecycleOwner = this
        binding.clientViewModel = viewModel

        setupLocationsObservers(viewModel, binding)

        viewModel.navigateToClientList.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController()
                    .navigate(ClientFragmentDirections.actionClientFragmentToHomeViewPagerFragment())
                viewModel.doneNavigatingToClientList()
            }
        })

        return binding.root
    }

    private fun setupLocationsObservers(viewModel: ClientViewModel, binding: FragmentClientBinding) {
        val adapter = ClientLocationAdapter(
            LocationOptionClickListener { locationId, checked ->
                viewModel.updateTrappingOption(locationId, checked)
            }, LocationOptionClickListener { locationId, checked ->
                viewModel.updateScaringOption(locationId, checked)
            })
        binding.locationsList.adapter = adapter

        viewModel.loadedLocations.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.locations.value = it.toMutableList()
            }
        })

        viewModel.locations.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })
    }
}
