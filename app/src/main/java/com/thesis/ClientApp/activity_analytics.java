package com.thesis.ClientApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class activity_analytics extends AppCompatActivity {

    TextView Emergency, Electrical_complaints, Public_incidents, Road_complaints, Household;
    ArrayList postList;
    PieChart pieChart;
    int [] colorClass = new int[]{Color.LTGRAY, Color.BLUE, Color.CYAN, Color.DKGRAY, Color.rgb(255, 58, 186)};

    ArrayList dataVals = new ArrayList<>();
    ArrayList dataVals1 = new ArrayList<>();

    int emergency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        Emergency = findViewById(R.id.emergency);
        Electrical_complaints = findViewById(R.id.electric_complaints);
        Public_incidents = findViewById(R.id.public_incident);
        Road_complaints = findViewById(R.id.road_complaints);
        Household = findViewById(R.id.household);

        postList = new ArrayList<>();
        getEmergencyCount();

        postList = new ArrayList<>();
        getElectricalCount();

        postList = new ArrayList<>();
        getPublicIncidentsCount();

        postList = new ArrayList<>();
        getHouseholdComplaintsCount();

        postList = new ArrayList<>();
        getRoadComplaintsCount();

        pieChart = findViewById(R.id.pieChart);


        showPie();

    }

        private void showPie(){
        PieDataSet pieDataSet = new PieDataSet(chartData1(), "FixIt");
        pieDataSet.setColors(colorClass);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.invalidate();

    }

    private ArrayList<PieEntry> chartData1(){
        dataVals1 = dataVals;

        return dataVals1;
    }

    private ArrayList<PieEntry> chartData(int counts){
        dataVals.add(new PieEntry(counts));



        return dataVals;
    }


//    private ArrayList<PieEntry> chartData(int counts, String category){
//        if(category == "Emergency"){
//            dataVals.add(new PieEntry(counts));
//        }
//        if(category == "Electrical Complaints"){
//            dataVals.add(new PieEntry(counts));
//        }
//        if(category == "Public Incidents"){
//            dataVals.add(new PieEntry(counts));
//        }
//        if(category == "Household Concerns"){
//            dataVals.add(new PieEntry(counts));
//        }
//        if(category == "Road Complaints"){
//            dataVals.add(new PieEntry(counts));
//        }
//
//        return dataVals;
//    }




    private void getEmergencyCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("type").getValue(String.class).equals("Emergency")){
                        count += 1;
                    }
                }
                //chartData(count, "Emergency");
                chartData(count);
                Emergency.setText(String.valueOf(count));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
    }

    private void getElectricalCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("type").getValue(String.class).equals("Electrical Complaints")){
                        count += 1;
                    }
                }
                //chartData(count, "Electrical Complaints");
                chartData(count);
                Electrical_complaints.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
    }

    private void getPublicIncidentsCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("type").getValue(String.class).equals("Public Incidents")){
                        count += 1;
                    }
                }
                //chartData(count, "Public Incidents");
                chartData(count);
                Public_incidents.setText(String.valueOf(count));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
    }

    private void getHouseholdComplaintsCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("type").getValue(String.class).equals("Household Concerns")){
                        count += 1;
                    }
                }
                //chartData(count, "Household Concerns");
                chartData(count);
                Household.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        ref.addListenerForSingleValueEvent(valueEventListener);
    }

    private void getRoadComplaintsCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {


                    if (ds.child("type").getValue(String.class).equals("Road Complaints")){
                        count += 1;
                    }
                }
                //chartData(count, "Road Complaints");
                chartData(count);
                Road_complaints.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        ref.addListenerForSingleValueEvent(valueEventListener);
    }
}