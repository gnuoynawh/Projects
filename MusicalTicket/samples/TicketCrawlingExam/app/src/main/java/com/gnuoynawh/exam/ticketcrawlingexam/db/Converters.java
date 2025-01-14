package com.gnuoynawh.exam.ticketcrawlingexam.db;

import android.annotation.SuppressLint;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Room Database
 * - Convert String to Date
 */
public class Converters {

    @SuppressLint("SimpleDateFormat")
    static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @TypeConverter
    public static Date fromTimestamp(String value) {
        if (value != null) {
            try {
                return df.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @TypeConverter
    public static String dateToTimestamp(Date date) {
        return date == null ? null : df.format(date);
    }
}
