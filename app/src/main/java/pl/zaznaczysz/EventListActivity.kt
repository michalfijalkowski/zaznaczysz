package pl.zaznaczysz

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.opengl.Visibility
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_event_list.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_proposition_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.Activity
import pl.zaznaczysz.model.Event
import pl.zaznaczysz.provider.ActivityProvider
import pl.zaznaczysz.provider.EventProvider
import java.io.File


class EventListActivity : AppCompatActivity() {

    val ClickListener =
        View.OnClickListener {
                v -> eventController(v.getTag(R.string.eventId).toString().toInt(), v.getTag(R.string.eventName).toString())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)
        Const.setBackground(eventRelativeLayout)
    }

    override fun onResume() {
        super.onResume()
        setTitle("${getIntent().getStringExtra("groupName")}")
        if ((eventLinearLayout as LinearLayout).childCount > 0) (eventLinearLayout as LinearLayout).removeAllViews()
        loadControls(getIntent().getIntExtra("groupId", 0))
    }


    private fun eventController(eventId : Int, eventName: String) {

        val intent = Intent(this, PropositionListActivity::class.java)
        intent.putExtra("userId", getIntent().getIntExtra("userId", 0))
        intent.putExtra("groupId", getIntent().getIntExtra("groupId", 0))
        intent.putExtra("groupName", getIntent().getStringExtra("groupName"))
        intent.putExtra("eventId", eventId)
        intent.putExtra("eventName", eventName)
        startActivity(intent)

    }


    private fun loadControls(groupId: Int) {

        eventLinearLayout.addView(createTextViewTitle("Lista dyskusji:"))

        createViewLine()


        doAsync {
            val eventProvider = EventProvider
            var list: List<Event> = eventProvider.eventList("WHERE id_group = $groupId ORDER BY id_event DESC;")

            val admin: List<Activity> = ActivityProvider.ActivityList("WHERE id_user = ${getIntent().getIntExtra("userId", 0)} AND id_group = $groupId")

            if(admin.get(0).admin_user) {
                uiThread {
                    fabAddEvent.isVisible = true
                }
            }
            uiThread {
                loadEvents(list)
            }

        }

        fabRankUser.setOnClickListener{
            userRank()

        }

        fabAddEvent.setOnClickListener{

            addEvent(groupId)
        }
    }

    private fun userRank() {
        val intent = Intent(this, UserRankingActivity::class.java)
        intent.putExtra("userId", getIntent().getIntExtra("userId", 0))
        intent.putExtra("groupId", getIntent().getIntExtra("groupId", 0))
        intent.putExtra("groupName", getIntent().getStringExtra("groupName"))
        startActivity(intent)
    }

    private fun addEvent(groupId: Int) {

        val intent = Intent(this, CreatePropositionActivity::class.java)
        intent.putExtra("option", 1)
        intent.putExtra("groupId", groupId)
        startActivity(intent)
    }

    private fun loadEvents(listEvent: List<Event>) {
        var lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        lp.setMargins(0, 0, 0, 30)

        listEvent.forEach {
            createControl(lp, it)
        }
    }

    private fun createControl(lp: LinearLayout.LayoutParams, event: Event) {

        val linearLayout = LinearLayout(this)
        linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.isClickable = true
        linearLayout.setOnClickListener(ClickListener)
        linearLayout.setTag(R.string.eventId, event.id_event)
        linearLayout.setTag(R.string.eventName, event.name)


        linearLayout.addView(createTextView(event.name, 23F, true), lp)
        linearLayout.addView(createTextView(event.description, 15F, false), lp)

        var lpView = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            4
        )
        var view = View(this)
        view.setBackgroundColor(Color.BLACK)
        linearLayout.addView(view, lpView)

        eventLinearLayout.addView(linearLayout, lp)

    }


    private fun createTextView(text: String, textSize: Float, bold: Boolean): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.textSize = textSize
        textView.setTextColor(Color.BLACK)
        if (bold == true) {
            textView.setTypeface(null, Typeface.BOLD)
        }

        return textView
    }

    private fun createViewLine() {
        var lpView = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            10
        )
        lpView.setMargins(0,40,0,40)
        var view = View(this)
        view.setBackgroundColor(Color.BLACK)
        eventLinearLayout.addView(view, lpView)

    }

    private fun createTextViewTitle(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.textSize = 40F
        textView.setTextColor(Color.BLACK)

        return textView
    }


}
