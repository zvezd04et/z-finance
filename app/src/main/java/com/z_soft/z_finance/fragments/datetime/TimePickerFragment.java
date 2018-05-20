package com.z_soft.z_finance.fragments.datetime;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

import com.z_soft.z_finance.utils.AppContext;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    private static Calendar calendar;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        calendar = (Calendar) getArguments().getSerializable(AppContext.DATE_CALENDAR);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener)getActivity(), hour, minute, true);
    }

}
