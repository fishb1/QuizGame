package fb.ru.quizgame.gameboard;

import android.databinding.BindingAdapter;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Биндинги кастомных данных фрагмента игры.
 *
 * Created by leon on 28.12.17.
 */
public class GameFragmentBindings {

    @BindingAdapter("app:messages")
    public static void setMessages(View view, ObservableList<GameLogAdapter.LogEntry> messages) {
        GameLogAdapter adapter = (GameLogAdapter) ((RecyclerView) view).getAdapter();
        if (adapter != null) {
            adapter.setData(messages);
        }
    }
}
