package com.developerspace.webrtcsample.util.misc

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

object TimeUtil {
    /**
     * formatMillisecondToHourMinute converts Milliseconds to Hours:Minutes format
     *
     * @param milliseconds: Long
     */
    fun formatMillisecondToHourMinute(milliseconds: Long): String {
        val dateTimeFormat = "dd-MM-yyyy HH:mm"
        val formatter = SimpleDateFormat(dateTimeFormat, Locale.getDefault())
        val dateTimeSlices = formatter.format(milliseconds).split(" ")
        return "${dateTimeSlices[1]}"
    }

    /**
     * formatMillisecondToHourMinute converts Milliseconds to Hours:Minutes format
     *
     * @param milliseconds: String
     */
    fun formatMillisecondToHourMinute(milliseconds: String): String {
        val pattern: Pattern = Pattern.compile("^[0-9]+$")
        val matcher: Matcher = pattern.matcher(milliseconds)
        if (! matcher.find())
            return ""

        val millis = milliseconds.toLong()
        val dateTimeFormat = "dd-MM-yyyy HH:mm"
        val formatter = SimpleDateFormat(dateTimeFormat, Locale.getDefault())
        val dateTimeSlices = formatter.format(millis).split(" ")
        return "${dateTimeSlices[1]}"
    }
}