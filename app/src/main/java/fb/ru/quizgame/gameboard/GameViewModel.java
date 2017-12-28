package fb.ru.quizgame.gameboard;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fb.ru.quizgame.model.QuestionRepository;

/**
 *
 * Created by leon on 28.12.17.
 */
public class GameViewModel extends ViewModel {

    private static final String TAG = "GameViewModel";
    private static final int TIME_TO_ANSWER = 30; // Time for 1 round in seconds

    public final ObservableList<GameLogAdapter.LogEntry> messages = new ObservableArrayList<>(); // Лог игры
    public final ObservableInt score = new ObservableInt(); // Очки
    public final ObservableInt time = new ObservableInt(); // Оставшееся врямя на ответ
    public final ObservableField<String> question = new ObservableField<>(); // Текущий вопрос
    public final ObservableField<String> answer = new ObservableField<>(); // Ответ на текущий вопрос

    private final QuestionRepository mQuestionsRepository;
    private Handler mHandler = new Handler(Looper.getMainLooper()); // Отвечает за отчет времени, обязательно в основном потоке, поскольку он обновлят данные адаптера
    private int mQuestionNumber;

    public GameViewModel(QuestionRepository repository) {
        mQuestionsRepository = repository;
    }

    public void startGame() {
        mQuestionNumber = 0;
        appendToLog(GameLogAdapter.MSG_TYPE_BOT, "Начата новая викторина!");
        nextQuestion();
    }

    private void startTimer() {
        time.set(0);
        final long delay = TIME_TO_ANSWER * 10;
//        Log.d(TAG, "Start new timer, delay=" + delay + " ms");
        mHandler.postDelayed(new Runnable() {
             @Override
             public void run() {
                 if (time.get() > 100) {
                     appendToLog(GameLogAdapter.MSG_TYPE_BOT, "Время на ответ истекло");
                     nextQuestion();
                 } else {
                     time.set(time.get() + 1);
                     mHandler.postDelayed(this, delay);
                 }
             }
         }, delay);
    }

    private void stopTimer() {
//        Log.d(TAG, "Stop timer");
        mHandler.removeCallbacksAndMessages(null);
    }

    public void stopGame() {

    }

    public void checkAnswer(String guess) {
        appendToLog(GameLogAdapter.MSG_TYPE_USER, guess);
        if (TextUtils.equals(guess.trim(), answer.get())) { // TODO: надо еще хотябы игнорировать регистр букв
            score.set(score.get() + 3);
            appendToLog(GameLogAdapter.MSG_TYPE_BOT, "Правильно! Ответ был дан за " + time.get() + " секунд");
            nextQuestion();
        }
    }

    public void nextQuestion() {
        stopTimer();
        String task = mQuestionsRepository.getQuestion();
        String[] parts = task.split("\\*");
        String q = parts[0];
        question.set(q.substring(0, 1).toUpperCase() + q.substring(1));
        answer.set(parts[1]);
        appendToLog(GameLogAdapter.MSG_TYPE_BOT, String.format("Вопрос №%s", ++mQuestionNumber));
        startTimer();
    }

    @MainThread
    private void appendToLog(int msgType, CharSequence text) {
        DateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        messages.add(new GameLogAdapter.LogEntry(msgType, text, format.format(new Date())));
    }
}
