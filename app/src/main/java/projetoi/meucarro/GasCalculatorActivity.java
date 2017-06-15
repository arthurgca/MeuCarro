package projetoi.meucarro;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import projetoi.meucarro.R;

public class GasCalculatorActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_calculator);

        this.gasValue = (EditText) findViewById(R.id.gasEditText);
        this.alcoolValue = (EditText) findViewById(R.id.alcoolEditText);
        this.calculateButton = (Button) findViewById(R.id.buttonCalculate);
        this.graphView = (GraphView) findViewById(R.id.gasCalculatorGraph);

        this.myGasStation = new GasStation(0.000, 0.000);

        this.graphView.setTitle(getResources().getString(R.string.efficiency_text));
        this.graphView.setTitleTextSize(getResources().getDimension(R.dimen.header_4));

        this.graphView.getGridLabelRenderer().setVerticalAxisTitle("%");

        this.graphView.getViewport().setYAxisBoundsManual(true);
        this.graphView.getViewport().setMinY(0.0);
        this.graphView.getViewport().setMaxY(100.0);

        this.staticLabelsFormatter = new StaticLabelsFormatter(graphView);
        this.staticLabelsFormatter.setHorizontalLabels(new String[] {
                getResources().getText(R.string.gas_text).toString(),
                getResources().getText(R.string.alcohol_text).toString()});

        this.graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        this.graphView.getGridLabelRenderer().setPadding(50);

        this.calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validField = true;

                try {
                    double gasPrice = Double.parseDouble(gasValue.getText().toString());
                    myGasStation.setGasPrice(gasPrice);
                } catch (NumberFormatException e) {
                    gasValue.setError(getResources().getString(R.string.number_format_exception));
                    validField = false;
                }
                try {
                    double alcoholPrice = Double.parseDouble(alcoolValue.getText().toString());
                    myGasStation.setAlcoholPrice(alcoholPrice);
                } catch (NumberFormatException e) {
                    alcoolValue.setError(getResources().getString(R.string.number_format_exception));
                    validField = false;
                }

                if (validField) {
                    graphView.removeAllSeries();
                    series = new BarGraphSeries<>(new DataPoint[] {
                            new DataPoint(1, myGasStation.getGasEfficiency()),
                            new DataPoint(2, myGasStation.getAlcoholEfficiency())
                    });

                    series.setAnimated(true);
                    series.setSpacing(0);
                    series.setDrawValuesOnTop(true);
                    series.setValuesOnTopColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                    series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                        @Override
                        public int get(DataPoint data) {
                            if (data.getX() == 1) {
                                return ContextCompat.getColor(getApplicationContext(), R.color.colorGas);
                            } else if (data.getX() == 2) {
                                return ContextCompat.getColor(getApplicationContext(), R.color.colorAlcohol);
                            } else {
                                return ContextCompat.getColor(getApplicationContext(), R.color.colorBlack);
                            }
                        }
                    });
                    graphView.addSeries(series);

                }

            }
        });
    }

}
