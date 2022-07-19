package com.gnuoynawh.musical.ticket.popup

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.RelativeLayout
import androidx.lifecycle.lifecycleScope
import com.gnuoynawh.musical.ticket.BuildConfig
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.common.site.Site
import com.gnuoynawh.musical.ticket.db.MTicketDatabase
import com.gnuoynawh.musical.ticket.db.Ticket
import com.gnuoynawh.musical.ticket.ui.MainActivity
import com.gnuoynawh.musical.ticket.web.MyJavaScriptInterface
import com.gnuoynawh.musical.ticket.web.MyWebViewClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class WebViewPopup(
    activity: MainActivity,
    val site: Site
): BottomSheetDialogFragment() {

    private val db by lazy {
        MTicketDatabase.getDatabase(activity)?.getTicket()
    }

    lateinit var webView: WebView
    lateinit var loadingView: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
//        // 팝업 생성 시 전체화면으로 띄우기
//        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
//        val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)
//        behavior.state = BottomSheetBehavior.STATE_EXPANDED
//
//        // 드래그해도 팝업이 종료되지 않도록
//        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
//                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
//                }
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
//        })

        //
        webView = view.findViewById(R.id.webView)
        loadingView = view.findViewById(R.id.layout_loading)

        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {

        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = false
        settings.domStorageEnabled = true
        settings.builtInZoomControls = false
        settings.displayZoomControls = false
        settings.mediaPlaybackRequiresUserGesture = false

        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.settings.setSupportMultipleWindows(false)

        //
        webView.webViewClient = MyWebViewClient(this, webView, site)
        webView.addJavascriptInterface(MyJavaScriptInterface(this, webView, site), "Android")

        //
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        webView.loadUrl(site.mainUrl)
        showLoading()

        webView.clearHistory()
        webView.clearFormData()
        webView.clearCache(true)

        val manager = CookieManager.getInstance()
        manager.removeAllCookie()
    }

    fun showLoading() {
        webView.visibility = View.INVISIBLE
        loadingView.visibility = View.VISIBLE
    }

    fun hideLoading() {
        webView.visibility = View.VISIBLE
        loadingView.visibility = View.INVISIBLE
    }

    fun onResultWithDB(list: ArrayList<Ticket>) {
        Log.e("TEST", "onResultWithDB : ${list.size}")

        // insert
        lifecycleScope.launch {
            db?.insert(*list.map { it }.toTypedArray())
            dismiss()
        }
    }
}