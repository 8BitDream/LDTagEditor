package com.ld.tageditor;

import android.nfc.tech.MifareUltralight;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import java.io.IOException;

public class JSAPI {
    MainActivity activity;
    WebView web;

    public JSAPI(MainActivity activity, WebView web) {
        this.activity = activity;
        this.web = web;
    }

    @JavascriptInterface
    public String readTag(byte page) {
        MifareUltralight mifare = MifareUltralight.get(this.activity.tag);
        try {
            Log.i("JSAPI", "Connecting");
            mifare.connect();
            Log.i("JSAPI", "Connected");
            Log.i("JSAPI", "Read");
            Log.i("JSAPI", String.format("Payload %02X%02X%02X%02X %02X%02X%02X%02X %02X%02X%02X%02X %02X%02X%02X%02X", new Object[]{Byte.valueOf(payload[0]), Byte.valueOf(payload[1]), Byte.valueOf(payload[2]), Byte.valueOf(payload[3]), Byte.valueOf(payload[4]), Byte.valueOf(payload[5]), Byte.valueOf(payload[6]), Byte.valueOf(payload[7]), Byte.valueOf(payload[8]), Byte.valueOf(payload[9]), Byte.valueOf(payload[10]), Byte.valueOf(payload[11]), Byte.valueOf(payload[12]), Byte.valueOf(payload[13]), Byte.valueOf(payload[14]), Byte.valueOf(mifare.readPages(page)[15])}));
            String encodeToString = Base64.encodeToString(payload, 0, 16, 0);
            if (mifare == null) {
                return encodeToString;
            }
            try {
                mifare.close();
                return encodeToString;
            } catch (IOException e) {
                Log.e("JSAPI", "Error closing tag...", e);
                return encodeToString;
            }
        } catch (IOException e2) {
            Log.e("JSAPI", "IOException while writing MifareUltralight message...", e2);
            if (mifare != null) {
                try {
                    mifare.close();
                } catch (IOException e22) {
                    Log.e("JSAPI", "Error closing tag...", e22);
                }
            }
            return null;
        } catch (Throwable th) {
            if (mifare != null) {
                try {
                    mifare.close();
                } catch (IOException e222) {
                    Log.e("JSAPI", "Error closing tag...", e222);
                }
            }
        }
        return null;
    }

    @JavascriptInterface
    public boolean writeTag(byte page, String payload) {
        byte[] data = Base64.decode(payload, 0);
        MifareUltralight ultralight = MifareUltralight.get(this.activity.tag);
        try {
            Log.i("JSAPI", "Connecting");
            ultralight.connect();
            Log.i("JSAPI", "Connected");
            Log.i("JSAPI", String.format("Writing %02X%02X%02X%02X", new Object[]{Byte.valueOf(data[0]), Byte.valueOf(data[1]), Byte.valueOf(data[2]), Byte.valueOf(data[3])}));
            ultralight.writePage(page, data);
            Log.i("JSAPI", "Writing Done");
            try {
                Log.i("JSAPI", "Closing");
                ultralight.close();
                Log.i("JSAPI", "Closed");
                return true;
            } catch (IOException e) {
                Log.e("JSAPI", "IOException while closing MifareUltralight...", e);
                return false;
            }
        } catch (IOException e2) {
            Log.e("JSAPI", "IOException while closing MifareUltralight...", e2);
            try {
                Log.i("JSAPI", "Closing");
                ultralight.close();
                Log.i("JSAPI", "Closed");
                return true;
            } catch (IOException e22) {
                Log.e("JSAPI", "IOException while closing MifareUltralight...", e22);
                return false;
            }
        } catch (Throwable th) {
            try {
                Log.i("JSAPI", "Closing");
                ultralight.close();
                Log.i("JSAPI", "Closed");
                return true;
            } catch (IOException e222) {
                Log.e("JSAPI", "IOException while closing MifareUltralight...", e222);
                return false;
            }
        }
    }

    private void callJavaScript(String methodName, Object... params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("javascript:try{(window.");
        stringBuilder.append(methodName);
        stringBuilder.append("||console.warn.bind(console,'UNHANDLED','");
        stringBuilder.append(methodName);
        stringBuilder.append("'))(");
        for (Object param : params) {
            Object param2 = "";
            if (!(param instanceof String)) {
                param2 = param.toString();
            }
            stringBuilder.append("'");
            stringBuilder.append(param2);
            stringBuilder.append("'");
            stringBuilder.append(",");
        }
        stringBuilder.append("''");
        stringBuilder.append(")}catch(error){console.error('ANDROID APP ERROR',error);}");
        this.web.loadUrl(stringBuilder.toString());
        Log.i("CallJS", stringBuilder.toString());
    }
}
