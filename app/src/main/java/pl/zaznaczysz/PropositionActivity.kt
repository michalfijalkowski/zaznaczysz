package pl.zaznaczysz

import android.content.Intent
import android.content.Intent.getIntent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_proposition.*
import org.jetbrains.anko.custom.onUiThread
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.model.Proposition
import pl.zaznaczysz.model.Vote
import pl.zaznaczysz.provider.ActivityProvider
import pl.zaznaczysz.provider.PropositionProvider
import pl.zaznaczysz.provider.UserProvider
import pl.zaznaczysz.provider.VoteProvider
import java.io.File


class PropositionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proposition)
        loadControls(getIntent().getIntExtra("propositionId", 0))
    }

    override fun onResume() {
        super.onResume()

    }


    fun loadControls(propositionId: Int) {

        val userId = getIntent().getIntExtra("userId", 0)
        val eventId = getIntent().getIntExtra("eventId", 0)

        doAsync {
            val list: List<Proposition> =
                PropositionProvider().propositionList("where id_proposition = $propositionId")
            val proposition: Proposition = list.get(0)
            uiThread {
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
                    VoteProvider().voteList("WHERE id_user = $userId AND id_event = $eventId")
                if (voteList.isNotEmpty())
                    PropositionProvider().updateVoteCount(voteList.get(0).id_proposition, -1)
                else {
                    ActivityProvider().updateActivityUser(getIntent().getIntExtra("userId", 0), getIntent().getIntExtra("groupId", 0), 3)
                    UserProvider().updateActivityUser(getIntent().getIntExtra("userId", 0), 3)
                }

                VoteProvider().updateVote(userId, eventId, propositionId)
                PropositionProvider().updateVoteCount(propositionId, 1)

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
    }

    fun loadProposition(proposition: Proposition) {
        tvName.setText(proposition.name)
        tvDescription.setText((proposition.description))

//        val imgFile = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/zaznaczysz",
//            proposition.photo_name + ".jpg"
//        )
//
//        if (imgFile.exists()) {
//            val myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
//            ivPhoto.setImageBitmap(myBitmap)
//        }


    }
}
