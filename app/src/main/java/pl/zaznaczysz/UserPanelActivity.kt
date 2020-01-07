package pl.zaznaczysz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user_panel.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.model.User
import pl.zaznaczysz.provider.ActivityProvider
import pl.zaznaczysz.provider.UserProvider

class UserPanelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_panel)
        toast("Cześć! Dobrze, że jesteś z nami! ")
    }

    override fun onResume() {
        super.onResume()
        loadControls(getIntent().getIntExtra("userId", 0))

    }

    fun loadControls(userId: Int) {


        var username = ""
        doAsync {
             val user = UserProvider().userList("WHERE id_user = $userId;").get(0)
            username = user.username
            uiThread {

                tvName.setText("Witaj ${user.username}")
                tvPoint.setText("Zdobyleś już ${user.activity_points.toString()}pkt")
            }

        }

        btnGroup.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        btnJoin.setOnClickListener{
            val code = etCode.text.toString()

            doAsync {
                //ActivityProvider().insertActivity(userId, 0, 0, username)


            }


        }

    }
}
