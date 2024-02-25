package com.example.project1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
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

        tvInfo =  findViewById(R.id.tvInfo);
        etInput =  findViewById(R.id.etInput);
        bControl =  findViewById(R.id.bControl);

        startNewGame();
    }

    private void startNewGame() {
        secretNumber = new Random().nextInt(200) + 1;
        Log.d("Game", "Secret number is: " + secretNumber);
        gameFinished = false;

        tvInfo.setText(getResources().getString(R.string.try_to_guess));
        bControl.setText(getResources().getString(R.string.play_more));
        etInput.setText("");
    }

    public void onClick(View v) {
        // Если игра окончена, начинаем новую
        if (gameFinished) {
            startNewGame();
            return;
        }

        try {
            // Получаем введённое число
            int userGuess = Integer.parseInt(etInput.getText().toString());

            // Сравниваем с загаданным числом
            if (userGuess < secretNumber) {
                tvInfo.setText(getResources().getString(R.string.ahead));
            } else if (userGuess > secretNumber) {
                tvInfo.setText(getResources().getString(R.string.behind));
            } else {
                tvInfo.setText(getResources().getString(R.string.hit));
                // Игра окончена, предлагаем сыграть снова
                gameFinished = true;
                bControl.setText(getResources().getString(R.string.play_more));
            }
        } catch (NumberFormatException e) {
            // Если ввод не является числом, показываем сообщение об ошибке
            Toast.makeText(this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }
}

