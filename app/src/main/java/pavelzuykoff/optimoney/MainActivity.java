package pavelzuykoff.optimoney;


import android.content.DialogInterface;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.DatePicker;
import android.widget.Spinner;

import android.widget.Toast;

import pavelzuykoff.optimoney.data.WorkWithDBContract;


public class MainActivity extends AppCompatActivity {


    private final String TAG = "happy";
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public String currency = "RUB";

    private int typeOfEntry = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fastInputDialog = (FloatingActionButton) findViewById(R.id.fab);
        fastInputDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();


                Log.d(TAG, "FAB onClick");
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
 
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Домой");
        adapter.addFragment(new InputDataFragment(), "Ввод");
        adapter.addFragment(new MoneyBoxFragment(), "Копилка");
        adapter.addFragment(new PeopleFragment(), "База");
        viewPager.setAdapter(adapter);



    }

    protected void showInputDialog() {
        String title = "Новая запись:";
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.fast_input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        DatePicker datePicker = (DatePicker) promptView.findViewById(R.id.datePicker);

        datePicker.setCalendarViewShown(false);
        datePicker.setSpinnersShown(true);

        CalendarValuesHandler date = new CalendarValuesHandler();

        datePicker.init(date.currentYear, date.currentMonth,
                date.currentDay, new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                        Toast.makeText(getApplicationContext(),
                                "onDateChanged", Toast.LENGTH_SHORT).show();


                    }
                });


        final Spinner typeSpinner = (Spinner) promptView.findViewById(R.id.typeSpinner);

        typeSpinner.setSelection(0);



        ArrayAdapter<?> typeSpinnerAdaptor =
                ArrayAdapter.createFromResource(MainActivity.this, R.array.types, android.R.layout.simple_spinner_item);
        typeSpinnerAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeSpinner.setAdapter(typeSpinnerAdaptor);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals(getString(R.string.type_income))){
                        typeOfEntry = WorkWithDBContract.MainTableEntry.TYPE_INCOME;
                        Log.d(TAG, "onItemSelected: " + typeOfEntry);
                    } else if (selection.equals(getString(R.string.type_spend))){
                        typeOfEntry = WorkWithDBContract.MainTableEntry.TYPE_SPEND;
                        Log.d(TAG, "onItemSelected: " + typeOfEntry);
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        alertDialogBuilder.setTitle(title);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "Dialog OK");
                        Toast.makeText(MainActivity.this, "Запись добавлена в БД.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void aboutOnClick(MenuItem item) {
        Log.d(TAG, "aboutOnClick: ");
    }

    public void settingsOnClick(MenuItem item) {
        Log.d(TAG, "settingsOnClick: ");
    }
}
