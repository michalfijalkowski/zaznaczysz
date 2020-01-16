package pl.zaznaczysz.provider

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.Group
import pl.zaznaczysz.model.User

class GroupProvider {
    companion object {
        fun groupList(where: String): List<Group> {
            val client = OkHttpClient()

            val request: Request = Request.Builder()
                .url(Const.SERVICE_GROUP)
                .addHeader(
                    "where",
                    where
                )
                .build()

            var response = client.newCall(request).execute()
            val json = JsonParser().parse(response.body!!.string()).asJsonArray
            var list: List<Group> = Gson().fromJson(json, object : TypeToken<List<Group>>() {}.type)

            return list
        }

//    fun insertGroup(username: String, password: String): User {
//        TODO()
//        val client = OkHttpClient()
//
//        var user = User()
//        user.id_user = -1
//        user.username = username
//        user.password = password
//        user.activity_points = -1
//
//
//
//        val xxx = Gson().toJson(user)
//        val formBody =  Gson().toJson(user).toRequestBody()
//
//        val request: Request = Request.Builder()
//            .post(formBody)
//            .header("Accept", "application/json")
//            .header("Content-type","application/json;charset=utf-8")
//            .url(Const.SERVICE_USER)
//            .build()
//
//        var response = client.newCall(request).execute()
//        val jsonStr :String = response.body!!.string()
//
//        val u =  Gson().fromJson(jsonStr, User::class.java)
//
//        return u
//    }

    }
}