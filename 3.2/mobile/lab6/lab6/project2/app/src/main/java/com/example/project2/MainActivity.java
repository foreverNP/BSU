package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnShowRecords, btnAddRecord, btnUpdateRecord;
    private EditText etLastName, etFirstName, etMiddleName, etBirthDate, etPhoneNumber;
    private EditText etUpdateId, etNewPhoneNumber;
    private DBRepository dbRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbRepository = new DBRepository(this);

        dbRepository.initData();

        btnShowRecords = findViewById(R.id.btnShowRecords);
        btnAddRecord = findViewById(R.id.btnAddRecord);
        btnUpdateRecord = findViewById(R.id.btnUpdateRecord);

        etLastName = findViewById(R.id.etLastName);
        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etBirthDate = findViewById(R.id.etBirthDate);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);

        etUpdateId = findViewById(R.id.etUpdateId);
        etNewPhoneNumber = findViewById(R.id.etNewPhoneNumber);

        btnShowRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                startActivity(intent);
            }
        });

        btnAddRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lastName = etLastName.getText().toString().trim();
                String firstName = etFirstName.getText().toString().trim();
                String middleName = etMiddleName.getText().toString().trim();
                String birthDate = etBirthDate.getText().toString().trim();
                String phoneNumber = etPhoneNumber.getText().toString().trim();

                if(lastName.isEmpty() || firstName.isEmpty() || birthDate.isEmpty() || phoneNumber.isEmpty()){
                    Toast.makeText(MainActivity.this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbRepository.addRecord(lastName, firstName, middleName, birthDate, phoneNumber);
                Toast.makeText(MainActivity.this, "Запись добавлена", Toast.LENGTH_SHORT).show();

                etLastName.setText("");
                etFirstName.setText("");
                etMiddleName.setText("");
                etBirthDate.setText("");
                etPhoneNumber.setText("");
            }
        });

        btnUpdateRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idStr = etUpdateId.getText().toString().trim();
                String newPhone = etNewPhoneNumber.getText().toString().trim();
                if(idStr.isEmpty() || newPhone.isEmpty()){
                    Toast.makeText(MainActivity.this, "Введите ID записи и новый номер", Toast.LENGTH_SHORT).show();
                    return;
                }
                int id = Integer.parseInt(idStr);
                dbRepository.updateRecord(id, newPhone);
                Toast.makeText(MainActivity.this, "Запись обновлена", Toast.LENGTH_SHORT).show();

                etUpdateId.setText("");
                etNewPhoneNumber.setText("");
            }
        });
    }
}
