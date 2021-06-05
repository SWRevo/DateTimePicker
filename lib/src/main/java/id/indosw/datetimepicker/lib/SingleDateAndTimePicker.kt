package id.indosw.datetimepicker.lib

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import id.indosw.datetimepicker.lib.widget.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("UNUSED_ANONYMOUS_PARAMETER", "unused")
class SingleDateAndTimePicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    private var dateHelper = DateHelper()
    private val yearsPicker: WheelYearPicker
    private val monthPicker: WheelMonthPicker
    private val daysOfMonthPicker: WheelDayOfMonthPicker
    private val daysPicker: WheelDayPicker
    private val minutesPicker: WheelMinutePicker
    private val hoursPicker: WheelHourPicker
    private val amPmPicker: WheelAmPmPicker
    private val pickers: MutableList<WheelPicker<*>> = ArrayList()
    private val listeners: MutableList<OnDateChangedListener> = ArrayList()
    private val dtSelector: View
    private var mustBeOnFuture = false
    private var minDate: Date? = null
    private var maxDate: Date? = null
    private var defaultDate: Date
    private var displayYears = false
    private var displayMonth = false
    private var displayDaysOfMonth = false
    private var displayDays = true
    private var displayMinutes = true
    private var displayHours = true
    private var isAmPm: Boolean
    fun setDateHelper(dateHelper: DateHelper) {
        this.dateHelper = dateHelper
    }

    fun setTimeZone(timeZone: TimeZone?) {
        if (timeZone != null) {
            dateHelper.setTimeZone(timeZone)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        yearsPicker.setOnYearSelectedListener { picker: WheelYearPicker, position: Int, year: Int ->
            updateListener()
            checkMinMaxDate(picker)
            if (displayDaysOfMonth) {
                updateDaysOfMonth()
            }
        }
        monthPicker.setOnMonthSelectedListener { picker: WheelMonthPicker, monthIndex: Int, monthName: String? ->
            updateListener()
            checkMinMaxDate(picker)
            if (displayDaysOfMonth) {
                updateDaysOfMonth()
            }
        }
        daysOfMonthPicker
                .setDayOfMonthSelectedListener { picker: WheelDayOfMonthPicker, dayIndex: Int ->
                    updateListener()
                    checkMinMaxDate(picker)
                }
        daysOfMonthPicker
                .setOnFinishedLoopListener { picker: WheelDayOfMonthPicker? ->
                    if (displayMonth) {
                        monthPicker.scrollTo(monthPicker.currentItemPosition + 1)
                        updateDaysOfMonth()
                    }
                }
        daysPicker
                .setOnDaySelectedListener { picker: WheelDayPicker, position: Int, name: String?, date: Date? ->
                    updateListener()
                    checkMinMaxDate(picker)
                }
        minutesPicker
                .setOnMinuteChangedListener { picker: WheelMinutePicker, minutes: Int ->
                    updateListener()
                    checkMinMaxDate(picker)
                }
                .setOnFinishedLoopListener { picker: WheelMinutePicker? -> hoursPicker.scrollTo(hoursPicker.currentItemPosition + 1) }
        hoursPicker
                .setOnFinishedLoopListener { picker: WheelHourPicker? -> daysPicker.scrollTo(daysPicker.currentItemPosition + 1) }
                .setHourChangedListener { picker: WheelHourPicker, hour: Int ->
                    updateListener()
                    checkMinMaxDate(picker)
                }
        amPmPicker
                .setAmPmListener { picker: WheelAmPmPicker, isAm: Boolean ->
                    updateListener()
                    checkMinMaxDate(picker)
                }
        setDefaultDate(defaultDate) //update displayed date
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        for (picker in pickers) {
            picker.isEnabled = enabled
        }
    }

    fun setDisplayYears(displayYears: Boolean) {
        this.displayYears = displayYears
        yearsPicker.visibility = if (displayYears) VISIBLE else GONE
    }

    fun setDisplayMonths(displayMonths: Boolean) {
        displayMonth = displayMonths
        monthPicker.visibility = if (displayMonths) VISIBLE else GONE
        checkSettings()
    }

    fun setDisplayDaysOfMonth(displayDaysOfMonth: Boolean) {
        this.displayDaysOfMonth = displayDaysOfMonth
        daysOfMonthPicker.visibility = if (displayDaysOfMonth) VISIBLE else GONE
        if (displayDaysOfMonth) {
            updateDaysOfMonth()
        }
        checkSettings()
    }

    fun setDisplayDays(displayDays: Boolean) {
        this.displayDays = displayDays
        daysPicker.visibility = if (displayDays) VISIBLE else GONE
        checkSettings()
    }

    fun setDisplayMinutes(displayMinutes: Boolean) {
        this.displayMinutes = displayMinutes
        minutesPicker.visibility = if (displayMinutes) VISIBLE else GONE
    }

    fun setDisplayHours(displayHours: Boolean) {
        this.displayHours = displayHours
        hoursPicker.visibility = if (displayHours) VISIBLE else GONE
        setIsAmPm(isAmPm)
        hoursPicker.setIsAmPm(isAmPm)
    }

    private fun setDisplayMonthNumbers(displayMonthNumbers: Boolean) {
        monthPicker.setDisplayMonthNumbers(displayMonthNumbers)
        monthPicker.updateAdapter()
    }

    private fun setMonthFormat(monthFormat: String?) {
        monthPicker.monthFormat = monthFormat
        monthPicker.updateAdapter()
    }

    fun setTodayText(todayText: DateWithLabel?) {
        if (todayText?.label != null && todayText.label.isNotEmpty()) {
            daysPicker.setTodayText(todayText)
        }
    }

    private fun setItemSpacing(size: Int) {
        for (picker in pickers) {
            picker.itemSpace = size
        }
    }

    private fun setCurvedMaxAngle(angle: Int) {
        for (picker in pickers) {
            picker.setCurvedMaxAngle(angle)
        }
    }

    fun setCurved(curved: Boolean) {
        for (picker in pickers) {
            picker.isCurved = curved
        }
    }

    private fun setCyclic(cyclic: Boolean) {
        for (picker in pickers) {
            picker.isCyclic = cyclic
        }
    }

    private fun setTextSize(textSize: Int) {
        for (picker in pickers) {
            picker.itemTextSize = textSize
        }
    }

    fun setSelectedTextColor(selectedTextColor: Int) {
        for (picker in pickers) {
            picker.selectedItemTextColor = selectedTextColor
        }
    }

    private fun setTextColor(textColor: Int) {
        for (picker in pickers) {
            picker.itemTextColor = textColor
        }
    }

    private fun setTextAlign(align: Int) {
        for (picker in pickers) {
            picker.itemAlign = align
        }
    }

    fun setTypeface(typeface: Typeface?) {
        if (typeface == null) return
        for (picker in pickers) {
            picker.typeface = typeface
        }
    }

    private fun setFontToAllPickers(resourceId: Int) {
        if (resourceId > 0) {
            for (i in pickers.indices) {
                pickers[i].typeface = ResourcesCompat.getFont(context, resourceId)
            }
        }
    }

    private fun setSelectorColor(selectorColor: Int) {
        dtSelector.setBackgroundColor(selectorColor)
    }

    private fun setSelectorHeight(selectorHeight: Int) {
        val dtSelectorLayoutParams = dtSelector.layoutParams
        dtSelectorLayoutParams.height = selectorHeight
        dtSelector.layoutParams = dtSelectorLayoutParams
    }

    fun setVisibleItemCount(visibleItemCount: Int) {
        for (picker in pickers) {
            picker.visibleItemCount = visibleItemCount
        }
    }

    fun setIsAmPm(isAmPm: Boolean) {
        this.isAmPm = isAmPm
        amPmPicker.visibility = if (isAmPm && displayHours) VISIBLE else GONE
        hoursPicker.setIsAmPm(isAmPm)
    }

    fun isAmPm(): Boolean {
        return isAmPm
    }

    fun setDayFormatter(simpleDateFormat: SimpleDateFormat?) {
        if (simpleDateFormat != null) {
            daysPicker.setDayFormatter(simpleDateFormat)
        }
    }

    fun getMinDate(): Date? {
        return minDate
    }

    fun setMinDate(minDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.timeZone = dateHelper.timeZone
        calendar.time = minDate
        this.minDate = calendar.time
        setMinYear()
    }

    fun getMaxDate(): Date? {
        return maxDate
    }

    fun setMaxDate(maxDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.timeZone = dateHelper.timeZone
        calendar.time = maxDate
        this.maxDate = calendar.time
        setMinYear()
    }

    fun setCustomLocale(locale: Locale?) {
        for (p in pickers) {
            p.setCustomLocale(locale)
            p.updateAdapter()
        }
    }

    private fun checkMinMaxDate(picker: WheelPicker<*>) {
        checkBeforeMinDate(picker)
        checkAfterMaxDate(picker)
    }

    private fun checkBeforeMinDate(picker: WheelPicker<*>) {
        picker.postDelayed({
            if (isBeforeMinDate(date)) {
                for (p in pickers) {
                    p.scrollTo(p.findIndexOfDate(minDate!!))
                }
            }
        }, DELAY_BEFORE_CHECK_PAST.toLong())
    }

    private fun checkAfterMaxDate(picker: WheelPicker<*>) {
        picker.postDelayed({
            if (isAfterMaxDate(date)) {
                for (p in pickers) {
                    p.scrollTo(p.findIndexOfDate(maxDate!!))
                }
            }
        }, DELAY_BEFORE_CHECK_PAST.toLong())
    }

    private fun isBeforeMinDate(date: Date): Boolean {
        return dateHelper.getCalendarOfDate(date).before(dateHelper.getCalendarOfDate(minDate!!))
    }

    private fun isAfterMaxDate(date: Date): Boolean {
        return dateHelper.getCalendarOfDate(date).after(dateHelper.getCalendarOfDate(maxDate!!))
    }

    fun addOnDateChangedListener(listener: OnDateChangedListener) {
        listeners.add(listener)
    }

    fun removeOnDateChangedListener(listener: OnDateChangedListener) {
        listeners.remove(listener)
    }

    fun checkPickersMinMax() {
        for (picker in pickers) {
            checkMinMaxDate(picker)
        }
    }

    val date: Date
        get() {
            var hour = hoursPicker.currentHour
            if (isAmPm && amPmPicker.isPm) {
                hour += PM_HOUR_ADDITION
            }
            val minute = minutesPicker.currentMinute
            val calendar = Calendar.getInstance()
            calendar.timeZone = dateHelper.timeZone
            if (displayDays) {
                val dayDate = daysPicker.currentDate
                calendar.time = dayDate
            } else {
                if (displayMonth) {
                    calendar[Calendar.MONTH] = monthPicker.currentMonth
                }
                if (displayYears) {
                    calendar[Calendar.YEAR] = yearsPicker.currentYear
                }
                if (displayDaysOfMonth) {
                    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                    if (daysOfMonthPicker.currentDay >= daysInMonth) {
                        calendar[Calendar.DAY_OF_MONTH] = daysInMonth
                    } else {
                        calendar[Calendar.DAY_OF_MONTH] = daysOfMonthPicker.currentDay + 1
                    }
                }
            }
            calendar[Calendar.HOUR_OF_DAY] = hour
            calendar[Calendar.MINUTE] = minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
            return calendar.time
        }

    fun setStepSizeMinutes(minutesStep: Int) {
        minutesPicker.setStepSizeMinutes(minutesStep)
    }

    private fun setStepSizeHours(hoursStep: Int) {
        hoursPicker.setStepSizeHours(hoursStep)
    }

    fun setDefaultDate(date: Date?) {
        if (date != null) {
            val calendar = Calendar.getInstance()
            calendar.timeZone = dateHelper.timeZone
            calendar.time = date
            defaultDate = calendar.time
            updateDaysOfMonth(calendar)
            for (picker in pickers) {
                picker.setDefaultDate(defaultDate)
            }
        }
    }

    fun selectDate(calendar: Calendar?) {
        if (calendar == null) {
            return
        }
        val date = calendar.time
        for (picker in pickers) {
            picker.selectDate(date)
        }
        if (displayDaysOfMonth) {
            updateDaysOfMonth()
        }
    }

    private fun updateListener() {
        val date = date
        val format = if (isAmPm) FORMAT_12_HOUR else FORMAT_24_HOUR
        val displayed = DateFormat.format(format, date).toString()
        for (listener in listeners) {
            listener.onDateChanged(displayed, date)
        }
    }

    private fun updateDaysOfMonth() {
        val date = date
        val calendar = Calendar.getInstance()
        calendar.timeZone = dateHelper.timeZone
        calendar.time = date
        updateDaysOfMonth(calendar)
    }

    private fun updateDaysOfMonth(calendar: Calendar) {
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        daysOfMonthPicker.daysInMonth = daysInMonth
        daysOfMonthPicker.updateAdapter()
    }

    fun setMustBeOnFuture(mustBeOnFuture: Boolean) {
        this.mustBeOnFuture = mustBeOnFuture
        daysPicker.showOnlyFutureDate = mustBeOnFuture
        if (mustBeOnFuture) {
            val now = Calendar.getInstance()
            now.timeZone = dateHelper.timeZone
            minDate = now.time //minDate is Today
        }
    }

    fun mustBeOnFuture(): Boolean {
        return mustBeOnFuture
    }

    private fun setMinYear() {
        if (displayYears) {
            val calendar = Calendar.getInstance()
            calendar.timeZone = dateHelper.timeZone
            calendar.time = minDate!!
            yearsPicker.setMinYear(calendar[Calendar.YEAR])
            calendar.time = maxDate!!
            yearsPicker.setMaxYear(calendar[Calendar.YEAR])
        }
    }

    private fun checkSettings() {
        require(!(displayDays && (displayDaysOfMonth || displayMonth))) { "You can either display days with months or days and months separately" }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SingleDateAndTimePicker)
        val resources = resources
        setTodayText(DateWithLabel(a.getString(R.styleable.SingleDateAndTimePicker_picker_todayText), Date()))
        setTextColor(a.getColor(R.styleable.SingleDateAndTimePicker_picker_textColor, ContextCompat.getColor(context, R.color.picker_default_text_color)))
        setSelectedTextColor(a.getColor(R.styleable.SingleDateAndTimePicker_picker_selectedTextColor, ContextCompat.getColor(context, R.color.picker_default_selected_text_color)))
        setSelectorColor(a.getColor(R.styleable.SingleDateAndTimePicker_picker_selectorColor, ContextCompat.getColor(context, R.color.picker_default_selector_color)))
        setItemSpacing(a.getDimensionPixelSize(R.styleable.SingleDateAndTimePicker_picker_itemSpacing, resources.getDimensionPixelSize(R.dimen.wheelSelectorHeight)))
        setCurvedMaxAngle(a.getInteger(R.styleable.SingleDateAndTimePicker_picker_curvedMaxAngle, WheelPicker.MAX_ANGLE))
        setSelectorHeight(a.getDimensionPixelSize(R.styleable.SingleDateAndTimePicker_picker_selectorHeight, resources.getDimensionPixelSize(R.dimen.wheelSelectorHeight)))
        setTextSize(a.getDimensionPixelSize(R.styleable.SingleDateAndTimePicker_picker_textSize, resources.getDimensionPixelSize(R.dimen.WheelItemTextSize)))
        setCurved(a.getBoolean(R.styleable.SingleDateAndTimePicker_picker_curved, IS_CURVED_DEFAULT))
        setCyclic(a.getBoolean(R.styleable.SingleDateAndTimePicker_picker_cyclic, IS_CYCLIC_DEFAULT))
        setMustBeOnFuture(a.getBoolean(R.styleable.SingleDateAndTimePicker_picker_mustBeOnFuture, MUST_BE_ON_FUTURE_DEFAULT))
        setVisibleItemCount(a.getInt(R.styleable.SingleDateAndTimePicker_picker_visibleItemCount, VISIBLE_ITEM_COUNT_DEFAULT))
        setStepSizeMinutes(a.getInt(R.styleable.SingleDateAndTimePicker_picker_stepSizeMinutes, 1))
        setStepSizeHours(a.getInt(R.styleable.SingleDateAndTimePicker_picker_stepSizeHours, 1))
        daysPicker.setDayCount(a.getInt(R.styleable.SingleDateAndTimePicker_picker_dayCount, SingleDateAndTimeConstants.DAYS_PADDING))
        setDisplayDays(a.getBoolean(R.styleable.SingleDateAndTimePicker_picker_displayDays, displayDays))
        setDisplayMinutes(a.getBoolean(R.styleable.SingleDateAndTimePicker_picker_displayMinutes, displayMinutes))
        setDisplayHours(a.getBoolean(R.styleable.SingleDateAndTimePicker_picker_displayHours, displayHours))
        setDisplayMonths(a.getBoolean(R.styleable.SingleDateAndTimePicker_picker_displayMonth, displayMonth))
        setDisplayYears(a.getBoolean(R.styleable.SingleDateAndTimePicker_picker_displayYears, displayYears))
        setDisplayDaysOfMonth(a.getBoolean(R.styleable.SingleDateAndTimePicker_picker_displayDaysOfMonth, displayDaysOfMonth))
        setDisplayMonthNumbers(a.getBoolean(R.styleable.SingleDateAndTimePicker_picker_displayMonthNumbers, monthPicker.displayMonthNumbers()))
        setFontToAllPickers(a.getResourceId(R.styleable.SingleDateAndTimePicker_fontFamily, 0))
        setFontToAllPickers(a.getResourceId(R.styleable.SingleDateAndTimePicker_android_fontFamily, 0))
        val monthFormat = a.getString(R.styleable.SingleDateAndTimePicker_picker_monthFormat)
        setMonthFormat(if (TextUtils.isEmpty(monthFormat)) WheelMonthPicker.MONTH_FORMAT else monthFormat)
        setTextAlign(a.getInt(R.styleable.SingleDateAndTimePicker_picker_textAlign, ALIGN_CENTER))
        checkSettings()
        setMinYear()
        a.recycle()
        if (displayDaysOfMonth) {
            val now = Calendar.getInstance()
            now.timeZone = dateHelper.timeZone
            updateDaysOfMonth(now)
        }
        daysPicker.updateAdapter() // For MustBeFuture and dayCount
    }

    interface OnDateChangedListener {
        fun onDateChanged(displayed: String?, date: Date?)
    }

    companion object {
        const val IS_CYCLIC_DEFAULT = true
        const val IS_CURVED_DEFAULT = false
        const val MUST_BE_ON_FUTURE_DEFAULT = false
        const val DELAY_BEFORE_CHECK_PAST = 200
        private const val VISIBLE_ITEM_COUNT_DEFAULT = 7
        private const val PM_HOUR_ADDITION = 12
        const val ALIGN_CENTER = 0
        const val ALIGN_LEFT = 1
        const val ALIGN_RIGHT = 2
        private val FORMAT_24_HOUR: CharSequence = "EEE d MMM H:mm"
        private val FORMAT_12_HOUR: CharSequence = "EEE d MMM h:mm a"
    }

    init {
        defaultDate = Date()
        isAmPm = !DateFormat.is24HourFormat(context)
        inflate(context, R.layout.single_day_and_time_picker, this)
        yearsPicker = findViewById(R.id.yearPicker)
        monthPicker = findViewById(R.id.monthPicker)
        daysOfMonthPicker = findViewById(R.id.daysOfMonthPicker)
        daysPicker = findViewById(R.id.daysPicker)
        minutesPicker = findViewById(R.id.minutesPicker)
        hoursPicker = findViewById(R.id.hoursPicker)
        amPmPicker = findViewById(R.id.amPmPicker)
        dtSelector = findViewById(R.id.dtSelector)
        pickers.addAll(listOf(
                daysPicker,
                minutesPicker,
                hoursPicker,
                amPmPicker,
                daysOfMonthPicker,
                monthPicker,
                yearsPicker
        ))
        for (wheelPicker in pickers) {
            wheelPicker.dateHelper = dateHelper
        }
        init(context, attrs)
    }
}