package com.example.project2;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView tvInfo;
    private EditText etInput;
    private Button bControl;
    private int secretNumber;
    private boolean gameFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvInfo = findViewById(R.id.tvInfo);
        etInput = findViewById(R.id.etInput);
        bControl = findViewById(R.id.bControl);

        startNewGame();
    }

    private void startNewGame() {
        secretNumber = new Random().nextInt(200) + 1;
        gameFinished = false;
        tvInfo.setText(getString(R.string.try_to_guess));
        bControl.setText(getString(R.string.play_more));
        etInput.setText("");
    }

    public void onClick(View v) {
        if (gameFinished) {
            startNewGame();
            return;
        }

        String inputText = etInput.getText().toString().trim();

        // Проверка: если поле ввода пустое
        if (inputText.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_no_input), Toast.LENGTH_SHORT).show();
            return;
        }

        int userGuess;
        try {
            userGuess = Integer.parseInt(inputText);
        } catch (NumberFormatException e) {
            // Если введено не число
            Toast.makeText(this, getString(R.string.error_not_a_number), Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка диапазона (число должно быть от 0 до 200)
        if (userGuess < 0 || userGuess > 200) {
            Toast.makeText(this, getString(R.string.error_out_of_range), Toast.LENGTH_SHORT).show();
            return;
        }

        // Логика сравнения введенного числа с загаданным
        if (userGuess < secretNumber) {
            tvInfo.setText(getString(R.string.ahead));
        } else if (userGuess > secretNumber) {
            tvInfo.setText(getString(R.string.behind));
        } else {
            tvInfo.setText(getString(R.string.hit));
            gameFinished = true;
            bControl.setText(getString(R.string.play_more));
        }

        etInput.setText("");
    }
}
