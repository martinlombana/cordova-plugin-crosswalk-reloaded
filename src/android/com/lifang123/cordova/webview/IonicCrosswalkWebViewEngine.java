package com.lifang123.cordova.webview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ionicframework.cordova.webview.IonicWebViewEngine;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine;
import org.apache.cordova.ICordovaCookieManager;
import org.apache.cordova.NativeToJsMessageQueue;
import org.apache.cordova.PluginManager;
import org.apache.cordova.engine.SystemWebViewClient;
import org.apache.cordova.engine.SystemWebViewEngine;
import org.apache.cordova.engine.SystemWebView;

public class IonicCrosswalkWebViewEngine implements CordovaWebViewEngine {

    public static final String TAG = "IonicWebViewEngine";

    public IonicWebViewEngine ionicWebViewEngine;
    public CrosswalkWebViewEngine crosswalkWebViewEngine;
    public boolean isUseCrosswalkWebView;

    public IonicCrosswalkWebViewEngine(Context context, CordovaPreferences preferences) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= 24) {
                isUseCrosswalkWebView = false;
            } else {
                isUseCrosswalkWebView = true;
            }
            SharedPreferences sharedPreferences = context.getSharedPreferences("use_webview_engine", 0);
            String useWebViewEngine = sharedPreferences.getString("WEBVIEW_ENGINE", "AUTO");
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), context.getPackageManager().GET_META_DATA);
            Bundle bundle = appInfo.metaData;
            String action = bundle.getString("WEBVIEW_ENGINE");
            Log.i(TAG, "meta-data:" + action);
            if (action.equals("CROSSWALK")) {
                isUseCrosswalkWebView = true;
            } else {
                if (action.equals("SYSTEM")) {
                    isUseCrosswalkWebView = false;
                }
            }

            if (useWebViewEngine != null && useWebViewEngine.equals("CROSSWALK")) {
                isUseCrosswalkWebView = true;
            } else {
                if (useWebViewEngine != null && useWebViewEngine.equals("SYSTEM")) {
                    isUseCrosswalkWebView = false;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        if (isUseCrosswalkWebView) {
            crosswalkWebViewEngine = new CrosswalkWebViewEngine(context, preferences);
        } else {
            ionicWebViewEngine = new IonicWebViewEngine(context, preferences);
        }

        Log.d(TAG, "Ionic Web View Engine Starting Right Up 1...");
    }

    @Override
    public void init(CordovaWebView parentWebView, CordovaInterface cordova, Client client,
                     CordovaResourceApi resourceApi, PluginManager pluginManager,
                     NativeToJsMessageQueue nativeToJsMessageQueue) {
        if (isUseCrosswalkWebView) {
            crosswalkWebViewEngine.init(parentWebView, cordova, client, resourceApi, pluginManager,
                    nativeToJsMessageQueue);
        } else {
            ionicWebViewEngine.init(parentWebView, cordova, client, resourceApi, pluginManager, nativeToJsMessageQueue);
        }
    }

    @Override
    public CordovaWebView getCordovaWebView() {
        if (isUseCrosswalkWebView) {
            return crosswalkWebViewEngine.getCordovaWebView();
        } else {
            return ionicWebViewEngine.getCordovaWebView();
        }
    }

    @Override
    public ICordovaCookieManager getCookieManager() {
        if (isUseCrosswalkWebView) {
            return crosswalkWebViewEngine.getCookieManager();
        } else {
            return ionicWebViewEngine.getCookieManager();
        }
    }

    @Override
    public View getView() {
        if (isUseCrosswalkWebView) {
            return crosswalkWebViewEngine.getView();
        } else {
            return ionicWebViewEngine.getView();
        }
    }

    @Override
    public void loadUrl(String url, boolean clearNavigationStack) {
        if (isUseCrosswalkWebView) {
            crosswalkWebViewEngine.loadUrl(url, clearNavigationStack);
        } else {
            ionicWebViewEngine.loadUrl(url, clearNavigationStack);
        }
    }

    @Override
    public void stopLoading() {
        if (isUseCrosswalkWebView) {
            crosswalkWebViewEngine.stopLoading();
        } else {
            ionicWebViewEngine.stopLoading();
        }
    }

    @Override
    public String getUrl() {
        if (isUseCrosswalkWebView) {
            return crosswalkWebViewEngine.getUrl();
        } else {
            return ionicWebViewEngine.getUrl();
        }
    }

    @Override
    public void clearCache() {
        if (isUseCrosswalkWebView) {
            crosswalkWebViewEngine.clearCache();
        } else {
            ionicWebViewEngine.clearCache();
        }
    }

    @Override
    public void clearHistory() {
        if (isUseCrosswalkWebView) {
            crosswalkWebViewEngine.clearHistory();
        } else {
            ionicWebViewEngine.clearHistory();
        }
    }

    @Override
    public boolean canGoBack() {
        if (isUseCrosswalkWebView) {
            return crosswalkWebViewEngine.canGoBack();
        } else {
            return ionicWebViewEngine.canGoBack();
        }
    }

    @Override
    public boolean goBack() {
        if (isUseCrosswalkWebView) {
            return crosswalkWebViewEngine.goBack();
        } else {
            return ionicWebViewEngine.goBack();
        }
    }

    @Override
    public void setPaused(boolean value) {
        if (isUseCrosswalkWebView) {
            crosswalkWebViewEngine.setPaused(value);
        } else {
            ionicWebViewEngine.setPaused(value);
        }
    }

    @Override
    public void destroy() {
        if (isUseCrosswalkWebView) {
            crosswalkWebViewEngine.destroy();
        } else {
            ionicWebViewEngine.destroy();
        }
    }

    @Override
    public void evaluateJavascript(String js, ValueCallback<String> callback) {
        if (isUseCrosswalkWebView) {
            crosswalkWebViewEngine.evaluateJavascript(js, callback);
        } else {
            ionicWebViewEngine.evaluateJavascript(js, callback);
        }
    }
}
