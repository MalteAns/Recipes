package de.malteans.recipes.core.data

import android.util.Log

actual object AnsLog {
    actual fun d(tag: String?, message: String, throwable: Throwable?) {
        Log.d(tag, message, throwable)
    }

    actual fun i(tag: String?, message: String, throwable: Throwable?) {
        Log.i(tag, message, throwable)
    }

    actual fun w(tag: String?, message: String, throwable: Throwable?) {
        Log.w(tag, message, throwable)
    }

    actual fun e(tag: String?, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }

    actual fun wtf(tag: String?, message: String, throwable: Throwable?) {
        Log.wtf(tag, message, throwable)
    }
}