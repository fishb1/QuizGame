package fb.ru.quizgame.gameboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ViewAnimator;

/**
 *
 * Created by leon on 20.12.17.
 */

public class HintItemView extends ViewAnimator {

    public HintItemView(Context context) {
        super(context);
    }

    public HintItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpec = MeasureSpec.makeMeasureSpec((int) (width * 1.62), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }

    @Override
    public void showNext() {
        post(() -> {
            AnimatorSet flipOut = new AnimatorSet();
            flipOut.play(ObjectAnimator.ofFloat(HintItemView.this, View.ROTATION_Y, 0, 90));
            flipOut.setDuration(250);
            flipOut.setInterpolator(new AccelerateInterpolator());
            flipOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    HintItemView.super.showNext();
                    AnimatorSet flipIn = new AnimatorSet();
                    flipIn.play(ObjectAnimator.ofFloat(HintItemView.this, View.ROTATION_Y, -90, 0));
                    flipIn.setDuration(250);
                    flipIn.setInterpolator(new DecelerateInterpolator());
                    flipIn.start();
                }
            });
            flipOut.start();
        });
    }
}
