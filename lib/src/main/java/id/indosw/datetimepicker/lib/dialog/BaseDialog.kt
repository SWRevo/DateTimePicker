package id.indosw.datetimepicker.lib.dialog

import android.graphics.Color
import androidx.annotation.ColorInt
import id.indosw.datetimepicker.lib.widget.SingleDateAndTimeConstants
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseDialog {
    private var isDisplaying = false

    @ColorInt
    protected var backgroundColor: Int = Color.WHITE

    @ColorInt
    protected var mainColor: Int = Color.BLUE

    @ColorInt
    protected var titleTextColor: Int? = null
    protected var okClicked = false
    protected var curved = false
    protected var mustBeOnFuture = false
    protected var minutesStep = SingleDateAndTimeConstants.STEP_MINUTES_DEFAULT
    protected var minDate: Date? = null
    protected var maxDate: Date? = null
    protected var defaultDate: Date? = null
    protected var displayDays = false
    protected var displayMinutes = false
    protected var displayHours = false
    protected var displayDaysOfMonth = false
    protected var displayMonth = false
    protected var displayYears = false
    protected var displayMonthNumbers = false
    protected var isAmPm: Boolean? = null
    protected var dayFormatter: SimpleDateFormat? = null
    protected var customLocale: Locale? = null
    open fun display() {
        isDisplaying = true
    }

    open fun close() {
        isDisplaying = false
    }

    open fun dismiss() {
        isDisplaying = false
    }

    fun setBackgroundColor(@ColorInt backgroundColor: Int?) {
        this.backgroundColor = backgroundColor!!
    }

    fun setMainColor(@ColorInt mainColor: Int?) {
        this.mainColor = mainColor!!
    }

    fun setTitleTextColor(@ColorInt titleTextColor: Int) {
        this.titleTextColor = titleTextColor
    }

    protected open fun onClose() {
        isDisplaying = false
    }

    companion object {
        const val DEFAULT_ITEM_COUNT_MODE_CURVED = 7
        const val DEFAULT_ITEM_COUNT_MODE_NORMAL = 5
    }
}