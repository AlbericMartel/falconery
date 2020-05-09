package am.falconry.quote.interventiondates

import am.falconry.database.quote.QuoteRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class QuoteInterventionDatesViewModel(
    private val quoteId: Long,
    private val quoteRepository: QuoteRepository
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var quote = quoteRepository.getQuote(quoteId)

    private val _showDatePicker = MutableLiveData(false)
    private val _dateToAdd = MutableLiveData<LocalDate>()
    val dateToAdd = formatDateToAdd()

    val interventionDates = quoteRepository.getAllInterventionDatesForQuote(quoteId)

    private val _interventionDateToFill = MutableLiveData<LocalDate>()
    val interventionDateToFill: LiveData<LocalDate>
        get() = _interventionDateToFill

    val showDatePicker: LiveData<Boolean>
        get() = _showDatePicker

    fun selectInterventionDate(date: LocalDate) {
        _dateToAdd.postValue(date)
    }

    fun addInterventionDate() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                _dateToAdd.value?.let {
                    val existingDates = interventionDates.value
                    if (existingDates == null || !existingDates.contains(it)) {
                        quoteRepository.insertInterventionDate(quoteId, it)
                    }
                }
            }
        }
    }

    fun showInterventionDateChoice() {
        _showDatePicker.postValue(true)
    }

    fun doneShowDatePicker() {
        _showDatePicker.postValue(false)
    }

    private fun formatDateToAdd(): LiveData<String> {
        return Transformations.map(_dateToAdd) {
            if (it == null) {
                ""
            } else {
                it.format(dateFormatter)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun goToInterventionDetail(date: LocalDate) {
        _interventionDateToFill.postValue(date)
    }

    fun doneGoToInterventionDetail() {
        _interventionDateToFill.postValue(null)
    }
}