package pl.zaznaczysz.model

class Comment {


    var id_comment: Int
    var text: String
    var username: String
    var id_proposition: Int


    constructor(

        id_comment: Int,
        text: String,
        username: String,
        id_proposition: Int

    ) {

        this.id_comment = id_comment
        this.text = text
        this.username = username
        this.id_proposition = id_proposition

    }

}