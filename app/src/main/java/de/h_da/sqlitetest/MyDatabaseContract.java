package de.h_da.sqlitetest;

import android.provider.BaseColumns;

/**
 * Created by User on 29.12.2016.
 */
public class MyDatabaseContract {
    private MyDatabaseContract() {}

    public static class MyDatabaseEntry implements BaseColumns
    {
        public static final String DATABASE_NAME = "THeNameOfMyDatabase.db";
        public static final int DATABASE_VERSION = 1;
        public static final String TABLE_NAME = "Expenditures";
        public static final String COLUMN_NAME_DAY = "day";
        public static final String COLUMN_NAME_MONTH = "month";
        public static final String COLUMN_NAME_YEAR = "year";
        public static final String COLUMN_NAME_SUM = "money";
        public static final String COLUMN_NAME_DESCRIPTION = "description";

    }
}
