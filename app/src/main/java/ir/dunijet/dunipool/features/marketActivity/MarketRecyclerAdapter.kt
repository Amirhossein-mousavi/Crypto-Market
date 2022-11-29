package ir.dunijet.dunipool.features.marketActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ir.dunijet.dunipool.R
import ir.dunijet.dunipool.apiManager.BASE_URL_IMAGE
import ir.dunijet.dunipool.apiManager.model.CoinsData
import ir.dunijet.dunipool.databinding.ItemRecyclerBinding

class MarketRecyclerAdapter(
    private val data : ArrayList<CoinsData.Data> ,
    private val coinsEvent: CoinsEvent
) :RecyclerView.Adapter<MarketRecyclerAdapter.CoinsViewHolder>() {
    lateinit var binding: ItemRecyclerBinding
    inner class CoinsViewHolder(itemView : View) : RecyclerView.ViewHolder (itemView){

        fun bindView(data : CoinsData.Data){
             binding.txtCoinName.text = data.coinInfo.fullName
            binding.txtPrice.text = data.dISPLAY.uSD.pRICE
            val taghir = data.rAW.uSD.cHANGEPCT24HOUR
            if (taghir > 0) {
                binding.txtChange.setTextColor(
                    ContextCompat.getColor(binding.root.context , R.color.colorGain)
                )
                binding.txtChange.text = taghir.toString().substring(0,5) + "%"
            } else if (taghir < 0) {
                binding.txtChange.setTextColor(
                    ContextCompat.getColor(binding.root.context , R.color.colorLoss)
                )
                binding.txtChange.text = taghir.toString().substring(0,6) + "%"
            } else {
                binding.txtChange.text = "0%"
            }

           val marketCap = data.rAW.uSD.mKTCAP / 1000000000
            val dot = marketCap.toString().indexOf(".")
            binding.txtMarketCap.text = "$" +marketCap.toString().substring(0 , dot+2) + "B"

            Glide.with(binding.root).load(BASE_URL_IMAGE+data.coinInfo.imageUrl).into(binding.imgItem)

            itemView.setOnClickListener {

                coinsEvent.click(data)

            }

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinsViewHolder {
        binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return CoinsViewHolder(binding.root)

    }
    override fun onBindViewHolder(holder: CoinsViewHolder, position: Int) {
        holder.bindView(data[position])
    }
    override fun getItemCount(): Int = data.size


    interface CoinsEvent {
        fun click(data : CoinsData.Data)
    }
}