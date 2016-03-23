package com.android.incongress.cd.conference.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.android.incongress.cd.conference.base.AppApplication;
import com.android.incongress.cd.conference.beans.ActivityBean;
import com.android.incongress.cd.conference.beans.AlertBean;
import com.android.incongress.cd.conference.base.Constants;
import com.android.incongress.cd.conference.beans.MeetingBean;
import com.android.incongress.cd.conference.beans.SpeakerBean;
import com.android.incongress.cd.conference.utils.MyLogger;
import com.android.incongress.cd.conference.utils.StringUtils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Pair;

/**
 * 数据库字段的建立，以及数据库的删除和重构
 */
public class DbAdapter {
    private static DbAdapter instance = null;

    public static int DB_VERSION = 2;
    private Context mContext;
    private SQLiteDatabase mDb = null;
    private DbHelper mDbHelper = null;

    public static DbAdapter getInstance() {
        if (instance == null) {
            instance = new DbAdapter(AppApplication.instance().getApplicationContext());
        }
        return instance;
    }

    private class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context context) {
            super(context, context.getFilesDir() + File.separator + Constants.DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTablesIfNotExist(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + ConferenceTables.TABLE_INCONGRESS_AD);
                db.execSQL("DROP TABLE IF EXISTS " + ConferenceTables.TABLE_INCONGRESS_ALERT);
                db.execSQL("DROP TABLE IF EXISTS " + ConferenceTables.TABLE_INCONGRESS_CLASS);
                db.execSQL("DROP TABLE IF EXISTS " + ConferenceTables.TABLE_INCONGRESS_CONFERENCES);
                db.execSQL("DROP TABLE IF EXISTS " + ConferenceTables.TABLE_INCONGRESS_CONFIELD);
                db.execSQL("DROP TABLE IF EXISTS " + ConferenceTables.TABLE_INCONGRESS_EXHIBITOR);
                db.execSQL("DROP TABLE IF EXISTS " + ConferenceTables.TABLE_INCONGRESS_EXHIBITORACTYIVITY);
                db.execSQL("DROP TABLE IF EXISTS " + ConferenceTables.TABLE_INCONGRESS_MAP);
                db.execSQL("DROP TABLE IF EXISTS " + ConferenceTables.TABLE_INCONGRESS_MEETING);
                db.execSQL("DROP TABLE IF EXISTS " + ConferenceTables.TABLE_INCONGRESS_NEWS);
                db.execSQL("DROP TABLE IF EXISTS " + ConferenceTables.TABLE_INCONGRESS_Note);
                db.execSQL("DROP TABLE IF EXISTS " + ConferenceTables.TABLE_INCONGRESS_SESSION);
                db.execSQL("DROP TABLE IF EXISTS " + ConferenceTables.TABLE_INCONGRESS_SPEAKER);
                db.execSQL("DROP TABLE IF EXISTS " + ConferenceTables.TABLE_INCONGRESS_TIPS);
                db.execSQL("DROP TABLE IF EXISTS " + ConferenceTables.TABLE_INCONGRESS_ROLE);
                onCreate(db);

                SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(mContext);
                Editor edit = preference.edit();
                edit.putInt(Constants.PREFERENCE_DB_VERSION, 0);
                edit.commit();
            }
        }

        public void dropTables(SQLiteDatabase db, String tableName) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }

        public void createTablesIfNotExist(SQLiteDatabase db) {
            //create table CONFERENCES
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + ConferenceTables.TABLE_INCONGRESS_CONFERENCES + " ("
                    + ConferenceTableField.CONFERENCES_CONFERENCESID + " INT PRIMARY KEY,"
                    + ConferenceTableField.CONFERENCES_ABBREVIATION + " TEXT,"
                    + ConferenceTableField.CONFERENCES_ADMINUSERID + " INT,"
                    + ConferenceTableField.CONFERENCES_CODE + " TEXT,"
                    + ConferenceTableField.CONFERENCES_CONFERENCESADDRESS + " TEXT,"
                    + ConferenceTableField.CONFERENCES_CONFERENCESSTARTTIME + " TEXT,"
                    + ConferenceTableField.CONFERENCES_CONFERENCESENDTIME + " TEXT,"
                    + ConferenceTableField.CONFERENCES_CONFERENCESNAME + " TEXT,"
                    + ConferenceTableField.CONFERENCES_CONFERENCESSTATE + " INT,"
                    + ConferenceTableField.CONFERENCES_CREATETIME + " TEXT,"
                    + ConferenceTableField.CONFERENCES_ENLINK + " TEXT,"
                    + ConferenceTableField.CONFERENCES_POSTERSHOWSTATE + " INT,"
                    + ConferenceTableField.CONFERENCES_POSTERSTATE + " INT,"
                    + ConferenceTableField.CONFERENCES_VIEWSTATE + " INT,"
                    + ConferenceTableField.CONFERENCES_ZHLINK + " TEXT" + ")");

            //create table SESSION
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + ConferenceTables.TABLE_INCONGRESS_SESSION + " ("
                    + ConferenceTableField.SESSION_SESSIONGROUPID + " INT PRIMARY KEY,"
                    + ConferenceTableField.SESSION_SESSIONNAME + " TEXT,"
                    + ConferenceTableField.SESSION_CLASSESID + " INT,"
                    + ConferenceTableField.SESSION_SESSIONDAY + " TEXT,"
                    + ConferenceTableField.SESSION_STARTTIME + " TEXT,"
                    + ConferenceTableField.SESSION_ENDTIME + " TEXT,"
                    + ConferenceTableField.SESSION_CONFIELDID + " INT,"
                    + ConferenceTableField.SESSION_REMARK + " TEXT,"
                    + ConferenceTableField.SESSION_ATTENTION + " INT,"
                    + ConferenceTableField.SESSION_SESSIONNAME_EN + " TEXT,"
                    + ConferenceTableField.SESSION_FACULTYID + " TEXT,"
                    + ConferenceTableField.SESSION_ROLEID + " TEXT"
                    + ")");

            //create table MEETING
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + ConferenceTables.TABLE_INCONGRESS_MEETING + " ("
                    + ConferenceTableField.MEETING_MEETINGID + " INT PRIMARY KEY,"
                    + ConferenceTableField.MEETING_TOPIC + " TEXT,"
                    + ConferenceTableField.MEETING_SUMMARY + " TEXT,"
                    + ConferenceTableField.MEETING_CLASSESID + " INT,"
                    + ConferenceTableField.MEETING_MEETINGDAY + " TEXT,"
                    + ConferenceTableField.MEETING_STARTTIME + " TEXT,"
                    + ConferenceTableField.MEETING_ENDTIME + " TEXT,"
                    + ConferenceTableField.MEETING_CONFIELDID + " INT,"
                    + ConferenceTableField.MEETING_SESSIONGROUPID + " INT,"
                    + ConferenceTableField.MEETING_ATTENTION + " INT,"
                    + ConferenceTableField.MEETING_TOPIC_EN + " TEXT,"
                    + ConferenceTableField.MEETING_SUMMARY_EN + " TEXT,"
                    + ConferenceTableField.MEETING_FACULTYID + " TEXT,"
                    + ConferenceTableField.MEETING_ROLEID + " TEXT"
                    + ")");

            //create table CONFIELD
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + ConferenceTables.TABLE_INCONGRESS_CONFIELD + " ("
                    + ConferenceTableField.CONFIELD_CONFIELDID + " INT PRIMARY KEY,"
                    + ConferenceTableField.CONFIELD_CONFIELDNAME + " TEXT" + ")");

            //create table CLASS
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + ConferenceTables.TABLE_INCONGRESS_CLASS + " ("
                    + ConferenceTableField.CLASS_CLASSESID + " INT PRIMARY KEY,"
                    + ConferenceTableField.CLASS_CONFERENCESID + " INT,"
                    + ConferenceTableField.CLASS_CLASSESCAPACITY + " INT,"
                    + ConferenceTableField.CLASS_CLASSESCODE + " TEXT,"
                    + ConferenceTableField.CLASS_CLASSESLOCATION + " TEXT,"
                    + ConferenceTableField.CLASS_MAPNAME + " TEXT,"
                    + ConferenceTableField.CLASS_CLASSLEVEL + " INT,"
                    + ConferenceTableField.CLASS_CLASSESCODEPINGYIN + " TEXT" + ")");

            //create table SPEAKER
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + ConferenceTables.TABLE_INCONGRESS_SPEAKER + " ("
                    + ConferenceTableField.SPEAKER_SPEAKERID + " INT PRIMARY KEY,"
                    + ConferenceTableField.SPEAKER_CONFERENCESID + " INT,"
                    + ConferenceTableField.SPEAKER_REMARK + " TEXT,"
                    + ConferenceTableField.SPEAKER_SPEAKERFROM + " TEXT,"
                    + ConferenceTableField.SPEAKER_SPEAKERNAME + " TEXT,"
                    + ConferenceTableField.SPEAKER_TYPE + " INT,"
                    + ConferenceTableField.SPEAKER_SPEAKERNAMEPINGYIN + " TEXT,"
                    + ConferenceTableField.SPEAKER_ATTENTION + " INT default 0,"
                    + ConferenceTableField.SPEAKER_ENNAME + " TEXT,"
                    + ConferenceTableField.SPEAKER_ENTITYID + " INT,"
                    + ConferenceTableField.SPEAKER_PYKEY + " TEXT,"
                    + ConferenceTableField.SPEAKER_USERID + " INT"
                    + ")");

            //create table EXHIBITOR
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + ConferenceTables.TABLE_INCONGRESS_EXHIBITOR + " ("
                    + ConferenceTableField.EXHIBITOR_EXHIBITORSID + " INT PRIMARY KEY,"
                    + ConferenceTableField.EXHIBITOR_ADDRESS + " TEXT,"
                    + ConferenceTableField.EXHIBITOR_TITLE + " TEXT,"
                    + ConferenceTableField.EXHIBITOR_INFO + " TEXT,"
                    + ConferenceTableField.EXHIBITOR_PHONE + " TEXT,"
                    + ConferenceTableField.EXHIBITOR_FAX + " TEXT,"
                    + ConferenceTableField.EXHIBITOR_NET + " TEXT,"
                    + ConferenceTableField.EXHIBITOR_LOCATION + " TEXT,"
                    + ConferenceTableField.EXHIBITOR_LOGO + " TEXT,"
                    + ConferenceTableField.EXHIBITOR_ATTENTION + " INT,"
                    + ConferenceTableField.EXHIBITOR_STORELOGO + " INT,"
                    + ConferenceTableField.EXHIBITOR_ADDRESS_EN + " TEXT,"
                    + ConferenceTableField.EXHIBITOR_TITLE_EN + " TEXT,"
                    + ConferenceTableField.EXHIBITOR_INFO_EN + " TEXT,"
                    + ConferenceTableField.EXHIBITOR_EXHIBITOR_LOCATION + " TEXT,"
                    + ConferenceTableField.EXHIBITOR_MAP_NAME + " TEXT"
                    + ")");

            //create table EXHIBITORACTYIVITY
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + ConferenceTables.TABLE_INCONGRESS_EXHIBITORACTYIVITY + " ("
                    + ConferenceTableField.EXHIBITORACTYIVITY_ACTIVITYID + " INT PRIMARY KEY,"
                    + ConferenceTableField.EXHIBITORACTYIVITY_NAME + " TEXT,"
                    + ConferenceTableField.EXHIBITORACTYIVITY_VERSION + " INT,"
                    + ConferenceTableField.EXHIBITORACTYIVITY_HOT + " INT,"
                    + ConferenceTableField.EXHIBITORACTYIVITY_URL + " TEXT,"
                    + ConferenceTableField.EXHIBITORACTYIVITY_LOGO + " TEXT,"
                    + ConferenceTableField.EXHIBITORACTYIVITY_STORELOGO + " INT,"
                    + ConferenceTableField.EXHIBITORACTYIVITY_STOREURL + " INT,"
                    + ConferenceTableField.EXHIBITORACTYIVITY_ATTENTION + " INT" + ")");

            //create table Notes
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + ConferenceTables.TABLE_INCONGRESS_Note + " ("
                    + ConferenceTableField.NOTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ConferenceTableField.NOTES_CONTENTS + " TEXT,"
                    + ConferenceTableField.NOTES_START + " varchar,"
                    + ConferenceTableField.NOTES_END + " varchar,"
                    + ConferenceTableField.NOTES_DATE + " varchar,"
                    + ConferenceTableField.NOTES_ROOM + " varchar,"
                    + ConferenceTableField.NOTES_CREATETIME + " varchar,"
                    + ConferenceTableField.NOTES_UPDATETIME + " varchar,"
                    + ConferenceTableField.NOTES_SESSIONID + " varchar,"
                    + ConferenceTableField.NOTES_CLASSID + " varchar,"
                    + ConferenceTableField.NOTES_MEETINGID + " varchar,"
                    + ConferenceTableField.NOTES_TITLE + " varchar" + ")");
            //create table TIPS
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + ConferenceTables.TABLE_INCONGRESS_TIPS + " ("
                    + ConferenceTableField.TIPS_TIPSID + " INT PRIMARY KEY,"
                    + ConferenceTableField.TIPS_CONFERENCESID + " INT,"
                    + ConferenceTableField.TIPS_TIPSCONTENT + " TEXT,"
                    + ConferenceTableField.TIPS_TIPSTIME + " TEXT,"
                    + ConferenceTableField.TIPS_TIPSTITLE + " TEXT,"
                    + ConferenceTableField.TIPS_TIPSTITLE_EN + " TEXT,"
                    + ConferenceTableField.TIPS_TIPSCONTENT_EN + " TEXT" + ")");

            //create table NEWS
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + ConferenceTables.TABLE_INCONGRESS_NEWS + " ("
                    + ConferenceTableField.NEWS_NEWSID + " INT PRIMARY KEY ,"
                    + ConferenceTableField.NEWS_NEWSTITLE + " TEXT,"
                    + ConferenceTableField.NEWS_NEWSSUMMARY + " TEXT,"
                    + ConferenceTableField.NEWS_NEWSIMAGEURL + " TEXT,"
                    + ConferenceTableField.NEWS_NEWSSOURCE + " TEXT,"
                    + ConferenceTableField.NEWS_NEWSDATE + " TEXT,"
                    + ConferenceTableField.NEWS_NEWSSTOREITEM + " INT,"
                    + ConferenceTableField.NEWS_NEWSSTOREDETAIL + " INT,"
                    + ConferenceTableField.NEWS_NEWSCONTENT + " TEXT" + ")");

            //create table ALERT
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + ConferenceTables.TABLE_INCONGRESS_ALERT + " ("
                    + ConferenceTableField.ALERT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ConferenceTableField.ALERT_DATE + " varchar,"
                    + ConferenceTableField.ALERT_ENABLE + " INTEGER,"
                    + ConferenceTableField.ALERT_RELATIVEID + " varchar,"
                    + ConferenceTableField.ALERT_REPEATDISTANCE + " INTEGER,"
                    + ConferenceTableField.ALERT_REPEATTIMES + " INTEGER,"
                    + ConferenceTableField.ALERT_TITLE + " varchar,"
                    + ConferenceTableField.ALERT_START + " varchar,"
                    + ConferenceTableField.ALERT_END + " varchar,"
                    + ConferenceTableField.ALERT_ROOM + " varchar,"
                    + ConferenceTableField.ALERT_TYPE + " INTEGER" + ")");

            //create table AD
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + ConferenceTables.TABLE_INCONGRESS_AD + " ("
                    + ConferenceTableField.AD_ADID + " INT PRIMARY KEY,"
                    + ConferenceTableField.AD_CONFERENCESID + " INT,"
                    + ConferenceTableField.AD_ADIMAGE + " TEXT,"
                    + ConferenceTableField.AD_ADLEVEL + " INT,"
                    + ConferenceTableField.AD_ADLINK + " TEXT,"
                    + ConferenceTableField.AD_IMGURL + " TEXT,"
                    + ConferenceTableField.AD_VERSION + " INT,"
                    + ConferenceTableField.AD_VIEWLEVEL + " INT,"
                    + ConferenceTableField.AD_STOPTIME + " INT" + ")");

            //create table MAP
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + ConferenceTables.TABLE_INCONGRESS_MAP + " ("
                    + ConferenceTableField.MAP_CONFERENCESMAPID + " INT PRIMARY KEY,"
                    + ConferenceTableField.MAP_CONFERENCESID + " INT,"
                    + ConferenceTableField.MAP_MAPREMARK + " TEXT,"
                    + ConferenceTableField.MAP_MAPURL + " TEXT" + ")");

            //create table ROLE
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + ConferenceTables.TABLE_INCONGRESS_ROLE + " ("
                    + ConferenceTableField.ROLE_ID + " INT PRIMARY KEY,"
                    + ConferenceTableField.ROLE_NAME + " TEXT,"
                    + ConferenceTableField.ROLE_ENNAME + " TEXT" + ")");
        }
    }

    private DbAdapter(Context context) {
        mContext = context;
        open();
        mDbHelper.createTablesIfNotExist(mDb);
        close();
    }

    public void open() throws SQLException {
        mDbHelper = new DbHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    public long insert(String tableName, DbBean bean) {
        ContentValues values = new ContentValues();
        List<Pair<String, String>> container = bean.getContainer();

        for (Pair<String, String> pair : container) {
            try {
                values.put(pair.first, pair.second);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        if (0 != values.size()) {
            return mDb.insert(tableName, null, values);
        }

        // error
        return -1;
    }

    public long insertNotes(String tableName, DbBean bean) {
        ContentValues values = new ContentValues();
        List<Pair<String, String>> container = bean.getContainer();

        for (Pair<String, String> pair : container) {
            try {
                if (pair.first.equals("id")) continue;
                values.put(pair.first, pair.second);
            } catch (Exception e) {
            }
        }

        if (0 != values.size()) {
            return mDb.insert(tableName, ConferenceTableField.NOTES_ID, values);
        }

        // error
        return -1;
    }

    public long deleteNotes(String id) {

        return mDb.delete(ConferenceTables.TABLE_INCONGRESS_Note, " id = ?", new String[]{id});

    }

    public long deleteItems(String table, String whereArgs, String[] args) {

        return mDb.delete(table, whereArgs, args);

    }

    public long insertOnConflict(String tableName, DbBean bean, int conflictAlgorithm) {
        ContentValues values = new ContentValues();
        List<Pair<String, String>> container = bean.getContainer();

        for (Pair<String, String> pair : container) {
            try {
                values.put(pair.first, pair.second);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        if (0 != values.size()) {
            return mDb.insertWithOnConflict(tableName, null, values, conflictAlgorithm);
        }

        // error
        return -1;
    }

    public long update(String tableName, DbBean bean, String whereClause, String[] whereArgs) {
        ContentValues values = new ContentValues();
        List<Pair<String, String>> container = bean.getContainer();

        for (Pair<String, String> pair : container) {
            try {
                values.put(pair.first, pair.second);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        if (0 != values.size()) {
            return mDb.update(tableName, values, whereClause, whereArgs);
        }
        return -1;
    }

    private static final String FETCH_LIST_SQL = "SELECT %s FROM %s";
    private static final String FETCH_GROUP_LIST_SQL = "SELECT %s FROM %s GROUP BY %s";
    private static final String FETCH_LIST_SQL_WITH_SORT = "SELECT %s FROM %s ORDER BY PINYIN";
    private static final String FETCH_LIST_BY_SQL = "SELECT %s FROM %s WHERE %s=%s ORDER BY PINYIN";
    private static final String QUERY_DICTIONARY = "SELECT %s FROM %s WHERE %s='%s'";

    public String queryDictionary(String tableName, String keyColumn,
                                  String keyValue, String valueColumn) {
        Cursor cursor = null;
        String result = null;

        String sqlString = String.format(QUERY_DICTIONARY, valueColumn,
                tableName, keyColumn, keyValue);
        cursor = mDb.rawQuery(sqlString, null);

        if (null != cursor && cursor.moveToFirst()) {
            result = cursor.getString(0);
        }

        if (cursor != null) {
            cursor.close();
        }
        return result;
    }

    public void deleteTable(String tableName) {
        mDb.execSQL("DROP TABLE " + tableName);
    }

    public String queryDictionaryBySql(String sqlString) {
        Cursor cursor = null;
        String result = null;

        cursor = mDb.rawQuery(sqlString, null);

        if (null != cursor && cursor.moveToFirst()) {
            result = cursor.getString(0);
        }
        if (cursor != null) {
            cursor.close();
        }
        return result;
    }

    public void execSql(String sql) {
        mDb.execSQL(sql);
    }

    // fetch list of one table
    public List<String> fetchSortedList(String tableName, String columnName) {
        Cursor cursor = null;
        List<String> list = null;

        String sqlString = String.format(FETCH_LIST_SQL_WITH_SORT, columnName,
                tableName);
        cursor = mDb.rawQuery(sqlString, null);
        if (null != cursor && cursor.moveToFirst()) {
            list = new ArrayList<String>();
            do {
                String data = cursor.getString(0);
                list.add(data);
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public List<String> fetchRawList(String tableName, String columnName) {
        Cursor cursor = null;
        List<String> list = null;

        String sqlString = String.format(FETCH_LIST_SQL, columnName, tableName);
        cursor = mDb.rawQuery(sqlString, null);
        if (null != cursor && cursor.moveToFirst()) {
            list = new ArrayList<String>();
            do {
                String data = cursor.getString(0);
                list.add(data);
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public List<String> fetchRawGroupList(String tableName, String columnName, String groupcolumnName) {
        Cursor cursor = null;
        List<String> list = null;

        String sqlString = String.format(FETCH_GROUP_LIST_SQL, columnName, tableName, groupcolumnName);
        cursor = mDb.rawQuery(sqlString, null);
        if (null != cursor && cursor.moveToFirst()) {
            list = new ArrayList<String>();
            do {
                String data = cursor.getString(0);
                list.add(data);
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     * fetch list of one table group by one columnName
     */


    /**
     * fetch list of one table
     *
     * @param sqlString
     * @return
     */
    public List<List<String>> fetchListBySql(String sqlString) {
        Cursor cursor = null;
        List<List<String>> list = null;
        System.out.println("fetchListBySql====" + sqlString);
        cursor = mDb.rawQuery(sqlString, null);
        if (null != cursor && cursor.moveToFirst()) {
            list = new ArrayList<List<String>>();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                List<String> subList = new ArrayList<String>();
                list.add(subList);
            }
            do {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String data = cursor.getString(i);
                    list.get(i).add(data);
                }
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     * 判断某张表是否存在
     * @param tabName 表名
     * @return
     */
    public boolean tabIsExist(String tabName) {
        boolean result = false;
        if (tabName == null) {
            return false;
        }
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
                    + tabName.trim() + "' ";
            cursor = mDb.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }

            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
        }
        return result;
    }

    public void beginTransaction() {
        mDb.beginTransaction();
    }

    public void endTransaction() {
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
    }

    public void endFailedTransaction() {
        mDb.endTransaction();
    }


    public void dropTables(String tableName) {
        mDbHelper.dropTables(mDb, tableName);
    }

    // [2012-7-1 Terry]: 
    public void resetTable(String tableName) {
        try {
            int ret = mDb.delete(tableName, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //查询出某张表的数据总量
    public int getTableItemCount(String tablename) {
        String sql = "select * from " + tablename;
        Cursor cursor = mDb.rawQuery(sql, null);
        return cursor.getCount();
    }

    public int getTableItemCountSql(String sql) {
        Cursor cursor = mDb.rawQuery(sql, null);
        return cursor.getCount();
    }

    /**
     * 根据专家id，查询该专家的session和meeting
     *
     * @param speakerId
     */
    public List<ActivityBean> getSessionAndMeetingBySpeakerId(int speakerId) {
        List<ActivityBean> beans = new ArrayList<ActivityBean>();

        //查询session表
        Cursor cursorSession = mDb.query(ConferenceTables.TABLE_INCONGRESS_SESSION, null,
                "facultyId like ? or facultyId like ? or facultyId like ? COLLATE NOCASE",
                new String[]{speakerId + ",%", "%," + speakerId + ",%", "%," + speakerId}, null, null, null);
        Cursor cursorSessionLocation = null;
        if (cursorSession != null) {
            while (cursorSession.moveToNext()) {
                ActivityBean activity = new ActivityBean();
                activity.setActivityName(cursorSession.getString(cursorSession.getColumnIndex(ConferenceTableField.SESSION_SESSIONNAME)));

                String facultyId = cursorSession.getString(cursorSession.getColumnIndex(ConferenceTableField.SESSION_FACULTYID));
                String roleId = cursorSession.getString(cursorSession.getColumnIndex(ConferenceTableField.SESSION_ROLEID));
                if (StringUtils.isEmpty(facultyId)) {
                    activity.setType(-1);
                } else {
                    String[] facultyIds = facultyId.split(",");
                    String[] roleIds = roleId.split(",");
                    activity.setType(getType(speakerId + "", facultyIds, roleIds));
                }

                activity.setTime(cursorSession.getString(cursorSession.getColumnIndex(ConferenceTableField.SESSION_SESSIONDAY))
                        + " " + cursorSession.getString(cursorSession.getColumnIndex(ConferenceTableField.SESSION_STARTTIME)));
                activity.setMeetingId(cursorSession.getInt(cursorSession.getColumnIndex(ConferenceTableField.SESSION_SESSIONGROUPID)));
                activity.setStart_time(cursorSession.getString(cursorSession.getColumnIndex(ConferenceTableField.SESSION_STARTTIME)));
                activity.setEnd_time(cursorSession.getString(cursorSession.getColumnIndex(ConferenceTableField.SESSION_ENDTIME)));
                activity.setDate(cursorSession.getString(cursorSession.getColumnIndex(ConferenceTableField.SESSION_SESSIONDAY)));
                activity.setActivityNameEN(cursorSession.getString(cursorSession.getColumnIndex(ConferenceTableField.SESSION_SESSIONNAME_EN)));
                activity.setIsSessionOrMeeting(AlertBean.TYPE_SESSTION);

                {
                    //设置身份名称
                    Cursor cursorTypeName = mDb.query(ConferenceTables.TABLE_INCONGRESS_ROLE, null, "roleId=?", new String[]{activity.getType() + ""}, null, null, null);
                    if (cursorTypeName != null) {
                        if (cursorTypeName.moveToFirst()) {
                            activity.setTypeName(cursorTypeName.getString(cursorTypeName.getColumnIndex(ConferenceTableField.ROLE_NAME)));
                        }
                    }
                    cursorTypeName.close();
                }

                int classId = cursorSession.getInt(cursorSession.getColumnIndex(ConferenceTableField.SESSION_CLASSESID));

                //根据classId获取到会议地点的中文名
                try {
                    cursorSessionLocation = mDb.query(ConferenceTables.TABLE_INCONGRESS_CLASS, null, "classesId = ?", new String[]{classId + ""}, null, null, null);
                    if (cursorSessionLocation.moveToNext()) {
                        if (cursorSessionLocation.moveToNext()) {
                            String location = cursorSessionLocation.getString(cursorSessionLocation.getColumnIndex(ConferenceTableField.CLASS_CLASSESCODE));
                            activity.setLocation(getLocationChinesAndEnglish(location)[0]);
                            activity.setLocationEn(getLocationChinesAndEnglish(location)[1]);
                        } else {
                            activity.setLocation("");
                            activity.setLocationEn("");
                        }
                    } else {
                        activity.setLocation("");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                beans.add(activity);
            }
        }

        //获取所有的Meeting,因为facultyId可能有一个也可能有多个，所以必须区分对待，不能用和sesion查询一样的方法
        //查询session表
        Cursor cursorMeeting = mDb.query(ConferenceTables.TABLE_INCONGRESS_MEETING, null, null, null, null, null, null);
        Cursor cursorMeetingLocation = null;

        if (cursorMeeting != null) {
            while (cursorMeeting.moveToNext()) {
                ActivityBean activity = new ActivityBean();
                activity.setActivityName(cursorMeeting.getString(cursorMeeting.getColumnIndex(ConferenceTableField.MEETING_TOPIC)));

                String facultyId = cursorMeeting.getString(cursorMeeting.getColumnIndex(ConferenceTableField.MEETING_FACULTYID));
                String roleId = cursorMeeting.getString(cursorMeeting.getColumnIndex(ConferenceTableField.MEETING_ROLEID));

                if (StringUtils.isEmpty(facultyId)) {
                    continue;
                } else {
                    String[] facultyIds = facultyId.split(",");
                    String[] roleIds = roleId.split(",");

                    boolean isNeedMeeting = false;
                    for (int i = 0; i < facultyIds.length; i++) {
                        if (StringUtils.isEquals(speakerId + "", facultyIds[i])) {
                            isNeedMeeting = true;
                            break;
                        }
                    }

                    if (isNeedMeeting) {
                        activity.setType(getType(speakerId + "", facultyIds, roleIds));
                    } else {
                        continue;
                    }
                }

                activity.setTime(cursorMeeting.getString(cursorMeeting.getColumnIndex(ConferenceTableField.MEETING_MEETINGDAY))
                        + " " + cursorMeeting.getString(cursorMeeting.getColumnIndex(ConferenceTableField.MEETING_STARTTIME)));
                activity.setMeetingId(cursorMeeting.getInt(cursorMeeting.getColumnIndex(ConferenceTableField.MEETING_MEETINGID)));
                activity.setStart_time(cursorMeeting.getString(cursorMeeting.getColumnIndex(ConferenceTableField.MEETING_STARTTIME)));
                activity.setEnd_time(cursorMeeting.getString(cursorMeeting.getColumnIndex(ConferenceTableField.MEETING_ENDTIME)));
                activity.setDate(cursorMeeting.getString(cursorMeeting.getColumnIndex(ConferenceTableField.MEETING_MEETINGDAY)));
                activity.setActivityNameEN(cursorMeeting.getString(cursorMeeting.getColumnIndex(ConferenceTableField.MEETING_TOPIC_EN)));
                activity.setIsSessionOrMeeting(AlertBean.TYPE_MEETING);

                {
                    //设置身份名称
                    Cursor cursorTypeName = mDb.query(ConferenceTables.TABLE_INCONGRESS_ROLE, null, "roleId=?", new String[]{activity.getType() + ""}, null, null, null);
                    if (cursorTypeName != null) {
                        if (cursorTypeName.moveToFirst()) {
                            activity.setTypeName(cursorTypeName.getString(cursorTypeName.getColumnIndex(ConferenceTableField.ROLE_NAME)));
                        }
                    }
                    cursorTypeName.close();
                }

                int classId = cursorMeeting.getInt(cursorMeeting.getColumnIndex(ConferenceTableField.MEETING_CLASSESID));

                //根据classId获取到会议地点的中文名
                try {
                    cursorMeetingLocation = mDb.query(ConferenceTables.TABLE_INCONGRESS_CLASS, null, "classesId = ?", new String[]{classId + ""}, null, null, null);
                    if (cursorMeetingLocation.moveToNext()) {
                        String location = cursorMeetingLocation.getString(cursorMeetingLocation.getColumnIndex(ConferenceTableField.CLASS_CLASSESCODE));
                        activity.setLocation(getLocationChinesAndEnglish(location)[0]);
                        activity.setLocationEn(getLocationChinesAndEnglish(location)[1]);
                    } else {
                        activity.setLocation("");
                        activity.setLocationEn("");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                beans.add(activity);
            }
        }

        return beans;
    }


    /**
     * 根据facultyId得到相应的数组位置
     *
     * @param facultyId
     * @param facultyIds
     * @param roleIds    若返回-1表示没有找到该id
     */
    private int getType(String facultyId, String[] facultyIds, String[] roleIds) {
        int position = -1;
        for (int i = 0; i < facultyIds.length; i++) {
            if (StringUtils.isEquals(facultyId, facultyIds[i])) {
                position = i;
            }
        }

        if (position == -1) {
            return 1;
        } else {
            return Integer.parseInt(roleIds[position]);
        }
    }

    private String[] getLocationChinesAndEnglish(String location) {
        return location.split("#@#");
    }
}
