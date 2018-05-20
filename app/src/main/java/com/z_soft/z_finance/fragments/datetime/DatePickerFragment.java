package com.z_soft.z_finance.fragments.datetime;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.z_soft.z_finance.utils.AppContext;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    private static Calendar calendar;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        calendar = (Calendar) getArguments().getSerializable(AppContext.DATE_CALENDAR);

        if (calendar == null){
            calendar = Calendar.getInstance();
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)getActivity(), year, month, day);

    }

}