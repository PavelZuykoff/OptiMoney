package pavelzuykoff.optimoney;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MoneyBoxFragment extends Fragment {

    private CalendarValuesHandler date = new CalendarValuesHandler();
    private final String TAG = "happy";
    private View view;
    private TextView targetDate;
    private TextView targetSum;

    int chosenDay = date.currentDay;
    int chosenMonth = date.currentMonth;
    int chosenYear = date.currentYear;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_money_box, container, false);
        Log.d(TAG, "onCreateView: ");

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
                Log.d(TAG, "onClick:");

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
                        targetDate = (TextView) view.findViewById(R.id.target_date_tv);
                        targetDate.setText(newDate);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getActivity(), "Операция отменена", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    protected void showInputSumDialog() {
        final EditText sumInput = (EditText) view.findViewById(R.id.sumInputTV);
        final String title = "Введите сумму:";
        final String[] newSum = new String[1];


        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.input_sum_dialog, null);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);


        alertDialogBuilder.setTitle(title);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "Dialog OK");

                        newSum[0] = sumInput.getText().toString();
                        Log.d(TAG, "onClick: " + newSum[0]);
                        /*targetSum.setText(newSum);*/


/*                        if (sumInput.length() > 0){
                        targetSum.setText(sumInput.getText()+" RUB");
                        }
                        else {
                            Toast.makeText(getActivity(), "Вы ничего не ввели", Toast.LENGTH_SHORT).show();
                            targetSum.setText("0 RUB");
                        }*/

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
