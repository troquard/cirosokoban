package eu.yalacirodev.sokoban;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "CiroSokoban.db";
    private static final String LEVEL_TABLE = "Level";
    private static final String L_ID = "lev_id";
    private static final String L_NAME = "lev_name";
    private static final String L_NUM_MOVES = "lev_num_moves";

    public static final int NOT_COMPLETED = Integer.MAX_VALUE;


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        // SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + LEVEL_TABLE + " ( " +
                L_ID + " INTEGER PRIMARY KEY, " +
                L_NAME + " TEXT, " +
                L_NUM_MOVES + " INTEGER ) ;";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //String query = "DROP TABLE IF EXISTS " + DB_NAME + " ;";
        //db.execSQL(query);
        //onCreate(db);
    }


    public Boolean insertData(int lev_id, String lev_name, int lev_num_moves) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(L_ID, lev_id);
        contentValues.put(L_NAME, lev_name);
        contentValues.put(L_NUM_MOVES, lev_num_moves);

        long result = db.insert(LEVEL_TABLE, null, contentValues);

        return (result != -1);
    }


    public Boolean deleteData(int lev_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = L_ID + " = " + lev_id;
        long result = db.delete(LEVEL_TABLE, whereClause, null);

        // exactly one row has been deleted?
        return (result == 1);
    }

    // TODO use db.update with return value
    public void updateData(int lev_id, int lev_num_moves) {

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "UPDATE " + LEVEL_TABLE +
                " SET " + L_NUM_MOVES + " = " + lev_num_moves +
                " WHERE " + L_ID + " = " + lev_id;

        db.execSQL(query);
    }

    public int getNumMoves(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + L_NUM_MOVES + " FROM " + LEVEL_TABLE
                + " WHERE " + L_ID + " = " + id + " ;";
        Cursor cursor = db.rawQuery(query, null);
        int numMoves;
        if (cursor.moveToFirst()) { // first row of the cursor
            numMoves = Integer.parseInt(cursor.getString(0)); // first column of the cursor
            cursor.close();
            return numMoves;
        } else {
            cursor.close();
            return NOT_COMPLETED;
        }
    }


    public Cursor getAllData () {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + LEVEL_TABLE;
        return db.rawQuery(query,null);
    }
}
