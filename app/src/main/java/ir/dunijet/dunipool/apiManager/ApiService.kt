package ir.dunijet.dunipool.apiManager

import ir.dunijet.dunipool.apiManager.model.ChartData
import ir.dunijet.dunipool.apiManager.model.CoinsData
import ir.dunijet.dunipool.apiManager.model.NewsData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET(value = "v2/news/")
    fun getNews(
        @Query(value = "sortOrder") sortOrder: String = "popular",
    ) :Call<NewsData>

    @GET(value = "top/totalvolfull")
    fun getCoins (
        @Query(value = "tsym") tsym :String = "USD" ,
        @Query(value = "limit") limit :String = "10"
    ) :Call<CoinsData>

    @GET(value = "v2/{histoPeriod}")
    fun getChartDAta (
        @Path ("histoPeriod") histoPeriod : String ,
        @Query("fsym") fsym : String,
        @Query("limit") limit :Int,
        @Query ("aggregate") aggregate : Int ,
        @Query ("tsym") tsym :String = "USD"
    ) :Call<ChartData>

}