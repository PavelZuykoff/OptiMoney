package pavelzuykoff.optimoney;


import android.content.ContentValues;
import android.content.DialogInterface;

import android.database.sqlite.SQLiteDatabase;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;

import pavelzuykoff.optimoney.data.OptimoneyDbContract;
import pavelzuykoff.optimoney.data.OptimoneyDbHelper;

import static pavelzuykoff.optimoney.R.id.noteText;


public class MainActivity extends AppCompatActivity {

    //константы для тостов
    static final String ADDED_TO_DB = "Данные внесены в БД";
    static final String NOTHING_TO_ADD = "Данные не введены";
    static final String DATE_UPDATED = "Дата установлена";
    static final String CANSELED = "Операция отменена";
    static final String WRONG_DATE = "Целевая дата не может быть меньше или равна текущей!";


    static final String TAG = "happy";

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    //Статический объект для работы с датой
    protected static final DateFactory DATE = new DateFactory();

    //константы текущий год месяц день (месяцы начинаются с 0)
    protected static final int CURRENT_DAY = DATE.currentDay;
    protected static final int CURRENT_MONTH = DATE.currentMonth;
    protected static final int CURRENT_YEAR = DATE.currentYear;






    protected static String currency = "₽";
    //TODO сделать подстановку типа валюты в окно ввода суммы.

    protected static OptimoneyDbHelper mDbHelper;


