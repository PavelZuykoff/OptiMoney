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
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import pavelzuykoff.optimoney.data.WorkWithDBContract;

/**
 * Created by novem on 13.08.2016.
 */
public class InputDataFragment extends Fragment {

    final String TAG = "happy";


    private CalendarValuesHandler date = new CalendarValuesHandler();

    int chosenDay = date.currentDay;
    int chosenMonth = date.currentMonth;
    int chosenYear = date.currentYear;

    private int typeOfEntry = 0;


    private View view;
    private TextView chosenDate;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.fragment_input_data, container, false);

        chosenDate = (TextView) view.findViewById(R.id.chosenDate);
        final Spinner typeSpinner = (Spinner) view.findViewById(R.id.typeSpinner);

        ArrayAdapter<?> typeSpinnerAdaptor =
                ArrayAdapter.createFromResource(getActivity(), R.array.types, android.R.layout.simple_spinner_item);
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

        chosenDate.setText(date.getCurrentDateStringFormat());

 /*       Log.d(TAG, "сегодня: " + mDay + "." + date + "." + mYear);*/


        chosenDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();


            }
        });

        return view;


    }

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
                Log.d(TAG, "onDateChanged: " + day + " " + month + " " + year);
                chosenDay = day;
                chosenMonth = month;
                chosenYear = year;


            }
        });


        alertDialogBuilder.setTitle(title);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "Dialog OK");
                        Toast.makeText(getActivity(), "Дата установлена", Toast.LENGTH_SHORT).show();
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
