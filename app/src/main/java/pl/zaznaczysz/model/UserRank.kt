package pl.zaznaczysz.model

class UserRank {

    var username: String? = null
    var activity_points = 0

    constructor(
        username: String?,
        activity_points: Int
    ) {
        this.username = username
        this.activity_points = activity_points
    }

    constructor() {}

}