package pl.zaznaczysz


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import android.text.Layout
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_event_list.*
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.Group
import pl.zaznaczysz.provider.GroupProvider
import java.io.File


class GroupActivity : AppCompatActivity() {

    val ClickListener =
        View.OnClickListener { v ->
            groupController(v.getTag(R.string.groupId).toString().toInt(), v.getTag(R.string.groupName).toString())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        Const.setBackground(groupRelativeLayout)

    }

    override fun onResume() {
        super.onResume()
        setTitle("TWOJE GRUPY")
        if ((groupLinearLayout as LinearLayout).childCount > 0) (groupLinearLayout as LinearLayout).removeAllViews()
        loadControls()
    }

    private fun groupController(groupId: Int, groupName: String) {
        val intent = Intent(this, EventListActivity::class.java)
        intent.putExtra("userId", getIntent().getIntExtra("userId", 0))
        intent.putExtra("groupId", groupId)
        intent.putExtra("groupName", groupName)
        startActivity(intent)

    }


    private fun loadControls() {

        doAsync {
            val groupProvider = GroupProvider
            var list: List<Group> = groupProvider.groupList("WHERE id_group IN (SELECT id_group from activity_point WHERE id_user = ${getIntent().getIntExtra("userId", 0)}) ORDER BY id_group;")

            uiThread {
                addGroups(list)
            }

        }
    }

    private fun addGroups(listGroup: List<Group>) {
        var lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        lp.setMargins(0, 0, 0, 30)

        listGroup.forEach {
            createControl(lp, it)
        }
    }

    private fun createControl(lp: LinearLayout.LayoutParams, group: Group) {

        val linearLayout = LinearLayout(this)
        linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        //linearLayout.setBackgroundColor(Color.parseColor("#33FFFFFF"))


        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.isClickable = true
        linearLayout.setOnClickListener(ClickListener)
        linearLayout.setTag(R.string.groupId, group.id_group)
        linearLayout.setTag(R.string.groupName, group.name)


        //groupLinearLayout.addView(createTextView("NAZWA:", 30F, true), lp)
        linearLayout.addView(createTextView(group.name, 23F, true), lp)
        //groupLinearLayout.addView(createImageView(group.id_group, group.name + ".jpg"), lp)
        //groupLinearLayout.addView(createTextView("OPIS:", 30F, true), lp)
        linearLayout.addView(createTextView(group.description, 15F, false), lp)

        var lpView = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            4
        )
        var view = View(this)
        view.setBackgroundColor(Color.BLACK)
        linearLayout.addView(view, lpView)

        groupLinearLayout.addView(linearLayout, lp)
    }

    private fun createTextView(
        text: String,
        textSize: Float,
        bold: Boolean
    ): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.textSize = textSize
        textView.setTextColor(Color.BLACK)
        if (bold == true)
            textView.setTypeface(null, Typeface.BOLD)

        return textView
    }

}



