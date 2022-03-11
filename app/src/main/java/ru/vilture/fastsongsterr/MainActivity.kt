package ru.vilture.fastsongsterr

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import fastsongsterr.R
import fastsongsterr.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import ru.vilture.fastsongsterr.Interfaces.Common
import ru.vilture.fastsongsterr.Model.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TabAdapter
    lateinit var layoutManager: LinearLayoutManager

    val result = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.mipmap.ic_launcher)
        supportActionBar!!.setDisplayUseLogoEnabled(true)

        binding.recyclerTabList.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        binding.recyclerTabList.layoutManager = layoutManager

        binding.txSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length !in 0..1) {
                    getSong(binding.txSearch.text.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun getSong(search: String) {
        Common.api.getSongs(search).enqueue(object : Callback<List<Response>> {
            override fun onResponse(
                call: Call<List<Response>>,
                response: retrofit2.Response<List<Response>>
            ) {
                if (response.body().isNullOrEmpty()) {
                    getArtist(search)
                } else {
                    onView(response.body()!!)

                    Log.d("retrofit", "OK: " + response.body()!!.size)
                }
            }

            override fun onFailure(call: Call<List<Response>>, t: Throwable) {
                Log.d("retrofit", "FAIL: " + t.message.toString())
            }

        })
    }

    private fun getArtist(search: String) {
        Common.api.getArtists(search).enqueue(object : Callback<List<Response>> {
            override fun onResponse(
                call: Call<List<Response>>,
                response: retrofit2.Response<List<Response>>
            ) {
                if (response.isSuccessful) {
                    onView(response.body()!!)

                    Log.d("retrofit", "OK: " + response.body()!!.size)
                }
            }

            override fun onFailure(call: Call<List<Response>>, t: Throwable) {
                Log.d("retrofit", "FAIL: " + t.message.toString())
            }

        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onView(body: List<Response>) {
        adapter = TabAdapter(baseContext, body)
        adapter.notifyDataSetChanged()
        binding.recyclerTabList.adapter = adapter
    }
}




