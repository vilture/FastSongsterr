package ru.vilture.fastsongsterr

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import fastsongsterr.R
import fastsongsterr.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import ru.vilture.fastsongsterr.Adapter.TabAdapter
import ru.vilture.fastsongsterr.DB.ConnectDB
import ru.vilture.fastsongsterr.Interfaces.Common
import ru.vilture.fastsongsterr.Model.Response


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TabAdapter
    private lateinit var layoutManager: LinearLayoutManager

    @RequiresApi(Build.VERSION_CODES.R)
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length !in 0..1) {
                    getSong(binding.txSearch.text.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ),
            1
        )

        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.isExternalStorageManager()
            } else {
                return
            }
        ) {
        } else {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            val uri: Uri = Uri.fromParts("package", this.packageName, null)
            intent.data = uri
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_favorites -> {
                if (item.isChecked) {
                    item.isChecked = false
                    binding.txSearch.visibility = View.VISIBLE
                    getSong(binding.txSearch.text.toString())
                } else {
                    item.isChecked = true
                    binding.txSearch.visibility = View.GONE
                    onView(ConnectDB(this).readData(), "favo")
                }

                return true
            }
            R.id.menu_backup -> {
                val ad = AlertDialog.Builder(this)
                ad.setMessage(getString(R.string.qustfavo))
                    .setCancelable(true)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton(getString(R.string.export)) { dialog, id ->
                        ConnectDB(this).exportDatabase(
                            this
                        )
                    }
                    .setNegativeButton(getString(R.string.importx)) { dialog, id -> ConnectDB(this).importDatabase(this) }
                    .setNeutralButton(getString(R.string.cancel)) { dialog, id -> dialog.cancel() }
                ad.create().show()

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun getSong(search: String) {
        Common.api.getSongs(search).enqueue(object : Callback<List<Response>> {
            override fun onResponse(
                call: Call<List<Response>>,
                response: retrofit2.Response<List<Response>>
            ) {
                if (binding.txSearch.text.isNotEmpty()) {
                    if (response.body().isNullOrEmpty()) {
                        getArtist(search)
                    } else {
                        onView(response.body()!!, "main")
                        Log.d("retrofit", "OK: " + response.body()!!.size)
                    }
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
                    onView(response.body()!!, "main")

                    Log.d("retrofit", "OK: " + response.body()!!.size)
                }
            }

            override fun onFailure(call: Call<List<Response>>, t: Throwable) {
                Log.d("retrofit", "FAIL: " + t.message.toString())
            }

        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onView(body: List<Response>, name: String) {
        adapter = TabAdapter(baseContext, name, body)
        adapter.notifyDataSetChanged()
        binding.recyclerTabList.adapter = adapter
    }
}




