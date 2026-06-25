package de.malteans.recipes.core.data

import platform.Foundation.NSLog

actual object AnsLog {
    actual fun d(tag: String?, message: String, throwable: Throwable?) {
        logPrint("D - $tag", message, throwable)
    }
    actual fun i(tag: String?, message: String, throwable: Throwable?) {
        logPrint("I - $tag", message, throwable)
    }
    actual fun w(tag: String?, message: String, throwable: Throwable?) {
        logPrint("W - $tag", message, throwable)
    }
    actual fun e(tag: String?, message: String, throwable: Throwable?) {
        logPrint("E - $tag", message, throwable)
    }
    actual fun wtf(tag: String?, message: String, throwable: Throwable?) {
        logPrint("WTF - $tag", message, throwable)
    }
    private fun logPrint(info: String, message: String, throwable: Throwable?) {
        NSLog("[$info] $message" + (throwable?.stackTraceToString() ?: ""))
    }
}