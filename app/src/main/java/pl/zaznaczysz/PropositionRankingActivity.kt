package pl.zaznaczysz

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_proposition_ranking.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.Proposition
import pl.zaznaczysz.provider.PropositionProvider
import pl.zaznaczysz.provider.VoteProvider


class PropositionRankingActivity : AppCompatActivity() {

    val ClickListener =
        View.OnClickListener { v ->
            propositionController(v.getTag().toString().toInt())
        }

    var pickProposition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proposition_ranking)
        Const.setBackground(propRankingRelativeLayout)
        setTitle(getIntent().getStringExtra("groupName") + " > " + getIntent().getStringExtra("eventName"))
        loadControlls(getIntent().getIntExtra("eventId", 0))
    }

    override fun onResume() {
        super.onResume()
        doAsync {
            val vote = VoteProvider.voteList(
                "WHERE id_user = ${getIntent().getIntExtra(
                    "userId",
                    0
                )} and id_event = ${getIntent().getIntExtra("eventId", 0)}"
            )

            var pickPropositionId = 0
            if (vote.isNotEmpty()) {
                pickPropositionId = vote.get(0).id_proposition
            }
            pickProposition = pickPropositionId
        }
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

    fun loadControlls(eventId: Int) {
        var lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val propositionProvider = PropositionProvider

        doAsync {
            val list: List<Proposition> =
                propositionProvider.propositionList("WHERE id_event = $eventId ORDER BY public.proposition.vote_count DESC;")
            uiThread {
                if (list.isNotEmpty())
                    for (x in 0..list.size - 1) {
                        var special = false
                        if (list.get(x).id_user == getIntent().getIntExtra("userId", 0))
                            special = true
                        propositionRankingLinearLayout.addView(
                            createTextView(
                                list.get(x).id_proposition,
                                "${x + 1}.   ${list.get(x).name}\n",
                                list.get(x).vote_count.toString(),
                                special
                            ), lp
                        )
                        createViewLine()
                    }
            }
        }


    }

    private fun createTextView(
        idProposition: Int,
        LeftText: String,
        RightText: String,
        special: Boolean
    ): TextView {
        val textView = TextView(this)
        textView.setOnClickListener(ClickListener)
        textView.setTag(idProposition)
        val resultText: String = LeftText + RightText
        val styledResultText = SpannableString(resultText)
        styledResultText.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE)
            , LeftText.length
            , LeftText.length + RightText.length
            , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = styledResultText
        textView.textSize = 25F

        if (special)
            textView.setTypeface(null, Typeface.BOLD)

        return textView
    }

    private fun createViewLine() {
        var lpView = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            1
        )
        var view = View(this)
        view.setBackgroundColor(Color.BLACK)
        propositionRankingLinearLayout.addView(view, lpView)

    }
}


