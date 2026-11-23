package com.ultracam.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.ValueCallback;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.KeyEvent;

public class MainActivity extends Activity {
    private WebView webView;
    private ValueCallback<Uri[]> uploadCallback;
    private ValueCallback<Uri> legacyUploadCallback;
    private static final int FILE_CHOOSER_REQUEST = 4102;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFFFFFFFF);
            getWindow().setNavigationBarColor(0xFFFFFFFF);
        }
        if (Build.VERSION.SDK_INT >= 23) getWindow().getDecorView().setSystemUiVisibility(0x00002000);

        webView = new WebView(this);
        setContentView(webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= 17) settings.setMediaPlaybackRequiresUserGesture(false);
        if (Build.VERSION.SDK_INT >= 21) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }
        CookieManager.getInstance().setAcceptCookie(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            // Android 5.0+
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                uploadCallback = callback;
                Intent intent = params.createIntent();
                try { startActivityForResult(intent, FILE_CHOOSER_REQUEST); }
                catch (Exception e) { uploadCallback = null; return false; }
                return true;
            }
            // Legacy WebView file chooser hooks used by older Android versions.
            public void openFileChooser(ValueCallback<Uri> callback) { openLegacyChooser(callback); }
            public void openFileChooser(ValueCallback<Uri> callback, String acceptType) { openLegacyChooser(callback); }
            public void openFileChooser(ValueCallback<Uri> callback, String acceptType, String capture) { openLegacyChooser(callback); }
        });
        webView.loadUrl("file:///android_asset/index.html");
    }

    private void openLegacyChooser(ValueCallback<Uri> callback) {
        legacyUploadCallback = callback;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        try { startActivityForResult(Intent.createChooser(intent, "Choose a file"), FILE_CHOOSER_REQUEST); }
        catch (Exception e) { legacyUploadCallback = null; }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != FILE_CHOOSER_REQUEST) return;
        Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
        if (uploadCallback != null) {
            uploadCallback.onReceiveValue(result == null ? null : new Uri[]{result});
            uploadCallback = null;
        }
        if (legacyUploadCallback != null) {
            legacyUploadCallback.onReceiveValue(result);
            legacyUploadCallback = null;
        }
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (webView != null) { webView.loadUrl("about:blank"); webView.stopLoading(); webView.destroy(); webView = null; }
        super.onDestroy();
    }
}
