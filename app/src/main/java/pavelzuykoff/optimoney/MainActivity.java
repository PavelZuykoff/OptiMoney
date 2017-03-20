package pavelzuykoff.optimoney;


import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;


import pavelzuykoff.optimoney.data.OptimoneyDbHelper;

import static pavelzuykoff.optimoney.data.OptimoneyDbContract.MainTableEntry.*;

import static pavelzuykoff.optimoney.R.id.noteText;


public class MainActivity extends AppCompatActivity {

    //константы для тостов
    public static final String DATA_ADDED_TO_DB = "Данные внесены в БД";
    public static final String DATA_NOT_ADDED = "Данные не введены";
    public static final String DATA_ERROR = "Ошибка в данных.";
    public static final String DATE_UPDATED = "Дата установлена";
    public static final String CANSELED = "Операция отменена";
    public static final String WRONG_DATE = "Целевая дата не может быть меньше или равна текущей!";
    public static final String SETTINGS_SAVED = "Настройки сохранены.";


    //Стартовые настройки
    public static final String TAG = "happy";
    public static final String APP_PREFERENCES = "Main_settings";
    public static final String APP_PREFERENCES_FIRST_LAUNCH = "First_launch"; //boolean
    public static final String APP_PREFERENCES_AVERAGE_MONTHLY_INCOME = "Average_monthly_income";
    public static final String APP_PREFERENCES_CONSIDER_ADDITIONAL_INCOME = "Consider_additional_income"; //boolean
    public static final String APP_PREFERENCES_MANDATORY_PAYMENTS = "Mandatory_payments";
    public static final String APP_PREFERENCES_MONTHLY_PAYMENTS_COMPLETELY_PAID = "MP_completely_paid"; //boolean
    public static final String APP_PREFERENCES_CREDIT_LIMIT = "Credit_limit";


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
    protected static int typeOfEntry = TYPE_SPEND;
    protected static int subTypeOfEntry = 0;
    protected static String note = "";
    protected static long modificationTimeUnixFormat;
    protected static int deletedEntry = DELETED_FALSE;
    protected static int synchronizedEntry = SYNCHRONIZED_FALSE;
    protected static String userName = "default_user";
    protected static String deviceName = "default_device";

    //переменные для настроек
    protected SharedPreferences mainPreferences;
    protected static boolean appFirstLaunch = true;
    protected static float averageMonthlyIncome = 0;
    protected static boolean considerAdditionalIncome = false;
    protected static float mandatoryPayments = 0;
    protected static boolean monthlyPaymentsCompletlyPayd = false;
    protected static float creditLimit = 0;

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
            }
        });

        mDbHelper = new OptimoneyDbHelper(this);

