package pavelzuykoff.optimoney;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;

import pavelzuykoff.optimoney.data.OptimoneyDbContract;

import static pavelzuykoff.optimoney.MainActivity.*;

public class MoneyBoxFragment extends Fragment {

    private View view;
    private TextView targetDate;
    private TextView targetSum;
    private TextView accumulatedSum;
    private TextView daysLeftToTarget;
    private int chosenDay;
    private int chosenMonth;
    private int chosenYear;
    private long currentUnixDate;
    private Button startSavings;

    //переменные для БД
    private long targetUnixDate;
    private double saveSum;
    private long startUnixDate;
    private double daylySavedSum;
    private String targetObject; //на что копим
    //статичиские переменные из MainActivity
    //modificationTimeUnixFormat;
    //deletedEntry = OptimoneyDbContract.MainTableEntry.DELETED_FALSE;
    //synchronizedEntry = OptimoneyDbContract.MainTableEntry.SYNCHRONIZED_FALSE;
    //userName = "default_user";
    //deviceName = "default_device";


    double inputedSum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_money_box, container, false);
        Log.d(TAG, "onCreateView: ");

        targetDate = (TextView) view.findViewById(R.id.target_date_tv);
        targetSum = (TextView) view.findViewById(R.id.accumulation_target_tv);
        startSavings = (Button) view.findViewById(R.id.save_changes);
        accumulatedSum = (TextView) view.findViewById(R.id.accumulated_tv);
        daysLeftToTarget = (TextView) view.findViewById(R.id.days_left_tv);


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

        startSavings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateSavingsTable();
                displayMoneyBoxInfo();

                Log.d(TAG, "start to saving money");

            }
        });


        //Текущая дата в Unix формате
        try {
            currentUnixDate = new DateFactory().getDateUnixFormat(CURRENT_DAY, CURRENT_MONTH, CURRENT_YEAR);
        } catch (ParseException e) {
            currentUnixDate = 0;
            e.printStackTrace();
        }

        setDateTomorrow();

        //Целевая дата в Unix формате (по умолчанию ЗАВТРА)
        try {
            targetUnixDate = new DateFactory().getDateUnixFormat(chosenDay, chosenMonth, chosenYear);
        } catch (ParseException e) {
            targetUnixDate = 0;
            e.printStackTrace();
        }

        //Стартовая дата для отсчёта накоплений. ЗАВТРА.
        try {
            startUnixDate = new DateFactory().getDateUnixFormat(chosenDay, chosenMonth, chosenYear);
        } catch (ParseException e) {
            startUnixDate = 0;
            e.printStackTrace();
        }

        displayMoneyBoxInfo();

        return view;
    }

    protected void showInputDateDialog() {
        String title = "Выберите дату:";
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.dialog_chose_date, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        final DatePicker datePicker = (DatePicker) promptView.findViewById(R.id.datePicker);
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

                try {
                    targetUnixDate = new DateFactory().getDateUnixFormat(chosenDay, chosenMonth, chosenYear);
                } catch (ParseException e) {
                    targetUnixDate = 0;
                    e.printStackTrace();
                }

                // проверка что выбранная дата больше текущей
                if (currentUnixDate + 86399 > targetUnixDate) {

                    setDateTomorrow();

                    try {
                        targetUnixDate = new DateFactory().getDateUnixFormat(chosenDay, chosenMonth, chosenYear);
                    } catch (ParseException e) {
                        targetUnixDate = 0;
                        e.printStackTrace();
                    }


                    Toast.makeText(getActivity(), WRONG_DATE, Toast.LENGTH_LONG).show();
                }
            }
        });


        alertDialogBuilder.setTitle(title);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        targetDate.setText(DATE.getChosenDateStringFormat(chosenDay, chosenMonth, chosenYear));

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

    protected void showInputSumDialog() {

        final String title = "Введите сумму:";
        final FragmentActivity activity = getActivity();

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View promptView = layoutInflater.inflate(R.layout.dialog_input_sum, null);

        final EditText sumInput = (EditText) promptView.findViewById(R.id.sumInputSumDialog);
        Log.d(TAG, "getActivity:" + getActivity());


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setView(promptView);


        alertDialogBuilder.setTitle(title);


        // setup a dialog window
        final AlertDialog.Builder builder = alertDialogBuilder;
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                Log.d(TAG, "stringSum " + sumInput.getText().toString());

                if (sumInput.length() > 0) {
                    saveSum = Double.parseDouble(sumInput.getText().toString());
                    targetSum.setText(sumInput.getText() + " " + currency);
                } else {
                    saveSum = 0;
                    targetSum.setText(saveSum + " " + currency);
                    Toast.makeText(getActivity(), DATA_NOT_ADDED, Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel",
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

    protected void setDateTomorrow() {

        String tomorrow = new DateFactory().getDateUtcFormat(currentUnixDate + 86400, true);

        chosenDay = Integer.parseInt(tomorrow.substring(0, 2));
        chosenMonth = Integer.parseInt(tomorrow.substring(3, 5)) - 1;
        chosenYear = Integer.parseInt(tomorrow.substring(6, 10));
        Log.d(TAG, "tomorrow:" + chosenDay + " " + chosenMonth + " " + chosenYear);
    }

    protected void updateSavingsTable() {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues initialValues = new ContentValues();


        if (saveSum >= 1) {
            //подготовка переменных для БД

            //targetUnixDate задана диалогом выбора даты или завтра по умолчанию
            //saveSum задана диалогом ввода суммыж
            //startUnixDate задана при старте копилки
            daylySavedSum = saveSum / ((targetUnixDate - currentUnixDate) / 86400);
            targetObject = "default"; //на что копим
            //статичиские переменные из MainActivity
            modificationTimeUnixFormat = System.currentTimeMillis() / 1000;
            deletedEntry = OptimoneyDbContract.MainTableEntry.DELETED_FALSE;
            synchronizedEntry = OptimoneyDbContract.MainTableEntry.SYNCHRONIZED_FALSE;
            userName = "default_user";
            deviceName = "default_device";

            Log.d(TAG, "дата: " + targetUnixDate
                    + " целевая сумма: " + saveSum
                    + " стартовая дата: " + startUnixDate
                    + " ежедневная суммв: " + daylySavedSum
                    + " предмет: " + targetObject
                    + " мод: " + modificationTimeUnixFormat
                    + " удалена: " + deletedEntry
                    + " синхронизирована: " + synchronizedEntry
                    + " пользователь: " + userName
                    + " девайс: " + deviceName);

            initialValues.put(OptimoneyDbContract.MoneyBoxTableEntry._ID, 1);
            initialValues.put(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_TARGET_DATE, targetUnixDate);
            initialValues.put(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_SAVE_SUM, saveSum);
            initialValues.put(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_START_DATE, startUnixDate);
            initialValues.put(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_DAYLY_SAVED_SUM, daylySavedSum);
            initialValues.put(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_TARGET_OBJECT, targetObject);
            initialValues.put(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_MODIFICATION_TIME, modificationTimeUnixFormat);
            initialValues.put(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_DELETED, deletedEntry);
            initialValues.put(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_SYNCRONIZED, synchronizedEntry);
            initialValues.put(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_USER, userName);
            initialValues.put(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_DEVICE, deviceName);

            long rowId = db.replace(OptimoneyDbContract.MoneyBoxTableEntry.TABLE_NAME, null, initialValues);
            if (rowId == -1) {
                // Если ID  -1, значит произошла ошибка
                Toast.makeText(getActivity(), "ОШИБКА!", Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(getActivity(), DATA_ADDED_TO_DB, Toast.LENGTH_SHORT).show();
        } else {
            saveSum = 0;
            Toast.makeText(getActivity(), DATA_NOT_ADDED + " " + CANSELED, Toast.LENGTH_SHORT).show();

        }


    }

    private void displayMoneyBoxInfo() {
        // Создадим и откроем для чтения базу данных
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Зададим условие для выборки - список столбцов
        String[] projection = {
                OptimoneyDbContract.MoneyBoxTableEntry._ID,
                OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_TARGET_DATE,
                OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_SAVE_SUM,
                OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_START_DATE,
                OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_DAYLY_SAVED_SUM,
                OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_TARGET_OBJECT,
                OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_MODIFICATION_TIME,
                OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_DELETED,
                OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_SYNCRONIZED,
                OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_USER,
                OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_DEVICE};

        // Делаем запрос
        Cursor cursor = db.query(
                OptimoneyDbContract.MoneyBoxTableEntry.TABLE_NAME,   // таблица
                projection,            // столбцы
                null,                  // столбцы для условия WHERE
                null,                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки

//        TextView displayTextView = (TextView) getView().findViewById(R.id.money_box_info);

        try {
//            displayTextView.setText("Копилка содержит " + cursor.getCount() + " записей.\n\n");
//            displayTextView.append(OptimoneyDbContract.MoneyBoxTableEntry._ID + "|" +
//                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_TARGET_DATE + "|" +
//                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_SAVE_SUM + "|" +
//                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_START_DATE + "|" +
//                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_DAYLY_SAVED_SUM + "|" +
//                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_TARGET_OBJECT + "|" +
//                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_MODIFICATION_TIME + "|" +
//                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_DELETED + "|" +
//                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_SYNCRONIZED + "|" +
//                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_USER + "|" +
//                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_DEVICE + "\n");

            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MoneyBoxTableEntry._ID);
            int targetDateColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_TARGET_DATE);
            int saveSumColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_SAVE_SUM);
            int startDateColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_START_DATE);
            int daylySaveSumColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_DAYLY_SAVED_SUM);
            int targetObjColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_TARGET_OBJECT);
            int modtimeColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_MODIFICATION_TIME);
            int deletedColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_DELETED);
            int syncronizedColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_SYNCRONIZED);
            int userColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_USER);
            int deviceColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_DEVICE);


            // Проходим через все ряды
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursor.getInt(idColumnIndex);
                int currentTagretDate = cursor.getInt(targetDateColumnIndex);
                double currentSaveSum = cursor.getDouble(saveSumColumnIndex);
                int currentStartDate = cursor.getInt(startDateColumnIndex);
                double currentDaylySave = cursor.getInt(daylySaveSumColumnIndex);
                String currentTargetObj = cursor.getString(targetObjColumnIndex);
                int currentModTime = cursor.getInt(modtimeColumnIndex);
                int currentDeleted = cursor.getInt(deletedColumnIndex);
                int currentSync = cursor.getInt(syncronizedColumnIndex);
                String currentUser = cursor.getString(userColumnIndex);
                String currentDev = cursor.getString(deviceColumnIndex);

                // Выводим искомые значения во вьюхи
                if (currentID == 1){

                try {
                    currentUnixDate = new DateFactory().getDateUnixFormat(CURRENT_DAY, CURRENT_MONTH, CURRENT_YEAR);
                } catch (ParseException e) {
                    currentUnixDate = 0;
                    e.printStackTrace();
                }

                String ctd = new DateFactory().getDateUtcFormat(currentTagretDate, true);

                ctd = new DateFactory().getChosenDateStringFormat(Integer.parseInt(ctd.substring(0, 2)),Integer.parseInt(ctd.substring(3, 5)) - 1,Integer.parseInt(ctd.substring(6, 10)));
//                chosenDay = Integer.parseInt(ctd.substring(0, 2));
//                chosenMonth = Integer.parseInt(ctd.substring(3, 5)) - 1;
//                chosenYear = Integer.parseInt(ctd.substring(6, 10));

                //String ctd = String.valueOf(currentTagretDate);
                targetDate.setText(ctd);
                targetSum.setText(currentSaveSum + " " + currency);

                long daysPassed = (currentUnixDate - currentStartDate) / 86400;
                if (daysPassed < 0) daysPassed = 0;
                accumulatedSum.setText(daysPassed * currentDaylySave + " " + currency);

                String dltt = ((currentTagretDate - currentUnixDate) / 86400 + " дней");
                daysLeftToTarget.setText(dltt);
                }


                // Выводим значения каждого столбца
//                displayTextView.append(("\n" + currentID + "|" +
//                        currentTagretDate + "|" +
//                        currentSaveSum + "|" +
//                        currentStartDate + "|" +
//                        currentDaylySave + "|" +
//                        currentTargetObj + "|" +
//                        currentModTime + "|" +
//                        currentDeleted + "|" +
//                        currentSync + "|" +
//                        currentUser + "|" +
//                        currentDev));
            }
        } finally {
            // Всегда закрываем курсор после чтения
            cursor.close();
        }
    }

}
