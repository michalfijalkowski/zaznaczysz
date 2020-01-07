package pl.zaznaczysz

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_event_list.*
import kotlinx.android.synthetic.main.activity_proposition_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.model.Proposition
import pl.zaznaczysz.provider.PropositionProvider
import pl.zaznaczysz.provider.VoteProvider
import java.io.File


class PropositionListActivity : AppCompatActivity() {

    val ClickListener =
        View.OnClickListener { v ->
            propositionController(v.getTag().toString().toInt())
        }

    var pickProposition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proposition_list)

    }

    override fun onResume() {
        super.onResume()
        setTitle("PROPOZYCJE")
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


        fab.setOnClickListener {
            addPropositions(userId, eventId)
        }

        fab2.setOnClickListener {
            propositionRanking(userId, eventId)
        }



        doAsync {
            val propositionProvider = PropositionProvider()
            var list: List<Proposition> =
                propositionProvider.propositionList("WHERE id_event = $eventId ORDER BY id_proposition DESC;")
            val vote = VoteProvider().voteList("WHERE id_user = $userId and id_event = $eventId")

            var pickPropositionId = 0
            if (vote.isNotEmpty()) {
                pickPropositionId = vote.get(0).id_proposition
            }
            pickProposition = pickPropositionId
            uiThread {
                loadPropositions(list, pickPropositionId)
            }
        }

    }

    private fun propositionRanking(userId: Int, eventId: Int) {
        val intent = Intent(this, PropositionRankingActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("eventId", eventId)
        startActivity(intent)
    }

    private fun addPropositions(userId: Int, eventId: Int) {

        val intent = Intent(this, CreatePropositionActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("eventId", eventId)
        startActivity(intent)
    }

    private fun loadPropositions(listProposition: List<Proposition>, pickPropositionId: Int) {
        var lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        lp.setMargins(0, 0, 0, 15)

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
            linearLayout.addView(createTextView(proposition.name, 40F, true, Color.parseColor("#03aa9f")), lp)
        } else {
            linearLayout.addView(createTextView(proposition.name, 40F, false, Color.BLACK), lp)
        }
//        propositionLinearLayout.addView(
//            createImageView(
//                proposition.id_proposition,
//                proposition.photo_name + ".jpg"
//            ), lp
//        )
       // linearLayout.addView(createTextView(proposition.description, 20F, false, Color.BLACK), lp)

        var lpView = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            4
        )
        var view = View(this)
        view.setBackgroundColor(Color.BLACK)
        linearLayout.addView(view, lpView)

        propositionLinearLayout.addView(linearLayout, lp)


    }

    private fun createViewLine() {
        var lpView = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            4
        )
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

    private fun createImageView(propositionId: Int, photoName: String): ImageView {
        val imageView = ImageButton(this)
        imageView.setOnClickListener(ClickListener)
        imageView.setBackgroundColor(Color.TRANSPARENT)
        imageView.setTag(propositionId)
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
