package pl.zaznaczysz.provider

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.Event
import pl.zaznaczysz.model.User

class EventProvider {

    fun eventList(where: String): List<Event> {
        val client = OkHttpClient()

        val request: Request = Request.Builder()
            .addHeader(
                "where",
                where
            )
            .url(Const.SERVICE_EVENT)
            .build()

        var response = client.newCall(request).execute()
        val json = JsonParser().parse(response.body!!.string()).asJsonArray
        var list: List<Event> = Gson().fromJson(json, object : TypeToken<List<Event>>() {}.type)

        return list
    }

    fun insertUser(username: String, password: String): User {
        TODO()
        val client = OkHttpClient()

        var user = User(-1, username, password, -1)


    val xxx = Gson().toJson(user)
    val formBody = Gson().toJson(user).toRequestBody()

    val request: Request = Request.Builder()
        .post(formBody)
        .header("Accept", "application/json")
        .header("Content-type", "application/json;charset=utf-8")
        .url(Const.SERVICE_USER)
        .build()

    var response = client.newCall(request).execute()
    val jsonStr: String = response.body!!.string()

    val u = Gson().fromJson(jsonStr, User::class.java)

    return u
}

}