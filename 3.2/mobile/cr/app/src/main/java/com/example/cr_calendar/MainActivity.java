package com.example.cr_calendar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textViewDateLabel;
    private Button buttonSelectDate;
    private Button buttonSave;

    private Calendar selectedDateCalendar;
    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "CalendarAppPrefs";
    private static final String DATE_KEY = "selectedDateMillis";
    private boolean dateWasSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        selectedDateCalendar = Calendar.getInstance();

        textViewDateLabel = findViewById(R.id.textViewDateLabel);
        buttonSelectDate = findViewById(R.id.buttonSelectDate);
        buttonSave = findViewById(R.id.buttonSave);

        loadDate();

        buttonSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateWasSelected) {
                    saveDate();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.date_not_selected_toast), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDatePickerDialog() {
        int year = selectedDateCalendar.get(Calendar.YEAR);
        int month = selectedDateCalendar.get(Calendar.MONTH);
        int day = selectedDateCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        selectedDateCalendar.set(Calendar.YEAR, year);
                        selectedDateCalendar.set(Calendar.MONTH, monthOfYear);
                        selectedDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        dateWasSelected = true;
                        updateDateInView();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void updateDateInView() {
        String dateFormatPattern = getString(R.string.date_format_pattern);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatPattern, Locale.getDefault());
        String formattedDate = sdf.format(selectedDateCalendar.getTime());
        textViewDateLabel.setText(formattedDate);
    }

    private void saveDate() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(DATE_KEY, selectedDateCalendar.getTimeInMillis());
        editor.apply();

        String dateFormatPattern = getString(R.string.date_format_pattern);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatPattern, Locale.getDefault());
        String formattedDate = sdf.format(selectedDateCalendar.getTime());
        String toastMessage = getString(R.string.date_saved_toast, formattedDate);
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    private void loadDate() {
        long savedDateMillis = sharedPreferences.getLong(DATE_KEY, -1);

        if (savedDateMillis != -1) {
            selectedDateCalendar.setTimeInMillis(savedDateMillis);
            dateWasSelected = true;
            updateDateInView();
        } else {
            textViewDateLabel.setText(getString(R.string.select_date_label));
            dateWasSelected = false;
        }
    }
}