package pavelzuykoff.optimoney.data;

import android.provider.BaseColumns;

/**
 * Created by novem on 13.02.2017.
 */

public final class OptimoneyDbContract {
    private OptimoneyDbContract() {

    }

    ;

    public static final class MainTableEntry implements BaseColumns {
        public static final String TABLE_NAME = "main";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_SUM = "sum";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_SUBTYPE = "subtype";
        public static final String COLUMN_COMMENT = "comment";
        public static final String COLUMN_MODIFICATION_TIME = "modification_time";
        public static final String COLUMN_DELETED = "deleted";
        public static final String COLUMN_SYNCRONIZED = "synchronized";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_DEVICE = "device";

        public static final int TYPE_INCOME = 0;
        public static final int TYPE_SPEND = 1;

        public static final int SUBTYPE_OTHER = 0;
        public static final int SUBTYPE_FOOD = 1;
        public static final int SUBTYPE_MEDICINE = 2;
        public static final int SUBTYPE_CAR = 3;

        // TODO сделать болшьше подтипов

        public static final int DELETED_FALSE = 0;
        public static final int DELETED_TRUE = 1;

        public static final int SYNCHRONIZED_FALSE = 0;
        public static final int SYNCHRONIZED_TRUE = 1;


    }

    ;


}
