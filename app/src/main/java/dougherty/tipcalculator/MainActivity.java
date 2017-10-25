package dougherty.tipcalculator;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.TextView.OnEditorActionListener;
import android.view.View.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements OnEditorActionListener, OnClickListener {

    // define member variables for the widgets
    private TextView percentTV;
    private TextView tipPercentTV;
    private TextView totalTV;
    private EditText billET;
    private Button percentUpButton;
    private Button percentDownButton;
    private Button resetButton;

    // define instance variable
    private String billAmountString = "";
    private float tipPercent = .15f;
    private SharedPreferences savedValues;

    private static final String TAG = "TipCalculator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get reference to the widget
        percentTV = (TextView) findViewById(R.id.percentTV);
        tipPercentTV = (TextView) findViewById(R.id.tipPercentTV);
        totalTV = (TextView) findViewById(R.id.totalTV);
        billET = (EditText) findViewById(R.id.billET);
        percentUpButton = (Button) findViewById(R.id.percentUpButton);
        percentDownButton = (Button) findViewById(R.id.percentDownButton);
        resetButton = (Button) findViewById(R.id.resetButton);

        // set Listeners
        billET.setOnEditorActionListener(this);
        percentUpButton.setOnClickListener(this);
        percentDownButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        // get the Shared Preferences
        savedValues = getSharedPreferences("SavedValues",MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.percentUpButton:
                tipPercent = tipPercent + .01f;
                calculateAndDisplay();
                break;
            case R.id.percentDownButton:
                tipPercent = tipPercent - .01f;
                calculateAndDisplay();
                break;
            case R.id.resetButton:
                tipPercent = .15f;
                billET.setText("");
                calculateAndDisplay();
                break;
        }

    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if(actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_UNSPECIFIED){

            calculateAndDisplay();
        }

        Toast.makeText(getApplicationContext(), "Action ID: " + actionId, Toast.LENGTH_LONG).show();

        return false;
    }

    private void calculateAndDisplay() {

        // get the bill amount
        billAmountString = billET.getText().toString();
        float billAmount;

        if (billAmountString.equals("")) {
            billAmount = 0;
        } else {
            billAmount = Float.parseFloat(billAmountString);
        }

        Log.d(TAG, "Bill Amount: " + billAmount);
        // calculate tip and total
        float tipAmount = billAmount * tipPercent;
        float totalAmount = billAmount + tipAmount;

        // display the formatted results
        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTV.setText(percent.format(tipPercent));

        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipPercentTV.setText(currency.format(tipAmount));
        totalTV.setText(currency.format(totalAmount));
    }

    @Override
    protected void onPause() {
        // save the instance variables
        Editor editor = savedValues.edit();
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.apply();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // get the instance variables
        billET.setText("");
        billAmountString = savedValues.getString("billAmountString", "");
        tipPercent = savedValues.getFloat("tipPercent", .15f);

        // set the bill amount on its widget
        billET.setText(billAmountString);

        // calculate and display method
        calculateAndDisplay();
    }
}