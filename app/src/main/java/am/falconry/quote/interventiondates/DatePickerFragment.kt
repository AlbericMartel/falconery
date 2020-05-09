package am.falconry.quote.interventiondates

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.time.LocalDate


class DatePickerFragment(private val dateSetListener: OnDateSetListener) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val now = LocalDate.now()
        return DatePickerDialog(requireContext(), dateSetListener, now.year, now.month.value, now.dayOfMonth)
    }
}