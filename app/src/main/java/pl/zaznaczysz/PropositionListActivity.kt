package pl.zaznaczysz

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_proposition_list.*
import okhttp3.internal.proxy.NullProxySelector.select
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.Proposition
import pl.zaznaczysz.provider.PropositionProvider
import pl.zaznaczysz.provider.VoteProvider
import kotlin.random.Random


class PropositionListActivity : AppCompatActivity() {

    val ClickListener =
        View.OnClickListener { v ->
            propositionController(v.getTag().toString().toInt())
        }

    var pickProposition = 0
    var fistRun: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proposition_list)
        Const.setBackground(propositionListRelativeLayout)
    }

    override fun onResume() {
        super.onResume()
        setTitle(getIntent().getStringExtra("groupName") + " > " + getIntent().getStringExtra("eventName"))
        if ((propositionLinearLayout as LinearLayout).childCount > 0) (propositionLinearLayout as LinearLayout).removeAllViews()
        loadControls(getIntent().getIntExtra("eventId", 0))

    }

    private fun propositionController(propositionId: Int) {

        val intent = Intent(this, PropositionActivity::class.java)
        intent.putExtra("userId", getIntent().getIntExtra("userId", 0))
        intent.putExtra("eventId", getIntent().getIntExtra("eventId", 0))
        intent.putExtra("groupId", getIntent().getIntExtra("groupId", 0))
        intent.putExtra("propositionId", propositionId)
        if (propositionId == pickProposition)
            intent.putExtra("pickProposition", true)
        else
            intent.putExtra("pickProposition", false)
        startActivity(intent)

    }


    private fun loadControls(eventId: Int) {
        val userId = getIntent().getIntExtra("userId", 0)

        propositionLinearLayout.addView(createTextViewTitle("Lista propozycji:"))

        createViewLine()


        fabAddProposition.setOnClickListener {
            addPropositions(userId, eventId, getIntent().getIntExtra("groupId", 0))
        }

        fabRankProposition.setOnClickListener {
            propositionRanking(userId, eventId)
        }



        doAsync {
            val propositionProvider = PropositionProvider
            var list: List<Proposition> =
                propositionProvider.propositionList("WHERE id_event = $eventId ORDER BY promotion DESC, random();")
            val vote = VoteProvider.voteList("WHERE id_user = $userId and id_event = $eventId")

            var pickPropositionId = 0
            if (vote.isNotEmpty()) {
                pickPropositionId = vote.get(0).id_proposition
            }
            pickProposition = pickPropositionId
            uiThread {
                loadPropositions(list, pickPropositionId)
                if (!fistRun) {
                    fistRun = true
                    var listPromote: List<Proposition> = arrayListOf()
                    list.forEach {
                        if (it.promotion > 0) {
                            listPromote += it
                        }
                    }
                    promoteInfo(listPromote)
                }

            }
        }

    }

    private fun propositionRanking(userId: Int, eventId: Int) {
        val intent = Intent(this, PropositionRankingActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("eventId", eventId)
        intent.putExtra("groupName", getIntent().getStringExtra("groupName"))
        intent.putExtra("eventName", getIntent().getStringExtra("eventName"))
        intent.putExtra("groupId", getIntent().getIntExtra("groupId", 0))
        intent.putExtra("pickProposition", pickProposition)
        startActivity(intent)
    }

    private fun addPropositions(userId: Int, eventId: Int, groupId: Int) {

        val intent = Intent(this, CreatePropositionActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("eventId", eventId)
        intent.putExtra("groupId", groupId)
        startActivity(intent)
    }

    private fun loadPropositions(listProposition: List<Proposition>, pickPropositionId: Int) {
        var lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        lp.setMargins(0, 0, 0, 30)

        listProposition.forEach {
            if (it.id_proposition == pickPropositionId) {
                createControl(lp, it, true)
            } else {
                createControl(lp, it, false)
            }
        }
    }

    private fun createControl(
        lp: LinearLayout.LayoutParams,
        proposition: Proposition,
        pick: Boolean
    ) {

        val linearLayout = LinearLayout(this)

        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.isClickable = true
        linearLayout.setOnClickListener(ClickListener)
        linearLayout.setTag(proposition.id_proposition)


        if (pick) {
            linearLayout.addView(
                createTextView(
                    proposition.name, 23F, true, Color.parseColor("#03aa9f")
                ), lp
            )
        } else if (proposition.promotion > 3) {
            linearLayout.addView(
                createTextView(
                    proposition.name, 30F, false, Color.parseColor("#ff8000")
                ), lp
            )
        } else if (proposition.promotion > 0) {
            linearLayout.addView(
                createTextView(
                    proposition.name, 23F, false, Color.parseColor("#FF0000")
                ), lp
            )
        } else {
            linearLayout.addView(createTextView(proposition.name, 23F, false, Color.BLACK), lp)
        }

        var lpView = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            4
        )
        var view = View(this)
        view.setBackgroundColor(Color.BLACK)
        linearLayout.addView(view, lpView)

        propositionLinearLayout.addView(linearLayout, lp)


    }


    private fun createTextViewTitle(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.textSize = 40F
        textView.setTextColor(Color.BLACK)


        return textView
    }

    private fun createViewLine() {
        var lpView = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            10
        )

        lpView.setMargins(0, 40, 0, 40)
        var view = View(this)
        view.setBackgroundColor(Color.BLACK)
        propositionLinearLayout.addView(view, lpView)

    }

    private fun createTextView(text: String, textSize: Float, bold: Boolean, color: Int): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.textSize = textSize
        textView.setTextColor(color)
        if (bold == true) {
            textView.setTypeface(null, Typeface.BOLD)
        }


        return textView
    }

    fun promoteInfo(list: List<Proposition>) {
        val alertDialog = AlertDialog.Builder(this).create()

        var nr: Int

        var size = list.size
        if (size > 0) {

            if (size < 3)
                nr = Random.nextInt(size)
            else
                nr = Random.nextInt(3)
            val proposition = list.get(nr)
            var textView = TextView(this)
            textView.text = "Zajrzyj do propozycji: <${proposition.name}>. Propozycja  może Cię zainteresować!"
            textView.textSize = 25F
            textView.setTextColor(Color.BLACK)
            textView.setPadding(80, 80, 80, 80)
            alertDialog.setCustomTitle(textView)
        }


        var view = ImageView(this)
        view.setImageResource(R.mipmap.ic_promote)

        alertDialog.setView(view)

        alertDialog.setMessage(
            "\n\n" +
                    "* Promowanie propozycji zwiększa zainteresowanie! Stwórz propozycję, kliknij jej nazwę i sprawdź naciskając czerwony przycisk z gwiazdą!"
        )


        //alertDialog.setFeatureDrawableResource(Window.FEATURE_RIGHT_ICON, R.drawable.ic_promote)
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

}
