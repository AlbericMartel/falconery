package am.falconry.quote.interventiondates

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InterventionDatesParams(
    var clientId: Long,
    var quoteId: Long
) : Parcelable