package com.proximity.labs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.proximity.labs.databinding.ActivityMainBinding
import com.proximity.labs.network.WebServicesProvider

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels{
        MainViewModelFactory(MainRepository(WebServicesProvider()))
    }
    private lateinit var binding: ActivityMainBinding
    private var adapter: AirQualityAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initUi()
        initObservers()

        viewModel.subscribeToSocketEvents()
    }

    private fun initUi() {
        adapter = AirQualityAdapter()
        binding.rvAqi.layoutManager = LinearLayoutManager(this)
        binding.rvAqi.adapter = adapter
    }

    private fun initObservers() {
        viewModel.loadingLiveData.observe(this, Observer {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
            binding.rvAqi.visibility = if (it) View.GONE else View.VISIBLE
        })

        viewModel.airQualityList.observe(this, Observer {
            it?.let { list ->
                adapter?.submitList(list)
            }
        })
    }
}