package com.gnuoynawh.musical.ticket.popup;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;

import androidx.annotation.NonNull;

import com.gnuoynawh.musical.ticket.R;

import java.util.Calendar;

public class DatePickerPopup extends Dialog {

    public DatePickerPopup(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public static class Builder {

        private Context context;

        private int yyyy, MM, dd;
        private OnDatePickerListener onDatePickerListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setOnDatePickerListener(OnDatePickerListener listener) {
            this.onDatePickerListener = listener;
            return this;
        }

        public DatePickerPopup build() {

            DatePickerPopup dialog = new DatePickerPopup(context);

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.popup_datepicker, null);

            Calendar cal = Calendar.getInstance();
            yyyy = cal.get(Calendar.YEAR);
            MM = cal.get(Calendar.MONTH);
            dd = cal.get(Calendar.DAY_OF_MONTH);

            DatePicker datePicker = view.findViewById(R.id.date_picker);
            datePicker.init(yyyy, MM, dd, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    yyyy = year;
                    MM = monthOfYear;
                    dd = dayOfMonth;
                }
            });

            view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (onDatePickerListener != null) {
                        onDatePickerListener.onClick(yyyy, MM + 1, dd);
                    }
                }
            });

            view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.setContentView(view);
            return dialog;
        }
    }

    public interface OnDatePickerListener {
        void onClick(int year, int monthOfYear, int dayOfMonth);
    }
}
