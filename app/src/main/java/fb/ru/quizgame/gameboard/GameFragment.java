package fb.ru.quizgame.gameboard;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import fb.ru.quizgame.R;
import fb.ru.quizgame.databinding.GameFragmentBinding;
import fb.ru.quizgame.model.QuestionRepository;

/**
 *
 * Created by leon on 21.12.17.
 */
public class GameFragment extends Fragment {

    @BindView(R.id.rv_game_log) RecyclerView mGameLog;

    public GameFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        GameFragmentBinding mBinding = GameFragmentBinding.inflate(inflater, container, false);
        GameViewModel mViewModel = ViewModelProviders.of(getActivity(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new GameViewModel(new QuestionRepository(getActivity()));
            }
        }).get(GameViewModel.class);
        mBinding.setViewModel(mViewModel);
        ButterKnife.bind(this, mBinding.getRoot());
        // Меню настроек нужно включать вручную
        setHasOptionsMenu(true);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecycler();
    }
    /**
     * Настроить RecyclerView на фрагменте.
     */
    private void initRecycler() {
        mGameLog.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setStackFromEnd(true);
        mGameLog.setLayoutManager(lm);
        mGameLog.setAdapter(new GameLogAdapter());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.game_menu, menu);
    }
}
