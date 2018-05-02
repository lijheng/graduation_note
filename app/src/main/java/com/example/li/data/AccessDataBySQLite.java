package com.example.li.data;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.li.bean.BNote;

import java.util.ArrayList;
import java.util.List;


/**
 * SQLite工具类
 * Created by li on 2018/4/1.
 * 标记：需重写
 */

public class AccessDataBySQLite {

    private Context mContext;
    public static String mDatabase= "note_ev.db";;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private final int version=1;

    public AccessDataBySQLite(Context context){
        mContext = context;
        mySQLiteOpenHelper = new MySQLiteOpenHelper(mContext,mDatabase,1);
        mySQLiteOpenHelper.getReadableDatabase();//创建数据库文件
    }

    /**
     * 向note中插入数据
     * @param note
     */
    public void insertNote(BNote note){
        SQLiteDatabase db = mySQLiteOpenHelper.getReadableDatabase();
        String sql = "insert into note values(?,?,?)";
        db.execSQL(sql,new Object[]{note.getId(),note.getContent(),note.getDate()});
    }

    /**
     * 删除指定数据
     * @param note_id 要删除数据的id
     */
    public void deleteNote(long note_id){
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();
        String sql = "delete from note where id=?";
        sqLiteDatabase.execSQL(sql,new Object[]{note_id});
    }

    public void closeDB(){
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();
        sqLiteDatabase.close();
    }


    /**
     * 更新数据
     * @param bNote
     */
    public void updateNote(BNote bNote){
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();
        String sql = "update note set content=?,date=?where id = ?";
        sqLiteDatabase.execSQL(sql,new Object[]{bNote.getContent(),bNote.getDate(),bNote.getId()});
    }

    /**
     * 查询所有数据,存储到List中
     */
    public List<BNote> queryNote(){
        List<BNote> bNotes = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from note order by date asc",null);
        if (cursor.moveToFirst()){
            do {
                BNote bNote = new BNote();
                bNote.setId(cursor.getLong(cursor.getColumnIndex("id")));
                bNote.setContent(cursor.getString(cursor.getColumnIndex("content")));
                bNote.setDate(cursor.getString(cursor.getColumnIndex("date")));
                bNotes.add(bNote);
            }while (cursor.moveToNext());
        }
       cursor.close();
        return bNotes;
    }

    public List<BNote> queryNote(String newText){
        List<BNote> bNotes = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        String sql="select *from note where content like '%"+newText+"%'";
        cursor = sqLiteDatabase.rawQuery(sql,null);
        if (cursor.moveToFirst()){
            do {
                BNote bNote = new BNote();
                bNote.setId(cursor.getLong(cursor.getColumnIndex("id")));
                bNote.setContent(cursor.getString(cursor.getColumnIndex("content")));
                bNote.setDate(cursor.getString(cursor.getColumnIndex("date")));
                bNotes.add(bNote);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return bNotes;
    }

    /**
     * 根据id查询
     */
    public BNote queryNote(long id){
        BNote bNote = new BNote();
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from note where id =?",new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()){
            bNote.setId(cursor.getLong(cursor.getColumnIndex("id")));
            bNote.setContent(cursor.getString(cursor.getColumnIndex("content")));
            bNote.setDate(cursor.getString(cursor.getColumnIndex("date")));
        }
        cursor.close();
        return bNote;
    }

    class MySQLiteOpenHelper extends SQLiteOpenHelper {

        MySQLiteOpenHelper(Context context, String name, int version){
            super(context,name,null,version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
//            创建note数据表
            String sql1 = "create table note(id INTEGER primary key,content TEXT,date TEXT)";
            db.execSQL(sql1);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
