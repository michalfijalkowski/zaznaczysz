package pl.zaznaczysz.cfg;

import android.graphics.Color;
import android.view.View;

import pl.zaznaczysz.R;

public class Const {

    //online
    public static final String SERVICE_USER   =  "http://zaznaczysz.appspot.com/api/User";
    public static final String SERVICE_GROUP   = "http://zaznaczysz.appspot.com/api/Group";
    public static final String SERVICE_EVENT  = "http://zaznaczysz.appspot.com/api/Event";
    public static final String SERVICE_PROPOSITION   = "http://zaznaczysz.appspot.com/api/Proposition";
    public static final String SERVICE_VOTE  = "http://zaznaczysz.appspot.com/api/Vote";
    public static final String SERVICE_COMMENT  = "http://zaznaczysz.appspot.com/api/Comment";
    public static final String SERVICE_JOIN  = "http://zaznaczysz.appspot.com/api/Join";
    public static final String SERVICE_ACTIVITY  = "http://zaznaczysz.appspot.com/api/Activity";
    public static final String SERVICE_USERSETTINGS  = "http://zaznaczysz.appspot.com/api/UserSettings";

    public static boolean CHALLENGE_DONE  = false;

    public static boolean isPisture = false;

    public static int MAIN_COLOR;
    public static final int BLUE = Color.parseColor("#ccebff");
    public static final int PINK = Color.parseColor("#f1dae3");
    public static final int GREEN = Color.parseColor("#ddff99");
    public static final int PURPLE = Color.parseColor("#dab3ff");
    public static final int YELLOW = Color.parseColor("#ffffb3");
    public static final int WHITE = Color.WHITE;

    public static final int BEER = R.drawable.beer;
    public static final int USTRO = R.drawable.ustro;
    public static final int LOVE = R.drawable.love;
    public static final int XXX = R.drawable.xxx;

    public static void setBackground(View layout) {
        if (isPisture)
            layout.setBackgroundResource(MAIN_COLOR);
        else
            layout.setBackgroundColor(MAIN_COLOR);

    }

    public static String getColorText() {
        if(MAIN_COLOR == WHITE) return "white";
        else if(MAIN_COLOR == BLUE) return  "blue";
        else if(MAIN_COLOR == PINK) return  "pink";
        else if(MAIN_COLOR == PURPLE) return  "purple";
        else if(MAIN_COLOR == GREEN) return  "green";
        else if(MAIN_COLOR == YELLOW) return  "yellow";
        else if(MAIN_COLOR == BEER) return  "beer";
        else if(MAIN_COLOR == USTRO) return  "ustro";
        else
            return "white";

    }
    
    




//     public static final String SERVICE_USER   = "http://127.0.0.1:8080/api/User";
//        public static final String SERVICE_GROUP   = "http://127.0.0.1:8080/api/Group";
//        public static final String SERVICE_EVENT  = "http://127.0.0.1:8080/api/Event";
//        public static final String SERVICE_PROPOSITION   = "http://127.0.0.1:8080/api/Proposition";
    //
    //    public static final String SERVICE_EVENT  = "http://10.0.0.1:3000/api/Event";
    //    public static final String SERVICE_USER   = "http://10.0.0.1:3000/api/User";
    //    public static final String SERVICE_GROUP   = "http://10.0.0.1:3000/api/Group";
    //    public static final String SERVICE_PROPOSITION   = "http://10.0.0.1:3000/api/Proposition";
}
