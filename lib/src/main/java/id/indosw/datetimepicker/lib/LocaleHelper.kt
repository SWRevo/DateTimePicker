package id.indosw.datetimepicker.lib

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.StringRes
import java.util.*

/**
 * Helper class, provides [Locale] specific methods.
 */
@Suppress("DEPRECATION")
object LocaleHelper {
    /**
     * Retrieves the string from resources by specific [Locale].
     *
     * @param context    The context.
     * @param locale     The requested locale.
     * @param resourceId The string resource id.
     *
     * @return The string.
     */
    @JvmStatic
    @SuppressLint("ObsoleteSdkInt")
    fun getString(context: Context, locale: Locale, @StringRes resourceId: Int): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            context.createConfigurationContext(config).getString(resourceId)
        } else {
            val resources = context.resources
            val conf = resources.configuration
            val savedLocale = conf.locale
            conf.locale = locale
            resources.updateConfiguration(conf, null)

            // retrieve resources from desired locale
            val result = resources.getString(resourceId)

            // restore original locale
            conf.locale = savedLocale
            resources.updateConfiguration(conf, null)
            result
        }
    }
}