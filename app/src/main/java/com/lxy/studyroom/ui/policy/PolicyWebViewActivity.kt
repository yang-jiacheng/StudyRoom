package com.lxy.studyroom.ui.policy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebViewClient
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.R
import com.lxy.studyroom.databinding.ActivityPolicyWebViewBinding

class PolicyWebViewActivity : BaseActivity() {

    private lateinit var binding: ActivityPolicyWebViewBinding

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
        binding = ActivityPolicyWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val title = intent.getStringExtra("title")
        val url = intent.getStringExtra("url")
        
        binding.diaStStName.text = title

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.webViewPro.settings.javaScriptEnabled = false
        binding.webViewPro.webViewClient = WebViewClient()
        binding.webViewPro.loadUrl(url!!)
    }

    override fun onBackPressed() {
        finish()
    }
}