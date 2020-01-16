package pl.zaznaczysz

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.getIntent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_proposition.*
import org.jetbrains.anko.custom.onUiThread
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.Proposition
import pl.zaznaczysz.model.User
import pl.zaznaczysz.model.Vote
import pl.zaznaczysz.provider.*
import pl.zaznaczysz.tool.CommonTool
import java.io.File


class PropositionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proposition)
        Const.setBackground(propositionRelativeLayout)
        loadControls(getIntent().getIntExtra("propositionId", 0))
    }

    override fun onResume() {
        super.onResume()

    }

    @SuppressLint("RestrictedApi")
    fun loadControls(propositionId: Int) {
        var proposition: Proposition = Proposition()
        val userId = getIntent().getIntExtra("userId", 0)
        val eventId = getIntent().getIntExtra("eventId", 0)
        val groupId = getIntent().getIntExtra("groupId", 0)

        doAsync {
            val list: List<Proposition> =
                PropositionProvider.propositionList("where id_proposition = $propositionId")
            proposition = list.get(0)
            uiThread {
                if (proposition.id_user == userId)
                    fabPromotion.visibility = View.VISIBLE
                loadProposition(proposition)
            }
        }

        if (getIntent().getBooleanExtra("pickProposition", false)) {
            btnVote.isEnabled = false
            btnVote.setBackgroundColor(Color.parseColor("#03aa9f"))
        }


        btnVote.setOnClickListener {
            btnVote.isEnabled = false
            doAsync {
                val voteList =
                    VoteProvider.voteList("WHERE id_user = $userId AND id_event = $eventId")
                if (voteList.isNotEmpty())
                    PropositionProvider.updateVoteCount(voteList.get(0).id_proposition, -1, 0)
                else {
                    if (Const.CHALLENGE_DONE) {
                        CommonTool.updateUserActivity(userId, groupId, 3)
                    } else {
                        val userS = UserSettingsProvider.userSettingsList("WHERE id_user = $userId").get(0)

                        if (userS.task2 > 0) {
                            CommonTool.updateUserActivity(userId, groupId, 3)
                        } else {
                            CommonTool.updateUserActivity(userId, groupId, 7)

                            UserSettingsProvider.updateActivityUser("UPDATE public.user_settings SET task2 = 1 WHERE id_user = $userId")


                        }
                    }
                }

                VoteProvider.updateVote(userId, eventId, propositionId)
                PropositionProvider.updateVoteCount(propositionId, 1, 0)

                uiThread {
                    btnVote.setBackgroundColor(Color.parseColor("#03aa9f"))
                }
            }
        }

        btnComment.setOnClickListener {

            val intent = Intent(this, DiscussionActivity::class.java)
            intent.putExtra("propositionId", propositionId)
            intent.putExtra("groupId", getIntent().getIntExtra("groupId", 0))
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        fabPromotion.setOnClickListener {

            promoteConfirmation(propositionId, userId)

        }
    }

    private fun promote(propositionId: Int, userId: Int) {
        doAsync {
            PropositionProvider.updateVoteCount(propositionId, 0, 1)
            UserProvider.updateActivityUser(userId, -5)
            val proposition = PropositionProvider.propositionList("where id_proposition = $propositionId").get(0)

            uiThread {

                toast(
                    "Udało się! Aktualny poziom promowania propozycji: ${proposition.promotion}"
                )
            }
        }
    }

    private fun loadProposition(proposition: Proposition) {
        tvName.setText(proposition.name)
        tvDescription.setText((proposition.description))
    }

    private fun promoteConfirmation(propositionId: Int, userId: Int) {

        var user = User()
        val alertDialog = AlertDialog.Builder(this).create()
        doAsync {
            user = UserProvider.userList("WHERE id_user = $userId").get(0)
            uiThread {
                if (user.activity_points < 5) {
                    toast("Nie masz wystarczająco dużo punktów :( Stwórz propozycję, zagłosuj lub skomentuj aby je zdobyć!")
                } else {
                    alertDialog.setTitle("CZY NA PEWNO CHCESZ PROMOWAĆ PROPOZYCJĘ?")
                    alertDialog.setMessage(
                        "Zasady promowania propozycji:\n" +
                                "- poziom promowanie można zwiększać wiele razy\n" +
                                "- większy poziom promowania oznacza lepsze pozycjonowanie\n" +
                                "- koszt promowania propozycji to 5 pkt aktywności\n" +
                                "- pierwszy poziom promowania zmienia kolor propozycji\n" +
                                "- drugi poziom promowania zwiększa czcionkę propozycji"
                    )

                    alertDialog.setIcon(R.drawable.ic_promote)

                    alertDialog.setButton(
                        AlertDialog.BUTTON_POSITIVE, "Tak"
                    ) { dialog, which ->
                        dialog.dismiss()
                        promote(propositionId, userId)
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
            }


        }
    }
}
