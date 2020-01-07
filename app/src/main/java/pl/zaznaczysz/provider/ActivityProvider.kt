package pl.zaznaczysz.provider

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.Activity

class ActivityProvider {

    fun ActivityList(where: String): List<Activity> {
        val client = OkHttpClient()

        val request: Request = Request.Builder()
            .url(Const.SERVICE_ACTIVITY)
            .addHeader(
                "where",
                where
            )
            .build()

        var response = client.newCall(request).execute()
        val json = JsonParser().parse(response.body!!.string()).asJsonArray
        var list: List<Activity> =
            Gson().fromJson(json, object : TypeToken<List<Activity>>() {}.type)

        return list
    }

    fun insertActivity(userId: Int, groupId: Int, points: Int, username: String): Activity {
        val client = OkHttpClient()

        var Activity = Activity(userId, groupId, points, username)

        val formBody = Gson().toJson(Activity).toRequestBody()

        val request: Request = Request.Builder()
            .post(formBody)
            .header("Accept", "application/json")
            .header("Content-type", "application/json;charset=utf-8")
            .url(Const.SERVICE_ACTIVITY)
            .build()

        var response = client.newCall(request).execute()
        val jsonStr: String = response.body!!.string()

        val prop = Gson().fromJson(jsonStr, Activity::class.java)

        return prop
    }

    fun updateActivityUser(idUser: Int, idGroup: Int, points: Int) {
        val client = OkHttpClient()

        var user = Activity(
            idUser,
            idGroup,
            points,
            ""
        )

        val formBody = Gson().toJson(user).toRequestBody()

        val request: Request = Request.Builder()
            .put(formBody)
            .header("Accept", "application/json")
            .header("Content-type", "application/json;charset=utf-8")
            .url(Const.SERVICE_ACTIVITY)
            .build()

        val xxx: String = request.toString()

        client.newCall(request).execute()
    }


}