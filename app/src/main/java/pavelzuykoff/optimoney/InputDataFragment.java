package pavelzuykoff.optimoney;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;

import pavelzuykoff.optimoney.data.OptimoneyDbContract;


import static pavelzuykoff.optimoney.MainActivity.*;



/**
 * Created by novem on 13.08.2016.
 */
public class InputDataFragment extends Fragment {

//    private DateFactory DATE = new DateFactory();
//


    //переменные для БД
//    protected long dateUnixFomat;
//    protected double sum = 0;
//    protected int typeOfEntry = OptimoneyDbContract.MainTableEntry.TYPE_INCOME;
//    protected int subTypeOfEntry = OptimoneyDbContract.MainTableEntry.SUBTYPE_OTHER;
//    protected String note = "";
//    protected long modificationTimeUnixFormat;
//    protected int deletedEntry = OptimoneyDbContract.MainTableEntry.DELETED_FALSE;
//    protected int synchronizedEntry = OptimoneyDbContract.MainTableEntry.SYNCHRONIZED_FALSE;
//    protected String userName = "default_user";
//    protected String deviceName = "default_device";

    private View view;
    private TextView entryDateTV;
    private EditText sumET;
    private TextView subTypeLegend;
    private EditText noteText;
    private Button addToDb;
    private int chosenDay;
    private int chosenMonth;
    private int chosenYear;
    Boolean checkPassed = true; //проверка и установка значения подтипа


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        Log.d(TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.fragment_input_data, container, false);

        entryDateTV = (TextView) view.findViewById(R.id.chosenDate);
        sumET = (EditText) view.findViewById(R.id.sumET);
        noteText = (EditText) view.findViewById(R.id.noteText);
        addToDb = (Button) view.findViewById(R.id.inputToDatabase);
        subTypeLegend = (TextView) view.findViewById(R.id.subTypeLegend);

        final Spinner typeSpinner = (Spinner) view.findViewById(R.id.typeSpinner);
        final Spinner subTypeSpinner = (Spinner) view.findViewById(R.id.subTypeSpinner);


        setCurrentDate();

        ArrayAdapter<?> typeSpinnerAdaptor =
                ArrayAdapter.createFromResource(getActivity(), R.array.types, android.R.layout.simple_spinner_item);
        typeSpinnerAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeSpinner.setAdapter(typeSpinnerAdaptor);



        final ArrayAdapter<?> incomeSpinnerAdaptor =
                ArrayAdapter.createFromResource(getActivity(), R.array.income_types, android.R.layout.simple_spinner_item);
        incomeSpinnerAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<?> spendSpinnerAdaptor =
                ArrayAdapter.createFromResource(getActivity(), R.array.spend_types, android.R.layout.simple_spinner_item);
        spendSpinnerAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.type_income))) {
                        typeOfEntry = OptimoneyDbContract.MainTableEntry.TYPE_INCOME;
                        subTypeLegend.setText("Вид дохода:");
                        subTypeSpinner.setAdapter(incomeSpinnerAdaptor);
                        Log.d(TAG, "onItemSelected: " + typeOfEntry);
                    } else if (selection.equals(getString(R.string.type_spend))) {
                        typeOfEntry = OptimoneyDbContract.MainTableEntry.TYPE_SPEND;
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

        //текущая дата в формате unix
        try {
            dateUnixFomat = new DateFactory().getDateUnixFormat(CURRENT_DAY, CURRENT_MONTH, CURRENT_YEAR);
        } catch (ParseException e) {
            dateUnixFomat = 0;
            e.printStackTrace();
        }

        entryDateTV.setText(DATE.getCurrentDateStringFormat());

 /*       Log.d(TAG, "сегодня: " + mDay + "." + DATE + "." + mYear);*/

        entryDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDateDialog();
                Log.d(MainActivity.TAG, "onClick: showInputDialog();");


            }
        });

        addToDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int spinnerPos = subTypeSpinner.getSelectedItemPosition();
                checkPassed = inspectSubTypeSpinner(spinnerPos);

                insertNewEntry();
                sumET.getText().clear();
                noteText.getText().clear();
            }
        });
        return view;
    }

    // метод для вставки строки с данными в БД

    private void insertNewEntry() {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();



        // подготовка данных к вставке
        if (sumET.length() > 0 && checkPassed) {
            //dateUnixFomat задана текущей датой по умолчанию или выбрана в календаре

            sum = Double.parseDouble(sumET.getText().toString());

            //typeOfEntry выбран спиннером

            //subTypeOfEntry по умолчанию

            if (noteText.length() > 0) {
                note = noteText.getText().toString().trim();
            } else {
                note = "";
            }
            modificationTimeUnixFormat = System.currentTimeMillis() / 1000;

            //deleted entry по умолчанию 0

            //syncronized entry по умолчанию 0

            //userName = default;

            //deviceName = default;

            Log.d(TAG, "дата: " + dateUnixFomat
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
                Toast.makeText(getActivity(), "ОШИБКА!", Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(getActivity(), DATA_ADDED_TO_DB, Toast.LENGTH_SHORT).show();
        } else {

            sum = 0;

            Toast.makeText(getActivity(), DATA_NOT_ADDED, Toast.LENGTH_SHORT).show();
        }


    }

    // Диалог выбора даты
    protected void showInputDateDialog() {
        String title = "Выберите дату:";

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.dialog_chose_date, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        DatePicker datePicker = (DatePicker) promptView.findViewById(R.id.datePicker);


        datePicker.setCalendarViewShown(true);
        datePicker.setSpinnersShown(false);


        datePicker.init(CURRENT_YEAR, CURRENT_MONTH, CURRENT_DAY, new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year,
                                      int month, int day) {
                Log.d(MainActivity.TAG, "onDateChanged: " + day + " " + month + " " + year);
                chosenDay = day;
                chosenMonth = month;
                chosenYear = year;

                try {
                    dateUnixFomat = new DateFactory().getDateUnixFormat(chosenDay, chosenMonth, chosenYear);
                } catch (ParseException e) {
                    dateUnixFomat = 0;
                    e.printStackTrace();
                }
                Log.d(TAG, "unixDate: " + dateUnixFomat);


            }
        });


        alertDialogBuilder.setTitle(title);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        entryDateTV.setText(DATE.getChosenDateStringFormat(chosenDay, chosenMonth, chosenYear));
                        setCurrentDate();
                        Toast.makeText(getActivity(), DATE_UPDATED, Toast.LENGTH_SHORT).show();


                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Toast.makeText(getActivity(), CANSELED, Toast.LENGTH_SHORT).show();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    private void setCurrentDate() {
        chosenDay = CURRENT_DAY;
        chosenMonth = CURRENT_MONTH;
        chosenYear = CURRENT_YEAR;
    }


}
