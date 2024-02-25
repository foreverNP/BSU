package com.example.project1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
import java.util.UUID;

public class GameActivity extends AppCompatActivity {

    private SharedPreferences gamePrefs;
    private TextView tvFeedback, tvScore;
    private EditText etGuess;
    private Button btnGuess, btnReset;

    private int randomNumber;
    private int attemptCount;
    private int bestScore;
    private int launchCount;
    private String uniqueID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gamePrefs = getSharedPreferences("game_settings", MODE_PRIVATE);

        tvFeedback = findViewById(R.id.tvFeedback);
        tvScore = findViewById(R.id.tvScore);
        etGuess = findViewById(R.id.etGuess);
        btnGuess = findViewById(R.id.btnGuess);
        btnReset = findViewById(R.id.btnReset);

        launchCount = gamePrefs.getInt("launchCount", 0) + 1;
        gamePrefs.edit().putInt("launchCount", launchCount).apply();

        uniqueID = gamePrefs.getString("uniqueID", null);
        if (uniqueID == null) {
            uniqueID = UUID.randomUUID().toString();
            gamePrefs.edit().putString("uniqueID", uniqueID).apply();
        }

        bestScore = gamePrefs.getInt("bestScore", 1000);

        startNewGame();

        btnGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String guessStr = etGuess.getText().toString();
                if (guessStr.isEmpty()) {
                    Toast.makeText(GameActivity.this, "Введите число", Toast.LENGTH_SHORT).show();
                    return;
                }
                int guess = Integer.parseInt(guessStr);
                attemptCount++;
                if (guess < randomNumber) {
                    tvFeedback.setText("Больше");
                } else if (guess > randomNumber) {
                    tvFeedback.setText("Меньше");
                } else {
                    tvFeedback.setText("Угадал!");
                    int score = Math.max(100 - attemptCount, 0);
                    Toast.makeText(GameActivity.this,
                            "Поздравляем! Вы угадали число за " + attemptCount + " попыток. Ваш счет: " + score,
                            Toast.LENGTH_LONG).show();
                    if (attemptCount < bestScore) {
                        bestScore = attemptCount;
                        gamePrefs.edit().putInt("bestScore", bestScore).apply();
                    }
                    btnGuess.setEnabled(false);
                }
                updateStatus();
                etGuess.setText("");
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGame();
            }
        });
    }

    private void startNewGame() {
        randomNumber = new Random().nextInt(101); // случайное число от 0 до 100
        attemptCount = 0;
        tvFeedback.setText("Угадайте число от 0 до 100");
        updateStatus();
        btnGuess.setEnabled(true);
    }

    private void updateStatus() {
        String status = "Попытки: " + attemptCount +
                "\nЛучший результат: " + bestScore +
                "\nЗапусков: " + launchCount +
                "\nID: " + uniqueID;
        tvScore.setText(status);
    }
}
