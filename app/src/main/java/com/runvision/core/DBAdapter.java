package com.runvision.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


//import java.sql.SQLException;

/**
 * Created by zhuhuilong on 2017/9/14.
 */

public class DBAdapter
{

    public static final String KEY_ROWID="id";
    public static final String KEY_NAME="name";//姓名 1
    public static final String KEY_GENDER="gender";  //性别 2
    public static final String KEY_ID_CARD="id_card";//身份证号 3
    public static final String KEY_FACEPIC="facepic";      //人证对比图片     4
    public static final String KEY_IDCARDPIC="idcardpic";   //身份证图片        5
    public static final String KEY_SIGN_IN ="sign_in";//签到时间
    
    public static final String TAG="DBAdapter";

    public static final String DATABASE_NAME="OneToMany";
    public static final String DATABASE_TABLE="titles";
    public static final int DATABASE_VERSION=1;
  //  public static final String DATABASE_CREATE="Greate table titles (_id integer primary key autoincrement,"
   //                                                     +"isbn text,title text, "
   //                                                     +"publisher text );";


    public static final String DATABASE_CREATE = "create table titles ("

            + "id integer primary key autoincrement, "

            + "name String, "

            + "gender String, "

            + "id_card String, "

            + "facepic String,"

            + "idcardpic String,"

            + "sign_in String)";



    private final Context context;
    private  DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context=ctx;
        DBHelper= new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        public DatabaseHelper(Context context) {//鏋勯�犲嚱鏁�,鎺ユ敹涓婁笅鏂囦綔涓哄弬鏁�,鐩存帴璋冪敤鐨勭埗绫荤殑鏋勯�犲嚱鏁�
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS titles");
            onCreate(db);
            if (oldVersion==1 && newVersion==2) {
                db.execSQL("ALTER TABLE restaurants ADD phone TEXT;");
            }
        }



    }
    public DBAdapter open()throws SQLException //打开数据库
    {
        db=DBHelper.getWritableDatabase();
        return this;
    }

    public void close()//关闭数据库
    {
        DBHelper.close();
    }





    public long insertTitle(String name,String gender,String id_card,String facepic,String idcardpic,String sign_in)//向数据库中插入一个标题
    {
        ContentValues inittialValues = new ContentValues();

        inittialValues.put(KEY_NAME,name);
        inittialValues.put(KEY_GENDER,gender);
        inittialValues.put(KEY_ID_CARD,id_card);
        inittialValues.put(KEY_FACEPIC,facepic);
        inittialValues.put(KEY_IDCARDPIC,idcardpic);
        inittialValues.put(KEY_SIGN_IN,sign_in);
        
        return db.insert(DATABASE_TABLE,null,inittialValues);
    }

    public boolean deleteTitle(long rowId)//删除一个指定标题
    {
        return db.delete(DATABASE_TABLE,KEY_ROWID + "=" + rowId,null)>0;
    }

    public Cursor getAllTitles()
    {
        return  db.query(DATABASE_TABLE,new String[]
                {
                        KEY_ROWID,
                        KEY_NAME,
                        KEY_GENDER,
                        KEY_ID_CARD,
                        KEY_FACEPIC,
                        KEY_IDCARDPIC,
                        KEY_SIGN_IN
                },
                null,
                null,
                null,
                null,
                null);

    }
    
    public Cursor getDataByAccount(String account)throws SQLException
    {
        Cursor mCursor = db.query(DATABASE_TABLE,new String[]
                {},
               "account=?",new String[]{account},null,null,null
                );
        if(mCursor!=null)
        {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor getTitle(long rowId)throws SQLException//检索一个指定标题
    {
        Cursor mCursor = db.query(true,DATABASE_TABLE,new String[]
                {
        		 KEY_ROWID,
                 KEY_NAME,
                 KEY_GENDER,
                 KEY_ID_CARD,
                 KEY_FACEPIC,
                 KEY_IDCARDPIC,
                 KEY_SIGN_IN,
                },
                KEY_ROWID + "=" + rowId,
                null,
                null,
                null,
                null,
                null);
        if(mCursor!=null)
        {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor getMillBydate(String date, String account)throws SQLException
    {
        Cursor mCursor = db.query(DATABASE_TABLE,new String[]
                {},
               "date2=? and date2=?",new String[]{date,account},null,null,null
                );
        if(mCursor!=null)
        {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    
/*    public Cursor getMillBydate2(String date)throws SQLException//妫�绱竴涓寚瀹氭爣棰�
    {
        Cursor mCursor = db.rawQuery("select * from titles where date1=? ",
        		new String[]{date,null});
        if(mCursor!=null)
        {
            mCursor.moveToFirst();
        }
        return mCursor;

    }*/
    public boolean updateTitle(String rowId,String name, String gender, String idcard, String facepic, String idcardpic,String sign_in)//更新一个标题
    {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME,name);
        args.put(KEY_GENDER,gender);
        args.put(KEY_ID_CARD,idcard);
        args.put(KEY_FACEPIC,facepic);
        args.put(KEY_IDCARDPIC,idcardpic);
        args.put(KEY_SIGN_IN,sign_in);
        return db.update(DATABASE_TABLE,args,KEY_ROWID + "=" + rowId,null)>0;
    }


}




