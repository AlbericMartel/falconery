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
    var clientId: Long,
    var name: String
) {
    companion object {
        fun newInterventionZone(): InterventionZone {
            return InterventionZone(0L, 0L, "")
        }
    }
}

data class InterventionPoint(
    var interventionPointId: Long,
    var interventionZoneId: Long,
    var name: String
) {
    companion object {
        fun newInterventionPoint(interventionZoneId: Long): InterventionPoint {
            return InterventionPoint(0L, interventionZoneId, "")
        }
    }
}