package projetoi.meucarro;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import projetoi.meucarro.models.CarroUser;
import projetoi.meucarro.models.Gasto;

public class ExpensesReportActivity extends AppCompatActivity {

    private static final String EXPENSES_SUM = "Total";
    private static int MAX_DAYS = 55555;
    private static int DAYS_INTERVAL = 30;
    private static int X_LABEL_ANGLE = 45;

    private GraphView expenseGraph;
    private ArrayList<LineGraphSeries<DataPoint>> seriesByExpenses;
    private LineGraphSeries<DataPoint> seriesTotal;
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
                        initGraph();
                    }

                    else {
                        Toast.makeText(ExpensesReportActivity.this, R.string.erro_nenhum_gasto,
                                Toast.LENGTH_LONG).show();
                    }
                }

                else {
                    Toast.makeText(ExpensesReportActivity.this, R.string.msg_home_listacarrosvazia,
                            Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nome", "loadPost:onCancelled", databaseError.toException());
            }
        };

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.carrosUserRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid());
        this.carrosUserRef.addValueEventListener(carrosUserListener);

        this.expenseGraph = (GraphView) findViewById(R.id.expenseByDateGraphView);
    }

    private void initGraph() {
        this.expenseGraph.setTitle(getResources().getString(R.string.gascalculator_expense_by_time));
        this.expenseGraph.setTitleTextSize(50);
        this.expenseGraph.getGridLabelRenderer().setVerticalAxisTitle(getResources().getString(R.string.gascalculator_brazilian_currency_symbol));
        this.expenseGraph.getGridLabelRenderer().setHumanRounding(false);
        this.expenseGraph.getGridLabelRenderer().setHorizontalLabelsAngle(X_LABEL_ANGLE);

        // set date label formatter
        this.expenseGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this.expenseGraph.getContext()));
        this.expenseGraph.getGridLabelRenderer().setNumHorizontalLabels(this.currentCar.listaGastos.size() + 1);

        // set manual x bounds to have nice steps
        this.expenseGraph.getViewport().setXAxisBoundsManual(true);
        if (!this.currentCar.listaGastos.isEmpty()) {
            Date dt = this.currentCar.listaGastos.get(0).data;
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            c.add(Calendar.DATE, DAYS_INTERVAL); //30 days on X value each screen space
            Date dt2 = c.getTime();

            this.expenseGraph.getViewport().setMinX(dt.getTime());
            this.expenseGraph.getViewport().setMaxX(dt2.getTime());
        }

        // legend
        this.expenseGraph.getLegendRenderer().setVisible(true);
        this.expenseGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        // enables horizontal scrolling
        this.expenseGraph.getViewport().setScrollable(true);

        this.loadSeries();
    }

    private void loadSeries() {
        String[] typesOfExpenses = getResources().getStringArray(R.array.adicionardialog_gastosarray);
        String[] seriesColors = getResources().getStringArray(R.array.color_expense);

        // Initializing series
        this.seriesTotal = new LineGraphSeries<>(new DataPoint[]{});

        this.seriesTotal.setTitle(EXPENSES_SUM);
        this.seriesTotal.setAnimated(true);
        this.seriesTotal.setDrawBackground(true);
        this.seriesTotal.setDrawDataPoints(true);
        this.seriesTotal.setColor(Color.parseColor(seriesColors[0]));

        this.seriesByExpenses = new ArrayList<>();

        for (int serieIndex = 0; serieIndex < typesOfExpenses.length; serieIndex++) {
            LineGraphSeries<DataPoint> serie = new LineGraphSeries(new DataPoint[] {});

            //Setting attributes
            serie.setTitle(typesOfExpenses[serieIndex].toString());
            serie.setAnimated(true);
            //serie.setDrawBackground(true);
            serie.setDrawDataPoints(true);
            serie.setColor(Color.parseColor(seriesColors[serieIndex + 1]));

            this.seriesByExpenses.add(serie);
        }

        // Adding expenses to the series
        for (Gasto expense : this.currentCar.listaGastos) {
            DataPoint dp = new DataPoint(expense.data, expense.valor);

            this.seriesTotal.appendData(dp, true, MAX_DAYS);

            if (expense.descricao.equals(typesOfExpenses[0])) {
                this.seriesByExpenses.get(0).appendData(dp, true, MAX_DAYS);

            } else if (expense.descricao.equals(typesOfExpenses[1])) {
                this.seriesByExpenses.get(1).appendData(dp, true, MAX_DAYS);

            } else if (expense.descricao.equals(typesOfExpenses[2])) {
                this.seriesByExpenses.get(2).appendData(dp, true, MAX_DAYS);

            } else if (expense.descricao.equals(typesOfExpenses[3])) {
                this.seriesByExpenses.get(3).appendData(dp, true, MAX_DAYS);

            } else if (expense.descricao.equals(typesOfExpenses[4])) {
                this.seriesByExpenses.get(4).appendData(dp, true, MAX_DAYS);

            } else if (expense.descricao.equals(typesOfExpenses[5])) {
                this.seriesByExpenses.get(5).appendData(dp, true, MAX_DAYS);

            }
        }

        // Adding series to the graph
        this.expenseGraph.addSeries(this.seriesTotal);

        for (int serieIndex = 0; serieIndex < typesOfExpenses.length; serieIndex++) {
            this.expenseGraph.addSeries(this.seriesByExpenses.get(serieIndex));
        }

    }


}
