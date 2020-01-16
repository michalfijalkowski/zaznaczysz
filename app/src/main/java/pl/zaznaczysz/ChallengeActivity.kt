package pl.zaznaczysz

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_challenge.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.model.UserSettings
import pl.zaznaczysz.provider.UserSettingsProvider

class ChallengeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenge)
    }

    override fun onResume() {
        super.onResume()
        loadControls(getIntent().getIntExtra("userId", 0))
    }

    fun loadControls(userId: Int) {

        doAsync {
            val userS = UserSettingsProvider.userSettingsList("WHERE id_user = $userId;").get(0)

            uiThread {

                if(userS.task1 > 0) {
                    tvTask1.setTextColor(Color.parseColor("#03aa9f"))
                }
                if(userS.task2 > 0) {
                    tvTask2.setTextColor(Color.parseColor("#03aa9f"))
                }
                if(userS.task3 > 0) {
                    tvTask3.setTextColor(Color.parseColor("#03aa9f"))
                }
                if(userS.task4 > 0) {
                    tvTask4.setTextColor(Color.parseColor("#03aa9f"))
                }
                if(userS.task5 > 0) {
                    tvTask5.setTextColor(Color.parseColor("#03aa9f"))
                }
                if(userS.completed() < 5) {
                    tvPremia.setText("Ukończyłeś ${userS.completed()}/5 zadań. Wykonaj zadania oznaczone na czerwono, aby otrzymać premię punktową!")
                }else {
                    tvPremia.setTextColor(Color.parseColor("#03aa9f"))
                    tvPremia.setText("Ukończyłeś wszytskie zadania! Gratulację!")
                }
            }

        }

    }
}
