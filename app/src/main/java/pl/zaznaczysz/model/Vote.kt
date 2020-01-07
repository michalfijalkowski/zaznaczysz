package pl.zaznaczysz.model

class Vote {
    var id_user: Int
    var id_event: Int
    var id_proposition: Int


    constructor(
        id_user: Int,
        id_event: Int,
        id_proposition: Int

    ) {
        this.id_user = id_user
        this.id_event = id_event
        this.id_proposition = id_proposition

    }

}