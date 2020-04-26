package am.falconry.clientlist

import am.falconry.HomeViewPagerFragmentDirections
import am.falconry.R
import am.falconry.database.FalconryDatabase
import am.falconry.databinding.FragmentClientsBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

class ClientsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application

        val binding: FragmentClientsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_clients, container, false)
        val dataSource = FalconryDatabase.getInstance(application).clientDatabaseDao
        val viewModelFactory = ClientsViewModelFactory(dataSource, application)
        val viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ClientsViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = ClientsAdapter(ClientClickListener { clientId ->
            viewModel.onClientClicked(clientId)
        })
        binding.clientList.adapter = adapter

        viewModel.clients.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.clients = it
            }
        })

        viewModel.navigateToClient.observe(viewLifecycleOwner, Observer { clientId ->
            clientId?.let {
                this.findNavController().navigate(
                    HomeViewPagerFragmentDirections.actionHomeViewPagerFragmentToNewClientFragment()
                        .setClientId(clientId)
                )
                viewModel.onClientDetailNavigated()
            }
        })

        return binding.root
    }
}
