package pavelzuykoff.optimoney;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import pavelzuykoff.optimoney.data.OptimoneyDbContract;

import static pavelzuykoff.optimoney.MainActivity.mDbHelper;


/**
 * Created by novem on 03.09.2016.
 */


public class DbFragment extends Fragment {

    View view;
    ImageView image;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_db, container, false);

        image = (ImageView) view.findViewById(R.id.edit_icon);

        image.setImageBitmap(BitmapFactory.decodeResource(this.getResources(),R.drawable.edit_entry_icon));

        return view;


    }


    @Override
    public void onStart() {
        super.onStart();
        displayDatabaseInfo();
        displayMoneyBoxInfo();





    }


    private void displayDatabaseInfo() {
        // Создадим и откроем для чтения базу данных
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Зададим условие для выборки - список столбцов
        String[] projection = {
                OptimoneyDbContract.MainTableEntry._ID,
                OptimoneyDbContract.MainTableEntry.COLUMN_DATE,
                OptimoneyDbContract.MainTableEntry.COLUMN_SUM,
                OptimoneyDbContract.MainTableEntry.COLUMN_TYPE,
                OptimoneyDbContract.MainTableEntry.COLUMN_SUBTYPE,
                OptimoneyDbContract.MainTableEntry.COLUMN_COMMENT,
                OptimoneyDbContract.MainTableEntry.COLUMN_MODIFICATION_TIME,
                OptimoneyDbContract.MainTableEntry.COLUMN_DELETED,
                OptimoneyDbContract.MainTableEntry.COLUMN_SYNCRONIZED,
                OptimoneyDbContract.MainTableEntry.COLUMN_USER,
                OptimoneyDbContract.MainTableEntry.COLUMN_DEVICE};

        // Делаем запрос
        Cursor cursor = db.query(
                OptimoneyDbContract.MainTableEntry.TABLE_NAME,   // таблица
                projection,            // столбцы
                null,                  // столбцы для условия WHERE
                null,                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки

        TextView displayTextView = (TextView) getView().findViewById(R.id.text_view_info);

        try {
            displayTextView.setText("Таблица содержит " + cursor.getCount() + " записей.\n\n");
            displayTextView.append(OptimoneyDbContract.MainTableEntry._ID + "|" +
                    OptimoneyDbContract.MainTableEntry.COLUMN_DATE + "|" +
                    OptimoneyDbContract.MainTableEntry.COLUMN_SUM + "|" +
                    OptimoneyDbContract.MainTableEntry.COLUMN_TYPE + "|" +
                    OptimoneyDbContract.MainTableEntry.COLUMN_SUBTYPE + "|" +
                    OptimoneyDbContract.MainTableEntry.COLUMN_COMMENT + "|" +
                    OptimoneyDbContract.MainTableEntry.COLUMN_MODIFICATION_TIME + "|" +
                    OptimoneyDbContract.MainTableEntry.COLUMN_DELETED + "|" +
                    OptimoneyDbContract.MainTableEntry.COLUMN_SYNCRONIZED + "|" +
                    OptimoneyDbContract.MainTableEntry.COLUMN_USER + "|" +
                    OptimoneyDbContract.MainTableEntry.COLUMN_DEVICE + "\n");

            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MainTableEntry._ID);
            int dateColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MainTableEntry.COLUMN_DATE);
            int sumColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MainTableEntry.COLUMN_SUM);
            int typeColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MainTableEntry.COLUMN_TYPE);
            int subtypeColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MainTableEntry.COLUMN_SUBTYPE);
            int commentColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MainTableEntry.COLUMN_COMMENT);
            int modtimeColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MainTableEntry.COLUMN_MODIFICATION_TIME);
            int deletedColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MainTableEntry.COLUMN_DELETED);
            int syncronizedColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MainTableEntry.COLUMN_SYNCRONIZED);
            int userColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MainTableEntry.COLUMN_USER);
            int deviceColumnIndex = cursor.getColumnIndex(OptimoneyDbContract.MainTableEntry.COLUMN_DEVICE);


            // Проходим через все ряды
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursor.getInt(idColumnIndex);
                int currentDate = cursor.getInt(dateColumnIndex);
                double currentSum = cursor.getDouble(sumColumnIndex);
                int currentType = cursor.getInt(typeColumnIndex);
                int currentSubtype = cursor.getInt(subtypeColumnIndex);
                String currentComment = cursor.getString(commentColumnIndex);
                int currentModTime = cursor.getInt(modtimeColumnIndex);
                int currentDeleted = cursor.getInt(deletedColumnIndex);
                int currentSync = cursor.getInt(syncronizedColumnIndex);
                String currentUser = cursor.getString(userColumnIndex);
                String currentDev = cursor.getString(deviceColumnIndex);


                // Выводим значения каждого столбца
                displayTextView.append(("\n" + currentID + "|" +
                        currentDate + "|" +
                        currentSum + "|" +
                        currentType + "|" +
                        currentSubtype + "|" +
                        currentComment + "|" +
                        currentModTime + "|" +
                        currentDeleted + "|" +
                        currentSync + "|" +
                        currentUser + "|" +
                        currentDev));
            }
        } finally {
            // Всегда закрываем курсор после чтения
            cursor.close();
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

        TextView displayTextView = (TextView) getView().findViewById(R.id.money_box_info);

        try {
            displayTextView.setText("Копилка содержит " + cursor.getCount() + " записей.\n\n");
            displayTextView.append(OptimoneyDbContract.MoneyBoxTableEntry._ID + "|" +
                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_TARGET_DATE + "|" +
                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_SAVE_SUM + "|" +
                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_START_DATE + "|" +
                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_DAYLY_SAVED_SUM + "|" +
                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_TARGET_OBJECT + "|" +
                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_MODIFICATION_TIME + "|" +
                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_DELETED + "|" +
                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_SYNCRONIZED + "|" +
                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_USER + "|" +
                    OptimoneyDbContract.MoneyBoxTableEntry.COLUMN_DEVICE + "\n");

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


                // Выводим значения каждого столбца
                displayTextView.append(("\n" + currentID + "|" +
                        currentTagretDate + "|" +
                        currentSaveSum + "|" +
                        currentStartDate + "|" +
                        currentDaylySave + "|" +
                        currentTargetObj + "|" +
                        currentModTime + "|" +
                        currentDeleted + "|" +
                        currentSync + "|" +
                        currentUser + "|" +
                        currentDev));
            }
        } finally {
            // Всегда закрываем курсор после чтения
            cursor.close();
        }
    }

}
