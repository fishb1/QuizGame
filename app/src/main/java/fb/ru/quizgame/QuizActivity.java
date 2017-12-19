package fb.ru.quizgame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
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

    @BindView(R.id.tv_question) TextView mQuestion;
    @BindView(R.id.lo_hint) GridView mHint;
    @BindView(R.id.pb_time_to_hint) ProgressBar mTimeToHint;
    private int mTime;
    private boolean isAnimating;
    ArrayAdapter<String> mAdapter;
    Timer mTimer;
    TimerTask mTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);
        mAdapter = new ArrayAdapter<>(this, R.layout.item_hint, R.id.tv_hint_letter);
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
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (++mTime > 100) {
                    mTimer.cancel();
                } else {
                    runOnUiThread(() -> mTimeToHint.setProgress(mTime));
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start the game!
        nextQuestion();
    }

    @Override
    protected void onStop() {
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
        String answer = parts[1];
        mAdapter.clear();
        mAdapter.addAll(answer.toUpperCase().split("(?!^)"));
        mHint.setNumColumns(mAdapter.getCount());
        mQuestion.setText(question);
        //
        startTimer();
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void startTimer() {
        mTimer = new Timer();
        mTime = 0;
        mTimer.schedule(mTimerTask, TimeUnit.SECONDS.toMillis(1));
    }
}
