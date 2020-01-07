package pl.zaznaczysz.provider

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.Vote

class VoteProvider {

    fun voteList(where: String): List<Vote> {
        val client = OkHttpClient()

        val request: Request = Request.Builder()
            .url(Const.SERVICE_VOTE)
            .addHeader(
                "where",
                where
            )
            .build()

        var response = client.newCall(request).execute()
        val json = JsonParser().parse(response.body!!.string()).asJsonArray
        var list: List<Vote> = Gson().fromJson(json, object : TypeToken<List<Vote>>() {}.type)

        return list
    }

    fun updateVote(userId: Int, eventId: Int, propositionId: Int): Vote {
        val client = OkHttpClient()

        var vote = Vote(userId, eventId, propositionId)

        val formBody =  Gson().toJson(vote).toRequestBody()

        val request: Request = Request.Builder()
            .post(formBody)
            .header("Accept", "application/json")
            .header("Content-type","application/json;charset=utf-8")
            .url(Const.SERVICE_VOTE)
            .build()

        var response = client.newCall(request).execute()
        val jsonStr :String = response.body!!.string()

        val prop =  Gson().fromJson(jsonStr, Vote::class.java)

        return prop
    }





}