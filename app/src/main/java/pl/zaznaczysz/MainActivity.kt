package pl.zaznaczysz

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.btnRegister
import kotlinx.android.synthetic.main.activity_main.password
import kotlinx.android.synthetic.main.activity_main.username
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.security.Password
import pl.zaznaczysz.provider.UserProvider
import pl.zaznaczysz.provider.UserSettingsProvider


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val xxx = Password.getHashed("o")
        val yyyy = Password.getHashed("y")
    }

    override fun onResume() {
        super.onResume()
        Const.setBackground(mainScrollView)

        loadControls()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.INTERNET
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.INTERNET
                    ),
                    0
                )
            }
        }

        username.setText("t")
        password.setText("t")

    }

    private fun loadControls() {

        for (i in 0 until mainScrollView.childCount) {
            val child: View = mainScrollView.getChildAt(i)
            child.setEnabled(true)
        }


        btnLogin.setOnClickListener {

            for (i in 0 until mainScrollView.childCount) {
                val child: View = mainScrollView.getChildAt(i)
                child.setEnabled(false)
            }

            val intent = Intent(this, UserPanelActivity::class.java)
            intent.putExtra("fromMain", 1)
            doAsync {
                val user =
                    UserProvider.insertUser(username.text.toString(), Password.getHashed(password.text.toString()), -1)
                if (user.id_user != 0 && user.id_user != -1)
                    uiThread {
                        intent.putExtra("userId", user.id_user)
                        startActivity(intent)
                    }
                else
                    uiThread {
                        for (i in 0 until mainScrollView.childCount) {
                            val child: View = mainScrollView.getChildAt(i)
                            child.setEnabled(true)
                        }
                        toast("Niepoprawne dane użytkownika")
                    }
            }
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }

    }




}