//        Log.d(TAG, "current date: " + CURRENT_DAY + CURRENT_MONTH + CURRENT_YEAR);


        mainPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        getSavedPreferences();

        Log.d(TAG, "after loading pref appFirstLaunch: " + appFirstLaunch
                + " averageMonthlyIncome: " + averageMonthlyIncome
                + " considerAdditionalIncome: " + considerAdditionalIncome
                + " mandatoryPayments: " + mandatoryPayments
                + " monthlyPaymentsCompletlyPayd: " + monthlyPaymentsCompletlyPayd
                + " creditLimit: " + creditLimit);


        if (appFirstLaunch) {
            firstLaunchDialog();
        }


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
        final View promptView = layoutInflater.inflate(R.layout.dialog_fast_input, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText sumET = (EditText) promptView.findViewById(R.id.sum);
        final EditText noteET = (EditText) promptView.findViewById(noteText);
        final TextView subTypeLegend = (TextView) promptView.findViewById(R.id.subTypeLegend);


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
//                        Toast.makeText(getApplicationContext(), DATE_UPDATED, Toast.LENGTH_SHORT).show();
                    }
                });


        final Spinner typeSpinner = (Spinner) promptView.findViewById(R.id.typeSpinner);
        final Spinner subTypeSpinner = (Spinner) promptView.findViewById(R.id.subTypeSpinner);

        ArrayAdapter<?> typeSpinnerAdaptor =
                ArrayAdapter.createFromResource(MainActivity.this, R.array.types, android.R.layout.simple_spinner_item);
        typeSpinnerAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<?> incomeSpinnerAdaptor =
                ArrayAdapter.createFromResource(MainActivity.this, R.array.income_types, android.R.layout.simple_spinner_item);
        incomeSpinnerAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<?> spendSpinnerAdaptor =
                ArrayAdapter.createFromResource(MainActivity.this, R.array.spend_types, android.R.layout.simple_spinner_item);
        spendSpinnerAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        typeSpinner.setAdapter(typeSpinnerAdaptor);


        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                String selection = (String) parent.getItemAtPosition(position);

                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.type_income))) {
                        typeOfEntry = TYPE_INCOME;
                        subTypeLegend.setText("Вид дохода:");
                        subTypeSpinner.setAdapter(incomeSpinnerAdaptor);
                        Log.d(TAG, "onItemSelected: " + typeOfEntry);
                    } else if (selection.equals(getString(R.string.type_spend))) {
                        typeOfEntry = TYPE_SPEND;
                        subTypeLegend.setText("Вид расхода:");
                        subTypeSpinner.setAdapter(spendSpinnerAdaptor);
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
                        int spinnerPos = subTypeSpinner.getSelectedItemPosition();
                        Boolean checkPassed = inspectSubTypeSpinner(spinnerPos);

                        if (sumET.length() > 0 && checkPassed) {

                            sumFromFastInput = Double.parseDouble(sumET.getText().toString());

                            if (noteET.length() > 0) {
                                noteFromFastInput = noteET.getText().toString().trim();
                            } else {
                                noteFromFastInput = "";
                            }

                            insertNewEntry(sumFromFastInput, noteFromFastInput);
                            Toast.makeText(MainActivity.this, DATA_ADDED_TO_DB, Toast.LENGTH_SHORT).show();

                        } else {
                            sum = 0;
                            Toast.makeText(MainActivity.this, DATA_ERROR + " " + DATA_NOT_ADDED, Toast.LENGTH_SHORT).show();
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

    public void mainSettingsOnClick(MenuItem item) {
        setAppPreferencesDialog();
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

        //subTypeOfEntry выбран спиннером


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

        values.put(COLUMN_DATE, dateUnixFomat);
        values.put(COLUMN_SUM, sum);
        values.put(COLUMN_TYPE, typeOfEntry);
        values.put(COLUMN_SUBTYPE, subTypeOfEntry);
        values.put(COLUMN_COMMENT, note);
        values.put(COLUMN_MODIFICATION_TIME, modificationTimeUnixFormat);
        values.put(COLUMN_DELETED, deletedEntry);
        values.put(COLUMN_SYNCRONIZED, synchronizedEntry);
        values.put(COLUMN_USER, userName);
        values.put(COLUMN_DEVICE, deviceName);

        long newRowId = db.insert(TABLE_NAME, null, values);

        if (newRowId == -1) {
            // Если ID  -1, значит произошла ошибка
            Toast.makeText(MainActivity.this, "ОШИБКА!", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(MainActivity.this, DATA_ADDED_TO_DB, Toast.LENGTH_SHORT).show();


    }

    public static void resetToDefaults() {
//переменные для БД
        dateUnixFomat = 0;
        sum = 0;
        typeOfEntry = TYPE_SPEND;
        subTypeOfEntry = SUBTYPE_INCOME_START;
        note = "";
        modificationTimeUnixFormat = 0;
        deletedEntry = DELETED_FALSE;
        synchronizedEntry = SYNCHRONIZED_FALSE;
        userName = "default_user";
        deviceName = "default_device";
    }

    //установка подтипа
    public static boolean inspectSubTypeSpinner(int spinnerPosition) {
        Boolean result = true;

        if (typeOfEntry == TYPE_INCOME) {
            switch (spinnerPosition) {
                case 0:
                    subTypeOfEntry = SUBTYPE_INCOME_START;
                    break;
                case 1:
                    subTypeOfEntry = SUBTYPE_INCOME_SALARY;
                    break;
                case 2:
                    subTypeOfEntry = SUBTYPE_INCOME_ADDITIONAL;
                    break;
                default: {
                    result = false;
                    Log.d(TAG, "ОШИБКА В ВЫБОРЕ ПОДТИПА ДОХОДА");
                }
                break;

            }

        }
        if (typeOfEntry == TYPE_SPEND) {
            switch (spinnerPosition) {
                case 0:
                    subTypeOfEntry = SUBTYPE_SPEND_MANDATORY_PAYMENTS;
                    break;
                case 1:
                    subTypeOfEntry = SUBTYPE_SPEND_FOOD;
                    break;
                case 2:
                    subTypeOfEntry = SUBTYPE_SPEND_ALCO;
                    break;
                case 3:
                    subTypeOfEntry = SUBTYPE_SPEND_HEALTH;
                    break;
                case 4:
                    subTypeOfEntry = SUBTYPE_SPEND_ENTERTAINMENT;
                    break;
                case 5:
                    subTypeOfEntry = SUBTYPE_SPEND_TRANSPORT;
                    break;
                case 6:
                    subTypeOfEntry = SUBTYPE_SPEND_FINE;
                    break;
                case 7:
                    subTypeOfEntry = SUBTYPE_SPEND_CLOTHES;
                    break;
                case 8:
                    subTypeOfEntry = SUBTYPE_SPEND_ELECTRONICS;
                    break;
                case 9:
                    subTypeOfEntry = SUBTYPE_SPEND_INTERIOR;
                    break;
                case 10:
                    subTypeOfEntry = SUBTYPE_SPEND_CHARITY;
                    break;
                case 11:
                    subTypeOfEntry = SUBTYPE_SPEND_OTHER;
                    break;
                default: {
                    result = false;
                    Log.d(TAG, "ОШИБКА В ВЫБОРЕ ПОДТИПА РАСХОДА");
                }
                break;
            }

        }


        Log.d(TAG, "subTypeSpinner" + subTypeOfEntry);

        return result;

    }

    protected void setAppPreferencesDialog() {

        String title = "Основные настройки:";

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.dialog_main_settings, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText averageMonthlyIncomeET = (EditText) promptView.findViewById(R.id.average_monthly_income);
        final Switch considerAdditionalIncomeS = (Switch) promptView.findViewById(R.id.consider_additional_income);
        final EditText mandatoryPaymentsET = (EditText) promptView.findViewById(R.id.mandatory_payments);
        final EditText creditLimitET = (EditText) promptView.findViewById(R.id.credit_limit);
        mandatoryPaymentsET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mandatoryPaymentsET.setText(String.valueOf(mandatoryPayments + " " + currency));
            }
        });

        averageMonthlyIncomeET.setText(String.valueOf(averageMonthlyIncome + " " + currency));
        considerAdditionalIncomeS.setChecked(considerAdditionalIncome);
        mandatoryPaymentsET.setText(String.valueOf(mandatoryPayments + " " + currency));
        creditLimitET.setText(String.valueOf(creditLimit + " " + currency));


        final SharedPreferences.Editor editor = mainPreferences.edit();


        alertDialogBuilder.setTitle(title);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        averageMonthlyIncome = Float.parseFloat(averageMonthlyIncomeET.getText().toString());
                        considerAdditionalIncome = Boolean.parseBoolean(considerAdditionalIncomeS.toString());
                        mandatoryPayments = Float.parseFloat(mandatoryPaymentsET.getText().toString());
                        creditLimit = Float.parseFloat(creditLimitET.getText().toString());

                        editor.putBoolean(APP_PREFERENCES_FIRST_LAUNCH, appFirstLaunch)
                                .putFloat(APP_PREFERENCES_AVERAGE_MONTHLY_INCOME, averageMonthlyIncome)
                                .putBoolean(APP_PREFERENCES_CONSIDER_ADDITIONAL_INCOME, considerAdditionalIncome)
                                .putFloat(APP_PREFERENCES_MANDATORY_PAYMENTS, mandatoryPayments)
                                .putBoolean(APP_PREFERENCES_MONTHLY_PAYMENTS_COMPLETELY_PAID, monthlyPaymentsCompletlyPayd)
                                .putFloat(APP_PREFERENCES_CREDIT_LIMIT, creditLimit);
                        editor.apply();

                        Log.d(TAG, " appFirstLaunch " + appFirstLaunch);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
//                                averageMonthlyIncome = 0D;
//                                considerAdditionalIncome = false;
//                                mandatoryPayments = 0D;
//                                creditLimit = 0D;
                                dialog.cancel();
                                Toast.makeText(MainActivity.this, CANSELED, Toast.LENGTH_SHORT).show();

                            }
                        });


        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    private void getSavedPreferences() {
        if (mainPreferences.contains(APP_PREFERENCES_FIRST_LAUNCH)) {
            appFirstLaunch = mainPreferences.getBoolean(APP_PREFERENCES_FIRST_LAUNCH, false);
        }
        if (mainPreferences.contains(APP_PREFERENCES_AVERAGE_MONTHLY_INCOME)) {
            averageMonthlyIncome = mainPreferences.getFloat(APP_PREFERENCES_AVERAGE_MONTHLY_INCOME, 0);
        }
        if (mainPreferences.contains(APP_PREFERENCES_CONSIDER_ADDITIONAL_INCOME)) {
            considerAdditionalIncome = mainPreferences.getBoolean(APP_PREFERENCES_CONSIDER_ADDITIONAL_INCOME, false);
        }
        if (mainPreferences.contains(APP_PREFERENCES_MANDATORY_PAYMENTS)) {
            mandatoryPayments = mainPreferences.getFloat(APP_PREFERENCES_MANDATORY_PAYMENTS, 0);
        }
        if (mainPreferences.contains(APP_PREFERENCES_MONTHLY_PAYMENTS_COMPLETELY_PAID)) {
            monthlyPaymentsCompletlyPayd = mainPreferences.getBoolean(APP_PREFERENCES_MONTHLY_PAYMENTS_COMPLETELY_PAID, false);
        }
        if (mainPreferences.contains(APP_PREFERENCES_CREDIT_LIMIT)) {
            creditLimit = mainPreferences.getFloat(APP_PREFERENCES_CREDIT_LIMIT, 0);
        }


    }


    // Цепочка стартовых диалогов для настройки программы
    protected void firstLaunchDialog() {

        String title = "Первый запуск.";

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.dialog_first_launch, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        TextView flt = (TextView) promptView.findViewById(R.id.first_launch_text);
        flt.setText("При первом запуске, для корректной работы программы, необходимо внести стартовые значения! Все внесенные значения можно будет изменить позже в настройках программы.");

        alertDialogBuilder.setTitle(title);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            setStartSumDialog();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        dialog.cancel();
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    protected void setStartSumDialog() throws ParseException {

        String title = "Установите стартовый баланс:";

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.dialog_set_one_sum, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        dateUnixFomat = new DateFactory().getDateUnixFormat(CURRENT_DAY, CURRENT_MONTH, CURRENT_YEAR);
        final EditText setSum = (EditText) promptView.findViewById(R.id.set_sum);
        typeOfEntry = TYPE_INCOME;
        subTypeOfEntry = SUBTYPE_INCOME_START;

        alertDialogBuilder.setTitle(title);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (setSum.length() == 0) {
                            Toast.makeText(MainActivity.this, "Введите стартовую сумму", Toast.LENGTH_SHORT).show();
                            try {
                                setStartSumDialog();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            dialog.cancel();
                        } else {

                            double startSum = Double.parseDouble(setSum.getText().toString());
                            insertNewEntry(startSum, "Стартовый баланс");
                            setAverageMonthlyIncomeDialog();
                            dialog.cancel();
                        }
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    protected void setAverageMonthlyIncomeDialog() {

        String title = "Установите среднемесячный доход:";

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.dialog_set_one_sum, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);


        final EditText setSum = (EditText) promptView.findViewById(R.id.set_sum);


        alertDialogBuilder.setTitle(title);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (setSum.length() == 0) {
                            Toast.makeText(MainActivity.this, "Установите среднемесячный доход!", Toast.LENGTH_SHORT).show();
                            setAverageMonthlyIncomeDialog();
                            dialog.cancel();
                        } else {
                            averageMonthlyIncome = (float) Double.parseDouble(setSum.getText().toString());
                            setMandatoryPaymentsDialog();
                            dialog.cancel();
                        }
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    protected void setMandatoryPaymentsDialog() {

        String title = "Установите ежемесячные платежи:";

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.dialog_set_mandatory_payments, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText utilityBillsET = (EditText) promptView.findViewById(R.id.utility_bills);
        final EditText trainingCostsET = (EditText) promptView.findViewById(R.id.training_costs);
        final EditText taxesET = (EditText) promptView.findViewById(R.id.taxes);
        final EditText creditPaymentsET = (EditText) promptView.findViewById(R.id.credit_payments);
        final EditText otherPayment = (EditText) promptView.findViewById(R.id.other_payments);


        alertDialogBuilder.setTitle(title);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (utilityBillsET.length() == 0
                                && trainingCostsET.length() == 0
                                && taxesET.length() == 0
                                && creditPaymentsET.length() == 0
                                && otherPayment.length() == 0) {
                            Toast.makeText(MainActivity.this, "Все графы должны быть заполнены!", Toast.LENGTH_SHORT).show();
                            setMandatoryPaymentsDialog();
                            dialog.cancel();
                        }
                        mandatoryPayments = (float) (Double.parseDouble(utilityBillsET.getText().toString())
                                + Double.parseDouble(trainingCostsET.getText().toString())
                                + Double.parseDouble(taxesET.getText().toString())
                                + Double.parseDouble(creditPaymentsET.getText().toString())
                                + Double.parseDouble(otherPayment.getText().toString()));

                        appFirstLaunch = false;
                        saveStartSettings();
                        dialog.cancel();
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    protected void saveStartSettings (){
        final SharedPreferences.Editor editor = mainPreferences.edit();

        editor.putBoolean(APP_PREFERENCES_FIRST_LAUNCH, appFirstLaunch)
                .putFloat(APP_PREFERENCES_AVERAGE_MONTHLY_INCOME, averageMonthlyIncome)
                .putBoolean(APP_PREFERENCES_CONSIDER_ADDITIONAL_INCOME, considerAdditionalIncome)
                .putFloat(APP_PREFERENCES_MANDATORY_PAYMENTS, mandatoryPayments)
                .putBoolean(APP_PREFERENCES_MONTHLY_PAYMENTS_COMPLETELY_PAID, monthlyPaymentsCompletlyPayd)
                .putFloat(APP_PREFERENCES_CREDIT_LIMIT, creditLimit);
        editor.apply();

        Toast.makeText(MainActivity.this, SETTINGS_SAVED, Toast.LENGTH_SHORT).show();

    }
    // Конец диалогов стартовых настроек

}
