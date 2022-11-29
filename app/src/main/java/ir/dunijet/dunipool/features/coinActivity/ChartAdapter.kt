package ir.dunijet.dunipool.features.coinActivity

import com.robinhood.spark.SparkAdapter
import ir.dunijet.dunipool.apiManager.model.ChartData

class ChartAdapter(private val data :ChartData.Data) :SparkAdapter() {
    override fun getCount(): Int =data.data.size

    override fun getItem(index: Int): Any {
        return data.data[index]
    }

    override fun getY(index: Int): Float {
       return data.data[index].close.toFloat()
    }

    override fun hasBaseLine(): Boolean = true
    override fun getBaseLine(): Float {

        val baseLine = data.data.maxByOrNull { it.close }

        return baseLine?.open?.toFloat() ?: super.getBaseLine()
    }
}