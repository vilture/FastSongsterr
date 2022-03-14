package ru.vilture.fastsongsterr

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fastsongsterr.R
import fastsongsterr.databinding.ActivityWebviewBinding
import java.io.File
import java.io.FileOutputStream


class WebView : AppCompatActivity() {
    private lateinit var binding: ActivityWebviewBinding
    private var url: String = ""
    private var name: String = ""
    lateinit var bitmap: Bitmap

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.mipmap.ic_launcher)
        supportActionBar!!.setDisplayUseLogoEnabled(true)

        val wv = binding.webView

        url = intent.getStringExtra("url").toString()
        name = intent.getStringExtra("song").toString()

        wv.settings.javaScriptEnabled = true
        wv.settings.loadWithOverviewMode = true
        wv.settings.domStorageEnabled = true
        wv.settings.useWideViewPort = true
        wv.settings.setSupportZoom(true)

        wv.clearCache(true)
        wv.setBackgroundColor(Color.WHITE)

        @Suppress("DEPRECATION")
        wv.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {

                wv.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )

                wv.layout(0, 0, wv.measuredWidth, wv.measuredHeight)
                wv.isDrawingCacheEnabled = true
                wv.buildDrawingCache()

                if (wv.measuredWidth > 0 && wv.measuredHeight > 0) {
                    bitmap = Bitmap.createBitmap(
                        wv.measuredWidth,
                        wv.measuredHeight,
                        Bitmap.Config.ARGB_8888
                    )
                }

                bitmap.let {
                    Canvas(bitmap).apply {
                        drawBitmap(it, 0f, bitmap.height.toFloat(), Paint())
                        wv.draw(this)
                    }
                }
            }
        }

        wv.loadUrl(url)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.wv_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    @SuppressLint("SdCardPath")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_savepng -> {
                savePng(File("/sdcard/FastSongsterr/"), "$name.png", bitmap)

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun savePng(directory: File, fileName: String, bitmap: Bitmap) {
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, fileName)

        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            Toast.makeText(this, getString(R.string.saveas) + file, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        binding.webView.settings.javaScriptEnabled = false
        binding.webView.destroy()
        super.onBackPressed()
    }
}