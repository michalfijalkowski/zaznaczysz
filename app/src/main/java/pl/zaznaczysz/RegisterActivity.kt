package pl.zaznaczysz

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.security.Password
import pl.zaznaczysz.provider.ActivityProvider
import pl.zaznaczysz.provider.JoinProvider
import pl.zaznaczysz.provider.UserProvider
import pl.zaznaczysz.provider.UserSettingsProvider


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Const.setBackground(registerScrollView)
    }

    override fun onResume() {
        super.onResume()

        loadControls()
    }

    fun loadControls() {


        btnRegister.setOnClickListener {

            if (validate(username) && validate(password)) {

                for (i in 0 until registerLayout.childCount) {
                    val child: View = registerLayout.getChildAt(i)
                    child.setEnabled(false)
                }
                val userProvider = UserProvider
                doAsync {
                    val user = userProvider.insertUser(
                        username.text.toString().trim(),
                        Password.getHashed(password.text.toString()),
                        0
                    )

                    uiThread {
                        if (user.id_user == 0) {
                            toast("Podana nazwa użytkownika jest zajęta")
                            for (i in 0 until registerLayout.childCount) {
                                val child: View = registerLayout.getChildAt(i)
                                child.setEnabled(true)
                            }
                        } else {
                            doAsync {
                                ActivityProvider.insertActivity(user.id_user, 1, 0, user.username)
                                ActivityProvider.insertActivity(user.id_user, 2, 0, user.username)
                                UserSettingsProvider.insertUser(user.id_user, "white")
                            }
                            toast("Zarejestrowano użytkownika ${user.username}")
                            finish()
                        }
                    }
                }
            } else
                toast("Wypełnij wszytskie pola")

        }
    }

    fun validate(editText: EditText): Boolean {
        if (editText.text.isEmpty())
            return false
        return true
    }
}