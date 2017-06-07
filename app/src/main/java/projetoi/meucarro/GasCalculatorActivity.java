package projetoi.meucarro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

        boolean isGasCheaper() {
            return this.gasPrice * 0.7 <= this.alcoholPrice;
        }
    }

    private EditText gasValue;
    private EditText alcoolValue;
    private TextView resultText;
    private Button calculateButton;
    private GasStation myGasStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_calculator);

        this.gasValue = (EditText) findViewById(R.id.gasEditText);
        this.alcoolValue = (EditText) findViewById(R.id.alcoolEditText);
        this.resultText = (TextView) findViewById(R.id.resultTextView);
        this.calculateButton = (Button) findViewById(R.id.buttonCalculate);

        this.myGasStation = new GasStation(0.0, 0.0);
        this.resultText.setText("");

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validField = true;

                try {
                    myGasStation.setGasPrice(Double.parseDouble(gasValue.getText().toString()));
                } catch (NumberFormatException e) {
                    gasValue.setError(getResources().getString(R.string.number_format_exception));
                    validField = false;
                }
                try {
                    myGasStation.setAlcoholPrice(Double.parseDouble(alcoolValue.getText().toString()));
                } catch (NumberFormatException e) {
                    alcoolValue.setError(getResources().getString(R.string.number_format_exception));
                    validField = false;
                }

                if (validField) {
                    if (myGasStation.isGasCheaper()) {
                        resultText.setText(getResources().getText(R.string.gas_text));
                    } else {
                        resultText.setText(getResources().getText(R.string.alcohol_text));
                    }
                }

            }
        });
    }

}
