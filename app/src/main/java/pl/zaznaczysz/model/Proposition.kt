package pl.zaznaczysz.model

class Proposition {

    var id_proposition: Int
    var name: String
    var description: String
    var photo_name: String
    var vote_count: Int
    var id_user: Int
    var id_event: Int
    var promotion: Int

    constructor(){
    this.id_proposition = 0
    this.name = ""
    this.description = ""
    this.photo_name = ""
    this.vote_count = 0
    this.id_user = 0
    this.id_event = 0
    this.promotion = 0
}
    
    constructor(
        id_proposition: Int,
        name: String,
        description: String,
        photo_name: String,
        vote_count: Int,
        id_user: Int,
        id_event: Int,
        promotion: Int
    ) {
        this.id_proposition = id_proposition
        this.name = name
        this.description = description
        this.photo_name = photo_name
        this.vote_count = vote_count
        this.id_user = id_user
        this.id_event = id_event
        this.promotion = promotion
    }

}