package pl.zaznaczysz.model

class Activity {
    var id_user: Int
    var id_group: Int
    var points: Int
    var username: String? = null

    constructor(
        id_user: Int,
        id_group: Int,
        points: Int,
        username: String?
    ) {
        this.id_user = id_user
        this.id_group = id_group
        this.points = points
        this.username = username

    }

}