package net.heybird.downloader;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDB  extends SQLiteOpenHelper{

    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "download.db";

    private static TaskDB mInstance = null;

    private final String SQL_CREATE_TABLE_TASK = "CREATE TABLE [tabTask] ("+
                                                      "[_id] INTEGER PRIMARY KEY AUTOINCREMENT, "+
                                                      "[name] NVARCHAR,"+
                                                      "[url] NVARCHAR,"+
                                                      "[path] NVARCHAR, "+
                                                      "[size] INT, "+
                                                      "[downloaded]  INT, "+
                                                      "[state] INT,"+
                                                      "[time] NVARCHAR)";

    private final String SQL_CREATE_TABLE_THREAD_INFO = "CREATE TABLE [tabPart] ("+
                                                  "[_id] INTEGER PRIMARY KEY AUTOINCREMENT, "+
                                                  "[task_id] INT, "+
                                                  "[offset] INT, "+
                                                  "[size] INT, "+
                                                  "[downloaded] INT)";

    public static TaskDB getInstance(Context context) {
        if(null==mInstance){
            synchronized (TaskDB.class) {
                if(null==mInstance) {
                    mInstance = new TaskDB(context);
                }
            }
        }
        return mInstance;
    }

    public static synchronized void clear() {
        mInstance = null;
    }

    protected long insertTask(String name, String url, String path, String time, 
            int size, int downloaded, int state) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("url", url);
        values.put("path", path);
        values.put("time", time);
        values.put("size", size);
        values.put("downloaded", downloaded);
        values.put("state", state);
        return getWritableDatabase().insert("tabTask", null, values);        
    }

    protected int updateTask(int id, int size, int downloaded, int state) {
        ContentValues values = new ContentValues();
        values.put("size", size);
        values.put("downloaded", downloaded);
        values.put("state", state);
        return getWritableDatabase().update("tabTask", values, "_id="+id, null);
    }
    
    protected DownloadTask getTask(int id) {
        DownloadTask result = null;
        Cursor c = query("select * from tabTask where _id="+id, null);
        if (c.moveToNext()) {
            result = new DownloadTask(c.getInt(c.getColumnIndex("_id")), 
                                    c.getString(c.getColumnIndex("name")) , 
                                    c.getString(c.getColumnIndex("url")), 
                                    c.getString(c.getColumnIndex("path")), 
                                    c.getInt(c.getColumnIndex("size")), 
                                    c.getInt(c.getColumnIndex("downloaded")), 
                                    c.getInt(c.getColumnIndex("state")),  
                                    c.getString(c.getColumnIndex("time")));
        }
        c.close();
        return result;
    }

    protected List<DownloadTask> getTasks() {
        List<DownloadTask> tasks = new ArrayList<DownloadTask>();
        Cursor c = query("select * from tabTask", null);
        while (c.moveToNext()) {
            tasks.add( new DownloadTask(c.getInt(c.getColumnIndex("_id")), 
                    c.getString(c.getColumnIndex("name")) , 
                    c.getString(c.getColumnIndex("url")), 
                    c.getString(c.getColumnIndex("path")), 
                    c.getInt(c.getColumnIndex("size")), 
                    c.getInt(c.getColumnIndex("downloaded")), 
                    c.getInt(c.getColumnIndex("state")),  
                    c.getString(c.getColumnIndex("time"))) );
        }
        c.close();
        return tasks;
    }

    protected int delTask(int id) {
        getWritableDatabase().delete("tabPart", "task_id="+id, null);
        return getWritableDatabase().delete("tabTask", "_id="+id, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = SQL_CREATE_TABLE_TASK;
        db.execSQL(sql);

        sql = SQL_CREATE_TABLE_THREAD_INFO;
        db.execSQL(sql);
    }

    private TaskDB(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
    }

    public Cursor query(String sql, String[] args) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, args);
        return cursor;
    }
}
