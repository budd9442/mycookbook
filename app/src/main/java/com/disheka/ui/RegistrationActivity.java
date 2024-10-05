package com.disheka.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.disheka.R;
import java.util.ArrayList;

public class RegistrationActivity extends AppCompatActivity {

    private TextView questionTextView;
    private RadioGroup radioGroup;
    private ProgressBar progressBar;
    private Button buttonNext;

    private String[] questions = {
            "What's your cooking skill level?",
            "How much time do you usually have to cook a meal?",
            "How many servings do you typically cook for?",
            "Do you have any dietary restrictions or preferences?"
    };

    private String[][] answers = {
            {"Beginner", "Intermediate", "Advanced"},
            {"Under 15 minutes", "15-30 minutes", "30-60 minutes", "Over 60 minutes"},
            {"1 (just for me)", "2 (couple)", "3-4 (small family)", "5+ (large family or group)"},
            {"None", "Vegan", "Vegetarian", "Gluten free", "Dairy Free", "Nut free", "Low Carb", "Keto"}
    };

    private int currentQuestionIndex = 0;
    private ArrayList<String> selectedAnswers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        questionTextView = findViewById(R.id.questionTextView);
        radioGroup = findViewById(R.id.radioGroup);
        progressBar = findViewById(R.id.progressBar);
        buttonNext = findViewById(R.id.buttonNext);

        loadQuestion();

        buttonNext.setOnClickListener(v -> {
            // Check if an answer is selected
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(RegistrationActivity.this, "Please select an answer.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get selected answer
            RadioButton selectedRadioButton = findViewById(selectedId);
            String selectedAnswer = selectedRadioButton.getText().toString();
            selectedAnswers.add(selectedAnswer);

            // Process the current question
            if (currentQuestionIndex < questions.length - 1) {
                currentQuestionIndex++;
                loadQuestion();
                progressBar.setProgress((currentQuestionIndex + 1) * (100 / questions.length));
            } else {
                // Pass answers to SignUpActivity
                Intent intent = new Intent(RegistrationActivity.this, SignUpActivity.class);
                for (String answer : selectedAnswers) {
                    Log.d("RegistrationActivity", "Answer: " + answer);
                }
                intent.putStringArrayListExtra("answers", selectedAnswers);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadQuestion() {
        // Load the current question
        questionTextView.setText(questions[currentQuestionIndex]);

        // Clear previous answers
        radioGroup.removeAllViews();

        // Load the corresponding answers into the RadioGroup
        for (String answer : answers[currentQuestionIndex]) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(answer);
            radioButton.setTextAppearance(android.R.style.TextAppearance_Medium);
            radioGroup.addView(radioButton);
        }
    }
}
