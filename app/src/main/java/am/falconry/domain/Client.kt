package am.falconry.domain

data class Client(
    var clientId: Long,
    var name: String,
    var email: String
) {
    companion object {
        fun newClient(): Client {
            return Client(0L, "", "")
        }
    }
}

data class InterventionZone(
    var interventionZoneId: Long,
    var name: String,
    var trapping: Boolean,
    var scaring: Boolean
) {
    companion object {
        fun newInterventionZone(): InterventionZone {
            return InterventionZone(0L, "", trapping = false, scaring = false)
        }
    }
}