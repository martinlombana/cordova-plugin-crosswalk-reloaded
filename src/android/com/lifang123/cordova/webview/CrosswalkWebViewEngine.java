package com.lifang123.cordova.webview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.WebResourceResponse;

import com.ionicframework.cordova.webview.IonicWebView;
import com.ionicframework.cordova.webview.WebViewLocalServer;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine;
import org.apache.cordova.NativeToJsMessageQueue;
import org.apache.cordova.PluginManager;
import org.apache.cordova.engine.SystemWebView;
import org.crosswalk.engine.XWalkCordovaResourceClient;
import org.crosswalk.engine.XWalkWebViewEngine;
import org.xwalk.core.XWalkView;


public class CrosswalkWebViewEngine extends XWalkWebViewEngine {
    public static final String TAG = "CrosswalkWebViewEngine";

    private WebViewLocalServer localServer;
    private String CDV_LOCAL_SERVER;
    private static final String LAST_BINARY_VERSION_CODE = "lastBinaryVersionCode";
    private static final String LAST_BINARY_VERSION_NAME = "lastBinaryVersionName";

    /**
     * Used when created via reflection.
     */
    public CrosswalkWebViewEngine(Context context, CordovaPreferences preferences) {
        super(context, preferences);
        Log.d(TAG, "Ionic Web View Engine Starting Right Up 1...");
    }



    @Override
    public void init(CordovaWebView parentWebView, CordovaInterface cordova, final CordovaWebViewEngine.Client client,
                     CordovaResourceApi resourceApi, PluginManager pluginManager,
                     NativeToJsMessageQueue nativeToJsMessageQueue) {
        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(cordova.getActivity());

        String hostname = preferences.getString("Hostname", "localhost");
        String scheme = preferences.getString("Scheme", "http");
        CDV_LOCAL_SERVER = scheme + "://" + hostname;

        ServerClient serverClient = new ServerClient(this, parser);

        localServer = new WebViewLocalServer(cordova.getActivity(), hostname, true, parser, scheme);
        localServer.hostAssets("www");

        //webView.setWebViewClient(new ServerClient(this, parser));
        webView.setResourceClient(serverClient);


        super.init(parentWebView, cordova, client, resourceApi, pluginManager, nativeToJsMessageQueue);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        /*
            final WebSettings settings = webView.getSettings();
            int mode = preferences.getInteger("MixedContentMode", 0);
            settings.setMixedContentMode(mode);
        */
        }
        SharedPreferences prefs = cordova.getActivity().getApplicationContext().getSharedPreferences(IonicWebView.WEBVIEW_PREFS_NAME, Activity.MODE_PRIVATE);
        String path = prefs.getString(IonicWebView.CDV_SERVER_PATH, null);
        if (!isDeployDisabled() && !isNewBinary() && path != null && !path.isEmpty()) {
            setServerBasePath(path);
        }
    }

    private boolean isNewBinary() {
        String versionCode = "";
        String versionName = "";
        SharedPreferences prefs = cordova.getActivity().getApplicationContext().getSharedPreferences(IonicWebView.WEBVIEW_PREFS_NAME, Activity.MODE_PRIVATE);
        String lastVersionCode = prefs.getString(LAST_BINARY_VERSION_CODE, null);
        String lastVersionName = prefs.getString(LAST_BINARY_VERSION_NAME, null);

        try {
            PackageInfo pInfo = this.cordova.getActivity().getPackageManager().getPackageInfo(this.cordova.getActivity().getPackageName(), 0);
            versionCode = Integer.toString(pInfo.versionCode);
            versionName = pInfo.versionName;
        } catch (Exception ex) {
            Log.e(TAG, "Unable to get package info", ex);
        }

        if (!versionCode.equals(lastVersionCode) || !versionName.equals(lastVersionName)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(LAST_BINARY_VERSION_CODE, versionCode);
            editor.putString(LAST_BINARY_VERSION_NAME, versionName);
            editor.putString(IonicWebView.CDV_SERVER_PATH, "");
            editor.apply();
            return true;
        }
        return false;
    }

    private boolean isDeployDisabled() {
        return preferences.getBoolean("DisableDeploy", false);
    }

    public class ServerClient extends XWalkCordovaResourceClient {
        private ConfigXmlParser parser;

        public ServerClient(XWalkWebViewEngine parentEngine, ConfigXmlParser parser) {
            super(parentEngine);
            this.parser = parser;
        }

        @Override
        public WebResourceResponse shouldInterceptLoadRequest(XWalkView view, String url) {
            return localServer.shouldInterceptRequest(Uri.parse(url), null);
        }

        @Override
        public void onLoadStarted(XWalkView view, String url) {
            super.onLoadStarted(view, url);
            /**为了保留加载 file:// 协议 ，以下代码暂时被注释掉*/
            // String launchUrl = parser.getLaunchUrl();
            // if (!launchUrl.contains(WebViewLocalServer.httpsScheme) && !launchUrl.contains(WebViewLocalServer.httpScheme) && url.equals(launchUrl)) {
            //     view.stopLoading();
            //     // When using a custom scheme the app won't load if server start url doesn't end in /
            //     String startUrl = CDV_LOCAL_SERVER;
            //     if (!CDV_LOCAL_SERVER.startsWith(WebViewLocalServer.httpsScheme) && !CDV_LOCAL_SERVER.startsWith(WebViewLocalServer.httpScheme)) {
            //         startUrl += "/";
            //     }
            //     view.loadUrl(startUrl);
            // }
        }

        @Override
        public void onLoadFinished(XWalkView view, String url) {
            super.onLoadFinished(view, url);
            view.loadUrl("javascript:(function() { " +
                    "window.WEBVIEW_SERVER_URL = '" + CDV_LOCAL_SERVER + "';" +
                    "})()");
        }


    }

    public void setServerBasePath(String path) {
        localServer.hostFiles(path);
        webView.loadUrl(CDV_LOCAL_SERVER);
    }

    public String getServerBasePath() {
        return this.localServer.getBasePath();
    }
}