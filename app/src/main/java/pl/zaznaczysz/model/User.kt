package pl.zaznaczysz.model

class User {

    var id_user = 0
    var username: String
    var password: String
    var activity_points = 0

    constructor(
        id_user: Int,
        username: String,
        password: String,
        activity_points: Int
    ) {
        this.id_user = id_user
        this.username = username
        this.password = password
        this.activity_points = activity_points
    }

}