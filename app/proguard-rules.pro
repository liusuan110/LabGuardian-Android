-dontwarn javax.annotation.**
-dontwarn kotlin.reflect.jvm.internal.**

# Moshi
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.internal.platform.*
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Hilt
-keep class dagger.hilt.** { *; }

# Model classes
-keep class com.labguardian.core.model.** { *; }
-keep class com.labguardian.core.network.generated.** { *; }
