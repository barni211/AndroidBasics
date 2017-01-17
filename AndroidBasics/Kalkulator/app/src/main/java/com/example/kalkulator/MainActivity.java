package com.example.kalkulator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import static com.example.kalkulator.Operator.DIFF;
import static com.example.kalkulator.Operator.MULTIPLY;
import static com.example.kalkulator.Operator.QUO;
import static com.example.kalkulator.Operator.SUM;
import static org.apache.log4j.BasicConfigurator.configure;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.kalkulator.MESSAGE";
    private StringBuilder arithmeticExpression;
    private TextView screen;
    private Operator currOperator = null;
    private StringBuilder stringToSend;
    private Logger logger;
    private Boolean isFirstChar = false;
    private Boolean isOperatorSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        screen = (TextView) findViewById(R.id.textView);
        arithmeticExpression = new StringBuilder();
        stringToSend = new StringBuilder();

        configure();
        logger = Logger.getRootLogger();
        Logger.getRootLogger().setLevel(Level.ERROR);
    }


    public void buttonClick(View view) {

            Button b = (Button) view;
            char chars = (b.getText()).charAt(0);
            if (chars != '=' && currOperator == null) {
                setOperator(chars);
                arithmeticExpression.append(b.getText());
                isFirstChar=false;
            }
            else if (chars=='-' && isFirstChar==true){
                arithmeticExpression.append(b.getText());
                isFirstChar=false;
            }
            else if (chars == '=' && isOperatorSelected==true) {
                equal();
                isFirstChar=false;
            }
            else if (chars == '+' || chars == '-' || chars == '*' || chars == '/' && isFirstChar==false && isOperatorSelected==true ) {
                //equal();
                arithmeticExpression.append(chars);
                setOperator(chars);
                isFirstChar=false;
            }
            else {
                arithmeticExpression.append(b.getText());
                isFirstChar=false;
            }
            refreshTextView(view);
    }

    private void setOperator(char value) {
        switch (value) {
            case '+':
                currOperator = SUM;
                isOperatorSelected=true;
                break;
            case '-':
                if(isOperatorSelected==false && isFirstChar==false) {
                    currOperator = DIFF;
                    isOperatorSelected=true;
                }
                break;
            case '*':
                currOperator = MULTIPLY;
                isOperatorSelected=true;
                break;
            case '/':
                currOperator = QUO;
                isOperatorSelected=true;
                break;
        }
    }

    private void refreshTextView(View view) {
        screen.setText(arithmeticExpression);
    }

    private void equal() {
        Integer result = 0;
        String[] values;
        String expression = arithmeticExpression.toString();
        switch (currOperator) {
            case SUM:
                values = expression.split("\\+");
                try {
                    result = Integer.parseInt(values[0]) + Integer.parseInt(values[1]);
                }catch (Exception ex) {
                    Toast.makeText(MainActivity.this, "Nastąpił błąd podczas wykonywania operacji dodawania.", Toast.LENGTH_LONG).show();
                    logger.error(ex.toString() + " " + ex.getStackTrace());
                }
                finally {
                    break;
                }
            case DIFF:
                    values = expression.split("-");
                try {
                    if(expression.charAt(0)=='-')
                    {
                        result = Integer.parseInt(("-" + values[1])) - Integer.parseInt(values[2]);
                    }
                    else
                    {
                        result = Integer.parseInt(values[0]) - Integer.parseInt(values[1]);
                    }
                }
                catch (Exception ex) {
                    Toast.makeText(MainActivity.this, "Nastąpił błąd podczas wykonywania operacji odejmowania.", Toast.LENGTH_LONG).show();
                    logger.error(ex.toString() + " " + ex.getStackTrace());
                }
                finally {
                    break;
                }
            case MULTIPLY:
                values = expression.split("\\*");
                try {
                    result = Integer.parseInt(values[0]) * Integer.parseInt(values[1]);
                }catch (Exception ex) {
                    Toast.makeText(MainActivity.this, "Nastąpił błąd podczas wykonywania operacji mnożenia.", Toast.LENGTH_LONG).show();
                    logger.error(ex.toString() + " " + ex.getStackTrace());
                }
                finally {
                    break;
                }
            case QUO:
                values = expression.split("/");
                if (Integer.parseInt(values[1]) != 0) {
                    try {
                        result = Integer.parseInt(values[0]) / Integer.parseInt(values[1]);
                    }catch (Exception ex) {
                        Toast.makeText(MainActivity.this, "Nastąpił błąd podczas wykonywania operacji dzielenia.", Toast.LENGTH_LONG).show();
                        logger.error(ex.toString() + " " + ex.getStackTrace());
                    }
                    finally {
                        break;
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Błędna operacja, dzielenie przez zero.", Toast.LENGTH_LONG).show();
                }
                break;
        }

        arithmeticExpression.delete(0, arithmeticExpression.length());
        arithmeticExpression.append(result.toString());
        stringToSend.append(expression + "=" + result.toString() + "\n");
        currOperator = null;
        isOperatorSelected=false;
        refreshTextView(screen);
    }

    public void clear(View view) {
        arithmeticExpression.delete(0, arithmeticExpression.length());
        currOperator = null;
        isFirstChar=true;
        stringToSend.delete(0, stringToSend.length());
        refreshTextView(screen);
    }

    public void summary(View view) {
        Intent intent = new Intent(this, SummaryActivity.class);
        String message = stringToSend.toString();
        intent.putExtra(EXTRA_MESSAGE, message); //wysyłanie wiadomości do podrzednej activity
        startActivity(intent); //intent to tylko handler do ac
    }

}