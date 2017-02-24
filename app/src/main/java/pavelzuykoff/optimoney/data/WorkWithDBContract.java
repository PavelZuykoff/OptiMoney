package pavelzuykoff.optimoney.data;

import android.provider.BaseColumns;

/**
 * Created by novem on 13.02.2017.
 */

public final class WorkWithDBContract {
    private WorkWithDBContract(){
    };
    
    public static final class MainTableEntry implements BaseColumns{
        public static final String TABLE_NAME = "main";

        public static final  String _ID = BaseColumns._ID;
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_SUM = "sum";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_SUBTYPE = "subtype";
        public static final String COLUMN_COMMENT = "comment";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_DELETED = "deleted";
        public static final String COLUMN_SYNCRONIZED = "synchronized";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_DEVICE = "device";

        public static final int TYPE_INCOME = 0;
        public static final int TYPE_SPEND = 1;

        public static final int SUBTIPE_SALARY = 0;
        public static final int SUBTIPE_FOOD = 1;
        public static final int SUBTIPE_MEDICINE = 2;
        public static final int SUBTIPE_CAR = 3;

        // TODO сделать болшьше подтипов

        public static final int DELETED_FALSE = 0;
        public static final int DELETED_TRUE = 1;

        public static final int SYNCHRONIZED_FALSE = 0;
        public static final int SYNCHRONIZED_TRUE = 1;





    };




}
