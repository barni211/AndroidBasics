package dbapp.com.example.dbapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    //EditText editName, editAge, editTextId;
    //Button btnAdd, btnDelete, btnviewAll, btnUpdate;
    public final static String EXTRA_MESSAGE = "dbapp.com.example.dbapp.MESSAGE";
    protected static String language = "";
    public static final String FORCE_LOCAL = "force_local";
    public static final String FORCE_CURRENCY = "force_currency";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeLanguage(this, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);
        createApplication();
    }

    public void createApplication()
    {
        final Button btnHistory= new Button(this);
        btnHistory.setId(Integer.parseInt("200"));
        btnHistory.setText(R.string.history);
        btnHistory.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnHistory.setHeight(60);
        btnHistory.setWidth(60);
        btnHistory.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor hist = myDb.getHistory();
                if (hist.getCount() == 0) {
                    Toast.makeText(MainActivity.this, "Nie wykonano żadnych operacji", Toast.LENGTH_LONG).show();
                } else {
                    StringBuffer historyString = new StringBuffer();
                    while (hist.moveToNext()) {
                        historyString.append("Operation: " + hist.getString(1) + "\n");
                        historyString.append("Name: " + hist.getString(2) + "\n");
                        historyString.append("Surname: " + hist.getString(4) + "\n");
                        historyString.append("Age: " + hist.getString(3) + "\n\n");

                    }
                    showMessage("History", historyString.toString());
                }
            }
        }
        );

        final Button languageButton = new Button(this);
        languageButton.setId(Integer.parseInt("200"));
        languageButton.setText(R.string.language);
        languageButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        languageButton.setHeight(60);
        languageButton.setWidth(60);
        languageButton.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (languageButton.getText().equals("PL")) {
                    language = "pl";
                } else if (languageButton.getText().equals("EN")) {
                    language = "en";
                }
                changeLanguage(MainActivity.this, language);
                onResume();
            }
        });


        final Button btnDodaj = new Button(this);
        btnDodaj.setId(Integer.parseInt("100"));
        btnDodaj.setText(R.string.add);
        btnDodaj.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnDodaj.setHeight(60);
        btnDodaj.setWidth(60);
        btnDodaj.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder message = new StringBuilder();
                message.append("dodawanie/");
                message.append((btnDodaj.getId()));
                sendMessage(v, message.toString());
            }
        }

        );

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(languageButton);
        layout.addView(btnHistory);
        layout.addView(btnDodaj);
        setContentView(layout);

        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            // show message
            Toast.makeText(MainActivity.this, "Brak danych", Toast.LENGTH_LONG).show();
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {

            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            buffer.append("Id :" + res.getString(0) + "\n");
            buffer.append("Name :" + res.getString(1) + "\n");
            buffer.append("Surname: " + res.getString(2) + "\n");
            buffer.append("Age :" + res.getString(3) + "\n");
            buffer.append("\n");


            TextView textView = new TextView(this);
            textView.setId(Integer.parseInt(res.getString(0)));
            textView.setTextSize(35);
            textView.setText(res.getString(1) + " " + res.getString(2) + " " + res.getString(3));
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));


            final Button btnEdytuj = new Button(this);
            btnEdytuj.setId(Integer.parseInt(res.getString(0)) + 10);
            btnEdytuj.setText(R.string.edit);
            btnEdytuj.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnEdytuj.setHeight(60);
            btnEdytuj.setWidth(60);
            btnEdytuj.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StringBuilder message = new StringBuilder();
                            message.append("edycja/");
                            message.append((btnEdytuj.getId() - 10));
                            sendMessage(v, message.toString());
                        }
                    }

            );

            final Button btnUsun = new Button(this);
            btnUsun.setId(Integer.parseInt(res.getString(0)) + 20);
            btnUsun.setText(R.string.delete);
            btnUsun.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnUsun.setHeight(60);
            btnUsun.setWidth(60);
            btnUsun.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String id = String.valueOf(btnUsun.getId() - 20);
                            TextView tv = (TextView) findViewById(Integer.parseInt(id));
                            String values[] = tv.getText().toString().split(" ");
                            Integer deletedRows = myDb.deleteData(id);
                            if (deletedRows > 0) {
                                myDb.addToHistory("Delete", values[0], values[1], values[2]);
                                Toast.makeText(MainActivity.this, "Usunięto dane", Toast.LENGTH_LONG).show();
                                refresh(v);
                            } else
                                Toast.makeText(MainActivity.this, "Nie udało się usunąć danych", Toast.LENGTH_LONG).show();
                        }
                    }
            );

            layout.addView(row);

            layout.addView(textView);
            layout.addView(btnEdytuj);
            layout.addView(btnUsun);
        }


        setContentView(layout);

    }

    public void sendMessage(View view, String mess) {
        Intent intent = new Intent(this, Edycja.class);
        String message = mess;
        intent.putExtra(EXTRA_MESSAGE, message); //wysyłanie wiadomości do podrzednej activity
        startActivity(intent); //intent to tylko handler do activity, więc musimy wywołać metode start activity / wywołanie metody onCreate()
    }

    public void refresh(View view) {
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        this.finish();
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }



    public void changeLanguage(Context context, String lang) {
       // Toast.makeText(MainActivity.this, "Wszedłem", Toast.LENGTH_LONG).show();
        Configuration configuration = new Configuration();
        SharedPreferences force_pref = PreferenceManager.getDefaultSharedPreferences(context);
        String language = force_pref.getString(FORCE_LOCAL, "");

        if(TextUtils.isEmpty(language)&&lang==null){
            configuration.locale = Locale.getDefault();

            SharedPreferences.Editor edit = force_pref.edit();
            String tmp="";
            tmp=Locale.getDefault().toString().substring(0, 2);

            edit.putString(FORCE_LOCAL, tmp);
            edit.commit();

        } else if(lang!=null){
            configuration.locale = new Locale(lang);
            SharedPreferences.Editor edit = force_pref.edit();
            edit.putString(FORCE_LOCAL, lang);
            edit.commit();

        } else if(!TextUtils.isEmpty(language)){
            configuration.locale = new Locale(language);
        }

        context.getResources().updateConfiguration(configuration, null);

    }

    @Override
    protected void onResume() {
        super.onResume();
        createApplication();

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        SharedPreferences force_pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext().getApplicationContext());

        String language = force_pref.getString(FORCE_LOCAL, "");

        super.onConfigurationChanged(newConfig);
        //changeLanguage(this, language);
    }


}


