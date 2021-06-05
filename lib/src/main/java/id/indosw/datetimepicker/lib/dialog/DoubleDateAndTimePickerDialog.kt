package id.indosw.datetimepicker.lib.dialog

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.annotation.ColorInt
import id.indosw.datetimepicker.lib.DateHelper
import id.indosw.datetimepicker.lib.R
import id.indosw.datetimepicker.lib.SingleDateAndTimePicker
import id.indosw.datetimepicker.lib.widget.DateWithLabel
import id.indosw.datetimepicker.lib.widget.SingleDateAndTimeConstants
import java.text.SimpleDateFormat
import java.util.*

@Suppress("SENSELESS_COMPARISON", "DEPRECATION", "unused")
class DoubleDateAndTimePickerDialog private constructor(context: Context, bottomSheet: Boolean = false) : BaseDialog() {
    private var listener: Listener? = null
    private val bottomSheetHelper: BottomSheetHelper
    private var buttonTab0: TextView? = null
    private var buttonTab1: TextView? = null
    private var pickerTab0: SingleDateAndTimePicker? = null
    private var pickerTab1: SingleDateAndTimePicker? = null
    private val dateHelper = DateHelper()
    private var tab0: View? = null
    private var tab1: View? = null
    private var tab0Text: String? = null
    private var tab1Text: String? = null
    private var title: String? = null
    private var titleTextSize: Int? = null
    private var bottomSheetHeight: Int? = null
    private var todayText: String? = null
    private var buttonOkText: String? = null
    private var tab0Date: Date? = null
    private var tab1Date: Date? = null
    private var secondDateAfterFirst = false
    private var tab0Days = false
    private var tab0Hours = false
    private var tab0Minutes = false
    private var tab1Days = false
    private var tab1Hours = false
    private var tab1Minutes = false
    private fun init(view: View) {
        buttonTab0 = view.findViewById<View>(R.id.buttonTab0) as TextView
        buttonTab1 = view.findViewById<View>(R.id.buttonTab1) as TextView
        pickerTab0 = view.findViewById<View>(R.id.picker_tab_0) as SingleDateAndTimePicker
        pickerTab1 = view.findViewById<View>(R.id.picker_tab_1) as SingleDateAndTimePicker
        tab0 = view.findViewById(R.id.tab0)
        tab1 = view.findViewById(R.id.tab1)
        if (pickerTab0 != null) {
            if (bottomSheetHeight != null) {
                val params = pickerTab0!!.layoutParams
                params.height = bottomSheetHeight as Int
                pickerTab0!!.layoutParams = params
            }
        }
        if (pickerTab1 != null) {
            if (bottomSheetHeight != null) {
                val params = pickerTab1!!.layoutParams
                params.height = bottomSheetHeight as Int
                pickerTab1!!.layoutParams = params
            }
        }
        val titleLayout = view.findViewById<View>(R.id.sheetTitleLayout)
        val titleTextView = view.findViewById<View>(R.id.sheetTitle) as TextView
        if (title != null) {
            if (titleTextView != null) {
                titleTextView.text = title
                if (titleTextColor != null) {
                    titleTextView.setTextColor(titleTextColor!!)
                }
                if (titleTextSize != null) {
                    titleTextView.textSize = titleTextSize!!.toFloat()
                }
            }
            if (mainColor != null && titleLayout != null) {
                titleLayout.setBackgroundColor(mainColor)
            }
        } else {
            titleLayout!!.visibility = View.GONE
        }
        pickerTab0!!.setTodayText(DateWithLabel(todayText, Date()))
        pickerTab1!!.setTodayText(DateWithLabel(todayText, Date()))
        val sheetContentLayout = view.findViewById<View>(R.id.sheetContentLayout)
        if (sheetContentLayout != null) {
            sheetContentLayout.setOnClickListener { }
            if (backgroundColor != null) {
                sheetContentLayout.setBackgroundColor(backgroundColor)
            }
        }
        tab1!!.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                tab1!!.viewTreeObserver.removeOnPreDrawListener(this)
                tab1!!.translationX = tab1!!.width.toFloat()
                return false
            }
        })
        buttonTab0!!.isSelected = true
        if (tab0Text != null) {
            buttonTab0!!.text = tab0Text
        }
        buttonTab0!!.setOnClickListener { displayTab0() }
        if (tab1Text != null) {
            buttonTab1!!.text = tab1Text
        }
        buttonTab1!!.setOnClickListener { displayTab1() }
        buttonTab0!!.setBackgroundDrawable(tabsListDrawable)
        buttonTab1!!.setBackgroundDrawable(tabsListDrawable)
        val buttonOk = view.findViewById<View>(R.id.buttonOk) as TextView
        if (buttonOk != null) {
            if (buttonOkText != null) {
                buttonOk.text = buttonOkText
            }
            if (mainColor != null) {
                buttonOk.setTextColor(mainColor)
            }
            if (titleTextSize != null) {
                buttonOk.textSize = titleTextSize!!.toFloat()
            }
        }
        buttonOk.setOnClickListener {
            if (isTab0Visible) {
                displayTab1()
            } else {
                okClicked = true
                close()
            }
        }
        if (curved) {
            pickerTab0!!.setCurved(true)
            pickerTab1!!.setCurved(true)
            pickerTab0!!.setVisibleItemCount(DEFAULT_ITEM_COUNT_MODE_CURVED)
            pickerTab1!!.setVisibleItemCount(DEFAULT_ITEM_COUNT_MODE_CURVED)
        } else {
            pickerTab0!!.setCurved(false)
            pickerTab1!!.setCurved(false)
            pickerTab0!!.setVisibleItemCount(DEFAULT_ITEM_COUNT_MODE_NORMAL)
            pickerTab1!!.setVisibleItemCount(DEFAULT_ITEM_COUNT_MODE_NORMAL)
        }
        pickerTab0!!.setDisplayDays(tab0Days)
        pickerTab0!!.setDisplayHours(tab0Hours)
        pickerTab0!!.setDisplayMinutes(tab0Minutes)
        pickerTab1!!.setDisplayDays(tab1Days)
        pickerTab1!!.setDisplayHours(tab1Hours)
        pickerTab1!!.setDisplayMinutes(tab1Minutes)
        pickerTab0!!.setMustBeOnFuture(mustBeOnFuture)
        pickerTab1!!.setMustBeOnFuture(mustBeOnFuture)
        pickerTab0!!.setStepSizeMinutes(minutesStep)
        pickerTab1!!.setStepSizeMinutes(minutesStep)
        if (mainColor != null) {
            pickerTab0!!.setSelectedTextColor(mainColor)
            pickerTab1!!.setSelectedTextColor(mainColor)
        }
        if (minDate != null) {
            pickerTab0!!.minDate = minDate
            pickerTab1!!.minDate = minDate
        }
        if (maxDate != null) {
            pickerTab0!!.maxDate = maxDate
            pickerTab1!!.maxDate = maxDate
        }
        if (defaultDate != null) {
            val calendar = Calendar.getInstance()
            calendar.time = defaultDate!!
            pickerTab0!!.selectDate(calendar)
            pickerTab1!!.selectDate(calendar)
        }
        if (tab0Date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = tab0Date!!
            pickerTab0!!.selectDate(calendar)
        }
        if (tab1Date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = tab1Date!!
            pickerTab1!!.selectDate(calendar)
        }
        if (dayFormatter != null) {
            pickerTab0!!.setDayFormatter(dayFormatter)
            pickerTab1!!.setDayFormatter(dayFormatter)
        }
        if (customLocale != null) {
            pickerTab0!!.setCustomLocale(customLocale)
            pickerTab1!!.setCustomLocale(customLocale)
        }
        if (secondDateAfterFirst) {
            pickerTab0!!.addOnDateChangedListener { _: String?, date: Date? ->
                pickerTab1!!.minDate = date
                pickerTab1!!.checkPickersMinMax()
            }
        }
    }

    private val tabsListDrawable: StateListDrawable
        get() {
            val colorState0 = StateListDrawable()
            colorState0.addState(intArrayOf(android.R.attr.state_selected), ColorDrawable(mainColor))
            colorState0.addState(intArrayOf(-android.R.attr.state_selected), ColorDrawable(backgroundColor))
            return colorState0
        }

    fun setTab0Text(tab0Text: String?): DoubleDateAndTimePickerDialog {
        this.tab0Text = tab0Text
        return this
    }

    fun setTab1Text(tab1Text: String?): DoubleDateAndTimePickerDialog {
        this.tab1Text = tab1Text
        return this
    }

    fun setButtonOkText(buttonOkText: String?): DoubleDateAndTimePickerDialog {
        this.buttonOkText = buttonOkText
        return this
    }

    fun setTitle(title: String?): DoubleDateAndTimePickerDialog {
        this.title = title
        return this
    }

    fun setTitleTextSize(titleTextSize: Int?): DoubleDateAndTimePickerDialog {
        this.titleTextSize = titleTextSize
        return this
    }

    fun setBottomSheetHeight(bottomSheetHeight: Int?): DoubleDateAndTimePickerDialog {
        this.bottomSheetHeight = bottomSheetHeight
        return this
    }

    fun setTodayText(todayText: String?): DoubleDateAndTimePickerDialog {
        this.todayText = todayText
        return this
    }

    fun setListener(listener: Listener?): DoubleDateAndTimePickerDialog {
        this.listener = listener
        return this
    }

    fun setCurved(curved: Boolean): DoubleDateAndTimePickerDialog {
        this.curved = curved
        return this
    }

    fun setMinutesStep(minutesStep: Int): DoubleDateAndTimePickerDialog {
        this.minutesStep = minutesStep
        return this
    }

    fun setMustBeOnFuture(mustBeOnFuture: Boolean): DoubleDateAndTimePickerDialog {
        this.mustBeOnFuture = mustBeOnFuture
        return this
    }

    fun setMinDateRange(minDate: Date?): DoubleDateAndTimePickerDialog {
        this.minDate = minDate
        return this
    }

    fun setMaxDateRange(maxDate: Date?): DoubleDateAndTimePickerDialog {
        this.maxDate = maxDate
        return this
    }

    fun setDefaultDate(defaultDate: Date?): DoubleDateAndTimePickerDialog {
        this.defaultDate = defaultDate
        return this
    }

    fun setDayFormatter(dayFormatter: SimpleDateFormat?): DoubleDateAndTimePickerDialog {
        this.dayFormatter = dayFormatter
        return this
    }

    fun setCustomLocale(locale: Locale?): DoubleDateAndTimePickerDialog {
        customLocale = locale
        return this
    }

    fun setTab0Date(tab0Date: Date?): DoubleDateAndTimePickerDialog {
        this.tab0Date = tab0Date
        return this
    }

    fun setTab1Date(tab1Date: Date?): DoubleDateAndTimePickerDialog {
        this.tab1Date = tab1Date
        return this
    }

    fun setSecondDateAfterFirst(secondDateAfterFirst: Boolean): DoubleDateAndTimePickerDialog {
        this.secondDateAfterFirst = secondDateAfterFirst
        return this
    }

    fun setTab0DisplayDays(tab0Days: Boolean): DoubleDateAndTimePickerDialog {
        this.tab0Days = tab0Days
        return this
    }

    fun setTab0DisplayHours(tab0Hours: Boolean): DoubleDateAndTimePickerDialog {
        this.tab0Hours = tab0Hours
        return this
    }

    fun setTab0DisplayMinutes(tab0Minutes: Boolean): DoubleDateAndTimePickerDialog {
        this.tab0Minutes = tab0Minutes
        return this
    }

    fun setTab1DisplayDays(tab1Days: Boolean): DoubleDateAndTimePickerDialog {
        this.tab1Days = tab1Days
        return this
    }

    fun setTab1DisplayHours(tab1Hours: Boolean): DoubleDateAndTimePickerDialog {
        this.tab1Hours = tab1Hours
        return this
    }

    fun setTab1DisplayMinutes(tab1Minutes: Boolean): DoubleDateAndTimePickerDialog {
        this.tab1Minutes = tab1Minutes
        return this
    }

    fun setFocusable(focusable: Boolean): DoubleDateAndTimePickerDialog {
        bottomSheetHelper.setFocusable(focusable)
        return this
    }

    private fun setTimeZone(timeZone: TimeZone?): DoubleDateAndTimePickerDialog {
        dateHelper.setTimeZone(timeZone)
        //        pickerTab0.setTimeZone(timeZone);
//        pickerTab1.setTimeZone(timeZone);
        return this
    }

    override fun display() {
        super.display()
        bottomSheetHelper.display()
    }

    override fun dismiss() {
        super.dismiss()
        bottomSheetHelper.dismiss()
    }

    override fun close() {
        super.close()
        bottomSheetHelper.hide()
    }

    override fun onClose() {
        super.onClose()
        if (listener != null && okClicked) {
            listener!!.onDateSelected(listOf(pickerTab0!!.date, pickerTab1!!.date))
        }
    }

    private fun displayTab0() {
        if (!isTab0Visible) {
            buttonTab0!!.isSelected = true
            buttonTab1!!.isSelected = false
            tab0!!.animate().translationX(0f)
            tab1!!.animate().translationX(tab1!!.width.toFloat())
        }
    }

    private fun displayTab1() {
        if (isTab0Visible) {
            buttonTab0!!.isSelected = false
            buttonTab1!!.isSelected = true
            tab0!!.animate().translationX(-tab0!!.width.toFloat())
            tab1!!.animate().translationX(0f)
        }
    }

    private val isTab0Visible: Boolean
        get() = tab0!!.translationX == 0f

    interface Listener {
        fun onDateSelected(dates: List<Date?>?)
    }

    class Builder(private val context: Context) {
        private var listener: Listener? = null
        private var bottomSheet = false
        private var dialog: DoubleDateAndTimePickerDialog? = null
        private var tab0Text: String? = null
        private var tab1Text: String? = null
        private var title: String? = null
        private var titleTextSize: Int? = null
        private var bottomSheetHeight: Int? = null
        private var buttonOkText: String? = null
        private var todayText: String? = null
        private var curved = false
        private var secondDateAfterFirst = false
        private var mustBeOnFuture = false
        private var minutesStep = SingleDateAndTimeConstants.STEP_MINUTES_DEFAULT
        private var dayFormatter: SimpleDateFormat? = null
        private var customLocale: Locale? = null

        @ColorInt
        private var backgroundColor: Int? = null

        @ColorInt
        private var mainColor: Int? = null

        @ColorInt
        private var titleTextColor: Int? = null
        private var minDate: Date? = null
        private var maxDate: Date? = null
        private var defaultDate: Date? = null
        private var tab0Date: Date? = null
        private var tab1Date: Date? = null
        private var tab0Days = true
        private var tab0Hours = true
        private var tab0Minutes = true
        private var tab1Days = true
        private var tab1Hours = true
        private var tab1Minutes = true
        private var focusable = false
        private var timeZone: TimeZone? = null
        fun title(title: String?): Builder {
            this.title = title
            return this
        }

        fun titleTextSize(titleTextSize: Int?): Builder {
            this.titleTextSize = titleTextSize
            return this
        }

        fun bottomSheetHeight(bottomSheetHeight: Int?): Builder {
            this.bottomSheetHeight = bottomSheetHeight
            return this
        }

        fun todayText(todayText: String?): Builder {
            this.todayText = todayText
            return this
        }

        fun bottomSheet(): Builder {
            bottomSheet = true
            return this
        }

        fun curved(): Builder {
            curved = true
            return this
        }

        fun mustBeOnFuture(): Builder {
            mustBeOnFuture = true
            return this
        }

        fun dayFormatter(dayFormatter: SimpleDateFormat?): Builder {
            this.dayFormatter = dayFormatter
            return this
        }

        fun customLocale(locale: Locale?): Builder {
            customLocale = locale
            return this
        }

        fun minutesStep(minutesStep: Int): Builder {
            this.minutesStep = minutesStep
            return this
        }

        fun titleTextColor(@ColorInt titleTextColor: Int): Builder {
            this.titleTextColor = titleTextColor
            return this
        }

        fun backgroundColor(@ColorInt backgroundColor: Int): Builder {
            this.backgroundColor = backgroundColor
            return this
        }

        fun mainColor(@ColorInt mainColor: Int): Builder {
            this.mainColor = mainColor
            return this
        }

        fun minDateRange(minDate: Date?): Builder {
            this.minDate = minDate
            return this
        }

        fun maxDateRange(maxDate: Date?): Builder {
            this.maxDate = maxDate
            return this
        }

        fun defaultDate(defaultDate: Date?): Builder {
            this.defaultDate = defaultDate
            return this
        }

        fun tab0Date(tab0Date: Date?): Builder {
            this.tab0Date = tab0Date
            return this
        }

        fun tab1Date(tab1Date: Date?): Builder {
            this.tab1Date = tab1Date
            return this
        }

        fun listener(
                listener: Listener?): Builder {
            this.listener = listener
            return this
        }

        fun tab1Text(tab1Text: String?): Builder {
            this.tab1Text = tab1Text
            return this
        }

        fun tab0Text(tab0Text: String?): Builder {
            this.tab0Text = tab0Text
            return this
        }

        fun buttonOkText(buttonOkText: String?): Builder {
            this.buttonOkText = buttonOkText
            return this
        }

        fun secondDateAfterFirst(secondDateAfterFirst: Boolean): Builder {
            this.secondDateAfterFirst = secondDateAfterFirst
            return this
        }

        fun setTab0DisplayDays(tab0Days: Boolean): Builder {
            this.tab0Days = tab0Days
            return this
        }

        fun setTab0DisplayHours(tab0Hours: Boolean): Builder {
            this.tab0Hours = tab0Hours
            return this
        }

        fun setTab0DisplayMinutes(tab0Minutes: Boolean): Builder {
            this.tab0Minutes = tab0Minutes
            return this
        }

        fun setTab1DisplayDays(tab1Days: Boolean): Builder {
            this.tab1Days = tab1Days
            return this
        }

        fun setTab1DisplayHours(tab1Hours: Boolean): Builder {
            this.tab1Hours = tab1Hours
            return this
        }

        fun setTab1DisplayMinutes(tab1Minutes: Boolean): Builder {
            this.tab1Minutes = tab1Minutes
            return this
        }

        fun setTimeZone(timeZone: TimeZone?): Builder {
            this.timeZone = timeZone
            return this
        }

        fun focusable(): Builder {
            focusable = true
            return this
        }

        private fun build(): DoubleDateAndTimePickerDialog {
            val dialog = DoubleDateAndTimePickerDialog(context, bottomSheet)
                    .setTitle(title)
                    .setTitleTextSize(titleTextSize)
                    .setBottomSheetHeight(bottomSheetHeight)
                    .setTodayText(todayText)
                    .setListener(listener)
                    .setCurved(curved)
                    .setButtonOkText(buttonOkText)
                    .setTab0Text(tab0Text)
                    .setTab1Text(tab1Text)
                    .setMinutesStep(minutesStep)
                    .setMaxDateRange(maxDate)
                    .setMinDateRange(minDate)
                    .setDefaultDate(defaultDate)
                    .setTab0DisplayDays(tab0Days)
                    .setTab0DisplayHours(tab0Hours)
                    .setTab0DisplayMinutes(tab0Minutes)
                    .setTab1DisplayDays(tab1Days)
                    .setTab1DisplayHours(tab1Hours)
                    .setTab1DisplayMinutes(tab1Minutes)
                    .setTab0Date(tab0Date)
                    .setTab1Date(tab1Date)
                    .setDayFormatter(dayFormatter)
                    .setCustomLocale(customLocale)
                    .setMustBeOnFuture(mustBeOnFuture)
                    .setSecondDateAfterFirst(secondDateAfterFirst)
                    .setTimeZone(timeZone)
                    .setFocusable(focusable)
            if (mainColor != null) {
                dialog.setMainColor(mainColor)
            }
            if (backgroundColor != null) {
                dialog.setBackgroundColor(backgroundColor)
            }
            if (titleTextColor != null) {
                dialog.setTitleTextColor(titleTextColor!!)
            }
            return dialog
        }

        fun display() {
            dialog = build()
            dialog!!.display()
        }

        fun close() {
            if (dialog != null) {
                dialog!!.close()
            }
        }

        fun dismiss() {
            if (dialog != null) {
                dialog!!.dismiss()
            }
        }
    }

    init {
        val layout = if (bottomSheet) R.layout.bottom_sheet_double_picker_bottom_sheet else R.layout.bottom_sheet_double_picker
        bottomSheetHelper = BottomSheetHelper(context, layout)
        bottomSheetHelper.setListener(object : BottomSheetHelper.Listener {
            override fun onOpen() {}
            override fun onLoaded(view: View?) {
                init(view!!)
            }

            override fun onClose() {
                this@DoubleDateAndTimePickerDialog.onClose()
            }
        })
    }
}