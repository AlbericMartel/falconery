package am.falconry.client

import am.falconry.R
import am.falconry.database.FalconryDatabase
import am.falconry.databinding.FragmentClientBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

class ClientFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application

        val arguments = ClientFragmentArgs.fromBundle(requireArguments())

        val binding: FragmentClientBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_client, container, false)
        val dataSource = FalconryDatabase.getInstance(application).clientDatabaseDao
        val viewModelFactory = ClientViewModelFactory(arguments.clientId, dataSource, application)
        val clientViewModel = ViewModelProviders.of(this, viewModelFactory).get(ClientViewModel::class.java)

        binding.lifecycleOwner = this
        binding.clientViewModel = clientViewModel

        val adapter = ClientLocationAdapter()
        binding.locationsList.adapter = adapter

        clientViewModel.locations.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })

        clientViewModel.navigateToClientList.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(ClientFragmentDirections.actionNewClientFragmentToClientsFragment())
                clientViewModel.doneNavigatingToClientList()
            }
        })

        return binding.root
    }
}
