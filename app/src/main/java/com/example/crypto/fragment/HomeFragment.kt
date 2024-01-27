package com.example.crypto.fragment

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.crypto.adapter.TopLossGainPagerAdapter
import com.example.crypto.adapter.TopMarketAdapter
import com.example.crypto.apis.ApiInterface
import com.example.crypto.apis.ApiUtilities
import com.example.crypto.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// ... (imports and other code)

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Set the LinearLayoutManager with horizontal orientation
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.topCurrencyRecyclerView.layoutManager = layoutManager

        getTopCurrencyList()

        setTabLayout()

        return binding.root
    }

    private fun setTabLayout() {
        val adapter = TopLossGainPagerAdapter(this)
        binding.contentViewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.contentViewPager) { tab, position ->
            var title = if (position == 0) {
                "Top Gainers"
            } else {
                "Top Losers"
            }
            tab.text = title
        }.attach()

        binding.contentViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    binding.topGainIndicator.visibility = View.VISIBLE
                    binding.topLoseIndicator.visibility = View.GONE
                } else {
                    binding.topGainIndicator.visibility = View.GONE
                    binding.topLoseIndicator.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun getTopCurrencyList() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response =
                    ApiUtilities.getInstance().create(ApiInterface::class.java).getMarketData()

                if (response.isSuccessful) {
                    val marketData = response.body()
                    marketData?.data?.cryptoCurrencyList?.let {
                        withContext(Dispatchers.Main) {
                            binding.topCurrencyRecyclerView.adapter =
                                TopMarketAdapter(requireContext(), it)
                        }
                        Log.d("SHUBH", "getTopCurrencyList: $it")
                    } ?: Log.e("SHUBH", "Response body is null")
                } else {
                    Log.e("SHUBH", "API call failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("SHUBH", "Exception: ${e.message}")
            }
        }
    }
}




