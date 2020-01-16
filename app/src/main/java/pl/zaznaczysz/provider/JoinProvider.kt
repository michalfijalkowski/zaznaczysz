package pl.zaznaczysz.provider

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.Join

class JoinProvider {
    companion object {

//        fun JoinList(where: String): List<Join> {
//            val client = OkHttpClient()
//
//            val request: Request = Request.Builder()
//                .url(Const.SERVICE_JOIN)
//                .addHeader(
//                    "where",
//                    where
//                )
//                .build()
//
//            var response = client.newCall(request).execute()
//            val json = JsonParser().parse(response.body!!.string()).asJsonArray
//            var list: List<Join> = Gson().fromJson(json, object : TypeToken<List<Join>>() {}.type)
//
//            return list
//        }
//
//        fun updateJoin(userId: Int, groupId: Int): Join {
//            val client = OkHttpClient()
//
//            var Join = Join(userId, groupId)
//
//            val formBody = Gson().toJson(Join).toRequestBody()
//
//            val request: Request = Request.Builder()
//                .post(formBody)
//                .header("Accept", "application/json")
//                .header("Content-type", "application/json;charset=utf-8")
//                .url(Const.SERVICE_JOIN)
//                .build()
//
//            var response = client.newCall(request).execute()
//            val jsonStr: String = response.body!!.string()
//
//            val prop = Gson().fromJson(jsonStr, Join::class.java)
//
//            return prop
//        }


    }
}