package edu.utrack.activity.dataview;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utrack.R;
import edu.utrack.data.app.AppEvent;
import edu.utrack.data.screen.ScreenEvent;
import edu.utrack.data.screen.ScreenEventType;
import edu.utrack.util.AppUtils;

/**
 * Created by Tobi on 30/03/2018.
 */

public class FragmentDataGraphs extends DataViewFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data_graphs, container, false);
    }

    @Override
    public void updateUI(List<AppEvent> appEvents, Map<ScreenEventType, List<ScreenEvent>> screenEvents) {
        if(getActivity() == null || getView() == null) return;

        updatePieChart(appEvents);
        updateLineGraph(appEvents);
    }

    private void updateLineGraph(List<AppEvent> appEvents) {
        GraphView graph = getView().findViewById(R.id.graphsAppGraph);

        Collections.sort(appEvents, (e1, e2) -> Long.compare(e1.getStartTime(), e2.getStartTime()));

        long startTime = this.event.getStartTime();

        double totalTime = 0;

        List<DataPoint> points = new ArrayList<>();
        points.add(new DataPoint(0, 0));

        for(AppEvent event : appEvents) {
            float start = getGraphTime(event.getStartTime(this.event) - startTime);
            float end = getGraphTime(event.getEndTime(this.event) - startTime);
            float duration = getGraphDuration(event.getDuration(this.event));

            points.add(new DataPoint(start, totalTime));

            if(duration != 0) {
                totalTime += duration;
                points.add(new DataPoint(end, totalTime));
            }
        }
        Collections.sort(points, (p1, p2) -> Double.compare(p1.getX(), p2.getX()));

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        for(DataPoint point : points) {
            series.appendData(point, false, points.size() + 1);
        }
        series.appendData(new DataPoint(getGraphTime(event.getDuration()), totalTime), true, points.size() + 1);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(totalTime);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(getGraphTime(event.getDuration()));
        graph.addSeries(series);
    }

    private float getGraphDuration(long durationMillis) {
        return durationMillis / 60_000f;
    }
    private float getGraphTime(long timeMillis) {
        if(event.getDuration() >= (12 * 60 * 60 * 1000L)) return timeMillis / 3_600_000f;
        return timeMillis / 60_000f;
    }

    private void updatePieChart(List<AppEvent> appEvents) {
        PieChart chart = getView().findViewById(R.id.graphsPiChart);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5,10,5,5);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setDrawHoleEnabled(false);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawEntryLabels(false);

        int[] colors = new int[] {
            Color.rgb(46, 204, 113),
            Color.rgb(241, 196, 15),
            Color.rgb(52, 152, 219),
            Color.rgb(73, 59, 226),
            Color.rgb(226, 59, 210),
            Color.rgb(231, 76, 60)
        };

        List<PieEntry> data = new ArrayList<>();
        Map<String, Integer> appDurations = new HashMap<>();
        int numb = 0, endTotalTime = 0;

        for(AppEvent event : appEvents) {
            Integer intTime = appDurations.get(event.getApp().getPackageName());
            if(intTime == null) intTime = 0;

            intTime += (int) (event.getDuration(this.event) / 1000L);
            appDurations.put(event.getApp().getPackageName(), intTime);
        }

        for(Map.Entry<String, Integer> entry : appDurations.entrySet()) {
            if(numb >= colors.length - 1) {
                endTotalTime += entry.getValue();
            } else {
                String appName = AppUtils.getAppName(entry.getKey(), getActivity());
                data.add(new PieEntry(entry.getValue(), numb > 3 ? null : appName, getActivity()));
            }
            numb++;
        }
        if(endTotalTime > 0) data.add(new PieEntry(endTotalTime, "Other", getActivity()));

        PieDataSet dataSet = new PieDataSet(data,"");
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);
        dataSet.setDrawValues(true);
        PieData d = new PieData(dataSet);

        chart.setData(d);
    }
}
