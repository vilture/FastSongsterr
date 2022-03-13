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

        val vw = binding.webView

        url = intent.getStringExtra("url").toString()
        name = intent.getStringExtra("song").toString()

        vw.webViewClient = object : WebViewClient() {}

        vw.settings.javaScriptEnabled = true
        vw.settings.loadWithOverviewMode = true
        vw.settings.useWideViewPort = true
        vw.setBackgroundColor(Color.WHITE)
        vw.clearCache(true)

        vw.webViewClient = object : WebViewClient() {
            @Suppress("DEPRECATION")
            override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                vw.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )

                vw.layout(0, 0, vw.measuredWidth, vw.measuredHeight)

                vw.isDrawingCacheEnabled = true
                vw.buildDrawingCache()

                if (vw.measuredWidth > 0 && vw.measuredHeight > 0) {
                    bitmap = Bitmap.createBitmap(
                        vw.measuredWidth,
                        vw.measuredHeight,
                        Bitmap.Config.ARGB_8888
                    )
                }

                bitmap.let {
                    Canvas(bitmap).apply {
                        drawBitmap(it, 0f, bitmap.height.toFloat(), Paint())
                        vw.draw(this)
                    }
                }
            }
        }


        vw.loadUrl(url)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.wv_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    @SuppressLint("SdCardPath")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_savepng -> {
                savePng(File("/sdcard/FastSongsterr/"), "$name.jpg", bitmap)

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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            Toast.makeText(this, getString(R.string.saveas) + file, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}