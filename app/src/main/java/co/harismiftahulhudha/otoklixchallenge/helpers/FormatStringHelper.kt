package co.harismiftahulhudha.otoklixchallenge.helpers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import co.harismiftahulhudha.otoklixchallenge.R
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class FormatStringHelper {
    fun convertGmtToDayAndDate(dataDate: String?): String? {
        var date: String?
        try {
            val dateFormatServer = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()
            )
            dateFormatServer.timeZone = TimeZone.getTimeZone("GMT") // IMP !!!
            val humanFormat =
                SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("ID"))
            date = humanFormat.format(dateFormatServer.parse(dataDate))
        } catch (e: ParseException) {
            e.printStackTrace()
            date = null
        }
        return date
    }

    fun convertGmtToHourAndMinute(dataDate: String?): String? {
        var date: String?
        try {
            val dateFormatServer = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()
            )
            dateFormatServer.timeZone = TimeZone.getTimeZone("GMT") // IMP !!!
            val humanFormat =
                SimpleDateFormat("HH:mm", Locale("ID"))
            date = humanFormat.format(dateFormatServer.parse(dataDate))
        } catch (e: ParseException) {
            e.printStackTrace()
            date = null
        }
        return date
    }

    fun convertToHourAndMinute(dataDate: String?): String? {
        var date: String?
        try {
            val dateFormatServer = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            )
            val humanFormat =
                SimpleDateFormat("HH:mm", Locale("ID"))
            date = humanFormat.format(dateFormatServer.parse(dataDate))
        } catch (e: ParseException) {
            e.printStackTrace()
            date = null
        }
        return date
    }

    fun covertTimeToText(dataDate: String): String? {
        var convTime: String? = null
        val prefix = ""
        val suffix = "lalu"
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("GMT")
            val pasTime = dateFormat.parse(dataDate)
            val nowTime = Date()
            val dateDiff = nowTime.time - pasTime.time
            val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
            if (second < 0) {
                convTime = "0 detik $suffix"
            } else if (second > 0 && second < 60) {
                convTime = "$second detik $suffix"
            } else if (minute < 60) {
                convTime = "$minute menit $suffix"
            } else if (hour < 24) {
                convTime = "$hour jam $suffix"
            } else if (day >= 7) {
                convTime = if (day > 360) {
                    (day / 360).toString() + " tahun " + suffix
                } else if (day > 30) {
                    (day / 30).toString() + " bulan " + suffix
                } else {
                    (day / 7).toString() + " minggu " + suffix
                }
            } else if (day < 7) {
                convTime = "$day hari $suffix"
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return convTime
    }

    fun formatValue(value: String): String {
        val splitValue = value.split(".")
        var newValue = splitValue[0].toInt()
        val arr = arrayOf("", "K", "M", "B", "T", "P", "E")
        var index = 0
        while (newValue / 1000 >= 1) {
            newValue = newValue / 1000
            index++
        }
        val decimalFormat = DecimalFormat("#.#")
        return String.format("%s%s", decimalFormat.format(newValue.toDouble()), arr[index])
    }

    fun formatNumber(number: String, isConverted: Boolean = false): String {
        if (number.toDouble() >= 100000) {
            formatValue(number)
        }
        val splitNumber = number.split(".")
        if (splitNumber.size == 2) {
            val coma = if (splitNumber[1].length == 1) {
                "${splitNumber[1]}0"
            } else {
                splitNumber[1]
            }
            return String.format(Locale.US, "%,d", splitNumber[0].toLong()).replace(',', '.') + ",${coma}"
        } else {
            return String.format(Locale.US, "%,d", splitNumber[0].toLong()).replace(',', '.')
        }
    }

    fun formatNumberRp(number: String): String {
        return "Rp " + formatNumber(number)
    }
}