package net.heybird.downloader;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDB  extends SQLiteOpenHelper{

    public static enum TaskState {
        STARTED,
        PAUSED,
        CANCELED,
        DOWNLOADED,
        DELETED
    }

    public static class DownloadTask {
        public int id;
        public String name;
        public String url;
        public String path;
        public int size;
        public int downloaded;
        public TaskState state;
        public String time;
        public List<TaskPart> threads;
    }

    public static class TaskPart {
        public int id;
        public int taskId;
        public int offset;
        public int size;
        public int downloaded;
    }

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
