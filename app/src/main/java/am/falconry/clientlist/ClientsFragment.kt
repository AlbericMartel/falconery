package am.falconry.clientlist

import am.falconry.R
import am.falconry.database.FalconryDatabase
import am.falconry.databinding.FragmentClientsBinding
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class ClientsFragment : Fragment() {

//    private var listener: OnListFragmentInteractionListener? = null

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

        val adapter = ClientsAdapter()
        binding.clientList.adapter = adapter

        viewModel.clients.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.clients = it
            }
        })
//        val view = inflater.inflate(R.layout.fragment_clients, container, false)
//
//        // Set the adapter
//        if (view is RecyclerView) {
//            with(view) {
//                layoutManager = when {
//                    columnCount <= 1 -> LinearLayoutManager(context)
//                    else -> GridLayoutManager(context, columnCount)
//                }
//                adapter = ClientsAdapter(DummyContent.ITEMS, listener)
//            }
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnListFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
//        listener = null
    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     *
//     *
//     * See the Android Training lesson
//     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
//     * for more information.
//     */
//    interface OnListFragmentInteractionListener {
//        fun onListFragmentInteraction(client: Client?)
//    }
//
//    companion object {
//
//        // TODO: Customize parameter argument names
//        const val ARG_COLUMN_COUNT = "column-count"
//
//        // TODO: Customize parameter initialization
//        @JvmStatic
//        fun newInstance(columnCount: Int) =
//            ClientListFragment().apply {
//                arguments = Bundle().apply {
//                    putInt(ARG_COLUMN_COUNT, columnCount)
//                }
//            }
//    }
}
