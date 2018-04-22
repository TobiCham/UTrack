package edu.utrack.activity.goals;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.Space;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.utrack.R;
import edu.utrack.activity.TrackActivity;
import edu.utrack.goals.GoalManager;
import edu.utrack.goals.GoalType;
import edu.utrack.goals.archived.ArchivedGoal;
import edu.utrack.util.AppUtils;

public class FragmentArchivedGoals extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.archived_goals, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reload();
    }

    public void reload() {
        if(getActivity() == null || getView() == null) return;

        GoalManager manager = ((TrackActivity) getActivity()).getGoalManager();

        List<ArchivedGoal> dailyGoals = new ArrayList<>();
        List<ArchivedGoal> weeklyGoals = new ArrayList<>();

        for(ArchivedGoal goal : manager.getArchivedGoals()) {
            if(goal.getType() == GoalType.DAILY) dailyGoals.add(goal);
            else weeklyGoals.add(goal);
        }

        if(dailyGoals.isEmpty() && weeklyGoals.isEmpty()) {
            this.<TextView>getView(R.id.archivedGoalsMessage).setText("No archived goals to show");
            return;
        }

        Collections.sort(dailyGoals, (g1, g2) -> Long.compare(g1.getBeginTime(), g2.getEndTime()));
        Collections.sort(weeklyGoals, (g1, g2) -> Long.compare(g1.getBeginTime(), g2.getEndTime()));

        LinearLayout layout = getView(R.id.archivedGoalsList);
        layout.removeAllViews();

        for(ArchivedGoal goal : dailyGoals) layout.addView(createLayout(goal));
        if(!dailyGoals.isEmpty() && !weeklyGoals.isEmpty()) {
            Space space = new Space(getActivity());
            space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, AppUtils.getPaddingPX(getActivity(), 5)));
            layout.addView(space);
        }
        for(ArchivedGoal goal : weeklyGoals) layout.addView(createLayout(goal));

        this.<TextView>getView(R.id.archivedGoalsMessage).setText("Click on a goal to show more information");
    }

    private LinearLayout createLayout(ArchivedGoal goal) {
        LinearLayout layout = new LinearLayout(getActivity());
        int dp = AppUtils.getPaddingPX(getActivity(), 8);
        layout.setPadding(dp, dp, dp, dp);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setBackgroundColor(getColor(goal));

        TextView typeView = createText(goal.getType().getName());
        ((LinearLayout.LayoutParams) typeView.getLayoutParams()).gravity = Gravity.LEFT;
        ((LinearLayout.LayoutParams) typeView.getLayoutParams()).weight = 1;
        typeView.setTypeface(Typeface.DEFAULT_BOLD);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        String dateText = format.format(new Date(goal.getBeginTime()));
        if(goal.getType() == GoalType.WEEKLY) {
            dateText += " - " + format.format(new Date(goal.getEndTime()));
        }

        TextView dateView = createText(dateText);
        ((LinearLayout.LayoutParams) typeView.getLayoutParams()).gravity = Gravity.RIGHT;

        layout.addView(typeView);
        layout.addView(dateView);

        layout.setOnClickListener((v) -> {
            Intent intent = new Intent(getActivity(), ActivityViewArchivedGoal.class);
            intent.putExtra("goal", new Gson().toJson(goal));
            getActivity().startActivity(intent);
        });

        return layout;
    }

    private TextView createText(String text) {
        TextView view = new TextView(getActivity());
        view.setText(text);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.setTextColor(Color.WHITE);
        return view;
    }

    private int getColor(ArchivedGoal goal) {

        int state = goal.getCompletionState();
        if(state == 0) return Color.rgb(64, 64, 64);
        if(state > 0) return Color.rgb(0, 174, 24);
        return Color.rgb(176, 24, 0);
    }

    private <T extends View> T getView(int id) {
        return (T) getView().findViewById(id);
    }
}
