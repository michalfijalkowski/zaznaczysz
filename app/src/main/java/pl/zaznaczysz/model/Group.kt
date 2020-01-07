package pl.zaznaczysz.model

class Group {

    var id_group: Int
    var name: String
    var description: String


    constructor(
        id_group: Int,
        name: String,
        description: String
    ) {
        this.id_group = id_group
        this.name = name
        this.description = description
    }

}