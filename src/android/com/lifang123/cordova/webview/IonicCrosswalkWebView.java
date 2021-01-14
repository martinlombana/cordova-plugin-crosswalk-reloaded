package com.lifang123.cordova.webview;

import android.app.Activity;
import android.content.SharedPreferences;

import com.ionicframework.cordova.webview.IonicWebViewEngine;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class IonicCrosswalkWebView extends CordovaPlugin {

    public static final String WEBVIEW_PREFS_NAME = "WebViewSettings";
    public static final String CDV_SERVER_PATH = "serverBasePath";

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("setServerBasePath")) {
            final String path = args.getString(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((IonicWebViewEngine) webView.getEngine()).setServerBasePath(path);
                }
            });
            return true;
        } else if (action.equals("getServerBasePath")) {
            callbackContext.success(((IonicWebViewEngine) webView.getEngine()).getServerBasePath());
            return true;
        } else if (action.equals("persistServerBasePath")) {
            String path = ((IonicWebViewEngine) webView.getEngine()).getServerBasePath();
            SharedPreferences prefs = cordova.getActivity().getApplicationContext().getSharedPreferences(WEBVIEW_PREFS_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(CDV_SERVER_PATH, path);
            editor.apply();
            return true;
        } else if (action.equals("useCrosswalkWebViewAtTheNextStartup")) {
            SharedPreferences sharedPreferences = this.cordova.getContext().getSharedPreferences("use_webview_engine",0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("WEBVIEW_ENGINE","CROSSWALK");
            editor.commit();
            return true;
        } else if (action.equals("useSystemWebViewAtTheNextStartup")) {
            SharedPreferences sharedPreferences = this.cordova.getContext().getSharedPreferences("use_webview_engine",0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("WEBVIEW_ENGINE","SYSTEM");
            editor.commit();
            return true;
        }
        return false;
    }

}