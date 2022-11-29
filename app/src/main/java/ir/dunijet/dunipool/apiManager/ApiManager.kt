package ir.dunijet.dunipool.apiManager

import ir.dunijet.dunipool.apiManager.model.ChartData
import ir.dunijet.dunipool.apiManager.model.CoinsData
import ir.dunijet.dunipool.apiManager.model.NewsData
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory



class ApiManager {
    val apiService: ApiService

    init {
        val okHttpClient = OkHttpClient.Builder().addInterceptor {
            val oldRequest = it.request()
            val newRequest = oldRequest.newBuilder()
            newRequest.addHeader("authorization",
                "c7910ae608c5539a808f808ba73076189598e794e43a54c6716643b81fef471c")
            it.proceed(newRequest.build())
        }.build()
        val retrofit = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    fun getNews(apiCallBack: ApiCallBack<ArrayList<Pair<String, String>>>) {
        apiService.getNews().enqueue(object : Callback<NewsData> {
            override fun onResponse(call: Call<NewsData>, response: Response<NewsData>) {
                val body = response.body()!!
                val data: ArrayList<Pair<String, String>> = arrayListOf()
                body.data.forEach {
                    data.add(Pair(it.title, it.url))
                }

                apiCallBack.onSuccess(data)


            }

            override fun onFailure(call: Call<NewsData>, t: Throwable) {
                apiCallBack.onError(t.message!!)
            }
        })
    }
    fun getCoins(apiCallBack: ApiCallBack<ArrayList<CoinsData.Data>>) {

        apiService.getCoins().enqueue(
            object : Callback<CoinsData> {
                override fun onResponse(call: Call<CoinsData>, response: Response<CoinsData>) {
                    val body = response.body()!!
                    val data = body.data
                    apiCallBack.onSuccess(ArrayList(data))

                }

                override fun onFailure(call: Call<CoinsData>, t: Throwable) {
                    apiCallBack.onError(t.message!!)
                }

            }
        )

    }
    fun getChart(period :String , fsym :String, apiCallBack: ApiCallBack <ChartData>){

        var histoPeriod = ""
        var limit = 30
        var aggregate = 1
        when (period) {
            HOUR -> {
                histoPeriod = HISTO_MINUTE
                limit = 60
                aggregate =12
            }
            HOURS24 -> {
                histoPeriod = HISTO_DAY
                limit = 24
            }
            WEEK -> {
                histoPeriod = HISTO_HOUR
                aggregate = 6
            }
            MONTH -> {
                histoPeriod = HISTO_DAY
                limit = 30
            }
            MONTH3 -> {
                histoPeriod = HISTO_DAY
                limit = 90
            }
            YEAR -> {
                histoPeriod = HISTO_DAY
                aggregate = 13
            }
            ALL -> {
                histoPeriod = HISTO_DAY
                aggregate =30
                limit = 2000
            }
        }

        apiService.getChartDAta(histoPeriod ,fsym , limit , aggregate ).enqueue(object :Callback<ChartData> {
            override fun onResponse(call: Call<ChartData>, response: Response<ChartData>) {
                apiCallBack.onSuccess(response.body()!!)
            }
            override fun onFailure(call: Call<ChartData>, t: Throwable) {
                apiCallBack.onError(t.message!!)
            }

        })



        }


    interface ApiCallBack<T> {

        fun onSuccess(data: T)

        fun onError(message: String)

    }

}