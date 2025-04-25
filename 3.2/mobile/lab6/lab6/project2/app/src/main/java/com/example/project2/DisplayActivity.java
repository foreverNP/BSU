package com.example.project2;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DisplayActivity extends AppCompatActivity {

    private TextView tvRecords;
    private DBRepository dbRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        tvRecords = findViewById(R.id.tvRecords);
        dbRepository = new DBRepository(this);

        StringBuilder builder = new StringBuilder();
        Cursor cursor = dbRepository.getAllRecords();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(DBRepository.COLUMN_ID));
                String lastName = cursor.getString(cursor.getColumnIndex(DBRepository.COLUMN_LAST_NAME));
                String firstName = cursor.getString(cursor.getColumnIndex(DBRepository.COLUMN_FIRST_NAME));
                String middleName = cursor.getString(cursor.getColumnIndex(DBRepository.COLUMN_MIDDLE_NAME));
                String birthDate = cursor.getString(cursor.getColumnIndex(DBRepository.COLUMN_BIRTH_DATE));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(DBRepository.COLUMN_PHONE_NUMBER));

                builder.append("ID: ").append(id).append("\n")
                        .append("Фамилия: ").append(lastName).append("\n")
                        .append("Имя: ").append(firstName).append("\n")
                        .append("Отчество: ").append(middleName).append("\n")
                        .append("Дата рождения: ").append(birthDate).append("\n")
                        .append("Телефон: ").append(phoneNumber).append("\n\n");
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            builder.append("Нет данных для отображения");
        }
        tvRecords.setText(builder.toString());
    }
}
