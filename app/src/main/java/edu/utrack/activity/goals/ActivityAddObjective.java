package edu.utrack.activity.goals;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import edu.utrack.R;
import edu.utrack.activity.TrackActivity;
import edu.utrack.goals.GoalType;
import edu.utrack.goals.Objective;
import edu.utrack.goals.ObjectiveType;
import edu.utrack.goals.ObjectiveValueType;
import edu.utrack.goals.active.ActiveGoal;
import edu.utrack.goals.active.ActiveObjective;
import edu.utrack.goals.archived.ArchivedObjective;

public class ActivityAddObjective extends TrackActivity implements AdapterView.OnItemSelectedListener, TextWatcher {

    private Objective objective;
    private boolean archived;
    private GoalType goalType;

    private ObjectiveType type;
    private ObjectiveValueType valueType;

    private int typeSelectedCount, valueTypeSelectedCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String data = getIntent().getStringExtra("objective");
        if(data != null) {
            archived = getIntent().getBooleanExtra("archived", archived);
            objective = new Gson().fromJson(data, archived ? ArchivedObjective.class : ActiveObjective.class);
        } else {
            goalType = GoalType.fromId(getIntent().getIntExtra("type", 0));
        }

//        archived = false;
//        objective = new ArchivedObjective("Test", ObjectiveType.APP_TIME, ObjectiveValueType.PERCENTAGE, 23.45, 1);
//        objective = new ActiveObjective("Test", ObjectiveType.APP_TIME, ObjectiveValueType.PERCENTAGE, 23.45);

        setContentView(R.layout.objective_add);

        setTitle(objective == null ? "Add Objective" : "Objective Information");

        setData(findViewById(R.id.objective_add_type), ObjectiveType.getNamesList());
        setValues();

        ((Spinner) findViewById(R.id.objective_add_type)).setOnItemSelectedListener(this);
        ((Spinner) findViewById(R.id.objective_add_value_type)).setOnItemSelectedListener(this);
        ((TextView) findViewById(R.id.objective_add_name)).addTextChangedListener(this);
        ((TextView) findViewById(R.id.objective_add_value)).addTextChangedListener(this);

        findViewById(R.id.objective_add_button).setOnClickListener((v) -> {
            String name = ((TextView) findViewById(R.id.objective_add_name)).getText() + "";
            String strVal = ((TextView) findViewById(R.id.objective_add_value)).getText() + "";

            Number value = Double.parseDouble(strVal);
            if(valueType.getDataType() == int.class) value = Integer.parseInt(strVal);

            ActiveObjective objective = new ActiveObjective(name, type, valueType, value);
            ActiveGoal goal = goalType == GoalType.DAILY ? getGoalManager().getDailyGoal() : getGoalManager().getWeeklyGoal();
            goal.addObjective(objective);

            getGoalManager().save();

            finish();
        });
    }

    private void setValues() {
        if(objective == null) {
            type = ObjectiveType.APP_TIME;
            valueType = ObjectiveValueType.ABSOLUTE;
            updateObjectiveType();
        } else {
            type = objective.getType();
            valueType = objective.getValueType();

            updateObjectiveType();

            findViewById(R.id.objective_add_type).setEnabled(false);

            ((Spinner) findViewById(R.id.objective_add_value_type)).setSelection(type.getValueTypeNames().indexOf(valueType.getDescription()));
            findViewById(R.id.objective_add_value_type).setEnabled(false);

            ((EditText) findViewById(R.id.objective_add_value)).setText(objective.getValue() + "");
            findViewById(R.id.objective_add_value).setEnabled(false);

            ((EditText) findViewById(R.id.objective_add_name)).setText(objective.getDescription() + "");
            findViewById(R.id.objective_add_name).setEnabled(false);

            findViewById(R.id.objective_add_button).setVisibility(Button.GONE);
        }
    }

    private void validateInput() {
        findViewById(R.id.objective_add_button).setEnabled(false);

        String name = ((TextView) findViewById(R.id.objective_add_name)).getText() + "";
        if(name.isEmpty()) return;

        Class<? extends Number> typeClass = valueType.getDataType();
        String strVal = ((TextView) findViewById(R.id.objective_add_value)).getText() + "";
        double val = -1;

        try {
            val = Double.parseDouble(strVal);
        } catch(NumberFormatException e) {
            return;
        }
        if(val < 0) return;

        if(val >= 100 && valueType == ObjectiveValueType.PERCENTAGE) return;
        if(typeClass == int.class && val != (int) val) return;

        findViewById(R.id.objective_add_button).setEnabled(true);
    }

    private void updateObjectiveType() {
        Spinner spinner = findViewById(R.id.objective_add_value_type);
        setData(spinner, type.getValueTypeNames());

        updateObjectiveValueType();
    }

    private void updateObjectiveValueType() {
        String symbol = valueType.getSymbol();
        ((TextView) findViewById(R.id.objective_add_value_type_name)).setText(symbol);
        validateInput();
    }

    private void setData(Spinner spinner, List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent == findViewById(R.id.objective_add_type)) {
            if(typeSelectedCount++ == 0) return;

            ObjectiveType newType = ObjectiveType.values()[position];
            if(newType == type) return;

            type = newType;
            valueType = type.getValueTypes()[0];
            updateObjectiveType();
        } else if(parent == findViewById(R.id.objective_add_value_type)) {
            if(valueTypeSelectedCount++ == 0) return;

            ObjectiveValueType newValueType = type.getValueTypes()[position];
            if(newValueType == valueType) return;
            valueType = newValueType;
            updateObjectiveValueType();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        validateInput();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void getMenuItems(Map<Integer, Runnable> menus) { }

    @Override
    public TrackMenuType getMenuType() {
        return TrackMenuType.BACK;
    }
}
