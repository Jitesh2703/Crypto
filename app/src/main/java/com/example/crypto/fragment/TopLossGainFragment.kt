package com.example.crypto.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.crypto.adapter.MarketAdapter
import com.example.crypto.apis.ApiInterface
import com.example.crypto.apis.ApiUtilities
import com.example.crypto.databinding.FragmentTopLossGainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TopLossGainFragment : Fragment() {

    lateinit var binding : FragmentTopLossGainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTopLossGainBinding.inflate(layoutInflater)

        getMarketData()

        return binding.root
    }

    private fun getMarketData() {
        val position = requireArguments().getInt("position")
        lifecycleScope.launch(Dispatchers.IO) {
            val res = ApiUtilities.getInstance().create(ApiInterface::class.java).getMarketData()
            if (res.isSuccessful) {
                withContext(Dispatchers.Main) {
                    val dataItem = res.body()?.data?.cryptoCurrencyList

                    dataItem?.let { apiList ->
                        val sortedList = apiList.sortedByDescending { it.quotes[0].percentChange24h }

                        binding.spinKitView.visibility = View.GONE
                        val sublist = if (position == 0) {
                            sortedList.take(10)
                        } else {
                            sortedList.takeLast(10)
                        }

                        binding.topGainLoseRecyclerView.adapter = MarketAdapter(
                            requireContext(),
                            sublist,
                            "home"
                        )
                    }
                }
            }
        }
    }






}