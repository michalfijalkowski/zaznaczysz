package pl.zaznaczysz

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_event_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.model.Event
import pl.zaznaczysz.provider.EventProvider
import java.io.File


class EventListActivity : AppCompatActivity() {

    val ClickListener =
        View.OnClickListener {
                v -> eventController(v.getTag().toString().toInt())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)


    }

    override fun onResume() {
        super.onResume()
        //scroll opada na sam dol
        //eventScrollView.postDelayed({ eventScrollView.fullScroll(ScrollView.FOCUS_DOWN) }, 500)
        val title = "DYSKUSJE"
        val s = SpannableString(title)
        s.setSpan(
            ForegroundColorSpan(Color.WHITE),
            0,
            title.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        supportActionBar!!.setTitle(s)
        if ((eventLinearLayout as LinearLayout).childCount > 0) (eventLinearLayout as LinearLayout).removeAllViews()
        loadControls(getIntent().getIntExtra("groupId", 0))
    }


    private fun eventController(eventId : Int) {

        val intent = Intent(this, PropositionListActivity::class.java)
        intent.putExtra("userId", getIntent().getIntExtra("userId", 0))
        intent.putExtra("groupId", getIntent().getIntExtra("groupId", 0))
        intent.putExtra("eventId", eventId)
        startActivity(intent)

    }


    private fun loadControls(groupId: Int) {


        doAsync {
            val eventProvider = EventProvider()
            var list: List<Event> = eventProvider.eventList("WHERE id_group = $groupId ORDER BY id_event DESC;")

            uiThread {
                loadEvents(list)
            }

        }

        fabRank.setOnClickListener{
            val intent = Intent(this, UserRankingActivity::class.java)
            intent.putExtra("userId", getIntent().getIntExtra("userId", 0))
            intent.putExtra("groupId", getIntent().getIntExtra("groupId", 0))
            startActivity(intent)

        }
    }

    private fun addEvents() {

        val intent = Intent(this, CreatePropositionActivity::class.java)
        intent.putExtra("userId", getIntent().getIntExtra("userId", 0))
        intent.putExtra("groupId", getIntent().getIntExtra("groupId", 0))
        startActivity(intent)
    }

    private fun loadEvents(listEvent: List<Event>) {
        var lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        lp.setMargins(0, 0, 0, 5)

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
        linearLayout.setTag(event.id_event)


        linearLayout.addView(createTextView(event.name, 30F, false), lp)
        //eventLinearLayout.addView(createImageView(event.id_event, event.name + ".jpg"), lp)
        linearLayout.addView(createTextView(event.description, 20F, false), lp)

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
        if (bold == true)
            textView.setTypeface(null, Typeface.BOLD)

        return textView
    }

    private fun createImageView(eventId : Int, photoName: String): ImageView {
        val imageView = ImageButton(this)
        imageView.setOnClickListener(ClickListener)
        imageView.setBackgroundColor(Color.TRANSPARENT)
        imageView.setTag(eventId)
        imageView.layoutParams = LinearLayout.LayoutParams(160, 360) // value is in pixels


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    0
                )
            }
        }

        val imgFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/zaznaczysz",
            photoName
        )
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
            imageView.setImageBitmap(myBitmap)
        } else {
            val imgResId = R.drawable.launcher_icon
            imageView.setImageResource(imgResId)
        }

        return imageView
    }
}
