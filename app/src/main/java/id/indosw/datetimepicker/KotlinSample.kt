package id.indosw.datetimepicker

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import id.indosw.datetimepicker.lib.SingleDateAndTimePicker
import id.indosw.datetimepicker.lib.dialog.DoubleDateAndTimePickerDialog
import id.indosw.datetimepicker.lib.dialog.SingleDateAndTimePickerDialog
import james.crasher.Crasher
import james.crasher.Crasher.OnCrashListener
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("unused", "UNUSED_VARIABLE")
@SuppressLint("NonConstantResourceId")
class KotlinSample : AppCompatActivity(), OnCrashListener {
    @BindView(R.id.doubleText)
    var doubleText: TextView? = null

    @BindView(R.id.singleText)
    var singleText: TextView? = null

    @BindView(R.id.singleTimeText)
    var singleTimeText: TextView? = null

    @BindView(R.id.singleDateText)
    var singleDateText: TextView? = null

    @BindView(R.id.singleDateLocaleText)
    var singleDateLocaleText: TextView? = null
    private var simpleDateFormat: SimpleDateFormat? = null
    private var simpleTimeFormat: SimpleDateFormat? = null
    private var simpleDateOnlyFormat: SimpleDateFormat? = null
    private var simpleDateLocaleFormat: SimpleDateFormat? = null
    private var singleBuilder: SingleDateAndTimePickerDialog.Builder? = null
    private var doubleBuilder: DoubleDateAndTimePickerDialog.Builder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.single_date_picker_activity_main_double_picker)
        val crasher = Crasher(this)
        crasher.addListener(this)
        crasher.email = "18jafenn90@gmail.com"
        ButterKnife.bind(this)
        simpleDateFormat = SimpleDateFormat("EEE d MMM HH:mm", Locale.getDefault())
        simpleTimeFormat = SimpleDateFormat("hh:mm aa", Locale.getDefault())
        simpleDateOnlyFormat = SimpleDateFormat("EEE d MMM", Locale.getDefault())
        simpleDateLocaleFormat = SimpleDateFormat("EEE d MMM", Locale.GERMAN)
    }

    override fun onPause() {
        super.onPause()
        if (singleBuilder != null) singleBuilder!!.dismiss()
        if (doubleBuilder != null) doubleBuilder!!.dismiss()
    }

    @OnClick(R.id.singleTimeText)
    fun simpleTimeClicked() {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 21
        calendar[Calendar.MINUTE] = 50
        val defaultDate = calendar.time
        singleBuilder = SingleDateAndTimePickerDialog.Builder(this)
                .setTimeZone(TimeZone.getDefault())
                .bottomSheet()
                .curved()
                .defaultDate(defaultDate)
                .titleTextColor(Color.GREEN)
                .backgroundColor(Color.BLACK)
                .mainColor(Color.GREEN)
                .displayMinutes(true)
                .displayHours(true)
                .displayDays(false)
                .displayMonth(true)
                .displayYears(true)
                .displayListener(object : SingleDateAndTimePickerDialog.DisplayListener {
                    override fun onDisplayed(picker: SingleDateAndTimePicker?) {
                        Log.d(TAG, "Dialog displayed")
                    }

                    override fun onClosed(picker: SingleDateAndTimePicker?) {
                        Log.d(TAG, "Dialog closed")
                    }
                })
                .title("Simple Time")
                .listener(object : SingleDateAndTimePickerDialog.Listener {
                    override fun onDateSelected(date: Date?) {
                        singleTimeText!!.text = simpleTimeFormat!!.format(date!!)
                    }
                })
        singleBuilder!!.display()
    }

    @OnClick(R.id.singleDateText)
    fun simpleDateClicked() {
        val calendar = Calendar.getInstance()
        val defaultDate = calendar.time
        singleBuilder = SingleDateAndTimePickerDialog.Builder(this)
                .setTimeZone(TimeZone.getDefault())
                .bottomSheet()
                .curved()
                .titleTextColor(Color.GREEN)
                .backgroundColor(Color.BLACK)
                .mainColor(Color.GREEN)
                .displayHours(false)
                .displayMinutes(false)
                .displayDays(true)
                .displayListener(object : SingleDateAndTimePickerDialog.DisplayListener {
                    override fun onDisplayed(picker: SingleDateAndTimePicker?) {
                        Log.d(TAG, "Dialog displayed")
                    }

                    override fun onClosed(picker: SingleDateAndTimePicker?) {
                        Log.d(TAG, "Dialog closed")
                    }
                })
                .title("")
                .listener(object : SingleDateAndTimePickerDialog.Listener {
                    override fun onDateSelected(date: Date?) {
                        singleDateText!!.text = simpleDateOnlyFormat!!.format(date!!)
                    }
                })
        singleBuilder!!.display()
    }

    @OnClick(R.id.singleLayout)
    fun simpleClicked() {
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_MONTH] = 4 // 4. Feb. 2018
        calendar[Calendar.MONTH] = 1
        calendar[Calendar.YEAR] = 2018
        calendar[Calendar.HOUR_OF_DAY] = 11
        calendar[Calendar.MINUTE] = 13
        val defaultDate = calendar.time
        singleBuilder = SingleDateAndTimePickerDialog.Builder(this)
                .setTimeZone(TimeZone.getDefault())
                .bottomSheet()
                .curved()
                .backgroundColor(Color.BLACK)
                .mainColor(Color.GREEN)
                .displayHours(false)
                .displayMinutes(false)
                .displayDays(false)
                .displayMonth(true)
                .displayDaysOfMonth(true)
                .displayYears(true)
                .defaultDate(defaultDate)
                .displayMonthNumbers(true)
                .mustBeOnFuture()
                .minutesStep(15)
                .mustBeOnFuture()
                .defaultDate(defaultDate) //.minDateRange(minDate)
                //.maxDateRange(maxDate)
                .displayListener(object : SingleDateAndTimePickerDialog.DisplayListener {
                    override fun onDisplayed(picker: SingleDateAndTimePicker?) {
                        Log.d(TAG, "Dialog displayed")
                    }

                    override fun onClosed(picker: SingleDateAndTimePicker?) {
                        Log.d(TAG, "Dialog closed")
                    }
                })
                .title("Simple")
                .listener(object : SingleDateAndTimePickerDialog.Listener {
                    override fun onDateSelected(date: Date?) {
                        singleText!!.text = simpleDateFormat!!.format(date!!)
                    }
                })
        singleBuilder!!.display()
    }

    @OnClick(R.id.doubleLayout)
    fun doubleClicked() {
        val now = Date()
        val calendarMin = Calendar.getInstance()
        val calendarMax = Calendar.getInstance()
        calendarMin.time = now // Set min now
        calendarMax.time = Date(now.time + TimeUnit.DAYS.toMillis(150)) // Set max now + 150 days
        val minDate = calendarMin.time
        val maxDate = calendarMax.time
        doubleBuilder = DoubleDateAndTimePickerDialog.Builder(this)
                .setTimeZone(TimeZone.getDefault())
                .bottomSheet()
                .curved()
                .backgroundColor(Color.BLACK)
                .mainColor(Color.GREEN)
                .minutesStep(15)
                .mustBeOnFuture()
                .minDateRange(minDate)
                .maxDateRange(maxDate)
                .secondDateAfterFirst(true)
                .defaultDate(now)
                .tab0Date(now)
                .tab1Date(Date(now.time + TimeUnit.HOURS.toMillis(1)))
                .title("Double")
                .tab0Text("Depart")
                .tab1Text("Return")
                .listener(object : DoubleDateAndTimePickerDialog.Listener{
                    override fun onDateSelected(dates: List<Date?>?) {
                        val stringBuilder = StringBuilder()
                        for (date in dates!!) {
                            stringBuilder.append(simpleDateFormat!!.format(date!!)).append("\n")
                        }
                        doubleText!!.text = stringBuilder.toString()
                    }
                })
        doubleBuilder!!.display()
    }

    @OnClick(R.id.singleDateLocaleLayout)
    fun singleDateLocaleClicked() {
        singleBuilder = SingleDateAndTimePickerDialog.Builder(this)
                .customLocale(Locale.GERMAN)
                .bottomSheet()
                .curved()
                .displayHours(false)
                .displayMinutes(false)
                .displayDays(true)
                .displayListener(object : SingleDateAndTimePickerDialog.DisplayListener {
                    override fun onDisplayed(picker: SingleDateAndTimePicker?) {
                        Log.d(TAG, "Dialog displayed")
                    }

                    override fun onClosed(picker: SingleDateAndTimePicker?) {
                        Log.d(TAG, "Dialog closed")
                    }
                })
                .title("")
                .listener(object : SingleDateAndTimePickerDialog.Listener {
                    override fun onDateSelected(date: Date?) {
                        singleDateLocaleText!!.text = simpleDateLocaleFormat!!.format(date!!)
                    }
                })
        singleBuilder!!.display()
    }

    override fun onCrash(thread: Thread, throwable: Throwable) {
        Log.d("SingleDatePicker", "Exception thrown: " + throwable.javaClass.name)
    }

    companion object {
        private const val TAG = "KotlinSample"
    }
}