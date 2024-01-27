package com.example.crypto.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.crypto.R
import com.example.crypto.databinding.FragmentDetailsBinding
import com.example.crypto.models.CryptoCurrency
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DetailsFragment : Fragment() {

    lateinit var binding: FragmentDetailsBinding

    private val item : DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(layoutInflater)

        val data : CryptoCurrency =item!!.data!!

        setUpDetails(data)

        loadChart(data)

        setButtonOnClick(data)

        addToWatchList(data)

        return binding.root
    }

    var watchList : ArrayList<String>? = null
    var watchListIsChecked = false


    private fun addToWatchList(data: CryptoCurrency) {
        readData()

        watchListIsChecked = watchList?.contains(data.symbol) == true

        updateWatchlistButton()

        binding.addWatchlistButton.setOnClickListener {
            watchListIsChecked = !watchListIsChecked

            if (watchListIsChecked) {
                watchList?.add(data.symbol)
            } else {
                watchList?.remove(data.symbol)
            }

            storeData()
            updateWatchlistButton()
        }
    }

    private fun updateWatchlistButton() {
        if (watchListIsChecked) {
            binding.addWatchlistButton.setBackgroundResource(R.drawable.ic_star)
        } else {
            binding.addWatchlistButton.setBackgroundResource(R.drawable.ic_star_outline)
        }
    }




    private fun storeData(){
        val sharedPreferences = requireContext().getSharedPreferences("watchList",Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json =gson.toJson(watchList)
        editor.putString("watchList", json)
        editor.apply()
    }

    private fun readData() {
        val sharedPreference = requireContext().getSharedPreferences("watchList", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreference.getString("watchList", ArrayList<String>().toString())
        val type = object : TypeToken<ArrayList<String>>(){}.type
        watchList = gson.fromJson(json,type)
    }

    private fun setButtonOnClick(item: CryptoCurrency) {

        val oneMonth = binding.button
        val oneWeek = binding.button1
        val oneDay = binding.button2
        val fourHour = binding.button3
        val oneHour = binding.button4
        val fifteenMinute = binding.button5

        val clickListener = View.OnClickListener {
            when (it.id) {
                fifteenMinute.id -> loadChartData(it, "15", item, oneDay, oneMonth, oneWeek, fourHour, oneHour)
                oneHour.id -> loadChartData(it, "1H", item, oneDay, oneMonth, oneWeek, fourHour, fifteenMinute)
                fourHour.id -> loadChartData(it, "4H", item, oneDay, oneMonth, oneWeek, fifteenMinute, oneHour)
                oneDay.id -> loadChartData(it, "D", item, fifteenMinute, oneMonth, oneWeek, fourHour, oneHour)
                oneWeek.id -> loadChartData(it, "W", item, oneDay, oneMonth, fifteenMinute, fourHour, oneHour)
                oneMonth.id -> loadChartData(it, "M", item, oneDay, fifteenMinute, oneWeek, fourHour, oneHour)
            }
        }


        fifteenMinute.setOnClickListener(clickListener)
        oneHour.setOnClickListener(clickListener)
        fourHour.setOnClickListener(clickListener)
        oneDay.setOnClickListener(clickListener)
        oneWeek.setOnClickListener(clickListener)
        oneMonth.setOnClickListener(clickListener)


    }

    private fun loadChartData(
        it: View?,
        s: String,
        item: CryptoCurrency,
        oneDay: AppCompatButton,
        oneMonth: AppCompatButton,
        oneWeek: AppCompatButton,
        fourHour: AppCompatButton,
        oneHour: AppCompatButton
    ) {

        disableButton(oneDay, oneMonth, oneWeek, fourHour, oneHour)
        it!!.setBackgroundResource(R.drawable.active_button)
        binding.detaillChartWebView.settings.javaScriptEnabled = true
        binding.detaillChartWebView.setLayerType(View.LAYER_TYPE_SOFTWARE,null)

        binding.detaillChartWebView.loadUrl(

            "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + item.symbol
                .toString() + "USD&interval="+s+"&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=" +
                    "F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=" +
                    "[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"

        )
    }

    private fun disableButton(oneDay: AppCompatButton, oneMonth: AppCompatButton, oneWeek: AppCompatButton, fourHour: AppCompatButton, oneHour: AppCompatButton) {
        oneDay.background = null
        oneMonth.background = null
        oneWeek.background = null
        fourHour.background = null
        oneHour.background = null
    }


    private fun loadChart(item: CryptoCurrency) {
        binding.detaillChartWebView.settings.javaScriptEnabled = true
        binding.detaillChartWebView.setLayerType(View.LAYER_TYPE_SOFTWARE,null)

        binding.detaillChartWebView.loadUrl(

            "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + item.symbol
                .toString() + "USD&interval=D&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=" +
                    "F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=" +
                    "[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"

        )

    }



    private fun setUpDetails(data: CryptoCurrency) {
        with(binding) {
            detailSymbolTextView.text = data.symbol

            // Verify URL and image loading
            val imageUrl = "https://s2.coinmarketcap.com/static/img/coins/64x64/${data.id}.png"
            Glide.with(requireContext())
                .load(imageUrl)
                .thumbnail(Glide.with(requireContext()).load(R.drawable.spinner))
                .into(detailImageView)

            detailPriceTextView.text = "${String.format("$%.04f", data.quotes[0].price)}"

            data.quotes?.get(0)?.let { quote ->
                val percentChange24h = quote.percentChange24h
                if (percentChange24h != null) {
                    if (percentChange24h > 0) {
                        detailChangeTextView.setTextColor(requireContext().resources.getColor(R.color.green))
                        detailChangeImageView.setImageResource(R.drawable.ic_caret_up)
                        detailChangeTextView.text = "+ ${String.format("%.02f", percentChange24h)} %"
                    } else {
                        detailChangeTextView.setTextColor(requireContext().resources.getColor(R.color.red))
                        detailChangeImageView.setImageResource(R.drawable.ic_caret_down)
                        detailChangeTextView.text = " ${String.format("%.02f", percentChange24h)} %"
                    }
                }
            }
        }
    }

}
