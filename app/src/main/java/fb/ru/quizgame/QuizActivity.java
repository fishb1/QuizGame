package fb.ru.quizgame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private int time;
    private boolean mStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        String[] letters = {"П", "И", "З", "Д", "Е", "Ц"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_hint, R.id.tv_hint_letter, letters);
        mHint.setAdapter(adapter);
        mHint.setNumColumns(adapter.getCount());
        mQuestion.setText("Тобi пiзда, москалик!");
        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (++time > 100) {
                    timer.cancel();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTimeToHint.setProgress(time);
                        }
                    });
                }
            }
        };
        timer.schedule(task, 0, TimeUnit.SECONDS.toMillis(1));
    }

    public void onClick(final View view) {
        if (!mStarted) {
            mStarted = true;
            final AnimatorSet set = new AnimatorSet();
//            final ObjectAnimator flipOut = ;
//            Log.d(TAG, "flipOut: "  + flipOut);
            set.playSequentially(ObjectAnimator.ofFloat(view, View.ROTATION_Y, 0, 90), ObjectAnimator.ofFloat(view, View.ROTATION_Y, -90, 0));
            set.setDuration(250);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animator) {
                    mStarted = false;
//                    Log.d(TAG, "Aminator: "  + animator);
//                    if (animator == flipOut) {
//                        ViewGroup card = (ViewGroup) view;
//                        View asterisk = card.findViewById(R.id.tv_hint_asterisk);
//                        boolean hidden = asterisk.getVisibility() == View.VISIBLE;
//                        card.findViewById(R.id.tv_hint_asterisk).setVisibility(hidden ? View.GONE : View.VISIBLE);
//                        card.findViewById(R.id.tv_hint_letter).setVisibility(hidden ? View.VISIBLE : View.GONE);
//                        set.play();
//                        set.resume();
//                    } else {
//                        mStarted = false;
//                    }
                }
            });
            set.start();
        }
    }
}
