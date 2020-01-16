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
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.activity_user_ranking.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.Activity
import pl.zaznaczysz.model.User
import pl.zaznaczysz.provider.ActivityProvider
import pl.zaznaczysz.provider.UserProvider


class UserRankingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_ranking)
        Const.setBackground(userRankRelativeLayout)
        setTitle("${getIntent().getStringExtra("groupName")}")
        loadControlls()
    }

    override fun onResume() {
        super.onResume()


    }

    fun loadControlls() {
        var lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )


        val idGroup = getIntent().getIntExtra("groupId", 0)
        doAsync {

            val list: List<Activity> = ActivityProvider.ActivityList("WHERE id_group = ${getIntent().getIntExtra("groupId", 0)} ORDER BY activity_points DESC;")
            uiThread {
                if (list.isNotEmpty())
                    for (x in 0..list.size-1) {
                        var special = false
                        if(list.get(x).id_user == getIntent().getIntExtra("userId", 0))
                            special = true
                        userRankingLinearLayout.addView(createTextView("${x+1}.   ${list.get(x).username}\n", list.get(x).points.toString(), special), lp)
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
        textView.textSize = 20F

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
        userRankingLinearLayout.addView(view, lpView)

    }
}


