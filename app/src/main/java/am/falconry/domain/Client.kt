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