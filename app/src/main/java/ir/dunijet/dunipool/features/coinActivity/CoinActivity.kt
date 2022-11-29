package ir.dunijet.dunipool.features.coinActivity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import ir.dunijet.dunipool.R
import ir.dunijet.dunipool.apiManager.*
import ir.dunijet.dunipool.apiManager.model.ChartData
import ir.dunijet.dunipool.apiManager.model.CoinsAbout
import ir.dunijet.dunipool.apiManager.model.CoinsData
import ir.dunijet.dunipool.databinding.ActivityCoinBinding
import ir.dunijet.dunipool.features.marketActivity.SEND_COIN_ABOUT
import ir.dunijet.dunipool.features.marketActivity.SEND_COIN_BUNDLE
import ir.dunijet.dunipool.features.marketActivity.SEND_COIN_DATA

class CoinActivity : AppCompatActivity() {
    lateinit var binding :ActivityCoinBinding
    lateinit var coinsData : CoinsData.Data
    lateinit var coinsAbout: CoinsAbout
    lateinit var fsym: String
    val apiManager = ApiManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView( binding.root )

        val getFromIntent = intent.getBundleExtra(SEND_COIN_BUNDLE)!!
        coinsData = getFromIntent.getParcelable<CoinsData.Data>(SEND_COIN_DATA)!!
        coinsAbout = getFromIntent.getParcelable<CoinsAbout>(SEND_COIN_ABOUT) ?: CoinsAbout()

        binding.layoutToolbar.toolbarMain.title = coinsData.coinInfo.fullName
        initUL()
    }

    private fun initUL() {

        showChart()
        showStatistics()
        showAbout()
        setChartText()

    }

    private fun showAbout() {

        binding.layoutAbout.txtAboutWebsite.text = coinsAbout.webSite
        binding.layoutAbout.txtAboutGithub.text = coinsAbout.gitHub
        binding.layoutAbout.txtAboutReddit.text = coinsAbout.reddit
        binding.layoutAbout.txtAboutTwitter.text = "@"+coinsAbout.twitter
        binding.layoutAbout.txtAbout.text = coinsAbout.desc

        binding.layoutAbout.txtAboutWebsite.setOnClickListener {
            if (coinsAbout.webSite != "no-data") {
                openLink(coinsAbout.webSite!!)
            }
        }
        binding.layoutAbout.txtAboutGithub.setOnClickListener {
            if (coinsAbout.gitHub != "no-data") {
                openLink(coinsAbout.gitHub!!)
            }
        }
        binding.layoutAbout.txtAboutReddit.setOnClickListener {
            if (coinsAbout.reddit != "no-data") {
                openLink(coinsAbout.reddit!!)
            }
        }
        binding.layoutAbout.txtAboutTwitter.setOnClickListener {
            if (coinsAbout.twitter != "no-data") {
                openLink("https://twitter.com/" +coinsAbout.twitter!!)
            }
        }

    }
    private fun openLink(url : String){
        val intent = Intent(Intent.ACTION_VIEW , Uri.parse(url))
        startActivity(intent)
    }

    private fun showStatistics() {
        binding.layoutStatistics.txtStatisticsOpen.text = coinsData.dISPLAY.uSD.oPEN24HOUR
        binding.layoutStatistics.txtStatisticsHigh.text = coinsData.dISPLAY.uSD.hIGH24HOUR
        binding.layoutStatistics.txtStatisticsLow.text = coinsData.dISPLAY.uSD.lOW24HOUR
        binding.layoutStatistics.txtStatisticsChange.text = coinsData.dISPLAY.uSD.cHANGE24HOUR
        binding.layoutStatistics.txtStatisticsAlgorithm.text = coinsData.coinInfo.algorithm
        binding.layoutStatistics.txtStatisticsTotal.text = coinsData.dISPLAY.uSD.tOTALVOLUME24H
        binding.layoutStatistics.txtStatisticsMarket.text = coinsData.dISPLAY.uSD.mKTCAP
        binding.layoutStatistics.txtStatisticsSupply.text = coinsData.dISPLAY.uSD.sUPPLY
    }

    private fun showChart() {
         fsym = coinsData.coinInfo.name
         apiManager.getChart(HOUR , fsym , object :ApiManager.ApiCallBack<ChartData> {
             override fun onSuccess(data: ChartData) {
                 val chartAdapter = ChartAdapter(data.data)
                 binding.layoutChart.sparkView.adapter = chartAdapter
             }

             override fun onError(message: String) {
                 Toast.makeText(this@CoinActivity, message, Toast.LENGTH_SHORT).show()
             }

         })

        binding.layoutChart.radioGroupChart.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId) {
                R.id.rdb_12h -> {
                    setChartAdapter(HOUR)
                }
                R.id.rdb_1d -> {
                    setChartAdapter(HOURS24)
                }
                R.id.rdb_1w -> {
                    setChartAdapter(WEEK)
                }
                R.id.rdb_1m -> {
                    setChartAdapter(MONTH)
                }
                R.id.rdb_3m -> {
                    setChartAdapter(MONTH3)
                }
                R.id.rdb_1y -> {
                    setChartAdapter(YEAR)
                }
                R.id.rdb_all -> {
                    setChartAdapter(ALL)
                }
            }
        }

    }
    private fun setChartAdapter (period:String ){

        apiManager.getChart( period, fsym , object :ApiManager.ApiCallBack<ChartData> {
            override fun onSuccess(data: ChartData) {
                val chartAdapter = ChartAdapter(data.data)
                binding.layoutChart.sparkView.adapter = chartAdapter
            }

            override fun onError(message: String) {
                Toast.makeText(this@CoinActivity, message, Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun setChartText (){
        binding.layoutChart.txtChartPrice.text = coinsData.dISPLAY.uSD.pRICE
        val taghir = coinsData.rAW.uSD.cHANGEPCT24HOUR

        if (taghir > 0) {
            binding.layoutChart.txtChartArrow.text = "▲"
            binding.layoutChart.txtChartArrow.setTextColor(
                ContextCompat.getColor(binding.root.context,R.color.colorGain)
            )
            binding.layoutChart.txtChartChange2.setTextColor(ContextCompat.getColor(binding.root.context , R.color.colorGain))
            binding.layoutChart.txtChartChange2.text = "+"+taghir.toString().substring(0,5) + "%"
            binding.layoutChart.sparkView.lineColor = ContextCompat.getColor(binding.root.context , R.color.colorGain)
        } else if (taghir < 0) {
            binding.layoutChart.txtChartArrow.text = "▼"
            binding.layoutChart.txtChartArrow.setTextColor(ContextCompat.getColor(binding.root.context , R.color.colorLoss))
            binding.layoutChart.txtChartChange2.setTextColor(ContextCompat.getColor(binding.root.context , R.color.colorLoss))
            binding.layoutChart.txtChartChange2.text = taghir.toString().substring(0,6) + "%"
            binding.layoutChart.sparkView.lineColor = ContextCompat.getColor(binding.root.context , R.color.colorLoss)
        }

        binding.layoutChart.txtChartChange1.text = coinsData.dISPLAY.uSD.cHANGE24HOUR

        binding.layoutChart.sparkView.setScrubListener {
            if (it == null) {
                binding.layoutChart.txtChartPrice.text = coinsData.dISPLAY.uSD.pRICE
            } else{
                binding.layoutChart.txtChartPrice.text = "$"+(it as ChartData.Data.Data).close.toString()
            }
        }

    }
}