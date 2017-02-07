package de.h_da.sqlitetest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by User on 29.12.2016.
 */
public class MyDatabaseAdapter {

    private static final String ASC = " ASC"; // for MIN sorting
    private static final String DESC = " DESC"; // for MAX sorting
    private static final String COMMA_JUMP = ", "; // for MAX sorting
    private MyDatabaseHelper helper;
    private MyData myData;
    private Context context;

    public MyDatabaseAdapter(Context context) {
        this.context = context;
        helper = new MyDatabaseHelper(context, MyDatabaseContract.MyDatabaseEntry.DATABASE_NAME, null,
                MyDatabaseContract.MyDatabaseEntry.DATABASE_VERSION);
        myData = null;
    }

    /**
     * Adds data to DB
     * @param day
     * @param month
     * @param year
     * @param sum
     * @param description
     * @return - id, of added item
     */
    public long insertData(String day, String month, String year, Double sum, String description) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_DAY, day);
        cv.put(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_MONTH, month);
        cv.put(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_YEAR, year);
        cv.put(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_SUM, sum);
        cv.put(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_DESCRIPTION, description);

        long id = db.insert(MyDatabaseContract.MyDatabaseEntry.TABLE_NAME, null, cv);
        db.close();
        return id;
    }

//    public ArrayList getAllInformation() {
//        ArrayList<MyData> arrayList = null;
//
//        SQLiteDatabase db = helper.getReadableDatabase();
//        MyData data = null;
//
//        String [] projection = null; //The Columns to return
//        String selection = null; //The Column where to find
//        String [] selectionArgs = null;                                          //The values to find
//        Cursor cursor = db.query(
//                MyDatabaseContract.MyDatabaseEntry.TABLE_NAME,               // THe table to query
//                projection,                                                 // THe columns to return (null - all)
//                selection,                                                  // THe columns for the WHERE clause
//                selectionArgs,                                              // The values for the WHERE clause
//                null,                                                       // don't group the rows
//                null,                                                       // don't filter by row groups
//                null                                                        // The sort order
//        );
//        if(cursor.moveToFirst()) {
//            arrayList = new ArrayList<>();
//
//            do {
//                int id = cursor.getInt(cursor.getColumnIndex(MyDatabaseContract.MyDatabaseEntry._ID));
//                String day = cursor.getString(cursor.getColumnIndex(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_DAY));
//                String month = cursor.getString(cursor.getColumnIndex(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_MONTH));
//                String year = cursor.getString(cursor.getColumnIndex(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_YEAR));
//                Double sum = cursor.getDouble(cursor.getColumnIndex(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_SUM));
//                String description = cursor.getString(cursor.getColumnIndex(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_DESCRIPTION));
//
//                data = new MyData(id, day, month, year, sum, description);
//                Log.d("TAG", "DATA : = " + id + " " + day + " " + month + " " + year + " " + sum + " " + description);
//                arrayList.add(data);
//            } while(cursor.moveToNext());
//            cursor.close();
//            db.close();
//            Log.d("TAG", "ArrayList contains: = " + arrayList.toString());
//            return arrayList;
//        }
//
//        else {
//            cursor.close();
//            db.close();
//            return null;
//        }
//    }

    /**
     * returns values from DB to a List View
     * @param choise - The type of sorting
     * @param values - if user chose by month and year - this is prefered month and year
     * @return - Map with desired results, where Key - date( F.e. 2016(year)12(month)21(day)000(just nulls)1(id)
     */
    public ArrayList showSortedValues (String choise, String[] values) {
        ArrayList<MyData> arrayList = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        MyData data = null;

        String sorting = MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_YEAR.concat(", ")
                .concat(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_MONTH).concat(", ")
                .concat(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_DAY); //Default sorting only year, month , date
        String selection = null;
        String[] selectionArgs = null;
        switch (choise) {
            case MainActivity.GROUP_BY_DATE: //if two records have the same date, first will be shown which is more expensive
                sorting = sorting.concat(", ").concat(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_SUM).concat(DESC);
                break;

            case MainActivity.GROUP_BY_MIN_SUM: //First priority MIN Sum, then date (By default it's " ASC" , but i mentioned it)
                sorting = MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_SUM.concat(ASC).concat(COMMA_JUMP).concat(sorting);
                break;

            case MainActivity.GROUP_BY_MAX_SUM: // First priority MAX Sum then date
                sorting = MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_SUM.concat(DESC).concat(COMMA_JUMP).concat(sorting);
                break;

            case MainActivity.GROUP_BY_MONTH_AND_YEAR: //First priority date and then MAX sum(from selected month and year)
                selectionArgs = values;
                if (values[0].equals(MainActivity.ALL_MONTH_SELECTED)) {
                    selection = MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_MONTH +
                            " LIKE ? OR " + MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_YEAR + " LIKE ?";
                }
                else { //The Column where to find
                    selection = MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_MONTH +
                            " LIKE ? AND " + MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_YEAR + " LIKE ?";
                }
                sorting = sorting.concat(", ").concat(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_SUM).concat(DESC);
                break;
            default:
                selection = null;
                selectionArgs = null;
                sorting = null;
        }

        Cursor cursor = db.query(
                MyDatabaseContract.MyDatabaseEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                sorting);

        if(cursor.moveToFirst()) {
            arrayList = new ArrayList<>();
            do {
                int id = cursor.getInt(cursor.getColumnIndex(MyDatabaseContract.MyDatabaseEntry._ID));
                String day = cursor.getString(cursor.getColumnIndex(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_DAY));
                String month = cursor.getString(cursor.getColumnIndex(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_MONTH));
                String year = cursor.getString(cursor.getColumnIndex(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_YEAR));
                Double sum = cursor.getDouble(cursor.getColumnIndex(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_SUM));
                String description = cursor.getString(cursor.getColumnIndex(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_DESCRIPTION));

                data = new MyData(id, day, month, year, sum, description);
                arrayList.add(data);
            } while (cursor.moveToNext());
            cursor.close();
            db.close();

            return arrayList;
        }
        else {
//            Toast.makeText(context, "Your DB is empty", Toast.LENGTH_SHORT).show();
            cursor.close();
            db.close();
            return null;
        }
    }

    /**
     * Checks whether the DB epty or not
     * @return
     */
    public boolean checkingEmpty() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + MyDatabaseContract.MyDatabaseEntry.TABLE_NAME, null);
        Boolean rowExists;

        if (cursor.moveToFirst())
        {
            rowExists = false;

        } else
        {
            rowExists = true;
        }
        cursor.close();
        db.close();
        return rowExists;
    }

    /**
     * Looks for a description of the record
     * @param id - id of the record in DB
     * @return - description of the record
     */
    public String lookingForDescriptonById(String id) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String [] projection = { MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_DESCRIPTION }; //The Columns to return
        String selection = MyDatabaseContract.MyDatabaseEntry._ID + " = ?";         //The Column where to find
        String [] selectionArgs = { id };                                           //The values to find
        Cursor cursor = db.query(
                MyDatabaseContract.MyDatabaseEntry.TABLE_NAME,               // THe table to query
                projection,                                                 // THe columns to return (null - all)
                selection,                                                  // THe columns for the WHERE clause
                selectionArgs,                                              // The values for the WHERE clause
                null,                                                       // don't group the rows
                null,                                                       // don't filter by row groups
                null                                                        // The sort order
        );
        if (cursor.moveToFirst()) {
            String found = cursor.getString(cursor.getColumnIndex(MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_DESCRIPTION));
            cursor.close();
            db.close();
           return found;
        }
        else {
            cursor.close();
            db.close();
            return null;
        }
    }

    /**
     * Deletes a record
     * @param id - id of the record to be deleted
     */
    public void deleteSpecificInfo(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = MyDatabaseContract.MyDatabaseEntry._ID + " LIKE ?";

        String[] selectionArgs = {String.valueOf(id)};

        db.delete(MyDatabaseContract.MyDatabaseEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    /**
     * Clears the DB
     */
    public void clearDatabase () {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(MyDatabaseContract.MyDatabaseEntry.TABLE_NAME, null, null);

        db.close();
    }


    class MyDatabaseHelper extends SQLiteOpenHelper {

        private static final String TEXT_TYPE = " TEXT";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String DOUBLE_TYPE = " DOUBLE";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + MyDatabaseContract.MyDatabaseEntry.TABLE_NAME + " (" +
                        MyDatabaseContract.MyDatabaseEntry._ID + " INTEGER PRIMARY KEY," +
                        MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_DAY + TEXT_TYPE + COMMA_SEP +
                        MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_MONTH + TEXT_TYPE + COMMA_SEP +
                        MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_YEAR + TEXT_TYPE + COMMA_SEP +
                        MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_SUM + DOUBLE_TYPE + COMMA_SEP +
                        MyDatabaseContract.MyDatabaseEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + " )";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + MyDatabaseContract.MyDatabaseEntry.TABLE_NAME;


        public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }

}
