package projetoi.meucarro;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

public class GasCalculatorFragment extends Fragment {

    private class GasStation {
        private double gasPrice;
        private double alcoholPrice;

        GasStation(double gasPrice, double alcoholPrice) {
            this.gasPrice = gasPrice;
            this.alcoholPrice = alcoholPrice;
        }

        public double getGasPrice() { return this.gasPrice; }
        public double getAlcoholPrice() { return this.alcoholPrice; }

        void setGasPrice(double gasPrice) { this.gasPrice = gasPrice; }
        void setAlcoholPrice(double alcoolPrice) { this.alcoholPrice = alcoolPrice; }

        double getGasParameter() {
            return this.gasPrice * 0.7;
        }

        double getAlcoholParameter() {
            return this.alcoholPrice;
        }

        boolean isGasCheaper() {
            return this.getGasParameter() <= this.alcoholPrice;
        }

        double getGasEfficiency() {
            double fuel = (this.getGasParameter() + this.getAlcoholParameter());

            if (fuel <= 0.001) return 0.0;
            else return 100 - ((100 * this.getGasParameter()) / fuel);
        }

        double getAlcoholEfficiency() {
            double fuel = (this.getGasParameter() + this.getAlcoholParameter());

            if (fuel <= 0.001) return 0.0;
            else return 100 - ((100 * this.getAlcoholParameter()) / fuel);
        }
    }

    private EditText gasValue;
    private EditText alcoolValue;
    private Button calculateButton;
    private GasStation myGasStation;
    private GraphView graphView;
    private BarGraphSeries<DataPoint> series;
    private StaticLabelsFormatter staticLabelsFormatter;
    private Context act;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_gas_calculator, container, false);

        act = getActivity();

        this.gasValue = (EditText) rootView.findViewById(R.id.gasEditText);
        this.alcoolValue = (EditText) rootView.findViewById(R.id.alcoolEditText);
        this.calculateButton = (Button) rootView.findViewById(R.id.buttonCalculate);
        this.graphView = (GraphView) rootView.findViewById(R.id.gasCalculatorGraphView);

        this.myGasStation = new GasStation(0.000, 0.000);
        this.initGraph(this.graphView);

        this.calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validField = true;

                try {
                    double gasPrice = Double.parseDouble(gasValue.getText().toString());
                    myGasStation.setGasPrice(gasPrice);
                } catch (NumberFormatException e) {
                    gasValue.setError(getResources().getString(R.string.erro_formato_valor));
                    validField = false;
                }
                try {
                    double alcoholPrice = Double.parseDouble(alcoolValue.getText().toString());
                    myGasStation.setAlcoholPrice(alcoholPrice);
                } catch (NumberFormatException e) {
                    alcoolValue.setError(getResources().getString(R.string.erro_formato_valor));
                    validField = false;
                }

                if (validField) {
                    GasCalculatorFragment.this.setGraph(GasCalculatorFragment.this.graphView);
                }

            }
        });
        return rootView;
    }

    private void initGraph(GraphView graphView) {
        graphView.setTitle(getResources().getString(R.string.gascalculator_efficiency_text));
        graphView.setTitleTextSize(50);

        graphView.getGridLabelRenderer().setVerticalAxisTitle("%");

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0.0);
        graphView.getViewport().setMaxY(100.0);

        staticLabelsFormatter = new StaticLabelsFormatter(graphView);
        staticLabelsFormatter.setHorizontalLabels(new String[] {
                getResources().getText(R.string.all_gastext).toString(),
                getResources().getText(R.string.all_alchooltext).toString()});

        graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graphView.getGridLabelRenderer().setPadding(50);
    }

    private void setGraph(GraphView graphView) {
        graphView.removeAllSeries();
        series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(1, this.myGasStation.getGasEfficiency()),
                new DataPoint(2, this.myGasStation.getAlcoholEfficiency())
        });

        series.setAnimated(true);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(ContextCompat.getColor(act, R.color.colorBlack));
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                if (data.getX() == 1) {
                    return ContextCompat.getColor(act, R.color.colorGas);
                } else if (data.getX() == 2) {
                    return ContextCompat.getColor(act, R.color.colorAlcohol);
                } else {
                    return ContextCompat.getColor(act, R.color.colorBlack);
                }
            }
        });

        graphView.addSeries(series);
    }
}
