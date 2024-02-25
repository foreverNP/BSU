package com.example.project1;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView tvInfo;
    private EditText etInput;
    private Button bControl;
    private ImageView imageView;
    private ProgressBar progressBar;
    private GuessingGame game;
    private final int MAX_ATTEMPTS = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvInfo = findViewById(R.id.tvInfo);
        etInput = findViewById(R.id.etInput);
        bControl = findViewById(R.id.bControl);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setMax(MAX_ATTEMPTS);
        startNewGame();
    }

    private void startNewGame() {
        game = new GuessingGame();
        Log.d("Game", "Secret number is: " + game.getSecretNumber());
        progressBar.setProgress(0);
        tvInfo.setText(getString(R.string.try_to_guess));
        bControl.setText(getString(R.string.play_more));
        etInput.setText("");
    }

    public void onClick(View v) {
        if (game.isGameFinished()) {
            startNewGame();
            return;
        }

        Animation scaleTranslate = AnimationUtils.loadAnimation(this, R.anim.scale_translate);
        Animation fade = AnimationUtils.loadAnimation(this, R.anim.fade);
        bControl.startAnimation(scaleTranslate);
        tvInfo.startAnimation(fade);

        String inputText = etInput.getText().toString().trim();
        if (inputText.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_no_input), Toast.LENGTH_SHORT).show();
            return;
        }

        int userGuess;
        try {
            userGuess = Integer.parseInt(inputText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.error_not_a_number), Toast.LENGTH_SHORT).show();
            return;
        }

        GuessingGame.GuessResult result = game.makeGuess(userGuess);
        progressBar.setProgress(game.getAttempts());

        switch (result) {
            case AHEAD:
                tvInfo.setText(getString(R.string.ahead));
                break;
            case BEHIND:
                tvInfo.setText(getString(R.string.behind));
                break;
            case HIT:
                tvInfo.setText(getString(R.string.hit));
                bControl.setText(getString(R.string.play_more));

                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.congrats_title))
                        .setMessage(getString(R.string.congrats_message, game.getAttempts()))
                        .setPositiveButton(getString(R.string.new_game), (dialog, which) -> startNewGame())
                        .show();

                Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
                imageView.startAnimation(rotate);
                break;
            case INVALID:
                Toast.makeText(this, getString(R.string.error_out_of_range), Toast.LENGTH_SHORT).show();
                break;
        }
        etInput.setText("");
    }
}
