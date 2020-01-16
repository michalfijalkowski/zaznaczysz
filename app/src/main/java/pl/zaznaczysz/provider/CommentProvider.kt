package pl.zaznaczysz.provider

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.Comment

class CommentProvider {

    companion object {

        fun propositionList(where: String): List<Comment> {
            val client = OkHttpClient()

            val request: Request = Request.Builder()
                .url(Const.SERVICE_COMMENT)
                .addHeader(
                    "where",
                    where
                )
                .build()

            var response = client.newCall(request).execute()
            val json = JsonParser().parse(response.body!!.string()).asJsonArray
            var list: List<Comment> =
                Gson().fromJson(json, object : TypeToken<List<Comment>>() {}.type)

            return list
        }

        fun insertComment(comment: Comment): Comment {
            val client = OkHttpClient()

            val formBody = Gson().toJson(comment).toRequestBody()

            val request: Request = Request.Builder()
                .post(formBody)
                .header("Accept", "application/json")
                .header("Content-type", "application/json;charset=utf-8")
                .url(Const.SERVICE_COMMENT)
                .build()

            var response = client.newCall(request).execute()
            val jsonStr: String = response.body!!.string()

            val prop = Gson().fromJson(jsonStr, Comment::class.java)

            return prop
        }

    }
}