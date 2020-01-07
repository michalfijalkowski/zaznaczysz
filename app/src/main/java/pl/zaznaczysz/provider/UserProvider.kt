package pl.zaznaczysz.provider

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.User

class UserProvider {

    fun correctLogin(username: String, password: String): Int {
        val client = OkHttpClient()

        val request: Request = Request.Builder()
            .addHeader(
                "where",
                "where username = '$username' and public.user.password = '$password';"
            )
            .url(Const.SERVICE_USER)
            .build()

        var response = client.newCall(request).execute()
        val json = JsonParser().parse(response.body!!.string()).asJsonArray
        var list: List<User> = Gson().fromJson(json, object : TypeToken<List<User>>() {}.type)

        if (list.isEmpty())
            return 0

        return list.get(0).id_user
    }

    fun userList(where: String): List<User> {
        val client = OkHttpClient()

        val request: Request = Request.Builder()
            .addHeader(
                "where",
                where
            )
            .url(Const.SERVICE_USER)
            .build()

        var response = client.newCall(request).execute()
        val json = JsonParser().parse(response.body!!.string()).asJsonArray
        var list: List<User> = Gson().fromJson(json, object : TypeToken<List<User>>() {}.type)

        return list
    }

    fun insertUser(username: String, password: String): User {
        val client = OkHttpClient()

        var user = User(-1, username, password, -1)

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

    fun updateActivityUser(id: Int, points: Int) {
        val client = OkHttpClient()

        var user = User(id, "", "", points)

        val formBody = Gson().toJson(user).toRequestBody()

        val request: Request = Request.Builder()
            .put(formBody)
            .header("Accept", "application/json")
            .header("Content-type", "application/json;charset=utf-8")
            .url(Const.SERVICE_USER)
            .build()

        val xxx: String = request.toString()

        client.newCall(request).execute()
    }

}