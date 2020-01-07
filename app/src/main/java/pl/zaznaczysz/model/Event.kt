package pl.zaznaczysz.model

class Event {

    var id_event: Int
    var name: String
    var description: String
    var id_group: Int


    constructor(
        id_event: Int,
        name: String,
        description: String,
        id_group: Int
    ) {
        this.id_event = id_event
        this.name = name
        this.description = description
        this.id_group = id_group
    }

}