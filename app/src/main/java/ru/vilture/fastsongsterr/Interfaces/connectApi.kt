package ru.vilture.fastsongsterr.Interfaces

import fastsongsterr.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import ru.vilture.fastsongsterr.Model.Response


interface ServerApi {
    @GET("songs.json")
    fun getSongs(@Query("pattern") pattern: String): Call<List<Response>>

    @GET("songs/byartists.json")
    fun getArtists(@Query("artists") artists: String): Call<List<Response>>
}


private const val baseUrl = "https://www.songsterr.com/a/ra/"


object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getServer(): Retrofit {
        if (retrofit == null) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(
                if (BuildConfig.DEBUG)
                    HttpLoggingInterceptor.Level.BODY
                else
                    HttpLoggingInterceptor.Level.NONE
            )

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()


        }

        return retrofit!!
    }
}

object Common {
    val api: ServerApi
        get() = RetrofitClient.getServer().create(ServerApi::class.java)
}
