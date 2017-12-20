package fb.ru.quizgame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewAnimator;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";

    private static final int TIME_TO_ANSWER = 100; // Time for 1 round in seconds

    @BindView(R.id.tv_question) TextView mQuestion;
    @BindView(R.id.gw_hint) GridView mHint;
    @BindView(R.id.pb_timer) ProgressBar mTimeToHint;
    @BindView(R.id.btn_answer) Button mButtonAnswer;
    @BindView(R.id.btn_next) Button mButtonNext;
    @BindView(R.id.tv_answer) EditText mInputAnswer;
    private String mAnswer;
    private int mTime;
    private boolean isAnimating;
    ArrayAdapter<String> mAdapter;
    Timer mTimer;
    TimerTask mTimerTask;
    boolean isPaused;
    int mQuestionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);
        mAdapter = new ArrayAdapter<>(this, R.layout.item_hint, R.id.tv_hint_obverse);
        mHint.setAdapter(mAdapter);
        mHint.setOnItemClickListener((parent, view, position, id) -> {
            if (!isAnimating) {
                isAnimating = true;
                AnimatorSet flipOut = new AnimatorSet();
                flipOut.play(ObjectAnimator.ofFloat(view, View.ROTATION_Y, 0, 90));
                flipOut.setDuration(250);
                flipOut.setInterpolator(new AccelerateInterpolator());
                flipOut.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ((ViewAnimator) view).showNext();
                        AnimatorSet flipIn = new AnimatorSet();
                        flipIn.play(ObjectAnimator.ofFloat(view, View.ROTATION_Y, -90, 0));
                        flipIn.setDuration(250);
                        flipIn.setInterpolator(new DecelerateInterpolator());
                        flipIn.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                isAnimating = false;
                            }
                        });
                        flipIn.start();
                    }
                });
                flipOut.start();
            }
        });
        mButtonNext.setOnClickListener((view) -> nextQuestion());
        mButtonAnswer.setOnClickListener((view) -> {
            String answer = String.valueOf(mInputAnswer.getText());
            mInputAnswer.setText("");
            if (TextUtils.equals(mAnswer, answer)) {
                appendToLog(GameLogFragment.MSG_TYPE_USER, Util.getColoredText(answer,
                        getResources().getColor(R.color.colorPrimary)));
                appendToLog(GameLogFragment.MSG_TYPE_BOT,
                        "Это правильный ответ, время: " + mTime + " секунд");
                nextQuestion();
            } else {
                appendToLog(GameLogFragment.MSG_TYPE_USER, Util.getColoredText(answer,
                        getResources().getColor(R.color.colorAccent)));
            }
        });
        appendToLog(GameLogFragment.MSG_TYPE_BOT, "Начата новая викторина!");
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        // Start the game!
        nextQuestion();
        isPaused = false;
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        isPaused = true;
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        stopTimer();
    }

    private void nextQuestion() {
        stopTimer();
        //
        QuestionRepository repository = new QuestionRepository(this);
        String task = repository.getQuestion();
        String[] parts = task.split("\\*");
        // Question
        String question = parts[0];
        question = question.substring(0, 1).toUpperCase() + question.substring(1);
        // Answer
        mAnswer = parts[1];
        mAdapter.clear();
        mAdapter.addAll(mAnswer.toUpperCase().split("(?!^)"));
        mHint.setNumColumns(mAdapter.getCount());
        mQuestion.setText(question);
        //
        appendToLog(GameLogFragment.MSG_TYPE_BOT, String.format("Вопрос №%s", ++mQuestionNumber));
        //
        startTimer();
    }

    private void stopTimer() {
        Log.d(TAG, "Stop timer!");
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            mTimerTask = null;
        }
    }

    private void startTimer() {
        Log.d(TAG, "Start timer!");
        // Reset time
        mTime = 0;
        // Create new timer
        mTimer = new Timer();
        // Create new Task (I can't reuse it)
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isPaused) {
                    if (++mTime > TIME_TO_ANSWER) {
                        runOnUiThread(() -> {
                            appendToLog(GameLogFragment.MSG_TYPE_BOT, "К сожалению, время на ответ истекло :(");
                            nextQuestion();
                        });
                    } else {
                        runOnUiThread(() -> mTimeToHint.setProgress(mTime));
                    }
                }
            }
        };
        // Schedule task
        mTimer.schedule(mTimerTask, 0, TimeUnit.SECONDS.toMillis(1));
    }

    private void appendToLog(int msgType, CharSequence text) {
        GameLogFragment log = (GameLogFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_game_log);
        log.appendToLog(msgType, text);
    }
}
