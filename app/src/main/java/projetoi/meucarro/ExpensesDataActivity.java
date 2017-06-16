package projetoi.meucarro;

import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_data);

        //BEGIN TEST !!!
        // generate Dates
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d3 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d4 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d5 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d6 = calendar.getTime();

        //create expenses
        List<Gasto> gastos = new ArrayList<Gasto>();
        gastos.add(new Gasto("a", d1, 10));
        gastos.add(new Gasto("b", d2, 60));
        gastos.add(new Gasto("c", d3, 25));
        gastos.add(new Gasto("d", d4, 80));
        gastos.add(new Gasto("e", d5, 88));
        gastos.add(new Gasto("f", d6, 15));

        this.currentCar = new CarroUser("modelo", "ano", "placa", 100, gastos);
        //END TEST !!!

        this.expenseByDateGraph = (GraphView) findViewById(R.id.expenseByDateGraphView);
        this.initGraph(expenseByDateGraph);
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
