package edu.utrack.activity.goals;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;

import edu.utrack.R;
import edu.utrack.activity.TrackActivity;
import edu.utrack.goals.Goal;
import edu.utrack.goals.GoalActivityData;
import edu.utrack.goals.GoalManager;
import edu.utrack.goals.GoalType;
import edu.utrack.goals.Objective;
import edu.utrack.goals.active.ActiveGoal;
import edu.utrack.goals.active.ActiveObjective;
import edu.utrack.goals.archived.ArchivedObjective;
import edu.utrack.util.AppUtils;
import edu.utrack.util.TimeUtils;

public class FragmentViewGoal extends Fragment {

    private GoalType goalType;
    private boolean archived;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goal_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getView().findViewById(R.id.goalViewAddObjective).setOnClickListener((v) -> {
            Intent intent = new Intent(getActivity(), ActivityAddObjective.class);
            intent.putExtra("type", goalType.getId());
            getActivity().startActivity(intent);
        });
    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    public GoalType getGoalType() {
        return goalType;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public boolean isArchived() {
        return archived;
    }

    public void reload(List<? extends Objective> objectives, GoalActivityData data) {
        if(getActivity() == null || getView() == null) return;

        this.<Button>getView(R.id.goalViewAddObjective).setVisibility(archived ? Button.GONE : Button.VISIBLE);
        this.<TextView>getView(R.id.goalViewProgressMessage).setText(archived ? "Goal Statistics" : "Current Progress");

        this.<TextView>getView(R.id.goalViewTotalAppTime).setText(TimeUtils.formatTimeShort(data.getTotalAppTime()));
        this.<TextView>getView(R.id.goalViewUniqueApps).setText(data.getUniqueApps() + "");

        this.<TextView>getView(R.id.goalViewPercentageAppTime).setText(data.getPercentageAppTime() < 0 ? "N/A" : String.format(Locale.getDefault(), "%.2f", data.getPercentageAppTime()) + "%");
        this.<TextView>getView(R.id.goalViewScreenOnsPerHour).setText(data.getScreenOnsPerHour() < 0 ? "N/A" : String.format(Locale.getDefault(), "%.1f", data.getScreenOnsPerHour()));
        this.<TextView>getView(R.id.goalViewScreenOns).setText(data.getScreenOns() + "");

        GoalManager manager = ((TrackActivity) getActivity()).getGoalManager();
        Goal goal = manager.getGoal(goalType);

        LinearLayout layout = this.<LinearLayout>getView(R.id.goalViewObjectives);
        layout.removeAllViews();

        if(objectives.isEmpty()) {
            this.<TextView>getView(R.id.goalViewMessage).setText("You have no objectives for this event");
        } else {
            for(Objective objective : objectives) {
                layout.addView(createLayout(objective, data));
            }
            this.<TextView>getView(R.id.goalViewMessage).setText("Click an objective for more information");
        }
    }

    private LinearLayout createLayout(Objective objective, GoalActivityData data) {
        LinearLayout layout = new LinearLayout(getActivity());
        int dp = AppUtils.getPaddingPX(getActivity(), 8);
        layout.setPadding(dp, dp, dp, dp);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setBackgroundColor(getColor(objective, data));

        TextView typeView = createText(objective.getDescription());
        ((LinearLayout.LayoutParams) typeView.getLayoutParams()).gravity = Gravity.LEFT;
        ((LinearLayout.LayoutParams) typeView.getLayoutParams()).weight = 1;
        typeView.setTypeface(Typeface.DEFAULT_BOLD);

        layout.addView(typeView);

        if(!archived) layout.addView(createDeleteButton(objective));

        layout.setOnClickListener((v) -> {
            Intent intent = new Intent(getActivity(), ActivityAddObjective.class);
            intent.putExtra("archived", archived);
            intent.putExtra("objective", new Gson().toJson(objective));
            getActivity().startActivity(intent);
        });

        return layout;
    }

    private ImageView createDeleteButton(Objective objective) {
        ImageView view = new ImageView(getActivity());
        view.setImageResource(R.drawable.ic_delete_white_24dp);

        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ((LinearLayout.LayoutParams) view.getLayoutParams()).gravity = Gravity.RIGHT;

        view.setClickable(true);
        view.setOnClickListener((v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Delete Objective");
            builder.setMessage("Do you really want to delete objective '" + objective.getDescription() + "'?");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton(android.R.string.yes, (d, w) -> {
                GoalManager manager = ((TrackActivity) getActivity()).getGoalManager();
                ActiveGoal goal = manager.getGoal(goalType);
                goal.getObjectives().remove(objective);
                manager.save();

                ((ActivityGoals) getActivity()).getReloader().reload();
            });
            builder.setNegativeButton(android.R.string.no, null);
            builder.show();
        });
        return view;
    }

    private TextView createText(String text) {
        TextView view = new TextView(getActivity());
        view.setText(text);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.setTextColor(Color.WHITE);
        ((LinearLayout.LayoutParams) view.getLayoutParams()).gravity = Gravity.CENTER_VERTICAL;
        return view;
    }

    private int getColor(Objective objective, GoalActivityData activityData) {
        int state = 0;
        if(objective instanceof ArchivedObjective) state = ((ArchivedObjective) objective).getCompletedState();
        else state = ((ActiveObjective) objective).getCompletionState(activityData);

        if(state == 0) return Color.rgb(64, 64, 64);
        if(state < 0) return Color.rgb(176, 24, 0);
        return Color.rgb(0, 174, 24);
    }

    private <T extends View> T getView(int id) {
        return (T) getView().findViewById(id);
    }
}
