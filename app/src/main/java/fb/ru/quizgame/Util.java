package fb.ru.quizgame;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

/**
 *
 * Created by leon on 19.12.17.
 */
public class Util {

    public static Spanned getColoredText(CharSequence text, int color) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        sb.setSpan(new ForegroundColorSpan(color), 0, text.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
