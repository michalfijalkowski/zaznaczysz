package pl.zaznaczysz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.btnRegister
import kotlinx.android.synthetic.main.activity_register.password
import kotlinx.android.synthetic.main.activity_register.username
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.model.User
import pl.zaznaczysz.provider.ActivityProvider
import pl.zaznaczysz.provider.JoinProvider
import pl.zaznaczysz.provider.UserProvider

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


    }

    override fun onResume() {
        super.onResume()

        loadControls()
    }

    fun loadControls() {


        btnRegister.setOnClickListener {

            if (validate(username) && validate(password)) {
                val userProvider = UserProvider()
                doAsync {
                    val user = userProvider.insertUser(
                        username.text.toString().trim(),
                        password.text.toString()
                    )

                    uiThread {
                        if (user.id_user == 0) {
                            toast("Podana nazwa użytkownika jest zajęta")
                            btnRegister.isEnabled = true
                        } else {
                            doAsync {
                                JoinProvider().updateJoin(user.id_user, 1)
                                JoinProvider().updateJoin(user.id_user, 2)
                                ActivityProvider().insertActivity(user.id_user, 1, 0, user.username)
                                ActivityProvider().insertActivity(user.id_user, 2, 0, user.username)
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