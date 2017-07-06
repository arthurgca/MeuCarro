package projetoi.meucarro;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import projetoi.meucarro.models.CarroUser;
import projetoi.meucarro.models.Gasto;

public class ExpensesReportActivity extends AppCompatActivity {

    private static int MAX = 366;

    private GraphView expenseGraph;
    private ArrayList<BarGraphSeries<DataPoint>> series;

    private String[] typeOfExpenses;
    private String[] yearsOfExpanses;

    private CarroUser currentCar;
    private DatabaseReference carrosUserRef;
    private ValueEventListener carrosUserListener;
    private String lastCarId;
    private FirebaseAuth mAuth;
    private ArrayList<CarroUser> userCarrosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarExpenseReport);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        carrosUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("lastCar").getValue() != null) {
                    lastCarId = dataSnapshot.child("lastCar").getValue().toString();
                    currentCar = dataSnapshot.child("carrosList").child(lastCarId).getValue(CarroUser.class);
                    typeOfExpenses = generateTypeOfExpanses();
                    yearsOfExpanses = generateYearsOfExpanses();

                    //itera sobre os nós da lista de carros
                    for (DataSnapshot dsCarro :  dataSnapshot.child("carrosList").getChildren()) {
                        //pega o carro e adiciona numa lista
                        CarroUser carro = dsCarro.getValue(CarroUser.class);
                        userCarrosList.add(carro);
                        Log.d("carro", carro.toString());
                    }

                    if (!currentCar.getListaGastos().isEmpty()) {
                        initGraph();
                    } else {
                        Toast.makeText(ExpensesReportActivity.this, R.string.erro_nenhum_gasto,
                                Toast.LENGTH_LONG).show();
                    }
                } else {
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

    private String[] generateTypeOfExpanses() {
        ArrayList<String> typesOfExpensesList = new ArrayList<>();

        typesOfExpensesList.add("Total");
        typesOfExpensesList.addAll(Arrays.asList(getResources().getStringArray(R.array.adicionardialog_gastosarray)));
        return typesOfExpensesList.toArray(new String[typesOfExpensesList.size()]);
    }

    private String[] generateYearsOfExpanses() {
        ArrayList<String> years = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        Date firstExpenseDate = this.currentCar.getListaGastos().get(0).getData();
        c.setTime(firstExpenseDate);
        int firstYear = c.get(Calendar.YEAR);

        for (int y = firstYear; y <= currentYear; y++) {
            years.add(Integer.toString(y));
        }

        return years.toArray(new String[years.size()]);
    }

    private void setLegendRenderer() {
        LegendRenderer legendRenderer = new LegendRenderer(this.expenseGraph);

        this.expenseGraph.setLegendRenderer(legendRenderer);
        this.expenseGraph.getLegendRenderer().setVisible(true);
        this.expenseGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    private void initGraph() {
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        this.setGridRenderer(0, 50);
        this.setXLabels("pt", "BR");
        this.setYLabels(10);
        this.setLegendRenderer();

        this.initSeries();
        this.loadSeries(thisYear);
    }

    private void initSeries() {
        String[] seriesColors = getResources().getStringArray(R.array.color_expense);
        this.series = new ArrayList<>();

        for (int serieIndex = 0; serieIndex < this.typeOfExpenses.length; serieIndex++) {
            BarGraphSeries serie = new BarGraphSeries<>(new DataPoint[]{});

            serie.setTitle(this.typeOfExpenses[serieIndex].toString());
            serie.setSpacing(10);
            serie.setAnimated(true);
            serie.setDrawValuesOnTop(true);
            serie.setColor(Color.parseColor(seriesColors[serieIndex]));
            serie.setValuesOnTopColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));

            this.series.add(serie);
        }
    }

    private void loadSeries(int year) {
        ArrayList<ArrayList<Gasto>> expensesByYear = this.currentCar.getExpensesByYear(year);

        for (int month = 0; month < expensesByYear.size(); month++) {
            ArrayList<Gasto> expensesMonth = expensesByYear.get(month);
            Double expensesSum = this.currentCar.calculateExpensesSum(expensesMonth);

            DataPoint dp = new DataPoint(month + 1, expensesSum);
            this.series.get(0).appendData(dp, true, MAX);

            for (int i = 1; i < this.typeOfExpenses.length; i++) {
                ArrayList<Gasto> expensesByType = this.currentCar.getExpensesByType(expensesMonth, this.typeOfExpenses[i]);
                Double expensesTypeSum = this.currentCar.calculateExpensesSum(expensesByType);
                DataPoint dpType = new DataPoint(month + 1, expensesTypeSum);
                this.series.get(i).appendData(dpType, true, MAX);
            }
        }
        this.expenseGraph.addSeries(series.get(0));
    }

    private void setGridRenderer(int labelAngle, int padding) {
        this.expenseGraph.getGridLabelRenderer().setVerticalAxisTitle(getResources().getString(R.string.gascalculator_brazilian_currency_symbol));
        this.expenseGraph.getGridLabelRenderer().setHumanRounding(false);
        this.expenseGraph.getGridLabelRenderer().setHorizontalLabelsAngle(labelAngle);
        this.expenseGraph.getGridLabelRenderer().setPadding(padding);

    }

    private void setXLabels(String language, String country) {
        String[] monthsNames = new DateFormatSymbols(new Locale(language, country)).getShortMonths();

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(this.expenseGraph);
        staticLabelsFormatter.setHorizontalLabels(monthsNames);
        this.expenseGraph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
    }

    private void setYLabels(int percentage) {
        double max = this.currentCar.calculateExpensesSum(this.currentCar.getListaGastos());
        this.expenseGraph.getViewport().setYAxisBoundsManual(true);
        this.expenseGraph.getViewport().setMinY(0);
        this.expenseGraph.getViewport().setMaxY(max + max/percentage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_expense_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.time_interval) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ExpensesReportActivity.this);
            mBuilder.setTitle(getResources().getString(R.string.choose_expense_year));

            mBuilder.setSingleChoiceItems(this.yearsOfExpanses, -1, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    expenseGraph.removeAllSeries();
                    series.clear();
                    initSeries();
                    loadSeries(Integer.parseInt(yearsOfExpanses[which]));
                }
            });

            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setLegendRenderer();
                }

            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();

        } else if (id == R.id.choose_type_of_expense) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ExpensesReportActivity.this);
            mBuilder.setTitle(getResources().getString(R.string.choose_expense_type));

            mBuilder.setSingleChoiceItems(this.typeOfExpenses, -1, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    expenseGraph.removeAllSeries();
                    expenseGraph.addSeries(series.get(which));
                }
            });

            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setLegendRenderer();
                }

            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
