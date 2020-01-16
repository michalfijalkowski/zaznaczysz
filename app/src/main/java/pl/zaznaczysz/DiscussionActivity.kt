package pl.zaznaczysz

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_discussion.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.Comment
import pl.zaznaczysz.provider.ActivityProvider
import pl.zaznaczysz.provider.CommentProvider
import pl.zaznaczysz.provider.UserProvider
import pl.zaznaczysz.provider.UserSettingsProvider
import pl.zaznaczysz.tool.CommonTool


class DiscussionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discussion)
        Const.setBackground(discussionRelativeLayout)
    }

    override fun onResume() {
        super.onResume()
        setTitle("CHAT")
        if ((discussionLinearLayout as LinearLayout).childCount > 0) (discussionLinearLayout as LinearLayout).removeAllViews()
        loadControls(getIntent().getIntExtra("propositionId", 0))
        discussionScrollView.postDelayed(
            { discussionScrollView.fullScroll(ScrollView.FOCUS_DOWN) },
            1000
        )
    }

    private fun loadControls(propositionId: Int) {

        var editText = createEditText()
        var btnSend = createButton(editText)

        doAsync {
            var list: List<Comment> = CommentProvider.propositionList("WHERE id_proposition = $propositionId ORDER BY id_comment")


            uiThread {
                loadComments(list)

                createViewLine(5)


                discussionLinearLayout.addView(editText)


                var lpButton = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                lpButton.setMargins(0, 5, 0, 10)

                discussionLinearLayout.addView(
                    btnSend, lpButton
                )
            }
        }
    }

    private fun createControl(
        lp: LinearLayout.LayoutParams,
        comment: Comment,
        backgroundColor: Boolean
    ) {

        discussionLinearLayout.addView(
            createTextView(
                comment.username + ":",
                20F,
                backgroundColor,
                Color.parseColor("#03aa9f")
            ), lp
        )
        discussionLinearLayout.addView(
            createTextView(
                comment.text,
                15F,
                backgroundColor,
                Color.BLACK
            ), lp
        )
        createViewLine(1)

    }

    private fun loadComments(comments: List<Comment>) {

        var lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        lp.setMargins(0, 0, 0, 0)

        var backgroundColor = false
        comments.forEach {
            createControl(lp, it, backgroundColor)
            backgroundColor = !backgroundColor
        }

    }

    private fun createEditText(): EditText {
        var editText = EditText(this)
        var lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            210
        )

        lp.setMargins(0, 10, 0, 0)

        editText.setOnTouchListener(OnTouchListener { v, event ->
            if (editText.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        return@OnTouchListener true
                    }
                }
            }
            false
        })


        editText.setLayoutParams(lp)
        editText.setBackgroundResource(R.drawable.edittext_form_round)
        editText.setPadding(30, 0, 30, 0)
        editText.gravity = Gravity.LEFT
        editText.hint = "komentarz"
        editText.textSize = 20f
        //editText.setBackgroundColor(Color.parseColor("#03aa9f"))
        return editText
    }

    private fun createButton(editText: EditText): Button {
        var btnSend = Button(this)
        btnSend.setBackgroundResource(R.drawable.button_form_zaznaczysz)
        btnSend.textSize = 20f
        btnSend.text = "        Wyślij"
        //btnSend.gravity = Gravity.BOTTOM
        btnSend.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_message, 0)
        btnSend.setBackgroundResource(R.drawable.button_form_zaznaczysz)


        val userId = getIntent().getIntExtra("userId", 0)
        val groupId = getIntent().getIntExtra("groupId", 0)

        btnSend.setOnClickListener {

            if (validate(editText)) {

                val newComment = Comment(
                    0,
                    editText.text.toString().trim(),
                    getIntent().getIntExtra("userId", 0).toString(),
                    getIntent().getIntExtra("propositionId", 0)
                )


                doAsync {
                    CommentProvider.insertComment(newComment)
                    if (Const.CHALLENGE_DONE) {
                        CommonTool.updateUserActivity(userId, groupId, 1)
                    } else {
                        val userS = UserSettingsProvider.userSettingsList("WHERE id_user = $userId").get(0)

                        if (userS.task1 > 0) {
                            CommonTool.updateUserActivity(userId, groupId, 1)
                        } else {
                            CommonTool.updateUserActivity(userId, groupId, 7)

                            UserSettingsProvider.updateActivityUser("UPDATE public.user_settings SET task1 = 1 WHERE id_user = $userId")


                        }
                    }
                    uiThread {
                        finish()
                        startActivity(getIntent())
                    }
                }

            } else {
                toast("Dodaj treść wiadomości")

            }
        }

        return btnSend
    }



    private fun createTextView(
        text: String,
        textSize: Float,
        special: Boolean,
        color: Int
    ): TextView {
        val textView = TextView(this)
        textView.setPadding(20, 0, 20, 0)
        textView.text = text
        textView.textSize = textSize
        textView.setTextColor(color)
        if (special == true)
            textView.setBackgroundColor(Color.LTGRAY)
        else
            textView.setBackgroundColor(Color.parseColor("#eeece9"))

        return textView
    }

    private fun createViewLine(size: Int) {
        var lpView = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            size
        )
        var view = View(this)
        view.setBackgroundColor(Color.BLACK)
        discussionLinearLayout.addView(view, lpView)

    }

    fun validate(editText: EditText): Boolean {
        if (editText.text.isEmpty())
            return false
        return true
    }
}
