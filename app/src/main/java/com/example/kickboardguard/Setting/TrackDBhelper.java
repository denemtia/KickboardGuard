package com.example.kickboardguard.Setting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TrackDBhelper {
    private static final String TAG = "TrackDBhelper";

    //DB에 저장될 컬럼 구성
    public  static final String KEY_TABLE_ID = " _id ";
    public static final String KEY_USR_NAME = " name ";
    public static final String KEY_USR_EMAIL = " email ";
    public static final String KEY_USR_PHONE = " phone ";
    public static final String KEY_USR_DISTANCE = " distance ";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = " trackdb ";
    private static final String DATABASE_TABLE = " track ";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            " CREATE TABLE "+ DATABASE_TABLE +" ("
                    +KEY_TABLE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +KEY_USR_NAME+" TEXT, "
                    +KEY_USR_EMAIL+" TEXT, "
                    +KEY_USR_PHONE+" TEXT, "
                    + KEY_USR_DISTANCE+" REAL "+ ");";

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        // context = DB관리하는 놈
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }
    }
    public TrackDBhelper(Context context){
        this.mCtx =context;
    }

    public TrackDBhelper open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        Log.d("들어옴2","들어옴2");
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

   // 종료할 때 거리 측정
    public long trackDBallFetch(float distance){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USR_DISTANCE,distance);
        return mDb.insert(DATABASE_TABLE, null, contentValues);
    }
    //종료할 때 이름,전화번호,폰번호 측정
    public long trackDBallFetch1(String usrname, String usremail, String usrphone){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USR_NAME,usrname);
        contentValues.put(KEY_USR_EMAIL,usremail);
        contentValues.put(KEY_USR_PHONE,usrphone);
        return mDb.insert(DATABASE_TABLE, null, contentValues);
    }

    // id로 내림차순 정렬해서 가져오기
    public Cursor fetchAllListOrderByDec(){
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        Cursor res = mDb.rawQuery(" select * from " + DATABASE_TABLE + " order by " + KEY_TABLE_ID + " desc ", null);
        return res;
    }

    //_id로 값 삭제하기
    public int removeList(int id){
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        return mDb.delete(DATABASE_TABLE, " _id = ? ",new String[]{String.valueOf(id)});
    }


}
