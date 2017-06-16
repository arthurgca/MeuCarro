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
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import projetoi.meucarro.R;
import projetoi.meucarro.models.CarroUser;
import projetoi.meucarro.models.Gasto;

public class ExpensesDataActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_expenses_data);
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
                        Toast.makeText(ExpensesDataActivity.this, R.string.erro_nenhum_gasto,
                                Toast.LENGTH_LONG).show();
                    }
                }

                else {
                    Toast.makeText(ExpensesDataActivity.this, R.string.home_erro_nenhum_carro,
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
        graphView.setTitleTextSize(getResources().getDimension(R.dimen.header_4));
        graphView.getGridLabelRenderer().setVerticalAxisTitle(getResources().getString(R.string.brazilian_currency_symbol));

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {});

        for (Gasto gasto : this.currentCar.listaGastos) {
            series.appendData(new DataPoint(gasto.data, gasto.valor), true, MAX_DAYS);
        }

        series.setAnimated(true);
        graphView.addSeries(series);

        // set date label formatter
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graphView.getContext()));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(this.currentCar.listaGastos.size() + 1);

        // set manual x bounds to have nice steps
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(this.currentCar.listaGastos.get(0).data.getTime());
        graphView.getViewport().setMaxX(this.currentCar.listaGastos.get(this.currentCar.listaGastos.size() - 1).data.getTime());

        // enables horizontal scrolling
        graphView.getViewport().setScalable(true);
    }

}
