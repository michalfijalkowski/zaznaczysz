package pl.zaznaczysz.provider

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.User
import pl.zaznaczysz.model.UserSettings
import java.util.concurrent.TimeUnit


class UserSettingsProvider {

    companion object {

        fun userSettingsList(where: String): List<UserSettings> {
            val client = OkHttpClient()

            val request: Request = Request.Builder()
                .addHeader(
                    "where",
                    where
                )
                .url(Const.SERVICE_USERSETTINGS)
                .build()

            var response = client.newCall(request).execute()
            val json = JsonParser().parse(response.body!!.string()).asJsonArray
            var list: List<UserSettings> = Gson().fromJson(json, object : TypeToken<List<UserSettings>>() {}.type)

            return list
        }

        fun insertUser(idUser: Int, theme: String) {
            val client = OkHttpClient()

            var userSettings = UserSettings(idUser, theme, 0, 0, 0, 0, 0)

            val formBody = Gson().toJson(userSettings).toRequestBody()

            val request: Request = Request.Builder()
                .post(formBody)
                .header("Accept", "application/json")
                .header("Content-type", "application/json;charset=utf-8")
                .url(Const.SERVICE_USERSETTINGS)
                .build()

            client.newCall(request).execute()
        }

        fun updateActivityUser(query: String) {
            val client = OkHttpClient()

            var userSettings = UserSettings(0, query, 0, 0, 0, 0, 0)

            val formBody = Gson().toJson(userSettings).toRequestBody()

            val request: Request = Request.Builder()
                .put(formBody)
                .header("Accept", "application/json")
                .header("Content-type", "application/json;charset=utf-8")
                .url(Const.SERVICE_USERSETTINGS)
                .build()

            client.newCall(request).execute()
        }

    }
}