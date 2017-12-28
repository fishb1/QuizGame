package fb.ru.quizgame.gameboard;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fb.ru.quizgame.BR;
import fb.ru.quizgame.R;

/**
 * Адаптер данных для RecyclerView на фрагменте игры.
 *
 * Created by leon on 28.12.17.
 */
public class GameLogAdapter extends RecyclerView.Adapter<GameLogAdapter.BindingHolder> {
    // Типы сообщений
    static final int MSG_TYPE_BOT = 0;
    static final int MSG_TYPE_USER = 1;
    // Соответствие типа сообщения и его макета
    private static final SparseIntArray LAYOUTS = new SparseIntArray();
    static {
        LAYOUTS.put(MSG_TYPE_BOT, R.layout.game_log_bot_item);
        LAYOUTS.put(MSG_TYPE_USER, R.layout.game_log_user_item);
    }
    // Обозреватель событий изменения данных (сообщает RecyclerView об изменениях при получении событий из ObservableList)
    private final ObservableList.OnListChangedCallback<ObservableList<LogEntry>> mOnListChangedListener = new ObservableList.OnListChangedCallback<ObservableList<LogEntry>>() {
        @Override
        public void onChanged(ObservableList sender) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {

            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart, itemCount);
        }
    };

    private ObservableList<LogEntry> mData;

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(LAYOUTS.get(viewType), parent, false);
        return new BindingHolder(view);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        holder.mDataBinging.setVariable(BR.entry, mData.get(position));
        holder.mDataBinging.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }
    /**
     * Установить данные. Вызывается из BindingAdapter, чтобы восстановить данные этому адаптеру.
     * Сам список хранится в GameModelView.
     *
     * @param data список данных
     */
    public void setData(ObservableList<LogEntry> data) {
        mData = data;
        mData.addOnListChangedCallback(mOnListChangedListener);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mData.removeOnListChangedCallback(mOnListChangedListener);
    }

    static class BindingHolder extends RecyclerView.ViewHolder {

        ViewDataBinding mDataBinging;

        BindingHolder(View itemView) {
            super(itemView);
            mDataBinging = DataBindingUtil.bind(itemView);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class LogEntry {

        public final int type;
        public final CharSequence text;
        public final String time;

        LogEntry(int type, CharSequence text, String time) {
            this.type = type;
            this.text = text;
            this.time = time;
        }
    }
}
