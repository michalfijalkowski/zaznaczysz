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
import kotlinx.android.synthetic.main.activity_group.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.model.Group
import pl.zaznaczysz.provider.GroupProvider
import java.io.File


class GroupActivity : AppCompatActivity() {

    val ClickListener =
        View.OnClickListener { v ->
            groupController(v.getTag().toString().toInt())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)


    }

    override fun onResume() {
        super.onResume()
        setTitle("TWOJE GRUPY")
        if ((groupLinearLayout as LinearLayout).childCount > 0) (groupLinearLayout as LinearLayout).removeAllViews()
        loadControls()
    }

    private fun groupController(groupId: Int) {
        val intent = Intent(this, EventListActivity::class.java)
        intent.putExtra("userId", getIntent().getIntExtra("userId", 0))
        intent.putExtra("groupId", groupId)
        startActivity(intent)

    }


    private fun loadControls() {

        doAsync {
            val groupProvider = GroupProvider()
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

        lp.setMargins(0, 0, 0, 5)

        listGroup.forEach {
            createControl(lp, it)
        }
    }

    private fun createControl(lp: LinearLayout.LayoutParams, group: Group) {

        val linearLayout = LinearLayout(this)
        linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.isClickable = true
        linearLayout.setOnClickListener(ClickListener)
        linearLayout.setTag(group.id_group)


        //groupLinearLayout.addView(createTextView("NAZWA:", 30F, true), lp)
        linearLayout.addView(createTextView(group.name, 30F, true), lp)
        //groupLinearLayout.addView(createImageView(group.id_group, group.name + ".jpg"), lp)
        //groupLinearLayout.addView(createTextView("OPIS:", 30F, true), lp)
        linearLayout.addView(createTextView(group.description, 20F, false), lp)

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
        if (bold == true)
            textView.setTypeface(null, Typeface.BOLD)

        return textView
    }

    private fun createImageView(groupId: Int, photoName: String): ImageView {
        val imageView = ImageButton(this)
        imageView.setOnClickListener(ClickListener)
        imageView.setBackgroundColor(Color.TRANSPARENT)
        imageView.setTag(groupId)
        imageView.layoutParams = LinearLayout.LayoutParams(360, 360) // value is in pixels


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



