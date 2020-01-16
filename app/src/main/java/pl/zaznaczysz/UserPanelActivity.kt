package pl.zaznaczysz

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_user_panel.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.levelEnum.Level
import pl.zaznaczysz.model.User
import pl.zaznaczysz.provider.UserProvider
import pl.zaznaczysz.provider.UserSettingsProvider
import pl.zaznaczysz.tool.CommonTool


class UserPanelActivity : AppCompatActivity() {

    var fistRun: Boolean = false
    var fistRunBackground: Boolean = false
    var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_panel)
        toast("Cześć! Dobrze, że jesteś z nami!")


    }

    override fun onResume() {
        super.onResume()
        userId = getIntent().getIntExtra("userId", 0)
        loadControls(userId)

    }

    @SuppressLint("RestrictedApi")
    fun loadControls(userId: Int) {

        doAsync {
            val user = UserProvider.userList("WHERE id_user = $userId;").get(0)

            val userS = UserSettingsProvider.userSettingsList("WHERE id_user = $userId;").get(0)

            uiThread {

                tvName.setText("Witaj ${user.username}")
                tvPoint.setText("Zdobyleś już ${user.activity_points}pkt")
                if (user.activity_points > 15) {
                    fabPaint.visibility = View.VISIBLE
                }



                if (!fistRunBackground) {
                    fistRunBackground = true
                    Const.MAIN_COLOR = userS.getTheme()

                }
                Const.setBackground(panelRelativeLayout)

                if (userS.completed() > 4) {
                    Const.CHALLENGE_DONE = true

                    if (userS.task1 < 2) {
                        gratulation()
                        tvPoint.setText("Zdobyleś już ${user.activity_points+50}pkt")
                        doAsync {
                            UserSettingsProvider.updateActivityUser("UPDATE public.user_settings SET task1 = 2, task2 = 2, task3 = 2, task4 = 2, task5 = 2 WHERE id_user = $userId")
                            CommonTool.updateUserActivity(userId, 0, 50)
                        }
                    }

                }

                if (!fistRun) {
                    fistRun = true
                    levelController(user.activity_points)
                }


            }
        }


        btnTask.setOnClickListener {
            val intent = Intent(this, ChallengeActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }


        btnGroup.setOnClickListener {
            val intent = Intent(this, GroupActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        btnJoin.setOnClickListener {
            toast("Jesteś dodany do odpowiednich grup.")

        }

        fabPaint.setOnClickListener {
            CreateAlertDialogWithRadioButtonGroup()
        }
    }

    fun levelController(points: Int) {

        val level = Level.getLevel(points)

        var message: String

        when (level) {
            Level.FIRST -> levelAcction("stopień: Kot", "yyy")
            Level.SECOND -> levelAcction("stopień: Student", "yyy")
            Level.THIRD -> levelAcction("stopień: Inżynier", "yyy")
            Level.FOURTH -> levelAcction("stopień: Magister", "yyy")
            Level.FIFTH -> levelAcction("stopień: Doktor", "yyy")
            Level.SEVENTH -> levelAcction("stopień: Profesor", "yyy")
        }
    }

    fun instruction() {
        btnGroup.setBackgroundColor(Color.BLUE)

        object : CountDownTimer(5000, 50) {
            override fun onTick(arg0: Long) { // TODO Auto-generated method stub
            }

            override fun onFinish() {
                btnGroup.setBackgroundColor(Color.RED)
            }
        }.start()


    }

    fun levelAcction(level: String, message: String) {
        tvLevel.setText(level)

        val mes = "W aplikacji możesz otrzymać punkty za określone działania:\n" +
                "* komentarz - 1 pkt\n" +
                "* oddanie głosu - 3 pkt\n" +
                "* stworzenie propozycji - 5pkt!"
        infoMessage(mes)
    }


    fun CreateAlertDialogWithRadioButtonGroup() {
        var alertDialog = AlertDialog.Builder(this).create()
        val values = arrayOf<CharSequence>(" Niebieski ", " Różowy ", " Zielony ", " Fioletowy ", " Żółty ", " Biały ", " Piwo ", " Ustro ", " Love ")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Wybierz motyw aplikacji:")
        builder.setSingleChoiceItems(values, -1, DialogInterface.OnClickListener { dialog, item ->
            if (item > 5)
                Const.isPisture = true
            else
                Const.isPisture = false

            when (item) {
                0 -> Const.MAIN_COLOR = Const.BLUE
                1 -> Const.MAIN_COLOR = Const.PINK
                2 -> Const.MAIN_COLOR = Const.GREEN
                3 -> Const.MAIN_COLOR = Const.PURPLE
                4 -> Const.MAIN_COLOR = Const.YELLOW
                5 -> Const.MAIN_COLOR = Const.WHITE
                6 -> Const.MAIN_COLOR = Const.BEER
                7 -> {
                    Const.MAIN_COLOR = Const.USTRO
                    doAsync {
                        if (Const.CHALLENGE_DONE) {
                        } else {
                            val userS = UserSettingsProvider.userSettingsList("WHERE id_user = $userId").get(0)

                            if (userS.task5 > 0) {
                            } else {
                                CommonTool.updateUserActivity(userId, 0, 7)

                                UserSettingsProvider.updateActivityUser("UPDATE public.user_settings SET task5 = 1 WHERE id_user = $userId")


                            }
                        }
                    }
                }
                8 -> Const.MAIN_COLOR = Const.XXX
            }
            alertDialog.dismiss()
            Const.setBackground(panelRelativeLayout)
        })
        alertDialog = builder.create()
        alertDialog.show()
    }


    fun infoMessage(message: String) {

        val alertDialog = AlertDialog.Builder(this).create()

        alertDialog.setTitle("DZIĘKUJE KOCHANI TESTERZY ZA POMOC!")
        alertDialog.setMessage(message)


        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "OK"
        ) { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.show()

        val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        btnNegative.setTextColor(Color.GRAY)
        btnNegative.textSize = 30f
        val layoutParams = btnNegative.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnNegative.layoutParams = layoutParams
    }

    fun gratulation() {

        val alertDialog = AlertDialog.Builder(this).create()

        alertDialog.setTitle("GRATULACJĘ")
        alertDialog.setMessage("Wykonałeś wszystkie zadania! Dostajesz extra 50 pkt! Możesz je wykorzystać do promowania swoich propozycji.")


        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "OK"
        ) { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.show()

        val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        btnNegative.setTextColor(Color.GRAY)
        btnNegative.textSize = 30f
        val layoutParams = btnNegative.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnNegative.layoutParams = layoutParams
    }

    override fun onBackPressed() {
        backConfirmation()
    }

    fun backConfirmation() {

        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("CZY CHCESZ SIĘ WYLOGOWAĆ?")

        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "Tak"
        ) { dialog, which ->
            dialog.dismiss()
            finish()
        }

        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE, "Nie"
        ) { dialog, which -> dialog.dismiss() }
        alertDialog.show()

        val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        btnPositive.setTextColor(Color.GRAY)
        btnPositive.textSize = 30f
        val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        btnNegative.setTextColor(Color.GRAY)
        btnNegative.textSize = 30f
        val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnPositive.layoutParams = layoutParams
        btnNegative.layoutParams = layoutParams
    }

    override fun onDestroy() {
        super.onDestroy()
        doAsync {
            UserSettingsProvider.updateActivityUser("UPDATE public.user_settings SET theme = '${Const.getColorText()}' WHERE id_user = $userId")
        }
    }


}
