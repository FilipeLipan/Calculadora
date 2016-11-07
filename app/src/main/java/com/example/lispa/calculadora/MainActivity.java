package com.example.lispa.calculadora;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.evgenii.jsevaluator.JsEvaluator;
import com.evgenii.jsevaluator.interfaces.JsCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    private static final String PREFS_FILES = "com.example.lispa.calculadora";
    private static final String TEXT_RESULT = "text_result";
    private static final String TEXT_HIST = "text_hist";

    @BindView(R.id.textResult)    TextView mTextResult;
    @BindView(R.id.textCalc)    TextView mTextHist;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //starting sharedPreferences
        mSharedPreferences = getSharedPreferences(PREFS_FILES, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        //recovering shared preferences , setting up the text views
        mTextResult.setText(mSharedPreferences.getString(TEXT_RESULT, ""));
        mTextHist.setText(mSharedPreferences.getString(TEXT_HIST, ""));

    }

    @Override
    protected void onPause() {
        super.onPause();

        //saving the values into sharedPreferences
        mEditor.putString(TEXT_RESULT, mTextResult.getText().toString());
        mEditor.putString(TEXT_HIST, mTextHist.getText().toString());
        mEditor.apply();
    }

    //this method add's a number to the result display
    @OnClick({R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5,
            R.id.button6, R.id.button7, R.id.button8, R.id.button9, R.id.buttonPoint})
    public void number(Button b) {

        // getting the result text
        String count = mTextResult.getText().toString();

        //this don't let the user input a point before putting a number
        if (!(b.getText().toString().equals(".") && count.length() == 0)) {

            //TODO: make the app allow one point per number
            mTextResult.setText(mTextResult.getText().toString() + b.getText().toString());
        }
    }

    //this method add's an operator to the result display
    @OnClick({R.id.buttonDivision, R.id.buttonMinus, R.id.buttonSum, R.id.buttonMult})
    public void operator(Button b) {

        //TODO: make a code that don't let the user put a operator in final position of the string

        String string = mTextResult.getText().toString();

        boolean lastIsOperator = false;

        if ((b.getText().toString().equals("/") || b.getText().toString().equals("X")) && string.length() == 0) {

        } else {
            if (string.length() > 0) {
                char c = string.charAt(string.length() - 1);
                if (c == '-' || c == '+' || c == '/' || c == 'X') {
                    string = string.substring(0, string.length() - 1);
                    string += b.getText();
                    mTextResult.setText(string);
                    lastIsOperator = true;
                }
            }

            if (!lastIsOperator) {
                mTextResult.setText(mTextResult.getText().toString() + b.getText().toString());
            }
        }
    }

    //this method execute evaluate the value into the display, it takes the string and show a value
    @OnClick(R.id.buttonEqual)
    public void doCalc() {
        //create the evaluator, this variable comes from the library JsEvaluator, it is the same as
        //the class ScriptEngineManager in java. Documentation: https://github.com/evgenyneu/js-evaluator-for-android
        JsEvaluator evaluator = new JsEvaluator(this);

        // getting the text in the display
        String count = mTextResult.getText().toString();
        //setting the historic with the text in the display
        mTextHist.setText(count);

        //change all the X with *, the evaluator don't see X but * as a valid operator
        count = count.replaceAll("X", "*");

        //this method calculates a string, see more information in the documentation: https://github.com/evgenyneu/js-evaluator-for-android
        evaluator.evaluate(count, new JsCallback() {
            @Override
            public void onResult(String s) {
                // on Result return NaN if the user tries to divide for 0 and undefined if the user
                //tries to make a crazy calculation, so this code will show the user that something
                //is wrong
                if (s.equals("NaN") || s.equals("undefined")) {
                    mTextHist.setText(R.string.resultadoIndefinido);
                } else {
                    mTextResult.setText(s);
                }
            }
        });
    }

    //clear the display
    @OnClick(R.id.buttonC)
    public void clearResult(Button b) {
        mTextResult.setText("");
    }

    //clear the display
    @OnClick(R.id.buttonCE)
    public void clearAll(Button b) {
        mTextHist.setText("");
        mTextResult.setText("");
    }

    //delete the last char in the display
    @OnClick(R.id.buttonDelete)
    public void delete() {
        String count = mTextResult.getText().toString();
        if (count != null && count.length() > 0) {
            count = count.substring(0, count.length() - 1);
            mTextResult.setText(count);
        }
    }



}
