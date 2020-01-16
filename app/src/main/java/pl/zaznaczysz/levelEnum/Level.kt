package pl.zaznaczysz.levelEnum

enum class Level {
    FIRST,
    SECOND,
    THIRD,
    FOURTH,
    FIFTH,
    SIXTH,
    SEVENTH;


    companion object {
        fun getLevel(points: Int): Level {

            if (points > 110)
                return SEVENTH
            else if (points > 75)
                return SIXTH
            else if (points > 50)
                return FIFTH
            else if (points > 30)
                return FOURTH
            else if (points > 15)
                return THIRD
            else if (points > 5)
                return SECOND


            return FIRST
        }
    }


}