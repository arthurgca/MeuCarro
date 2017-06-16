package projetoi.meucarro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Date;

import projetoi.meucarro.models.CarroUser;
import projetoi.meucarro.models.Gasto;

public class ExpensesReportActivity extends AppCompatActivity {

    private static int MAX_DAYS = 99999;

    private GraphView expenseByDateGraph;
    private CarroUser currentCar;
    private DatabaseReference carrosUserRef;
    private ValueEventListener carrosUserListener;
    private String lastCarId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_report);
        mAuth = FirebaseAuth.getInstance();

        carrosUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("lastCar").getValue() != null) {
                    lastCarId = dataSnapshot.child("lastCar").getValue().toString();
                    currentCar = dataSnapshot.child("carrosList").child(lastCarId).getValue(CarroUser.class);

                    if (currentCar.listaGastos != null) {
                        initGraph(expenseByDateGraph);
                    }

                    else {
                        Toast.makeText(ExpensesReportActivity.this, R.string.erro_nenhum_gasto,
                                Toast.LENGTH_LONG).show();
                    }
                }

                else {
                    Toast.makeText(ExpensesReportActivity.this, R.string.home_erro_nenhum_carro,
                            Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nome", "loadPost:onCancelled", databaseError.toException());
            }
        };

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        carrosUserRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid());
        carrosUserRef.addValueEventListener(carrosUserListener);


        this.expenseByDateGraph = (GraphView) findViewById(R.id.expenseByDateGraphView);
    }

    private void initGraph(GraphView graphView) {
        graphView.setTitle(getResources().getString(R.string.expanse_by_time));
        graphView.setTitleTextSize(50);
        graphView.getGridLabelRenderer().setVerticalAxisTitle(getResources().getString(R.string.brazilian_currency_symbol));
        graphView.getGridLabelRenderer().setHumanRounding(false);
        graphView.getGridLabelRenderer().setHorizontalLabelsAngle(45);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {});

        for (Gasto gasto : this.currentCar.listaGastos) {
            series.appendData(new DataPoint(gasto.data, gasto.valor), true, MAX_DAYS);
        }

        series.setAnimated(true);
        series.setDrawBackground(true);
        series.setDrawDataPoints(true);
        graphView.addSeries(series);

        // set date label formatter
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graphView.getContext()));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(this.currentCar.listaGastos.size() + 1);

        // set manual x bounds to have nice steps
        graphView.getViewport().setXAxisBoundsManual(true);
        if (!this.currentCar.listaGastos.isEmpty()) {
            Date dt = this.currentCar.listaGastos.get(0).data;
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            c.add(Calendar.DATE, 30); //30 days on X value each screen space
            Date dt2 = c.getTime();

            graphView.getViewport().setMinX(dt.getTime());
            graphView.getViewport().setMaxX(dt2.getTime());
        }

        // enables horizontal scrolling
        graphView.getViewport().setScrollable(true);
    }

}