    //переменные для БД
    protected static long dateUnixFomat;
    protected static double sum = 0;
    protected static int typeOfEntry = OptimoneyDbContract.MainTableEntry.TYPE_INCOME;
    protected static int subTypeOfEntry = OptimoneyDbContract.MainTableEntry.SUBTYPE_OTHER;
    protected static String note = "";
    protected static long modificationTimeUnixFormat;
    protected static int deletedEntry = OptimoneyDbContract.MainTableEntry.DELETED_FALSE;
    protected static int synchronizedEntry = OptimoneyDbContract.MainTableEntry.SYNCHRONIZED_FALSE;
    protected static String userName = "default_user";
    protected static String deviceName = "default_device";


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
                fastInputDialog();
//                Log.d(TAG, "FAB onClick");
            }
        });

        mDbHelper = new OptimoneyDbHelper(this);

        Log.d(TAG, "current date: " + CURRENT_DAY + CURRENT_MONTH + CURRENT_YEAR);

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
        adapter.addFragment(new DbFragment(), "База");
        viewPager.setAdapter(adapter);


    }

    protected void fastInputDialog() {

        String title = "Новая запись:";

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.fast_input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText sumET = (EditText) promptView.findViewById(R.id.sum);
        final EditText noteET = (EditText) promptView.findViewById(noteText);


        DatePicker datePicker = (DatePicker) promptView.findViewById(R.id.datePicker);

        datePicker.setCalendarViewShown(false);
        datePicker.setSpinnersShown(true);

        datePicker.init(CURRENT_YEAR, CURRENT_MONTH,
                CURRENT_DAY, new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int month, int day) {

                        int chosenDay = day;
                        int chosenMonth = month;
                        int chosenYear = year;

                        Log.d(TAG, "onDateChanged: " + day + " " + month + " " + year);

                        try {
                            dateUnixFomat = new DateFactory().getDateUnixFormat(chosenDay, chosenMonth, chosenYear);
                        } catch (ParseException e) {
                            dateUnixFomat = 0;
                            e.printStackTrace();
                        }
                        Log.d(TAG, "unixDate: " + dateUnixFomat);
                        Toast.makeText(getApplicationContext(), DATE_UPDATED, Toast.LENGTH_SHORT).show();


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

                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.type_income))) {
                        typeOfEntry = OptimoneyDbContract.MainTableEntry.TYPE_INCOME;
                        Log.d(TAG, "onItemSelected: " + typeOfEntry);
                    } else if (selection.equals(getString(R.string.type_spend))) {
                        typeOfEntry = OptimoneyDbContract.MainTableEntry.TYPE_SPEND;
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
                        String noteFromFastInput;
                        Double sumFromFastInput;

                        if (sumET.length() > 0) {

                            sumFromFastInput = Double.parseDouble(sumET.getText().toString());

                            if (noteET.length() > 0) {
                                noteFromFastInput = noteET.getText().toString().trim();
                            } else {
                                noteFromFastInput = "";
                            }

                            insertNewEntry(sumFromFastInput, noteFromFastInput);

                        } else {
                            sum = 0;
                            Toast.makeText(MainActivity.this, NOTHING_TO_ADD, Toast.LENGTH_SHORT).show();
                        }

                        resetToDefaults();

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                resetToDefaults();
                                dialog.cancel();
                                Toast.makeText(MainActivity.this, CANSELED, Toast.LENGTH_SHORT).show();

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

    private void insertNewEntry(double sumFromFastInput, String noteFromFastInput) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();


        // подготовка данных к вставке

        //dateUnixFomat задана текущей датой по умолчанию или выбрана в календаре

        sum = sumFromFastInput;

        note = noteFromFastInput;

        //typeOfEntry выбран спиннером

        //subTypeOfEntry по умолчанию


        modificationTimeUnixFormat = System.currentTimeMillis() / 1000;

        //deleted entry по умолчанию 0

        //syncronized entry по умолчанию 0

        //userName = default;

        //deviceName = default;

        Log.d(MainActivity.TAG, "дата: " + dateUnixFomat
                + " сумма: " + sum
                + " Тип: " + typeOfEntry
                + " Подтип: " + subTypeOfEntry
                + " заметка: " + note
                + " мод: " + modificationTimeUnixFormat
                + " удалена: " + deletedEntry
                + " синхронизирована: " + synchronizedEntry
                + " пользователь: " + userName
                + " девайс: " + deviceName);

        values.put(OptimoneyDbContract.MainTableEntry.COLUMN_DATE, dateUnixFomat);
        values.put(OptimoneyDbContract.MainTableEntry.COLUMN_SUM, sum);
        values.put(OptimoneyDbContract.MainTableEntry.COLUMN_TYPE, typeOfEntry);
        values.put(OptimoneyDbContract.MainTableEntry.COLUMN_SUBTYPE, subTypeOfEntry);
        values.put(OptimoneyDbContract.MainTableEntry.COLUMN_COMMENT, note);
        values.put(OptimoneyDbContract.MainTableEntry.COLUMN_MODIFICATION_TIME, modificationTimeUnixFormat);
        values.put(OptimoneyDbContract.MainTableEntry.COLUMN_DELETED, deletedEntry);
        values.put(OptimoneyDbContract.MainTableEntry.COLUMN_SYNCRONIZED, synchronizedEntry);
        values.put(OptimoneyDbContract.MainTableEntry.COLUMN_USER, userName);
        values.put(OptimoneyDbContract.MainTableEntry.COLUMN_DEVICE, deviceName);

        long newRowId = db.insert(OptimoneyDbContract.MainTableEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            // Если ID  -1, значит произошла ошибка
            Toast.makeText(MainActivity.this, "ОШИБКА!", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(MainActivity.this, ADDED_TO_DB, Toast.LENGTH_SHORT).show();


    }

    public static void resetToDefaults() {
//переменные для БД
        dateUnixFomat = 0;
        sum = 0;
        typeOfEntry = OptimoneyDbContract.MainTableEntry.TYPE_INCOME;
        subTypeOfEntry = OptimoneyDbContract.MainTableEntry.SUBTYPE_OTHER;
        note = "";
        modificationTimeUnixFormat = 0;
        deletedEntry = OptimoneyDbContract.MainTableEntry.DELETED_FALSE;
        synchronizedEntry = OptimoneyDbContract.MainTableEntry.SYNCHRONIZED_FALSE;
        userName = "default_user";
        deviceName = "default_device";
    }
}
