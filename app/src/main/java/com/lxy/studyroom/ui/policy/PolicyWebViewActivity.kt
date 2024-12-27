package com.lxy.studyroom.ui.policy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebViewClient
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.R
import kotlinx.android.synthetic.main.activity_policy_web_view.*

class PolicyWebViewActivity : BaseActivity() {

    companion object {
        @JvmStatic
        fun actionStart(context: Context, title: String,url: String){
            val intent = Intent(context, PolicyWebViewActivity::class.java).apply {
                putExtra("title", title)
                putExtra("url", url)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = intent.getStringExtra("title")
        val url = intent.getStringExtra("url")
        setContentView(R.layout.activity_policy_web_view)
        diaStStName.text = title

        ivBack.setOnClickListener {
            finish()
        }

        webViewPro.settings.javaScriptEnabled = false
        webViewPro.webViewClient = WebViewClient()
        webViewPro.loadUrl(url!!)


    }

    override fun onBackPressed() {
        finish()
    }


}