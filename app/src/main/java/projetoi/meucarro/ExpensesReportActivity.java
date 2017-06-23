package projetoi.meucarro;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
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

    private String[] typesOfExpenses;
    private boolean[] checkedItens;

    private CarroUser currentCar;
    private DatabaseReference carrosUserRef;
    private ValueEventListener carrosUserListener;
    private String lastCarId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarExpenseReport);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //TODO

        mAuth = FirebaseAuth.getInstance();

        this.typesOfExpenses = getResources().getStringArray(R.array.adicionardialog_gastosarray);
        this.checkedItens = new boolean[this.typesOfExpenses.length];

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
        // Format date for X axis label
        String dayText = getResources().getString(R.string.all_day_text);
        String monthText = getResources().getString(R.string.all_month_text);
        String yearText = getResources().getString(R.string.all_year_text);

        String dateFormat = monthText + "/" + dayText + "/" + yearText;

        //this.expenseGraph.setTitle(getResources().getString(R.string.gascalculator_expense_by_time));
        //this.expenseGraph.setTitleTextSize(50);
        this.expenseGraph.getGridLabelRenderer().setVerticalAxisTitle(getResources().getString(R.string.gascalculator_brazilian_currency_symbol));
        this.expenseGraph.getGridLabelRenderer().setHorizontalAxisTitle(dateFormat);
        this.expenseGraph.getGridLabelRenderer().setHumanRounding(false);
        this.expenseGraph.getGridLabelRenderer().setHorizontalLabelsAngle(X_LABEL_ANGLE);
        this.expenseGraph.getGridLabelRenderer().setPadding(50);


        // set date label formatter
        this.expenseGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this.expenseGraph.getContext()));
        this.expenseGraph.getGridLabelRenderer().setNumHorizontalLabels(this.currentCar.listaGastos.size() + 1);

        // set manual x bounds to have nice steps
        this.expenseGraph.getViewport().setXAxisBoundsManual(true);
        if (!this.currentCar.listaGastos.isEmpty()) {
            int amountExpenses = this.currentCar.listaGastos.size();

            Date lastExpenseDate = this.currentCar.listaGastos.get(amountExpenses - 1).data;

            Calendar c = Calendar.getInstance();
            c.setTime(lastExpenseDate);

            Date maxDate = c.getTime();

            c.add(Calendar.DATE, -DAYS_INTERVAL); //30 days of space on X axis
            Date minDate = c.getTime();

            this.expenseGraph.getViewport().setMinX(minDate.getTime());
            this.expenseGraph.getViewport().setMaxX(maxDate.getTime());
        }

        // set legend
        this.expenseGraph.getLegendRenderer().setVisible(true);
        this.expenseGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        // enables horizontal scrolling
        this.expenseGraph.getViewport().setScrollable(true);

        this.loadSeries();
    }

    private void loadSeries() {
        String[] seriesColors = getResources().getStringArray(R.array.color_expense);

        // Initializing series
        this.seriesTotal = new LineGraphSeries<>(new DataPoint[]{});

        this.seriesTotal.setTitle(EXPENSES_SUM);
        this.seriesTotal.setAnimated(true);
        this.seriesTotal.setDrawBackground(true);
        this.seriesTotal.setDrawDataPoints(true);
        this.seriesTotal.setColor(Color.parseColor(seriesColors[0]));

        this.seriesByExpenses = new ArrayList<>();

        for (int serieIndex = 0; serieIndex < this.typesOfExpenses.length; serieIndex++) {
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

            for (int expenseIndex = 0; expenseIndex < this.typesOfExpenses.length; expenseIndex++) {
                if (expense.descricao.equals(typesOfExpenses[expenseIndex])) {
                    this.seriesByExpenses.get(expenseIndex).appendData(dp, true, MAX_DAYS);
                    break;
                }
            }
        }

        // Adding series to the graph
        this.expenseGraph.addSeries(this.seriesTotal);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_expense_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.time_interval) {

        } else if (id == R.id.choose_type_of_expense) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ExpensesReportActivity.this);
            mBuilder.setTitle(getResources().getString(R.string.choose_expense_type));
            mBuilder.setMultiChoiceItems(this.typesOfExpenses, this.checkedItens, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (isChecked) {
                        expenseGraph.addSeries(seriesByExpenses.get(which));
                    } else {
                        expenseGraph.removeSeries(seriesByExpenses.get(which));
                    }
                }
            });

            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LegendRenderer mLegendRenderer = new LegendRenderer(expenseGraph);

                    expenseGraph.setLegendRenderer(mLegendRenderer);
                    expenseGraph.getLegendRenderer().setVisible(true);
                    expenseGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                }

            });

            mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
