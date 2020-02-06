package edu.utrack.activity.goals;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.utrack.R;
import edu.utrack.activity.TrackActivity;
import edu.utrack.goals.GoalType;
import edu.utrack.goals.archived.ArchivedGoal;

public class ActivityViewArchivedGoal extends TrackActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_archived_goal);

        setTitle("Archived Goal");

        ArchivedGoal goal = new Gson().fromJson(getIntent().getStringExtra("goal"), ArchivedGoal.class);

        FragmentViewGoal fragment = (FragmentViewGoal) getSupportFragmentManager().findFragmentById(R.id.viewArchivedGoalFragment);
        fragment.setArchived(true);
        fragment.setGoalType(goal.getType());

        fragment.reload(goal.getObjectives(), goal.getActivityData());

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        String dateText = format.format(new Date(goal.getBeginTime()));
        if(goal.getType() == GoalType.WEEKLY) {
            dateText += " - " + format.format(new Date(goal.getEndTime()));
        }

        ((TextView) findViewById(R.id.viewArchivedGoalDate)).setText(dateText);
    }

    @Override
    public TrackMenuType getMenuType() {
        return TrackMenuType.BACK;
    }
}
