#!/bin/bash
set -e

echo "=== INICIANDO COMPILACIÓN DE APK NATIVO ANDROID ==="
WORKDIR="/tmp/android_apk_build"
rm -rf "$WORKDIR"
mkdir -p "$WORKDIR/src/com/ecosdolugar"
mkdir -p "$WORKDIR/res/values"
mkdir -p "$WORKDIR/res/drawable"
mkdir -p "$WORKDIR/res/mipmap-hdpi"
mkdir -p "$WORKDIR/bin"

ANDROID_JAR="/usr/lib/android-sdk/platforms/android-23/android.jar"
APP_URL="https://ais-pre-54aukgd57pdcdewumjczwr-452532466563.europe-west2.run.app"

# 1. AndroidManifest.xml
cat << 'EOF' > "$WORKDIR/AndroidManifest.xml"
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.ecosdolugar"
    android:versionCode="1"
    android:versionName="1.0.0">

    <uses-sdk android:minSdkVersion="21" android:targetSdkVersion="34" />

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.VIBRATE" />

    <application
        android:label="@string/app_name"
        android:icon="@drawable/ic_launcher"
        android:theme="@android:style/Theme.NoTitleBar.Fullscreen"
        android:allowBackup="true"
        android:hardwareAccelerated="true"
        android:usesCleartextTraffic="true">
        <activity
            android:name=".MainActivity"
            android:label="@string/app_name"
            android:configChanges="orientation|screenSize|keyboardHidden"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
EOF

# 2. strings.xml
cat << 'EOF' > "$WORKDIR/res/values/strings.xml"
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Ecos do Lugar</string>
</resources>
EOF

# 3. Create app icon (copy public/vite.svg or generate simple PNG)
# Let's check public icon
if [ -f "public/pwa-512x512.png" ]; then
    cp public/pwa-512x512.png "$WORKDIR/res/drawable/ic_launcher.png"
elif [ -f "public/icon.png" ]; then
    cp public/icon.png "$WORKDIR/res/drawable/ic_launcher.png"
else
    # Generate a simple 192x192 PNG if needed using python or imagemagick or touch
    python3 -c "
import struct, zlib

def make_png(w, h, color):
    raw = b''.join([b'\x00' + color * w for _ in range(h)])
    def chunk(tag, data):
        return struct.pack('>I', len(data)) + tag + data + struct.pack('>I', zlib.crc32(tag + data) & 0xffffffff)
    return b'\x89PNG\r\n\x1a\n' + chunk(b'IHDR', struct.pack('>IIBBBBB', w, h, 8, 2, 0, 0, 0)) + chunk(b'IDAT', zlib.compress(raw)) + chunk(b'IEND', b'')

with open('$WORKDIR/res/drawable/ic_launcher.png', 'wb') as f:
    f.write(make_png(96, 96, b'\x1b\x30\x22'))
"
fi

# 4. MainActivity.java
cat << EOF > "$WORKDIR/src/com/ecosdolugar/MainActivity.java"
package com.ecosdolugar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private static final String DEFAULT_URL = "$APP_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setBackgroundColor(Color.parseColor("#061b0e"));

        mWebView = new WebView(this);
        mWebView.setBackgroundColor(Color.parseColor("#061b0e"));

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        settings.setUserAgentString(settings.getUserAgentString() + " EcosDoLugarApp/1.0");

        mProgressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setMax(100);
        mProgressBar.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, 8
        ));
        mProgressBar.setVisibility(View.GONE);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    return false;
                }
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                String fallbackHtml = "<html><body style='background:#061b0e;color:#ffddba;font-family:sans-serif;padding:30px;text-align:center;'>"
                    + "<h2 style='color:#faba71;'>Ecos do Lugar</h2>"
                    + "<p>Reconectando con la frontera viva...</p>"
                    + "<button onclick='location.reload()' style='background:#faba71;color:#1b3022;border:none;padding:12px 24px;border-radius:20px;font-weight:bold;margin-top:20px;'>Reintentar</button>"
                    + "</body></html>";
                view.loadDataWithBaseURL(null, fallbackHtml, "text/html", "utf-8", null);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        rootLayout.addView(mWebView, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        ));
        rootLayout.addView(mProgressBar);

        setContentView(rootLayout);

        mWebView.loadUrl(DEFAULT_URL);
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
EOF

# 5. Compile Android Resources with aapt
echo "Empaquetando recursos con aapt..."
aapt package -f -m \
    -J "$WORKDIR/src" \
    -M "$WORKDIR/AndroidManifest.xml" \
    -S "$WORKDIR/res" \
    -I "$ANDROID_JAR"

# 6. Compile Java classes
echo "Compilando clases Java con javac..."
javac -source 1.8 -target 1.8 \
    -bootclasspath "$ANDROID_JAR" \
    -cp "$WORKDIR/src" \
    -d "$WORKDIR/bin" \
    $(find "$WORKDIR/src" -name "*.java")

# 7. Convert bytecode to Dalvik DEX
echo "Generando Dalvik DEX con dalvik-exchange..."
/usr/bin/dalvik-exchange --dex --output="$WORKDIR/bin/classes.dex" "$WORKDIR/bin"

# 8. Create unsigned APK packaging resources
echo "Generando APK base con aapt..."
aapt package -f \
    -M "$WORKDIR/AndroidManifest.xml" \
    -S "$WORKDIR/res" \
    -I "$ANDROID_JAR" \
    -F "$WORKDIR/bin/app-unsigned-unaligned.apk"

# 9. Add classes.dex into APK
echo "Añadiendo classes.dex..."
cd "$WORKDIR/bin"
zip -u "app-unsigned-unaligned.apk" classes.dex
cd -

# 10. Zipalign APK
echo "Alineando APK con zipalign..."
zipalign -v -p 4 "$WORKDIR/bin/app-unsigned-unaligned.apk" "$WORKDIR/bin/app-aligned.apk"

# 11. Generate release debug signing keystore if not exists
KEYSTORE="/tmp/ecosdolugar.keystore"
if [ ! -f "$KEYSTORE" ]; then
    keytool -genkeypair -v \
        -keystore "$KEYSTORE" \
        -alias ecosdolugar \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 \
        -storepass "ecosdolugar123" \
        -keypass "ecosdolugar123" \
        -dname "CN=EcosDoLugar, OU=Development, O=EcosDoLugar, L=Tui, ST=Pontevedra, C=ES"
fi

# 12. Sign APK with apksigner
echo "Firmando APK con apksigner..."
apksigner sign \
    --ks "$KEYSTORE" \
    --ks-key-alias ecosdolugar \
    --ks-pass pass:ecosdolugar123 \
    --key-pass pass:ecosdolugar123 \
    --out "$WORKDIR/bin/ecos-do-lugar.apk" \
    "$WORKDIR/bin/app-aligned.apk"

# Verify signature
echo "Verificando firma del APK..."
apksigner verify --verbose "$WORKDIR/bin/ecos-do-lugar.apk"

# Copy to public web server directory so it can be downloaded!
mkdir -p public/downloads
cp "$WORKDIR/bin/ecos-do-lugar.apk" "public/downloads/ecos-do-lugar.apk"
cp "$WORKDIR/bin/ecos-do-lugar.apk" "public/ecos-do-lugar.apk"

echo "=== APK CREADO EXITOSAMENTE ==="
ls -lh public/ecos-do-lugar.apk
