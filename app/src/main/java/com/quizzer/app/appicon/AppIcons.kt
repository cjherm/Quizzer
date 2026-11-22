package com.quizzer.app.appicon

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.quizzer.app.R

/** The selectable launcher icons. Each one is an activity-alias in AndroidManifest.xml. */
enum class AppIcon(
    val alias: String,
    val label: String,
    val background: Int,
    val foreground: Int,
) {
    SunsetQ(".LauncherDefault", "Sunset Q", R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground),
    Answers(".Launcher2B", "Answers", R.drawable.ic_launcher_2b_background, R.drawable.ic_launcher_2b_foreground),
    NeonQ(".Launcher2C", "Neon Q", R.drawable.ic_launcher_2c_background, R.drawable.ic_launcher_2c_foreground),
    Badge(".Launcher2H", "Badge", R.drawable.ic_launcher_2h_background, R.drawable.ic_launcher_2h_foreground),
    ;

    companion object {
        val DEFAULT = SunsetQ
    }
}

/**
 * Switches the launcher icon by enabling exactly one activity-alias.
 *
 * Android closes the app's screen when the launcher entry it was started from is disabled,
 * so a new choice is only remembered by [choose] and applied by [applyChosen] when the app
 * goes to the background. The launcher may take a moment to update, and on some launchers
 * a home screen shortcut to the previous icon has to be re-added.
 */
object AppIcons {

    private const val PREFS = "quizzer"
    private const val KEY_CHOSEN = "app_icon"

    /** The icon the user chose (possibly not applied yet). */
    fun chosen(context: Context): AppIcon =
        prefs(context).getString(KEY_CHOSEN, null)
            ?.let { name -> AppIcon.entries.firstOrNull { it.name == name } }
            ?: enabled(context)

    fun choose(context: Context, icon: AppIcon) {
        prefs(context).edit().putString(KEY_CHOSEN, icon.name).apply()
    }

    /** Enables the chosen icon's launcher entry if it isn't already. Call when the app is in the background. */
    fun applyChosen(context: Context) {
        val icon = chosen(context)
        if (icon == enabled(context)) return
        val pm = context.packageManager
        // Enable the new entry first, so there's never a moment without a launcher entry.
        pm.setComponentEnabledSetting(
            component(context, icon),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP,
        )
        AppIcon.entries.filter { it != icon }.forEach { other ->
            pm.setComponentEnabledSetting(
                component(context, other),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP,
            )
        }
    }

    /** The icon whose launcher entry is currently enabled. */
    private fun enabled(context: Context): AppIcon {
        val pm = context.packageManager
        return AppIcon.entries.firstOrNull { icon ->
            when (pm.getComponentEnabledSetting(component(context, icon))) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> icon == AppIcon.DEFAULT // as in the manifest
                else -> false
            }
        } ?: AppIcon.DEFAULT
    }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun component(context: Context, icon: AppIcon) =
        ComponentName(context.packageName, context.packageName + icon.alias)
}
