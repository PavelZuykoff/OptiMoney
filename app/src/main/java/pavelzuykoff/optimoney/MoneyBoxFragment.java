package pavelzuykoff.optimoney;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MoneyBoxFragment extends Fragment {

    private DateConverter date = new DateConverter();

    private View view;
    private TextView targetDate;
    private TextView targetSum;

    int chosenDay = date.currentDay;
    int chosenMonth = date.currentMonth;
    int chosenYear = date.currentYear;
    double inputedSum;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_money_box, container, false);
        Log.d(MainActivity.TAG, "onCreateView: ");

        targetDate = (TextView) view.findViewById(R.id.target_date_tv);
        targetSum = (TextView) view.findViewById(R.id.accumulation_target_tv);

        targetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDateDialog();
            }
        });

        targetSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.TAG, "onClick:");

               showInputSumDialog();
            }
        });


        return view;
    }

    protected void showInputDateDialog() {
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
                        targetDate = (TextView) view.findViewById(R.id.target_date_tv);
                        targetDate.setText(newDate);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getActivity(), MainActivity.CANSELED, Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    protected void showInputSumDialog() {

        final String title = "Введите сумму:";
        final FragmentActivity activity = getActivity();
//        final String[] newSum = new String[1];



        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View promptView = layoutInflater.inflate(R.layout.input_sum_dialog, null);

        final EditText sumInput = (EditText) promptView.findViewById(R.id.sumInputSumDialog);
        Log.d(MainActivity.TAG, "getActivity:" + getActivity());




        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setView(promptView);


        alertDialogBuilder.setTitle(title);


        // setup a dialog window
        final AlertDialog.Builder builder = alertDialogBuilder;
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(MainActivity.TAG, "Dialog OK");

                String stringSum = sumInput.getText().toString();

                Log.d(MainActivity.TAG, "stringSum " + sumInput.getText().toString());

                if (sumInput.length() > 0) {
                    inputedSum = Double.parseDouble(stringSum);
                    targetSum.setText(sumInput.getText()+ " " + MainActivity.currency);
                }
                else {
                    inputedSum = 0;
                    targetSum.setText(inputedSum + " " + MainActivity.currency);
                    Toast.makeText(getActivity(), MainActivity.NOTHING_TO_ADD, Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel",
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
