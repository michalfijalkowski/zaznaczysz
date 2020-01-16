package pl.zaznaczysz.model

import pl.zaznaczysz.R
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.levelEnum.Level

class UserSettings {

    var id_user: Int
    var theme: String
    var task1: Int
    var task2: Int
    var task3: Int
    var task4: Int
    var task5: Int


    constructor() {
        this.id_user = 0
        this.theme = ""
        this.task1 = 0
        this.task2 = 0
        this.task3 = 0
        this.task4 = 0
        this.task5 = 0

    }

    constructor(
        id_user: Int,
        theme: String,
        task1: Int,
        task2: Int,
        task3: Int,
        task4: Int,
        task5: Int
    ) {
        this.id_user = id_user
        this.theme = theme
        this.task1 = task1
        this.task2 = task2
        this.task3 = task3
        this.task4 = task4
        this.task5 = task5
    }

    fun completed(): Int {
        var completed = 0

        if (task1 > 0) completed++
        if (task2 > 0) completed++
        if (task3 > 0) completed++
        if (task4 > 0) completed++
        if (task5 > 0) completed++

        return completed;
    }

    fun getTheme(): Int {
        Const.isPisture = false
        when (theme) {
            "white" -> return Const.WHITE
            "blue" -> return Const.BLUE
            "pink" -> return Const.PINK
            "green" -> return Const.GREEN
            "purple" -> return Const.PURPLE
            "yellow" -> return Const.YELLOW
            "ustro" -> {
                Const.isPisture = true
                return R.drawable.ustro
            }
            "beer" -> {
                Const.isPisture = true
                return Const.BEER
            }
        }

    return Const.WHITE
}

}