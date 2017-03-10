package pavelzuykoff.optimoney;


import android.content.DialogInterface;
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

import static pavelzuykoff.optimoney.MainActivity.TAG;


/**
 * Created by novem on 13.08.2016.
 */
public class InputDataFragment extends Fragment {

    private DateConverter date = new DateConverter();

    int chosenDay = date.currentDay;
    int chosenMonth = date.currentMonth;
    int chosenYear = date.currentYear;

    //переменные для БД
    protected long dateUnixFomat;
    protected double sum = 0;
    private int typeOfEntry = OptimoneyDbContract.MainTableEntry.TYPE_INCOME;
    protected int subTypeOfEntry = OptimoneyDbContract.MainTableEntry.SUBTYPE_OTHER;
    protected String note = "";
    protected long modificationTimeUnixFormat;
    protected int deletedEntry = OptimoneyDbContract.MainTableEntry.DELETED_FALSE;
    protected int synchronizedEntry = OptimoneyDbContract.MainTableEntry.SYNCHRONIZED_FALSE;
    protected String userName = "default_user";
    protected String deviceName = "default_device";

    private View view;
    private TextView chosenDate;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(MainActivity.TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.fragment_input_data, container, false);

        chosenDate = (TextView) view.findViewById(R.id.chosenDate);
        final EditText sumET = (EditText) view.findViewById(R.id.sumET);
        final EditText noteText = (EditText) view.findViewById(R.id.noteText);
        Button addToDb = (Button) view.findViewById(R.id.inputToDatabase);

        final Spinner typeSpinner = (Spinner) view.findViewById(R.id.typeSpinner);

        ArrayAdapter<?> typeSpinnerAdaptor =
                ArrayAdapter.createFromResource(getActivity(), R.array.types, android.R.layout.simple_spinner_item);
        typeSpinnerAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeSpinner.setAdapter(typeSpinnerAdaptor);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.type_income))) {
                        typeOfEntry = OptimoneyDbContract.MainTableEntry.TYPE_INCOME;
                        Log.d(MainActivity.TAG, "onItemSelected: " + typeOfEntry);
                    } else if (selection.equals(getString(R.string.type_spend))) {
                        typeOfEntry = OptimoneyDbContract.MainTableEntry.TYPE_SPEND;
                        Log.d(MainActivity.TAG, "onItemSelected: " + typeOfEntry);
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

        try {
            dateUnixFomat = new DateConverter().getDateUnixFormat(chosenDay, chosenMonth, chosenYear);
        } catch (ParseException e) {
            dateUnixFomat = 0;
            e.printStackTrace();
        }

        chosenDate.setText(date.getCurrentDateStringFormat());

 /*       Log.d(TAG, "сегодня: " + mDay + "." + date + "." + mYear);*/

        chosenDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
                Log.d(MainActivity.TAG, "onClick: showInputDialog();");


            }
        });

        addToDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sumET.length() > 0) {
                    //dateUnixFomat задана текущей датой по умолчанию или выбрана в календаре

                    sum = Double.parseDouble(sumET.getText().toString());

                    //typeOfEntry выбран спиннером

                    //subTypeOfEntry по умолчанию

                    if (noteText.length() > 0) {
                        note = noteText.getText().toString();
                    } else {
                        note = "";
                    }
                    modificationTimeUnixFormat = System.currentTimeMillis();

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


                    Toast.makeText(getActivity(), MainActivity.ADDED_TO_DB, Toast.LENGTH_SHORT).show();
                } else {

                    sum = 0;

                    Toast.makeText(getActivity(), MainActivity.NOTHING_TO_ADD, Toast.LENGTH_SHORT).show();
                }

            }
        });


        return view;


    }

    // Диалог выбора даты
    protected void showInputDialog() {
        String title = "Выберите дату:";
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.chose_date_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        DatePicker datePicker = (DatePicker) promptView.findViewById(R.id.datePicker);

        datePicker.setCalendarViewShown(true);
        datePicker.setSpinnersShown(false);


        datePicker.init(chosenYear, chosenMonth, chosenDay, new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year,
                                      int month, int day) {
                Log.d(MainActivity.TAG, "onDateChanged: " + day + " " + month + " " + year);
                chosenDay = day;
                chosenMonth = month;
                chosenYear = year;

                try {
                    dateUnixFomat = new DateConverter().getDateUnixFormat(chosenDay, chosenMonth, chosenYear);
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
                        Log.d(MainActivity.TAG, "Dialog OK");
                        Toast.makeText(getActivity(), MainActivity.DATE_UPDATED, Toast.LENGTH_SHORT).show();
                        String newDate = date.getChosenDateStringFormat(chosenDay, chosenMonth, chosenYear);
                        chosenDate = (TextView) view.findViewById(R.id.chosenDate);
                        chosenDate.setText(newDate);
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

}
