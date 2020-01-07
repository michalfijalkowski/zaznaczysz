package pl.zaznaczysz

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
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.model.Proposition
import pl.zaznaczysz.provider.PropositionProvider


class PropositionRankingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proposition_ranking)
        loadControlls(getIntent().getIntExtra("eventId", 0))
    }

    override fun onResume() {
        super.onResume()


    }

    fun loadControlls(eventId: Int) {
        var lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val propositionProvider = PropositionProvider()

        doAsync {
            val list: List<Proposition> = propositionProvider.propositionList("WHERE id_event = $eventId ORDER BY public.proposition.vote_count DESC;")
            uiThread {
                if (list.isNotEmpty())
                    for (x in 0..list.size-1) {
                        var special = false
                        if(list.get(x).id_user == getIntent().getIntExtra("userId", 0))
                            special = true
                        propositionRankingLinearLayout.addView(createTextView("${x+1}.   ${list.get(x).name}\n", list.get(x).vote_count.toString(), special), lp)
                        createViewLine()
                    }
            }
        }


    }

    private fun createTextView(LeftText: String, RightText: String, special: Boolean): TextView {
        val textView = TextView(this)
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

        if(special)
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


