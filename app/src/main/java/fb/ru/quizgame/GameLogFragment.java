package fb.ru.quizgame;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    public static final SparseIntArray RESOURCES = new SparseIntArray();
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
        DateFormat format = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
        mAdapter.addEntry(new LogEntry(msgType, "[" + format.format(new Date()) + "] " + text));
    }

    static class LogEntry {

        int type;
        CharSequence text;

        LogEntry(int type, CharSequence text) {
            this.type = type;
            this.text = text;
        }
    }

    private static class GameLogAdapter extends RecyclerView.Adapter<GameLogAdapter.ViewHolder> {

        private static final String TAG = "GameLogAdapter";

        static class ViewHolder extends RecyclerView.ViewHolder {

            TextView mTextView;

            ViewHolder(TextView itemView) {
                super(itemView);
                mTextView = itemView;
            }
        }

        List<LogEntry> mData = new ArrayList<>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "ViewType: " + viewType);
            TextView view = (TextView) LayoutInflater.from(parent.getContext()).inflate(
                    RESOURCES.get(viewType), parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(mData.get(position).text);
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
