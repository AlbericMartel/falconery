package am.falconry.domain

data class Client (
    var clientId: Long,
    var name: String,
    var email: String
)

data class Location (
    var locationId: Long,
    var name: String,
    var trapping: Boolean,
    var scaring: Boolean
)

class ClientFactory {

    companion object {
        fun newClient(): Client {
            return Client(0L, "", "")
        }

        fun newLocation(): Location {
            return Location(0L, "", trapping = false, scaring = false)
        }
    }
}