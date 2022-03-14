package ru.vilture.fastsongsterr

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
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
import android.view.animation.AnimationUtils
import android.webkit.CookieManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import fastsongsterr.R
import fastsongsterr.databinding.ActivityMainBinding
import fastsongsterr.databinding.ChooseinstBinding
import retrofit2.Call
import retrofit2.Callback
import ru.vilture.fastsongsterr.Adapter.TabAdapter
import ru.vilture.fastsongsterr.DB.ConnectDB
import ru.vilture.fastsongsterr.Interfaces.Common
import ru.vilture.fastsongsterr.Model.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


open class MainActivity : AppCompatActivity() {

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

        chooseInst(' ')

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

    fun readPref(context: Context): String? {
        return PreferenceManager.getDefaultSharedPreferences(context)?.getString("inst", "")
    }

    @SuppressLint("CommitPrefEdits")
    private fun chooseInst(mode: Char?) {
        val sPref = PreferenceManager.getDefaultSharedPreferences(this)
        val bindModal = ChooseinstBinding.inflate(layoutInflater)
        val dialog = Dialog(
            this,
            R.style.Dialog
        )
        dialog.setTitle(getString(R.string.finst))
        dialog.setContentView(bindModal.root)
        dialog.setCancelable(false)


        if (readPref(this) == "") {
            dialog.show()
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        }

        if (mode == 'X') {
            dialog.show()
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        }

        bindModal.chElectro.setOnClickListener {
            sPref.edit().clear().apply()
            bindModal.imgElectro.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animg))
            sPref.edit().putString("inst", "&inst=default").apply()

            dialog.dismiss()
        }

        bindModal.chBass.setOnClickListener {
            sPref.edit().clear().apply()
            bindModal.imgBass.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animg))
            sPref.edit().putString("inst", "&inst=bass").apply()

            dialog.dismiss()
        }

        bindModal.chDrum.setOnClickListener {
            sPref.edit().clear().apply()
            bindModal.imgDrum.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animg))
            sPref.edit().putString("inst", "&inst=drum").apply()

            dialog.dismiss()
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
                        ConnectDB(this).exportDatabase()
                    }
                    .setNegativeButton(getString(R.string.importx)) { dialog, id ->
                        importDatabase.launch("application/octet-stream")

                    }
                    .setNeutralButton(getString(R.string.cancel)) { dialog, id -> dialog.cancel() }
                ad.create().show()

                return true
            }
            R.id.menu_inst -> {
                chooseInst('X')
            }
        }
        return super.onOptionsItemSelected(item)
    }


    var importDatabase =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            val currentDB = File(uri.encodedPath)
            val backupDB =
                File("/data/data/ru.vilture.fastsongsterr/databases/fastsongsterr.db")
            try {
                val source = FileInputStream(currentDB).channel
                val destination = FileOutputStream(backupDB).channel
                destination.transferFrom(source, 0, source.size())
                source.close()
                destination.close()
                Toast.makeText(this, getString(R.string.msgimport), Toast.LENGTH_SHORT)
                    .show()
            } catch (ex: Exception) {
                Toast.makeText(
                    this,
                    getString(R.string.errimport, ex.message),
                    Toast.LENGTH_LONG
                )
                    .show()
                ex.printStackTrace()
            }
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

    fun getArtist(search: String) {
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




