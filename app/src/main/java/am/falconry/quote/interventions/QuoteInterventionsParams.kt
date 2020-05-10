package am.falconry.quote.interventions

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Parcelize
data class QuoteInterventionsParams(
    var quoteId: Long,
    var date: LocalDate
) : Parcelable