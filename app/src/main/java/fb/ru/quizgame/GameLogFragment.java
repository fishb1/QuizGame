package fb.ru.quizgame;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 *
 * Created by leon on 19.12.17.
 */
public class GameLogFragment extends Fragment {

    public static final int MSG_TYPE_BOT = 0;
    public static final int MSG_TYPE_USER = 1;
    private static final SparseIntArray RESOURCES = new SparseIntArray();
    static {
        RESOURCES.put(MSG_TYPE_BOT, R.layout.item_log_bot);
        RESOURCES.put(MSG_TYPE_USER, R.layout.item_log_user);
    }

    private GameLogAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView mMainView = new RecyclerView(getContext(), null);
        mMainView.setHasFixedSize(true);
        mMainView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new GameLogAdapter();
        mMainView.setAdapter(mAdapter);
        return mMainView;
    }

    public void appendToLog(int msgType, CharSequence text) {
        DateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        mAdapter.addEntry(new LogEntry(msgType, text, format.format(new Date())));
    }

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

    private static class GameLogAdapter extends RecyclerView.Adapter<GameLogAdapter.BindingHolder> {

        static class BindingHolder extends RecyclerView.ViewHolder {

            ViewDataBinding mDataBinging;

            BindingHolder(View itemView) {
                super(itemView);
                mDataBinging = DataBindingUtil.bind(itemView);
            }
        }

        List<LogEntry> mData = new ArrayList<>();

        @Override
        public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(RESOURCES.get(viewType), parent, false);
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

        private void addEntry(LogEntry text) {
            mData.add(text);
            notifyDataSetChanged();
        }
    }
}
