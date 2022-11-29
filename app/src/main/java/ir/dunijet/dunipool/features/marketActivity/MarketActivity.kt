package ir.dunijet.dunipool.features.marketActivity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import ir.dunijet.dunipool.R
import ir.dunijet.dunipool.apiManager.ApiManager
import ir.dunijet.dunipool.apiManager.BASE_URL_IMAGE
import ir.dunijet.dunipool.apiManager.model.CoinsAbout
import ir.dunijet.dunipool.apiManager.model.CoinsAboutData
import ir.dunijet.dunipool.apiManager.model.CoinsData
import ir.dunijet.dunipool.databinding.ActivityMarketBinding
import ir.dunijet.dunipool.features.coinActivity.CoinActivity

const val SEND_COIN_DATA = "sendCOinData"
const val SEND_COIN_ABOUT = "sendCOinAbout"
const val SEND_COIN_BUNDLE = "sendBundle"

class MarketActivity : AppCompatActivity() , MarketRecyclerAdapter.CoinsEvent {
    lateinit var binding: ActivityMarketBinding
    lateinit var news : ArrayList<Pair<String,String>>
    lateinit var coins : ArrayList<CoinsData.Data>
    lateinit var  coinsMap : MutableMap<String , CoinsAbout>
    val apiManager = ApiManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutToolbar.toolbarMain.title = "Market"

        GetAboutCoins()
        moreCoins()
        refreshData()



       


    }
    override fun onResume() {
        super.onResume()
        initUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        notification()
    }

    private fun refreshData() {
        binding.swipeRefresh.setOnRefreshListener {

            Handler(Looper.getMainLooper() ).postDelayed({
                binding.swipeRefresh.isRefreshing =false
                val anim =AlphaAnimation(0f , 1f)
                anim.duration = 1000
                binding.layoutNews.txtNews.startAnimation(anim)
                binding.layoutList.recyclerView.startAnimation(anim)
                initUI()




            } , 750)

        }
    }

    private fun moreCoins() {
        binding.layoutList.btnMore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW , Uri.parse("https://www.livecoinwatch.com/"))
            startActivity(intent)
        }
    }

    private fun initUI() {
        getNewsAPI()
        getCoins()
    }


    private fun getNewsAPI() {
        apiManager.getNews(
            object :ApiManager.ApiCallBack<ArrayList<Pair<String,String>>> {
                override fun onSuccess(data: ArrayList<Pair<String, String>>) {
                    news = data
                    refreshNewS()
                }

                override fun onError(message: String) {
                    Toast.makeText(this@MarketActivity, message, Toast.LENGTH_SHORT).show()
                }


            }
        )
    }
    fun refreshNewS(){
        val randomAccess = (0..49).random()
        binding.layoutNews.txtNews.text = news[randomAccess].first
        binding.layoutNews.imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW , Uri.parse(news[randomAccess].second))
            startActivity(intent)
        }
        binding.layoutNews.txtNews.setOnClickListener {
            val anim = AlphaAnimation(0f , 1f)
            anim.duration = 1000
            binding.layoutNews.txtNews.startAnimation(anim)
            refreshNewS()
        }
    }

    private fun getCoins() {
        apiManager.getCoins(object : ApiManager.ApiCallBack<ArrayList<CoinsData.Data>> {
            override fun onSuccess(data: ArrayList<CoinsData.Data>) {
                coins = data
                refreshCoins()
            }

            override fun onError(message: String) {
                Toast.makeText(this@MarketActivity, message, Toast.LENGTH_SHORT).show()
                Log.v("amir" , message)
            }

        })
    }
    fun refreshCoins() {

        val myAdapter = MarketRecyclerAdapter(coins ,this)
        binding.layoutList.recyclerView.adapter = myAdapter
        binding.layoutList.recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        binding.layoutList.recyclerView.recycledViewPool.setMaxRecycledViews(0,0)

    }
    override fun click(data: CoinsData.Data) {
        val coinAbout = coinsMap[data.coinInfo.name]
         val intent = Intent(this , CoinActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable(SEND_COIN_DATA , data)
        bundle.putParcelable(SEND_COIN_ABOUT , coinAbout)
        intent.putExtra(SEND_COIN_BUNDLE , bundle)

        startActivity(intent)
    }

    fun GetAboutCoins () {
        val textFile = applicationContext
            .assets
            .open("currencyinfo.json")
            .bufferedReader()
            .use {
                it.readText()
            }
        val gson = Gson()
        val coinsAboutData = gson.fromJson(textFile ,CoinsAboutData :: class.java)

         coinsMap = mutableMapOf<String , CoinsAbout>()

        coinsAboutData.forEach {
            coinsMap[it.currencyName] = CoinsAbout(
                webSite = it.info.web ,
                twitter = it.info.twt,
                gitHub = it.info.github,
                reddit = it.info.reddit,
                desc = it.info.desc
            )
        }


    }

    private fun notification () {
        coins.forEach {
            val id = it.coinInfo.id.toInt()
            val intent = Intent(this , MarketActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val pendingIntent = PendingIntent.getActivity(this , id , intent , 0)
            if (it.rAW.uSD.cHANGEPCT24HOUR > 0 ) {
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val notification = NotificationCompat.Builder(this, "Crypto")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(it.coinInfo.fullName)
                    .setContentText(it.coinInfo.fullName + " goes up ")
                    .setContentIntent(pendingIntent)
                    .build()
                notificationManager.notify(id, notification)
            }
        }

    }

}