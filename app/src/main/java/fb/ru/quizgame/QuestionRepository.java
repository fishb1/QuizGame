package fb.ru.quizgame;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * Created by leon on 19.12.17.
 */
public class QuestionRepository {

    private static final String TAG = "QuestionRepository";

    Context mContext;

    public QuestionRepository(Context context) {
        mContext = context;
    }

    public String getQuestion() {
        List<String> questions = new ArrayList<>();
        long start = System.nanoTime();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(mContext.getAssets().open("quiz.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                questions.add(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "error!", e);
        }
        Log.d(TAG, "Read for: " + ((System.nanoTime() - start) / 1_000_000) + " ms");
        Collections.shuffle(questions);
        start = System.nanoTime();
        Log.d(TAG, "Shuffle for: " + ((System.nanoTime() - start) / 1_000_000) + " ms");
        return questions.get(0);
    }
}
