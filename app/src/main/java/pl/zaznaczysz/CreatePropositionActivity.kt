package pl.zaznaczysz

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_create_proposition.*
import kotlinx.android.synthetic.main.activity_create_proposition.textView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.Proposition
import pl.zaznaczysz.provider.ActivityProvider
import pl.zaznaczysz.provider.PropositionProvider
import pl.zaznaczysz.provider.UserProvider
import pl.zaznaczysz.provider.UserSettingsProvider
import pl.zaznaczysz.tool.CommonTool


class CreatePropositionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_proposition)
        loadControls()
        Const.setBackground(createScrollView)
    }

    private fun loadControls() {

        if (getIntent().getIntExtra("option", 0) == 1) {
            btnAdd.setText("DODAJ DYSKUSJE")
            textView.setText("Stwórz dyskusję")
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CAMERA
                    ),
                    0
                )
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    0
                )
            }
        }


        btnAdd.setOnClickListener {

            if (getIntent().getIntExtra("option", 0) == 1) {

                toast("Stworzyłeś dyskusję!")
            } else {

                confirmAdd()
            }

        }
    }

    private fun createProposition() {

        if (validate(prop_name) && validate(prop_description)) {
            for (i in 0 until createPropositionLayout.childCount) {
                val child: View = createPropositionLayout.getChildAt(i)
                child.setEnabled(false)
            }
            val userId = getIntent().getIntExtra("userId", 0)
            val eventId = getIntent().getIntExtra("eventId", 0)
            val groupId = getIntent().getIntExtra("groupId", 0)
            doAsync {
                var proposition = Proposition(
                    0,
                    prop_name.text.toString().trim(),
                    prop_description.text.toString().trim(),
                    "",
                    0,
                    userId,
                    eventId,
                    0
                )

                val addedProposition = PropositionProvider.insertProposition(proposition)

                uiThread {
                    if (addedProposition.id_user == 0) {
                        toast("Propozycja o podanej nazwie już istnieje")
                        for (i in 0 until createPropositionLayout.childCount) {
                            val child: View = createPropositionLayout.getChildAt(i)
                            child.setEnabled(true)
                        }
                    } else {
                        doAsync {

                            if (Const.CHALLENGE_DONE) {
                                CommonTool.updateUserActivity(userId, groupId, 5)
                            } else {
                                val userS = UserSettingsProvider.userSettingsList("WHERE id_user = $userId").get(0)
                                //tu dajemy grupe testowania aplikacji
                                if (groupId == 2) {
                                    if (userS.task4 > 0) {
                                        CommonTool.updateUserActivity(userId, groupId, 5)
                                    } else {
                                        CommonTool.updateUserActivity(userId, groupId, 7)

                                        UserSettingsProvider.updateActivityUser("UPDATE public.user_settings SET task4 = 1 WHERE id_user = $userId")

                                    }

                                } else {

                                    if (userS.task3 > 0) {
                                        CommonTool.updateUserActivity(userId, groupId, 5)
                                    } else {
                                        CommonTool.updateUserActivity(userId, groupId, 7)

                                        UserSettingsProvider.updateActivityUser("UPDATE public.user_settings SET task3 = 1 WHERE id_user = $userId")

                                    }
                                }
                            }
                            uiThread {
                                toast("Propozycja została dodana")
                                finish()
                            }
                        }
                    }
                }
            }
        } else {
            toast("Wypełnij wszytskie pola")
        }
    }


    fun validate(editText: EditText): Boolean {
        if (editText.text.isEmpty())
            return false
        return true
    }


    override fun onBackPressed() {
        backConfirmation()
    }

    fun backConfirmation() {

        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("Czy chcesz zakończyć tworzenie propozycji?")
        alertDialog.setMessage("Wypełnione dane nie zostaną zapisane!")

        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "Tak"
        ) { dialog, which ->
            dialog.dismiss()
            finish()
        }

        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE, "Nie"
        ) { dialog, which -> dialog.dismiss() }
        alertDialog.show()

        val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        btnPositive.setTextColor(Color.GRAY)
        btnPositive.textSize = 30f
        val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        btnNegative.setTextColor(Color.GRAY)
        btnNegative.textSize = 30f
        val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnPositive.layoutParams = layoutParams
        btnNegative.layoutParams = layoutParams
    }

    fun confirmAdd() {

        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("Czy chcesz dodać propozycję?")
        alertDialog.setMessage("Dodanej propozycji nie można usunąć!")

        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "Tak"
        ) { dialog, which ->
            dialog.dismiss()
            createProposition()
        }

        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE, "Nie"
        ) { dialog, which -> dialog.dismiss() }
        alertDialog.show()

        val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        btnPositive.setTextColor(Color.GRAY)
        btnPositive.textSize = 30f
        val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        btnNegative.setTextColor(Color.GRAY)
        btnNegative.textSize = 30f
        val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnPositive.layoutParams = layoutParams
        btnNegative.layoutParams = layoutParams
    }


}
