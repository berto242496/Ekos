# Supabase JSON is parsed manually with org.json (no reflection), so no model keep rules are needed.

# The map WebView calls these methods by name from JavaScript.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep stack traces readable in Play Console crash reports.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Desktop/iOS-only references pulled in by common code.
-dontwarn java.awt.**
-dontwarn javax.swing.**
