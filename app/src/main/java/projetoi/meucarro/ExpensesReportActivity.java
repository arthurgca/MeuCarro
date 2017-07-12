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
import com.jjoe64.graphview.series.Series;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import projetoi.meucarro.models.Carro;
import projetoi.meucarro.models.Gasto;
import projetoi.meucarro.models.User;

public class ExpensesReportActivity extends AppCompatActivity {

    private static int MAX = 366;

    private GraphView expenseGraph;
    private ArrayList<BarGraphSeries<DataPoint>> series;
    private ArrayList<BarGraphSeries<DataPoint>> seriesToCompare;

    private String[] listOfCars;
    private String[] typeOfExpenses;
    private String[] yearsOfExpanses;

    private ArrayList<Carro> userCarrosList;
    private Carro currentCar;
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

        mAuth = FirebaseAuth.getInstance();

        carrosUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.currentCar() != null) {
                    currentCar = user.currentCar();
                    userCarrosList = new ArrayList<>();

                    //itera sobre os nós da lista de carros
                    for (Carro carro : user.cars) {
                        //pega o carro e adiciona numa lista
                        userCarrosList.add(carro);
                    }
                    listOfCars = generateCarID(userCarrosList);

                    if (!currentCar.estaSemGastos()) {
                        typeOfExpenses = generateTypeOfExpanses();
                        yearsOfExpanses = generateYearsOfExpanses();
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

    private String[] generateCarID(ArrayList<Carro> listOfCars) {
        ArrayList<String> cars = new ArrayList<>();
        for (Carro carUser : listOfCars) {
            cars.add(carUser.getModelo() + " " + carUser.getAno() + " " + carUser.getPlaca());
        }

        return cars.toArray(new String[cars.size()]);
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
        this.setGridRenderer();
        this.setLegendRenderer();
        this.initSeries();
        this.loadSeries(thisYear);
        this.setXLabels("pt", "BR");
        this.setYLabels(10);
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

    private void setGridRenderer() {
        this.expenseGraph.getGridLabelRenderer().setVerticalAxisTitle(getResources().getString(R.string.gascalculator_brazilian_currency_symbol));
        this.expenseGraph.getGridLabelRenderer().setHumanRounding(false);
        this.expenseGraph.getGridLabelRenderer().setHorizontalLabelsAngle(0);
        this.expenseGraph.getGridLabelRenderer().setPadding(50);
        this.expenseGraph.getGridLabelRenderer().setNumVerticalLabels(5);
    }

    private void setXLabels(String language, String country) {
        String[] monthsNames = new DateFormatSymbols(new Locale(language, country)).getShortMonths();

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(this.expenseGraph);
        staticLabelsFormatter.setHorizontalLabels(monthsNames);
        this.expenseGraph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
    }

    private Double maxExpense(GraphView graphView) {
        Double max = 0.0;
        for (Series series: graphView.getSeries()) {
            max = Math.max(series.getHighestValueY(), max);
        }

        return max;
    }

    private void setYLabels(int percentage) {
        double max = this.maxExpense(this.expenseGraph);

        this.expenseGraph.getViewport().setYAxisBoundsManual(true);
        this.expenseGraph.getViewport().setMinY(0);
        this.expenseGraph.getViewport().setMaxY(max + max/percentage);
        this.expenseGraph.getViewport().setDrawBorder(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_expense_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.choose_car) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ExpensesReportActivity.this);
            mBuilder.setTitle(getResources().getString(R.string.choose_expense_year));

            mBuilder.setSingleChoiceItems(this.listOfCars, -1, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int thisYear = Calendar.getInstance().get(Calendar.YEAR);
                    currentCar = userCarrosList.get(which);
                    yearsOfExpanses = generateYearsOfExpanses();
                    expenseGraph.removeAllSeries();
                    series.clear();
                    initSeries();
                    if (!currentCar.estaSemGastos()) {
                        loadSeries(thisYear);
                    }
                    setLegendRenderer();
                    setGridRenderer();
                    setYLabels(10);
                }
            });

            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }

            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();

        }else if (id == R.id.choose_time_interval) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ExpensesReportActivity.this);
            mBuilder.setTitle(getResources().getString(R.string.choose_expense_year));

            mBuilder.setSingleChoiceItems(this.yearsOfExpanses, -1, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    expenseGraph.removeAllSeries();
                    series.clear();
                    initSeries();
                    loadSeries(Integer.parseInt(yearsOfExpanses[which]));
                    setLegendRenderer();
                    setGridRenderer();
                    setYLabels(10);
                }
            });

            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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
                    setLegendRenderer();
                    setGridRenderer();
                }
            });

            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }

            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
