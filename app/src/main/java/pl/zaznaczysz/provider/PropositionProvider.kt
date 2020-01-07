package pl.zaznaczysz.provider

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pl.zaznaczysz.cfg.Const
import pl.zaznaczysz.model.Proposition

class PropositionProvider {

    fun propositionList(where: String): List<Proposition> {
        val client = OkHttpClient()

        val request: Request = Request.Builder()
            .url(Const.SERVICE_PROPOSITION)
            .addHeader(
                "where",
                where
            )
            .build()

        var response = client.newCall(request).execute()
        val json = JsonParser().parse(response.body!!.string()).asJsonArray
        var list: List<Proposition> = Gson().fromJson(json, object : TypeToken<List<Proposition>>() {}.type)

        return list
    }

    fun insertProposition(proposition: Proposition): Proposition {
        val client = OkHttpClient()


        val formBody =  Gson().toJson(proposition).toRequestBody()

        val request: Request = Request.Builder()
            .post(formBody)
            .header("Accept", "application/json")
            .header("Content-type","application/json;charset=utf-8")
            .url(Const.SERVICE_PROPOSITION)
            .build()

        var response = client.newCall(request).execute()
        val jsonStr :String = response.body!!.string()

        val prop =  Gson().fromJson(jsonStr, Proposition::class.java)

        return prop
    }

    fun updateVoteCount(id: Int, vote: Int) {
        val client = OkHttpClient()

        val proposition = Proposition(id, "", "", "", vote, 0, 0)

        val formBody =  Gson().toJson(proposition).toRequestBody()

        val request: Request = Request.Builder()
            .put(formBody)
            .header("Accept", "application/json")
            .header("Content-type","application/json;charset=utf-8")
            .url(Const.SERVICE_PROPOSITION)
            .build()

        var response = client.newCall(request).execute()
    }

}