package am.falconry.quote.interventiondates

import am.falconry.R
import am.falconry.database.FalconryDatabase
import am.falconry.database.quote.QuoteRepository
import am.falconry.databinding.FragmentQuoteInterventionDatesBinding
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import java.time.LocalDate

class QuoteInterventionDatesFragment : Fragment() {

    private lateinit var viewModel: QuoteInterventionDatesViewModel
    private lateinit var binding: FragmentQuoteInterventionDatesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application

        val arguments = QuoteInterventionDatesFragmentArgs.fromBundle(requireArguments())

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quote_intervention_dates, container, false)
        val quoteRepository = QuoteRepository(FalconryDatabase.getInstance(application))
        val viewModelFactory = QuoteInterventionDatesViewModelFactory(arguments.params.quoteId, quoteRepository)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(QuoteInterventionDatesViewModel::class.java)

        binding.topAppBar.setNavigationOnClickListener { view ->
            view.findNavController().navigate(QuoteInterventionDatesFragmentDirections.actionQuoteInterventionDatesFragmentToQuotesFragment(arguments.params.clientId))
        }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupInterventionDatePicker()

        setupInterventionDatesList()

        return binding.root
    }

    private fun setupInterventionDatePicker() {
        with(binding.newInterventionDate) {
            this.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    viewModel.showInterventionDateChoice()
                }
            }

            this.setOnClickListener {
                viewModel.showInterventionDateChoice()
            }
        }

        viewModel.showDatePicker.observe(viewLifecycleOwner, Observer {
            it?.let { show ->
                if (show) {
                    val datePicker = DatePickerFragment(
                        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            viewModel.selectInterventionDate(LocalDate.of(year, month + 1, dayOfMonth))
                        })
                    datePicker.show(parentFragmentManager, "intervention DatePicker");
                    viewModel.doneShowDatePicker()
                }
            }
        })
    }

    private fun setupInterventionDatesList() {
        val adapter = ClientInterventionZonesAdapter(
            InterventionZoneClickListener { interventionZoneId ->
                viewModel.goToInterventionDetail(interventionZoneId)
            })
        binding.interventionDatesList.adapter = adapter

        viewModel.interventionDates.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })

//        viewModel.interventionDateToFill.observe(viewLifecycleOwner, Observer { date ->
//            date?.let {
//                this.findNavController()
//                    .navigate(QuoteInterventionDatesFragmentDirections.(it))
//                viewModel.doneGoToInterventionDetail()
//            }
//        })
    }
}
