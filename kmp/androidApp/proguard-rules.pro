# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class app.mihon.**$$serializer { *; }
-keepclassmembers class app.mihon.** { *** Companion; }
-keepclasseswithmembers class app.mihon.** { kotlinx.serialization.KSerializer serializer(...); }

# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }

# SQLDelight
-keep class app.mihon.shared.data.db.** { *; }

# Koin
-keep class org.koin.** { *; }

# Coil
-keep class coil3.** { *; }

# Voyager
-keep class cafe.adriel.voyager.** { *; }
