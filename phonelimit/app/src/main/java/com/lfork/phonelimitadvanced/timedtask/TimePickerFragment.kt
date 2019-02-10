package com.lfork.phonelimitadvanced.timedtask

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.format.DateFormat
import android.widget.TimePicker
import java.util.*

class TimePickerFragment : DialogFragment() {

    var timePickListener:TimePickerDialog.OnTimeSetListener?= null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, timePickListener, hour, minute, DateFormat.is24HourFormat(activity))
    }

//    override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
//
//    }
//
//    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
//        // Do something with the time chosen by the user
//    }
}