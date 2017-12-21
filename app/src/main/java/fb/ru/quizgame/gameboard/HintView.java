package fb.ru.quizgame.gameboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewAnimator;

/**
 *
 * Created by leon on 20.12.17.
 */

public class HintView extends ViewAnimator {

    public HintView(Context context) {
        super(context);
    }

    public HintView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpec = MeasureSpec.makeMeasureSpec((int) (width * 1.62), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
