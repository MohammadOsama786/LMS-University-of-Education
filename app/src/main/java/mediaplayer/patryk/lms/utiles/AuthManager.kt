package mediaplayer.patryk.lms.utiles

import android.content.Context

object AuthManager {

    private const val PREF = "auth_pref"
    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD = "password"

    fun saveUser(context: Context, email: String, password: String) {
        val pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        pref.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .apply()
    }

    fun login(context: Context, email: String, password: String): Boolean {
        val pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val savedEmail = pref.getString(KEY_EMAIL, null)
        val savedPass = pref.getString(KEY_PASSWORD, null)

        return email == savedEmail && password == savedPass
    }

    fun changePassword(context: Context, newPassword: String) {
        val pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        pref.edit().putString(KEY_PASSWORD, newPassword).apply()
    }
}
