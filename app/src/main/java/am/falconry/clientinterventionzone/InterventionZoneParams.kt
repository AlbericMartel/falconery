package am.falconry.clientinterventionzone

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InterventionZoneParams(
    var clientId: Long,
    var interventionZoneId: Long
) : Parcelable