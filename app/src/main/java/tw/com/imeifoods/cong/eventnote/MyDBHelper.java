package tw.com.imeifoods.cong.eventnote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static tw.com.imeifoods.cong.eventnote.KeyValuePart.convertArrayList;

/**
 * Created by user on 2017/6/27.
 */
//Cursor.getColumnIndex() 欄位名稱大小寫有差別

public class MyDBHelper extends SQLiteOpenHelper
{

    public static final String DEFAULT_DB_NAME = "test12.db";
    public static final String TB_DAYLIST = "DayList"; //日期清單資料表名稱
    public static final String TB_TYPES = "Types"; //檢查項目資料表名稱
    public static final String TB_DEPARTMENTS = "Departments"; //部門資料表名稱
    public static final String TB_LOCATIONS = "Locations"; //地點資料表名稱
    public static final String FD_TYPE_ID = "_id";
    public static final String FD_TYPE_TYPE = "Type";
    public static final String FD_DEPT_ID = "_id";
    public static final String FD_DEPT_DEPARTMENT = "Department";
    public static final String FD_LOCA_ID = "_id";
    public static final String FD_LOCA_DEPID = "DepId";
    public static final String FD_LOCA_LOCA = "Location";
    public static final String FD_DAYLIST_ID = "_id";
    public static final String FD_DAYLIST_TITLE = "Title";


    public static final String TB_EVENTLIST = "EventList"; //事件資料表名稱
    public static final String FD_EVENT_ID = "_id";
    public static final String FD_EVENT_DAYID = "DayId";
    public static final String FD_EVENT_TYPID = "TypeId";
    public static final String FD_EVENT_DEPID = "DepartmentId";
    public static final String FD_EVENT_LOCID = "LocationID";
    public static final String FD_EVENT_ABNORMAL = "Abnormal";
    public static final String FD_EVENT_SUGGEST = "Suggestion";
    public static final String FD_EVENT_REMARK = "Remark";
    public static final String FD_EVENT_UPST = "UPST";
    public static final String FD_EVENT_CREATEAT = "CreateAt";
    public static final String FD_ID = "_id";
    public static final String FD_UPST = "UPST";

    public static final String TB_DeviceINF = "DEVICEINF";
    public static final String FD_DeviceID = "DeviceId";
    public static final String FD_DeviceName= "DEVNAME";
    public static final String FD_Device_IdInServer = "IdInServer"; //註冊於伺服器上的ID

    public static final String TB_CONFIG = "TConfig";
    public static final String FD_CONFIG_parKey = "parKey";
    public static final String FD_CONFIG_parVal = "parVal";

    //檢查表
    public static String TB_CheckNote1 = "CheckNote1";
    public static String FD_CheckNote1_DayId = "dayid";


    //1-已上傳過 2-未上傳過
    public static final int HAS_UPLOAD = 1;
    public static final int NEED_UPLOAD = 0;


    private static final int DEFAULT_DB_VERSION = 15;

    public MyDBHelper(Context context)
    {
        super(context, DEFAULT_DB_NAME, null, DEFAULT_DB_VERSION);

        //todo:暫時code，待刪
        // onUpgradeTo11_1(this.getWritableDatabase());
        //String tsql = "ALTER TABLE  CheckNote1 ADD COLUMN upst int default 0;";
        //this.getWritableDatabase().execSQL(tsql);
        //this.getWritableDatabase().execSQL("delete from " + TB_DeviceINF);
        //onUpgradeTo14(this.getWritableDatabase()); //debug use only
        //this.getWritableDatabase().setVersion(DEFAULT_DB_VERSION - 1); //debug use only
    }

    public MyDBHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbName, factory, version);
    }

    //onCreate是在Android載入時，找不到對應的資料庫資料，就會觸發的一個方法
    @Override
    public void onCreate(SQLiteDatabase db) {
        //建立部門資料表
        db.execSQL(
                "CREATE  TABLE " + TB_DEPARTMENTS +
                    " (\"" + FD_DEPT_ID + "\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                    "\"" + FD_DEPT_DEPARTMENT + "\" NVARCHAR(128) )"
        );

        //跳過自動編號0
        //db.execSQL("insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ") values ('0');");
        //db.execSQL("delete from "  + TB_DEPARTMENTS + ";" );
        //預先增加一個其他部門
        db.execSQL("insert into Departments (Department) values ('原料處理中心');"); //1
        db.execSQL("insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ") values ('料理餐食總廠');"); //2
        db.execSQL("insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ") values ('蛋捲喜餅廠');"); //3
        db.execSQL("insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ") values ('西點麵包廠');"); //4
        db.execSQL("insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ") values ('冰品奶品廠');"); //5
        db.execSQL("insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ") values ('飲料廠');"); //6
        db.execSQL("insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ") values ('資材部');"); //7
        db.execSQL("insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ") values ('廠區外圍及工務系統');"); //8



        //建立地點資料表
        db.execSQL(
                "CREATE  TABLE " + TB_LOCATIONS +
                        " (\"" + FD_LOCA_ID + "\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                        "\"" + FD_LOCA_DEPID + "\" INTEGER NO NULL, " +
                        "\"" + FD_LOCA_LOCA + "\" NVARCHAR(128) )"
        );

        //跳過自動編號0
        //db.execSQL( "insert into " + TB_LOCATIONS + " (" + FD_LOCA_DEPID + ", " + FD_LOCA_LOCA +") values (1,1);");
        //db.execSQL("delete from "  + TB_LOCATIONS + ";");
        //預先增加一個其他地點
        db.execSQL( "insert into " + TB_LOCATIONS + " (" + FD_LOCA_DEPID + ", " + FD_LOCA_LOCA +")  values (1,'原料處理中心1F');");
        db.execSQL( "insert into " + TB_LOCATIONS + " (" + FD_LOCA_DEPID + ", " + FD_LOCA_LOCA +")  values (1,'原料處理中心2F');");
        db.execSQL( "insert into " + TB_LOCATIONS + " (" + FD_LOCA_DEPID + ", " + FD_LOCA_LOCA +")  values (2,'料理餐食總廠1F');");
        db.execSQL( "insert into " + TB_LOCATIONS + " (" + FD_LOCA_DEPID + ", " + FD_LOCA_LOCA +")  values (2,'料理餐食總廠2F');");
        db.execSQL( "insert into " + TB_LOCATIONS + " (" + FD_LOCA_DEPID + ", " + FD_LOCA_LOCA +")  values (3,'蛋捲喜餅廠1F');");
        db.execSQL( "insert into " + TB_LOCATIONS + " (" + FD_LOCA_DEPID + ", " + FD_LOCA_LOCA +")  values (3,'蛋捲喜餅廠2F');");
        db.execSQL( "insert into " + TB_LOCATIONS + " (" + FD_LOCA_DEPID + ", " + FD_LOCA_LOCA +")  values (4,'西點麵包廠1F');");
        db.execSQL( "insert into " + TB_LOCATIONS + " (" + FD_LOCA_DEPID + ", " + FD_LOCA_LOCA +")  values (4,'西點麵包廠2F');");
        db.execSQL( "insert into " + TB_LOCATIONS + " (" + FD_LOCA_DEPID + ", " + FD_LOCA_LOCA +")  values (5,'冰品奶品廠1F');");
        db.execSQL( "insert into " + TB_LOCATIONS + " (" + FD_LOCA_DEPID + ", " + FD_LOCA_LOCA +")  values (5,'冰品奶品廠2F');");
        db.execSQL( "insert into " + TB_LOCATIONS + " (" + FD_LOCA_DEPID + ", " + FD_LOCA_LOCA +")  values (6,'飲料廠1F');");
        db.execSQL( "insert into " + TB_LOCATIONS + " (" + FD_LOCA_DEPID + ", " + FD_LOCA_LOCA +")  values (6,'飲料廠2F');");
        db.execSQL( "insert into " + TB_LOCATIONS + " (" + FD_LOCA_DEPID + ", " + FD_LOCA_LOCA +")  values (7,'資材部1F');");
        db.execSQL( "insert into " + TB_LOCATIONS + " (" + FD_LOCA_DEPID + ", " + FD_LOCA_LOCA +")  values (7,'資材部2F');");


        //建立項目料別資料表
        db.execSQL(
                "CREATE  TABLE " + TB_TYPES +
                        " (\"" + FD_TYPE_ID + "\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                        "\"" + FD_TYPE_TYPE  + "\" NVARCHAR(128) )"
        );
        //跳過自動編號0
        //db.execSQL("insert into " + TB_TYPES + " (" + FD_TYPE_TYPE + ") values (1);");
        //db.execSQL("delete from "  + TB_TYPES + ";");
        //建立預設項目
        db.execSQL("insert into " + TB_TYPES + " (" + FD_TYPE_TYPE + ") values ('生產設備異常');");
        db.execSQL("insert into " + TB_TYPES + " (" + FD_TYPE_TYPE + ") values ('環境與生產設備衛生異常');");
        db.execSQL("insert into " + TB_TYPES + " (" + FD_TYPE_TYPE + ") values ('廠房能源設施未關閉妥當');");
        db.execSQL("insert into " + TB_TYPES + " (" + FD_TYPE_TYPE + ") values ('鼠蟲害防治缺失');");
        db.execSQL("insert into " + TB_TYPES + " (" + FD_TYPE_TYPE + ") values ('災害異常緊急處理');");
        db.execSQL("insert into " + TB_TYPES + " (" + FD_TYPE_TYPE + ") values ('廠區安全維護');");



        //建立 日期清單 資料表
        db.execSQL(
                "CREATE  TABLE " + TB_DAYLIST +
                        " (\"" + FD_DAYLIST_ID + "\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                        "\"" + FD_DAYLIST_TITLE + "\" NVARCHAR(128) )"
        );
        //跳過自動編號0
        //db.execSQL( "insert into " + TB_DAYLIST + " (" + FD_DAYLIST_TITLE + ") values (1);");
        //db.execSQL("delete from "  + TB_DAYLIST + ";");

        //建立 事件清單 資料表
        db.execSQL(
                "CREATE  TABLE " + TB_EVENTLIST + " " +
                        " (\"" + FD_EVENT_ID + "\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                        "\"" + FD_EVENT_DAYID + "\" INTEGER NOT NULL , " +
                        "\"" + FD_EVENT_TYPID + "\" INTEGER NOT NULL , " +
                        "\"" + FD_EVENT_DEPID + "\" INTEGER NOT NULL , " +
                        "\"" + FD_EVENT_LOCID + "\" INTEGER NOT NULL , " +
                        "\"" + FD_EVENT_ABNORMAL + "\" NVARCHAR(1024), " +
                        "\"" + FD_EVENT_SUGGEST + "\" NVARCHAR(1024), " +
                        "\"" + FD_EVENT_REMARK + "\" NVARCHAR(1024), " +
                        "\"" + FD_EVENT_UPST + "\" INTEGER DEFAULT 0 , " +
                        "\"" + FD_EVENT_CREATEAT + "\" DATETIME DEFAULT CURRENT_TIMESTAMP)"
        );
        /*
        db.execSQL("insert into " + TB_EVENTLIST
                + " (" + FD_EVENT_DAYID
                + ", " + FD_EVENT_TYPID
                + ", " + FD_EVENT_DEPID
                + ", " + FD_EVENT_LOCID
                + " ) values (1,1,1,1);");
        db.execSQL("delete from "  + TB_EVENTLIST + ";" );
        */
        onUpgrade1To5(db);
        onUpgradeTo7(db);
        onUpgradeTo8(db);
        onUpgradeTo9(db);
        onUpgradeTo10_1(db);
        onUpgradeTo11_1(db);
        onUpgradeTo12(db);
        onUpgradeTo13(db);
        onUpgradeTo14(db);
    }



    //取得 類型資料表
    public Cursor getTypeCursor()
    {
        return getTypeCursor(null);
    }

    public String getTypeName(long pId)
    {
        String vTypeName = null;

        Cursor vCursor = getTypeCursor(pId);

        vTypeName = "test";
        if (vCursor.moveToNext())
        {
            vTypeName = vCursor.getString(vCursor.getColumnIndex(FD_TYPE_TYPE));
        }
        vCursor.close();
        return vTypeName;
    }

    //取得 類型資料表
    public Cursor getTypeCursor(Long argID)
    {
        String vSql = "select * from " + TB_TYPES + " ";

        if (argID != null)
        {
            vSql = vSql + " where _id = " + String.valueOf(argID) + " ";
        }
        return this.getReadableDatabase().rawQuery(vSql,null);
    }

    //取得 類型
    public ArrayList<KeyValuePart> getTypesList()
    {
        Cursor vCursor = getTypeCursor(null);
        return convertArrayList(vCursor, vCursor.getColumnIndex("_id"), vCursor.getColumnIndex("Type") );
    }

    //region 操作部門
    //取得 部門
    public Cursor getDepartmentCursor()
    {
        String vSql = "select * from " + TB_DEPARTMENTS + " where FD_DEPT_NL = " + getConfig_Location();
        return this.getReadableDatabase().rawQuery(vSql, null);
    }

    //取得 部門
    public ArrayList<KeyValuePart> getDepartmentList()
    {
        Cursor vCursor = this.getDepartmentCursor();
        return convertArrayList(vCursor,  vCursor.getColumnIndex("_id"), vCursor.getColumnIndex("Department") );
    }

    //新增 部門
    public long insertDepartments(String argDept)
    {
        ContentValues vContentValues = new ContentValues();
        vContentValues.put(FD_DEPT_DEPARTMENT, argDept);
        vContentValues.put(FD_DEPT_NL,  getConfig_Location());
        return this.getWritableDatabase().insert(TB_DEPARTMENTS, null, vContentValues);
    }

    //更新 部門
    public int updateDepartments(long pId, String pDept)
    {
        ContentValues vValue = new ContentValues();
        vValue.put(FD_DEPT_DEPARTMENT, pDept);
        return
                this.getWritableDatabase().update(
                    TB_DEPARTMENTS,
                    vValue,
                    FD_DEPT_ID + " = " + String.valueOf(pId),
                    null);
    }

    //取得 部門名稱
    public String getDepartmentName(long id)
    {
        String ret = "";
        Cursor cursor = this.getReadableDatabase().rawQuery( "select " + FD_DEPT_DEPARTMENT  + " from " + TB_DEPARTMENTS + " where " + FD_DEPT_ID + " = " + id + ";", null);
        if (cursor != null && cursor.moveToFirst()) {
            ret = cursor.getString(0);
            cursor.close();
        }
        return ret;
    }

    //刪除 部門
    public int deleteDepartment(long pId)
    {
        return this.getWritableDatabase().delete(
                TB_DEPARTMENTS,
                FD_DEPT_ID + " = " + pId,
                null);
    }

    //計算有多少筆事件與地點，使用此部門
    public int countDepartmentUsed(long pDepId)
    {
        //事件中有使用此部門
        String vSql = "select count(*) from " + TB_EVENTLIST + " where " + FD_EVENT_DEPID + " = " + String.valueOf(pDepId);
        Cursor vCursor = this.getReadableDatabase().rawQuery(vSql, null);
        vCursor.moveToFirst();
        int vRet = vCursor.getInt(0);
        vCursor.close();

        //地點中有使用此部門
        vSql = "select count(*) from " + TB_LOCATIONS + " where " + FD_LOCA_DEPID + " = " + String.valueOf(pDepId);
        vCursor = this.getReadableDatabase().rawQuery(vSql, null);
        vCursor.moveToFirst();
        vRet = vRet + vCursor.getInt(0);
        vCursor.close();

        //生產中有使用此部門
        vSql = "select count(*) from " + TB_Works + " where " + FD_Works_DepId + " = " + String.valueOf(pDepId);
        vCursor = this.getReadableDatabase().rawQuery(vSql, null);
        vCursor.moveToFirst();
        vRet = vRet + vCursor.getInt(0);
        vCursor.close();

        return vRet;
    }

    public static String FD_DEPT_NL = "FD_DEPT_NL";
    public boolean onUpgradeTo9_4(SQLiteDatabase db)
    {
        boolean vRet = false;

        try {
                db.execSQL("ALTER TABLE " + TB_DEPARTMENTS + " ADD COLUMN FD_DEPT_NL INTEGER DEFAULT 0; ");
            db.execSQL("insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ", FD_DEPT_NL ) values ('龍潭一廠', 1);");
            db.execSQL("insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ", FD_DEPT_NL ) values ('龍潭二廠', 1);");
            db.execSQL("insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ", FD_DEPT_NL ) values ('龍潭三廠', 1);");
            db.execSQL("insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ", FD_DEPT_NL ) values ('資材部', 1);");
            db.execSQL("insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ", FD_DEPT_NL ) values ('廠區外圍', 1);");


            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
            return false;
        }
        return vRet;
    }
    //endregion

    //region 操作 地點資料表
    //取得 地點
    public Cursor getLocationCursor(long argDepId)
    {
        String vSql = "select * from " + TB_LOCATIONS + " where " + FD_LOCA_DEPID + " = " + String.valueOf(argDepId) + " ";
        return this.getReadableDatabase().rawQuery(vSql, null);
    }

    //取得 地點
    public ArrayList<KeyValuePart> getLocationList(long argDepId)
    {
        Cursor vCursor = this.getLocationCursor(argDepId);
        return convertArrayList(vCursor, vCursor.getColumnIndex(FD_LOCA_ID), vCursor.getColumnIndex(FD_LOCA_LOCA));
    }

    //新增 地點
    public long insertLocations(long argDepId, String argLoca)
    {
        ContentValues vContentValues = new ContentValues();
        vContentValues.put(FD_LOCA_DEPID, argDepId);
        vContentValues.put(FD_LOCA_LOCA, argLoca);
        return this.getWritableDatabase().insert(TB_LOCATIONS, null, vContentValues);
    }

    //更新 地點
    public int updateLocation(long pLocId, String pLoc)
    {
        ContentValues vValue = new ContentValues();
        vValue.put(FD_LOCA_LOCA, pLoc);
        return this.getWritableDatabase().update(
                TB_LOCATIONS,
                vValue,
                FD_LOCA_ID + " = " + String.valueOf(pLocId),
                null);
    }

    //刪除 地點
    public int deleteLocation(long pLocId)
    {
        return this.getWritableDatabase().delete(
                TB_LOCATIONS,
                FD_LOCA_ID + " = ? ",
                new String[]{String.valueOf(pLocId)}
        );

    }

    //計算共多少筆事件記錄有使用此 地點
    public int countLocationUsed(long pLocId)
    {
        String vSql = "select count(*) from " + TB_EVENTLIST + " where " + FD_EVENT_LOCID + " = " + String.valueOf(pLocId);
        Cursor vCursor =  this.getReadableDatabase().rawQuery(vSql, null);
        vCursor.moveToFirst();
        int vRet = vCursor.getInt(0);
        vCursor.close();

        vSql = "select count(*) from " + TB_Works + " where " + FD_Works_LocId + " = " + String.valueOf(pLocId);
        vCursor =  this.getReadableDatabase().rawQuery(vSql, null);
        vCursor.moveToFirst();
        vRet += vCursor.getInt(0);
        vCursor.close();
        return vRet;
    }
    //endregion


    //region 操作 異常事項說明資料表
    //取得 異常事項說明選單
    public ArrayList<KeyValuePart> getListAbnormal(long pTypeId)
    {
        Cursor vCursor = getAbnormalCursor(pTypeId);
        ArrayList<KeyValuePart> ret = KeyValuePart.convertArrayList(vCursor,  vCursor.getColumnIndex(FD_LIST_ABN_ID), vCursor.getColumnIndex(FD_LIST_ABN_STR) );
        vCursor.close();
        return ret;
    }

    //取得 異常事項說明選單
    public Cursor getAbnormalCursor(long pTypeId)
    {
        String vSql = "select * from " + TB_LIST_ABN + " where " + FD_LIST_ABN_TypeID + " = " + pTypeId + ";";
        Cursor vCursor = this.getReadableDatabase().rawQuery(vSql, null);
        return vCursor;
    }

    //新增 異常原因
    public long insertAbnormals(long pTypeId, String pAbnormalString)
    {
        ContentValues vContentValues = new ContentValues();
        vContentValues.put(FD_LIST_ABN_TypeID, pTypeId);
        vContentValues.put(FD_LIST_ABN_STR, pAbnormalString);

        return this.getWritableDatabase().insert(TB_LIST_ABN, null, vContentValues);
    }

    //刪除 異常原因說明
    public int deleteAbnormal(long pId)
    {
        int vRet =
        this.getWritableDatabase().delete(
                TB_LIST_ABN,
                FD_LIST_ABN_ID + " = " + pId,
                null);

        if (vRet > 0) {
            deleteAllSuggest(pId);
        }

        return vRet;

    }

    //更新 異常原因說明
    public int updateAbnroml(long pId, String pAbnormalString)
    {
        ContentValues vValue = new ContentValues();
        vValue.put(FD_LIST_ABN_STR, pAbnormalString);
        return
                this.getWritableDatabase().update(
                        TB_LIST_ABN,
                        vValue,
                        FD_LIST_ABN_ID + " = " + String.valueOf(pId),
                        null);
    }

    //找出異常原因找代碼
    public long findAbnormalID(long pTypeId, String pAbnormalString)
    {
        long vRet = 0;
        Cursor vCursor = null;
        try
        {
            String tSql = "select " + FD_LIST_ABN_ID + " from " + TB_LIST_ABN
                    + " where " + FD_LIST_ABN_TypeID + " = " + pTypeId + " "
                    + " AND " + FD_LIST_ABN_STR + " = '" + pAbnormalString + "';";
            vCursor =  this.getReadableDatabase().rawQuery(tSql, null);
            if (vCursor.moveToNext()) {
                vRet = vCursor.getLong(0);
            }
        }
        catch(Exception ex)
        {

        }
        finally {
            if (vCursor != null) {
                vCursor.close();
            }
        }
        return vRet;
    }

    //更新資料表：加入與檢查項目連動
    public boolean onUpgradeTo9_6(SQLiteDatabase db)
    {
        boolean vRet = false;

        try {
            db.execSQL("ALTER TABLE " + TB_LIST_ABN + " ADD COLUMN " + FD_LIST_ABN_TypeID + " INTEGER DEFAULT 1; ");
            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
            return false;
        }
        return vRet;
    }
    //endregion

    //取得 建議處置說明選單
    public ArrayList<KeyValuePart> getListSuggest(long  pTypeId, String pAbnormalString)
    {
        Cursor vCursor = getSuggestCursor(pTypeId, pAbnormalString);
        ArrayList<KeyValuePart> ret = KeyValuePart.convertArrayList(vCursor,  vCursor.getColumnIndex(FD_LIST_SUG_ID), vCursor.getColumnIndex(FD_LIST_SUG_STR) );
        vCursor.close();
        return ret;
    }

    //取得 建議處置說明選單
    public Cursor getSuggestCursor(long  pTypeId, String pAbnormalString)
    {
        long vAbnId = findAbnormalID(pTypeId, pAbnormalString);

        return getSuggestCursor(vAbnId);
    }

    //取得 建議處置說明選單
    public Cursor getSuggestCursor(long pAbnormal_ID)
    {
        String vSql = "select * from " + TB_LIST_SUG +
                " where " + FD_LIST_SUG_LSTABN_ID + " = " + pAbnormal_ID + ";" ;
        Cursor vCursor = this.getReadableDatabase().rawQuery(vSql, null);
        return vCursor;
    }

    //新增 處置、建議
    public long insertSuggests(long pAbnId, String pSuggest)
    {
        ContentValues vContentValues = new ContentValues();
        vContentValues.put(FD_LIST_SUG_LSTABN_ID, pAbnId);
        vContentValues.put(FD_LIST_SUG_STR, pSuggest);
        return this.getWritableDatabase().insert(TB_LIST_SUG, null, vContentValues);
    }

    //刪除 處置、建議
    public int deleteSuggest(long pId)
    {
        return this.getWritableDatabase().delete(
                TB_LIST_SUG,
                FD_LIST_SUG_ID + " = " + pId,
                null);
    }

    public int deleteAllSuggest(long pListEventID)
    {
        return this.getWritableDatabase().delete(
                TB_LIST_SUG,
                FD_LIST_SUG_LSTABN_ID + " = " + pListEventID,
                null);
    }

    //更新 處置、建議說明
    public int updateSuggest(long pId, String pSuggest)
    {
        ContentValues vValue = new ContentValues();
        vValue.put(FD_LIST_SUG_STR, pSuggest);
        return
                this.getWritableDatabase().update(
                        TB_LIST_SUG,
                        vValue,
                        FD_LIST_SUG_ID + " = " + String.valueOf(pId),
                        null);
    }

    //更新資料表：加入與異常說明連動
    public boolean onUpgradeTo9_5(SQLiteDatabase db)
    {
        boolean vRet = false;

        try {
            db.execSQL("ALTER TABLE " + TB_LIST_SUG + " ADD COLUMN " + FD_LIST_SUG_LSTABN_ID + " INTEGER DEFAULT 0; ");
            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
            return false;
        }
        return vRet;
    }
    //endregion


    //region 操作 生產項目資料表
    public static final String TB_LIST_WorkItem = "TB_LIST_WorkItem";
    public static final String FD_LIST_WorkItem_DeptId =  "DeptId";
    //public static final String FD_List_WorkItem_ID = FD_ID;
    public static final String FD_LIST_WorkItem_STR = "WorkItem_STR";


    //建立  生產項目資料表
    public boolean onUpgradeTo9_2(SQLiteDatabase db)
    {
        boolean vRet = false;
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TB_LIST_WorkItem + ";");
            db.execSQL(
                    "CREATE TABLE " + TB_LIST_WorkItem + " " +
                    " ( " +  FD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
                    ", " + FD_LIST_WorkItem_STR + " nvarchar(1024) ) " );

            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
            return false;
        }
        return vRet;
    }

    //更新資料表：加入與檢查項目連動
    public boolean onUpgradeTo10_1(SQLiteDatabase db)
    {
        boolean vRet = false;

        try {
            db.execSQL("ALTER TABLE " + TB_LIST_WorkItem + " ADD COLUMN " + FD_LIST_WorkItem_DeptId + " INTEGER DEFAULT 0; ");
            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
            return false;
        }
        return vRet;
    }

    //取得 生產項目選單
    public ArrayList<KeyValuePart> getListWorkItem(long pDeptId)
    {
        Cursor vCursor = getWorkItemCursor(pDeptId);
        int vKeyIndex = vCursor.getColumnIndex(FD_ID);
        int vValueIndex = vCursor.getColumnIndex(FD_LIST_WorkItem_STR);
        ArrayList<KeyValuePart> ret = KeyValuePart.convertArrayList(vCursor, vKeyIndex , vValueIndex );
        vCursor.close();
        return ret;
    }

    //取得 生產項目
    public Cursor getWorkItemCursor(long pDeptId)
    {
        String vSql = "select * from " + TB_LIST_WorkItem  + " where " + FD_LIST_WorkItem_DeptId + " = " + pDeptId + "; ";
        Cursor vCursor = this.getReadableDatabase().rawQuery(vSql, null);
        return vCursor;
    }

    //新增 生產項目
    public long insertWorkItem(long pDeptId, String workItem)
    {
        ContentValues vContentValues = new ContentValues();
        vContentValues.put(FD_LIST_WorkItem_DeptId, pDeptId);
        vContentValues.put(FD_LIST_WorkItem_STR, workItem);
        return this.getWritableDatabase().insert(TB_LIST_WorkItem, null, vContentValues);
    }

    //刪除 生產項目
    public int deleteWorkItem(long pId)
    {
        return this.getWritableDatabase().delete(
                TB_LIST_WorkItem,
                FD_ID + " = " + pId,
                null);
    }

    //更新 生產項目
    public int updateWorkItem(long pId, String pWorkItem)
    {
        ContentValues vValue = new ContentValues();
        vValue.put(FD_LIST_WorkItem_STR, pWorkItem);
        return
                this.getWritableDatabase().update(
                        TB_LIST_WorkItem,
                        vValue,
                        FD_ID + " = " + String.valueOf(pId),
                        null);
    }


    //endregion


    //region == 操作 生產狀況資料表 ，用於 生產狀況記錄 ==

    public static final String TB_LIST_WorkStatus = "TB_LIST_WorkStatus";
    //public static final String FD_List_WorkItem_ID = FD_ID;
    public static final String FD_LIST_WorkStatus_STR = "WorkStatus_STR";

    //建立  生產項目資料表
    public boolean onUpgradeTo9_3(SQLiteDatabase db)
    {
        boolean vRet = false;
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TB_LIST_WorkStatus + ";");
            db.execSQL(
                    "CREATE TABLE " + TB_LIST_WorkStatus + " " +
                            " ( " +  FD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
                            ", " + FD_LIST_WorkStatus_STR + " nvarchar(1024) ) " );

            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
            return false;
        }
        return vRet;
    }


    //取得 生產狀況選單
    public ArrayList<KeyValuePart> getListWorkItemStatus()
    {
        Cursor vCursor = getWorkItemStatusCursor();
        ArrayList<KeyValuePart> ret = KeyValuePart.convertArrayList(vCursor,  vCursor.getColumnIndex(FD_ID), vCursor.getColumnIndex(FD_LIST_WorkStatus_STR) );
        vCursor.close();
        return ret;
    }

    //取得 生產狀況
    public Cursor getWorkItemStatusCursor()
    {
        String vSql = "select * from " + TB_LIST_WorkStatus + " ";
        Cursor vCursor = this.getReadableDatabase().rawQuery(vSql, null);
        return vCursor;
    }

    //新增 生產狀況
    public long insertWorkItemStatus(String value)
    {
        ContentValues vContentValues = new ContentValues();
        vContentValues.put(FD_LIST_WorkStatus_STR, value);
        return this.getWritableDatabase().insert(TB_LIST_WorkStatus, null, vContentValues);
    }

    //刪除 生產狀況
    public int deleteWorkItemStatus(long pId)
    {
        return this.getWritableDatabase().delete(
                TB_LIST_WorkStatus,
                FD_ID + " = " + pId,
                null);
    }

    //更新 生產狀況
    public int updateWorkItemStatus(long pId, String value)
    {
        ContentValues vValue = new ContentValues();
        vValue.put(FD_LIST_WorkStatus_STR, value);
        return
                this.getWritableDatabase().update(
                        TB_LIST_WorkStatus,
                        vValue,
                        FD_ID + " = " + String.valueOf(pId),
                        null);
    }


    //endregion





    //region == 操作 生產資料表 ，用於 生產狀況記錄 ==
    //作業記錄
    public static final String TB_Works = "works";
    //_id
    public static final String FD_Works_DayId = "dayid";
    public static final String FD_Works_DepId = "depid";
    public static final String FD_Works_LocId = "locid";
    public static final String FD_Works_WorkItem = "workitem";
    public static final String FD_Works_WorkStatus = "workstatus";
    //UPST

    //建立  生產記錄資料表
    public boolean onUpgradeTo9_1(SQLiteDatabase db)
    {
        boolean vRet = false;
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TB_Works + ";");
            db.execSQL(
                    "CREATE TABLE " + TB_Works + " " +
                            " ( " +  FD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
                            ", " + FD_Works_DayId + " INTEGER " +
                            ", " + FD_Works_DepId + " INTEGER " +
                            ", " + FD_Works_LocId + " INTEGER " +
                            ", " + FD_Works_WorkItem + " nvarchar(1024) " +
                            ", " + FD_Works_WorkStatus + " nvarchar(1024) " +
                            ", " + FD_UPST  + " INTEGER DEFAULT 0 " + " )"
            );

            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
            return false;
        }
        return vRet;
    }

    public Cursor getWorkListCursorByDay(Long pDayId) {
        return this.getReadableDatabase().rawQuery(
                        "select " + TB_Works + ".*, " + TB_DEPARTMENTS + "." + FD_DEPT_DEPARTMENT
                        + " from " + TB_Works + " inner join " + TB_DEPARTMENTS + " on "
                        + TB_Works + "." + FD_Works_DepId + " = " + TB_DEPARTMENTS + "." + FD_DEPT_ID
                        + " where " + FD_Works_DayId + " = " + pDayId
                        + " order by " + TB_Works + "." + FD_ID + " desc "
                , null);
        //Cursor c = db.rawQuery("select * from exp(info, amount)");
    }

    public Cursor getWorkNoteCursorByID(long pWorkID)
    {
        return this.getReadableDatabase().rawQuery(
                "select * from " + TB_Works
                        + " where " + FD_ID + " = " + pWorkID
                , null);
    }

    public int deleteWorkList(long id)
    {
        return this.getWritableDatabase().delete(TB_Works, FD_ID + " = " + id, null);
    }

    public int signStatus_Work(long id, Boolean hasUpdate)
    {
        int valueStatus = 0;
        if (hasUpdate) valueStatus = 1;
        ContentValues vValue = new ContentValues();
        vValue.put("[" + FD_UPST + "]", valueStatus );
        int affectRows =
                this.getWritableDatabase().update(
                        TB_Works,
                        vValue,
                        FD_ID + " = " + id + " ",
                        null);
        return affectRows;
    }

    public Cursor getWorkNoteForUpload()
    {
        String vSql = "select " + TB_Works + ".*, " + FD_DEPT_DEPARTMENT + ", " + FD_LOCA_LOCA + " from " + TB_Works
                + " inner join " + TB_DEPARTMENTS + " on " + TB_Works + "." + FD_Works_DepId + " = " + TB_DEPARTMENTS + "." + FD_DEPT_ID
                + " inner join " + TB_LOCATIONS + " on " + TB_Works + "." + FD_Works_LocId + " = " + TB_LOCATIONS + "." + FD_LOCA_ID
                + " where " + FD_UPST + " < 1 ";
        return this.getReadableDatabase().rawQuery(vSql, null);
    }

    //新增 生產記錄
    public long insertWorkList(
            long argDayId, long argDepartmentId, long argLocationId
            , String argWorkItem, String argWorkItemStatus)
    {
        ContentValues vContentValues = new ContentValues();
        vContentValues.put(FD_Works_DayId, argDayId);
        vContentValues.put(FD_Works_DepId, argDepartmentId);
        vContentValues.put(FD_Works_LocId, argLocationId);
        vContentValues.put(FD_Works_WorkItem, argWorkItem);
        vContentValues.put(FD_Works_WorkStatus, argWorkItemStatus);

        long vRet = -1;
        try {
            vRet = this.getWritableDatabase().insert(TB_Works, null, vContentValues);
        }
        catch (Exception ex){}

        return vRet;
    }



    //修改 生產記錄
    public int updateWorkList(
            long argId, long argDepartmentId, long argLocationId
            , String argWorkItem, String argWorkItemStatus)
    {
        ContentValues vContentValues = new ContentValues();
        vContentValues.put(FD_Works_DepId, argDepartmentId);
        vContentValues.put(FD_Works_LocId, argLocationId);
        vContentValues.put(FD_Works_WorkItem, argWorkItem);
        vContentValues.put(FD_Works_WorkStatus, argWorkItemStatus);
        vContentValues.put(FD_UPST, 0);
        int vRet = -1;
        try {
            vRet = this.getWritableDatabase().update(TB_Works, vContentValues, FD_ID + " = " + argId, null);
        }
        catch(Exception ex) {}
        return vRet;
    }

    //刪除 生產記錄
    public int deleteWorklist(long id)
    {
        int vRet = -1;
        try {
            vRet = this.getWritableDatabase().delete(TB_Works, FD_ID + " = " + id, null);
        }
        catch(Exception ex) {}
        return vRet ;
    }
    //endregion





    //region 操作 事件資料表
    //取得事件資料
    public Cursor getEventListCursorByEvent(long pEventId) {
        return this.getReadableDatabase().rawQuery(
                "select * from " + TB_EVENTLIST
                        + " where " + FD_EVENT_ID + " = " + pEventId
                        + " order by " + FD_EVENT_ID + " desc", null);
        //Cursor c = db.rawQuery("select * from exp(info, amount)");
    }

    //取得 事件記錄 資料
    public Cursor getEventListCursorByDay(Long pDayId)
    {
        String vSql = "select "
                + TB_EVENTLIST + ".*,"
                + TB_DEPARTMENTS + "." + FD_DEPT_DEPARTMENT
                + " from " + TB_EVENTLIST + " inner join "  + TB_DEPARTMENTS + " on "
                + TB_EVENTLIST + "." + FD_EVENT_DEPID + " = " + TB_DEPARTMENTS + "." + FD_DEPT_ID + " "
                + " where " + FD_EVENT_DAYID + " = " + pDayId + " order by " + FD_EVENT_ID + " desc";
        return this.getReadableDatabase().rawQuery(vSql, null);
    }


    //新增 事件記錄
    public long insertEventList(long argDayId, long argTypeId
            ,long argDepartmentId, long argLocationId, String argAbnormal
            ,String argSuggestion, String argRemark   )
    {
        ContentValues vContentValues = new ContentValues();
        vContentValues.put(FD_EVENT_DAYID, argDayId);
        vContentValues.put(FD_EVENT_TYPID, argTypeId);
        vContentValues.put(FD_EVENT_DEPID, argDepartmentId);
        vContentValues.put(FD_EVENT_LOCID, argLocationId);
        vContentValues.put(FD_EVENT_ABNORMAL,argAbnormal);
        vContentValues.put(FD_EVENT_SUGGEST, argSuggestion);
        vContentValues.put(FD_EVENT_REMARK, argRemark);
        long vRet = this.getWritableDatabase().insert(TB_EVENTLIST, null, vContentValues);
        /*
        String vSql = "update " + TB_EVENTLIST + " set "
                + FD_EVENT_DAYID + " = " + argDayId + " "
                + FD_EVENT_TYPID + " = " + argTypeId + " "
                + FD_EVENT_DEPID + " = " + argDepartmentId + " "
                + FD_EVENT_LOCID + " = " + argLocationId + " "
                + FD_EVENT_ABNORMAL + " = '" + argAbnormal + "' "
                + FD_EVENT_SUGGEST + " = '" + argSuggestion + "' "
                + FD_EVENT_REMARK + " = '" + argRemark + "' "
                + " where " + FD_EVENT_ID + " = " + argDayId + " ";
        this.getWritableDatabase().execSQL(vSql);
        */
        return vRet;
    };



    //修改 事件記錄
    public long updateEventList(long argEventId, long argTypeId
            ,long argDepartmentId, long argLocationId, String argAbnormal
            ,String argSuggestion, String argRemark   )
    {
        ContentValues vContentValues = new ContentValues();
        vContentValues.put(FD_EVENT_TYPID, argTypeId);
        vContentValues.put(FD_EVENT_DEPID, argDepartmentId);
        vContentValues.put(FD_EVENT_LOCID, argLocationId);
        vContentValues.put(FD_EVENT_ABNORMAL,argAbnormal);
        vContentValues.put(FD_EVENT_SUGGEST, argSuggestion);
        vContentValues.put(FD_EVENT_REMARK, argRemark);
        vContentValues.put(FD_EVENT_UPST, 0);
        return this.getWritableDatabase().update(TB_EVENTLIST, vContentValues, FD_EVENT_ID +  " = ? ", new String[] {String.valueOf(argEventId)});
    }

    //刪除 事件記錄
    public int deleteEventlist(long pEventId)
    {
        return this.getWritableDatabase().delete(
                TB_EVENTLIST,
                FD_EVENT_ID + " = ?",
                new String[]{String.valueOf(pEventId)} );
    }
    //endregion





    //region 日誌清單 資料表操作
    public int signStatus_DayList(long id, Boolean hasUpdate)
    {
        int valueStatus = NEED_UPLOAD;
        if (hasUpdate) valueStatus = HAS_UPLOAD;
        ContentValues vValue = new ContentValues();
        vValue.put("[" + FD_UPST + "]", valueStatus );
        int affectRows =
                this.getWritableDatabase().update(
                        TB_DAYLIST,
                        vValue,
                        FD_ID + " = " + id + " ",
                        null);
        return affectRows;
    }

    public Cursor getDayListForUpload()
    {
        String vSql = "select * from " + TB_DAYLIST  + " where " + FD_UPST + " = " + NEED_UPLOAD;
        return this.getReadableDatabase().rawQuery(vSql, null);
    }

    //新增 日誌
    public long insertDayList(String argTitle)
    {
        ContentValues vContentValues = new ContentValues();
        vContentValues.put("Title", argTitle );
        return this.getWritableDatabase().insert(TB_DAYLIST,null, vContentValues);
    }

    //修改 日誌
    public void updateDayList(long pId, String pTitle)
    {
        String vSql = "update " + TB_DAYLIST + " set "
                + FD_DAYLIST_TITLE + " = '" + pTitle + "', "
                + FD_UPST + " = " + NEED_UPLOAD + " "
                + " where " + FD_DAYLIST_ID + " = " + String.valueOf(pId) + "; ";
        this.getWritableDatabase().execSQL(vSql);
    }

    //刪除 日誌 (含事件、生產、檢查)
    public ArrayList<Long> deleteDayList(long pDayId)
    {
        ArrayList<Long> vEventIds = new ArrayList<>();
        try {
            //取得日誌內所有事件id
            String vSql = "SELECT " + FD_EVENT_ID + " FROM " + TB_EVENTLIST + " where " + FD_EVENT_DAYID + " = " + pDayId + "; ";
            String query = String.format(vSql);
            Cursor c = this.getReadableDatabase().rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    vEventIds.add(c.getLong(0));
                } while (c.moveToNext());
            }


            SQLiteDatabase vDb = this.getWritableDatabase();
            vSql = "delete from " + TB_EVENTLIST + " where " + FD_EVENT_DAYID + " = " + pDayId + "; ";
            vDb.execSQL(vSql);

            vSql = "delete from " + TB_Works + " where " + FD_Works_DayId + " = " + pDayId + "; ";
            vDb.execSQL(vSql);

            vSql = "delete from " + TB_CheckNote1 + " where " + FD_CheckNote1_DayId + " = " + pDayId + "; ";
            vDb.execSQL(vSql);

            vSql = "delete from " + TB_DAYLIST + " where " + FD_DAYLIST_ID + " = " + pDayId + "; ";
            vDb.execSQL(vSql);
        }
        catch(Exception ex)
        {
            Log.d("congErr:", ex.getMessage());
        }
        return vEventIds;
    }

    //取得所有的日誌清單
    public Cursor getDayListCursor() {
        return this.getReadableDatabase().rawQuery(
                "select * from " + TB_DAYLIST + " order by _id desc", null);
    }

    //取得日期名稱
    public String getDayName(long id)
    {
        String ret = "";
        Cursor cursor = this.getReadableDatabase().rawQuery( "select " + FD_DAYLIST_TITLE  + " from " + TB_DAYLIST + " where " + FD_DAYLIST_ID + " = " + id + ";", null);
        if (cursor != null && cursor.moveToFirst()) {
            ret = cursor.getString(0);
            cursor.close();
        }
        return ret;
    }


    //endregion 日誌 資料表操作





    //region 設備清單 資料表操作
    public Cursor getDeviceInf(String pAndroid_ID)
    {
        return this.getReadableDatabase().rawQuery(
                "select * from " + TB_DeviceINF + " where " + FD_DeviceID + " = '" + pAndroid_ID + "';", null);
    }

    //寫入設備名稱
    public long  InsertDeviceInf(String pDeviceName, String pAndroid_ID)
    {
        long vRet = -1;
        try {

            ContentValues vValue = new ContentValues();
            vValue.put("[" + FD_DeviceID + "]", pAndroid_ID);
            vValue.put("[" + FD_DeviceName + "]", pDeviceName);
            vValue.put("[" + FD_UPST + "]", 0);
            vRet = this.getWritableDatabase().insert(TB_DeviceINF, null, vValue);
        }
        catch (Exception ex)
        {
            vRet = -1;
            Log.d("congErr:", ex.getMessage());
        }
        return vRet;
    }

    //修改設備名稱
    public long UpdateDeviceInf(String pDeviceName, String pAndroid_ID)
    {
        long vRet = -1;
        try {
            String vSql = "insert into " + TB_DeviceINF
                    + " (" + FD_DeviceID + ", " + FD_DeviceName + ", " + FD_UPST + ") values "
                    + " ('" + pAndroid_ID + "', '" + pDeviceName + "', 0) ;";
            ContentValues vValue = new ContentValues();
            vValue.put("[" + FD_DeviceID + "]", pAndroid_ID);
            vValue.put("[" + FD_DeviceName + "]", pDeviceName);
            vValue.put("[" + FD_UPST + "]", 0);
            vRet = this.getWritableDatabase().update(
                    TB_DeviceINF
                    , vValue
                    , FD_DeviceID + " = '" + pAndroid_ID + "' "
                    , null);

        }
        catch (Exception ex)
        {
            vRet = -1;
            Log.d("congErr:", ex.getMessage());
        }
        return vRet;
    }

    //將從server端取得的id回寫到設備中
    public int UpdateDeviceIdOnServer(int pDevIdInServerId, String pAndroid_ID)
    {
        ContentValues vContentValues = new ContentValues();
        vContentValues.put(FD_Device_IdInServer, pDevIdInServerId);
        int resultCount =
                this.getWritableDatabase().update(TB_DeviceINF, vContentValues,
                FD_DeviceID +  " = '" + pAndroid_ID + "' ",null);
        return resultCount;
    }

    //一開始欄位型態規劃錯誤，實際值都是數值型態
    public int getDeviceIdOnServer(String pAndroid_ID)
    {
        int vRet = -1;

        try
        {
            Cursor curosr = getDeviceInf(pAndroid_ID);

            if (curosr.moveToNext())
            {
                String tmpStr = curosr.getString(curosr.getColumnIndex(FD_Device_IdInServer));
                vRet = MyUtil.tryParseInt(tmpStr, -1);
            }
            curosr.close();
        }
        catch (Exception ex)
        {
            Log.d("congErr:", ex.getMessage());
        }
        return vRet;
    }

    public String getDeviceName(String pAndroid_ID)
    {
        String vRet = "NotFoundDeviceName";
        String vSql = "select devname from deviceinf where deviceid = '" + pAndroid_ID + "';";
        Cursor vCursor =  this.getReadableDatabase().rawQuery(vSql, null);

        if (vCursor.moveToNext()) {
            vRet = vCursor.getString(0);
            vCursor.close();
        }
        return vRet;
    }

    public int signDeviceInfoStatus( String pAndroid_ID, boolean hasUpdate)
    {
        int valueStatus = 0;
        if (hasUpdate) valueStatus = 1;
        ContentValues vValue = new ContentValues();
        vValue.put("[" + FD_UPST + "]", valueStatus );
        int affectRows =
                this.getWritableDatabase().update(
                        TB_DeviceINF,
                        vValue,
                        FD_DeviceID + " = '" + pAndroid_ID + "' ",
                        null);
        return affectRows;
    }
    //endregion

    //region 參數檔 資料表操作

    //取得指定的參數值
    public String getConfig(String key)
    {
        String vRet = "";
        String tSql = "select " + FD_CONFIG_parVal
                + " from " + TB_CONFIG
                + " where " + FD_CONFIG_parKey + " = '" + key + "';";
        try
        {
            Cursor cursor = this.getReadableDatabase().rawQuery(tSql    , null);
            if (cursor.moveToNext())
            {
                vRet = cursor.getString(cursor.getColumnIndex(FD_CONFIG_parVal));
            }
            cursor.close();
        }
        catch(Exception ex)
        {
            Log.d("CongError:" , ex.getMessage());
        }
        return vRet;
    }

    //寫入指定的參數值
    public Boolean updateConfig(String key, String val)
    {
        Boolean vRet = false;
        String tSql = "update " + TB_CONFIG + " set "
                + FD_CONFIG_parVal + " = '" + val + "' "
                + ", " + FD_UPST + " = 0 "
                + " where " + FD_CONFIG_parKey + " = '" + key + "';";
        try
        {
            this.getWritableDatabase().execSQL(tSql);
            vRet = true;
        }
        catch(Exception ex)
        {
            Log.d("CongError:" , ex.getMessage());
            vRet = false;
        }
        return vRet ;
    }

    //取得 參數檔中待上傳的資料
    public Cursor getConfig_ForUpdate()
    {
        String tSql = "select * "
                + " from " + TB_CONFIG
                + " where " + FD_UPST + " = 0;";

         return   this.getReadableDatabase().rawQuery(tSql    , null);
    }

    public int signConfigStatus(int id, Boolean hasUpdate)
    {
        int valueStatus = 0;
        if (hasUpdate) valueStatus = 1;
        ContentValues vValue = new ContentValues();
        vValue.put("[" + FD_UPST + "]", valueStatus );
        return
                this.getWritableDatabase().update(
                        TB_CONFIG,
                        vValue,
                        FD_ID + " = " + id,
                        null);
    }

    //取得使用者廠別 0:南崁 1:龍潭
    public String getConfig_Location()
    {
        return getConfig("p1");
    }
    //endregion


    public Cursor getEventListForUpdate() {
        Boolean flagDebug = false; //測試模式，上傳所有的資料
        String vSql =
                        "select " + TB_EVENTLIST + "." + FD_EVENT_ID + " "
                        + " , "  + TB_EVENTLIST + "." + FD_EVENT_DAYID + " "
                        + " , " + TB_TYPES + "." + FD_TYPE_TYPE + " "
                        + " , " + TB_DEPARTMENTS + "." + FD_DEPT_DEPARTMENT + " "
                        + " , " + TB_LOCATIONS + "." + FD_LOCA_LOCA + " "
                        + " , " + TB_EVENTLIST + "." + FD_EVENT_ABNORMAL + " "
                        + " , " + TB_EVENTLIST + "." + FD_EVENT_SUGGEST + " "
                        + " , " + TB_EVENTLIST + "." + FD_EVENT_REMARK + " "
                        + " , " + TB_EVENTLIST + "." + FD_EVENT_CREATEAT + " "
                        + " from " + TB_EVENTLIST
                        + " left outer join " + TB_TYPES + " "
                        + " on " + TB_EVENTLIST + "." + FD_EVENT_TYPID + " = " + TB_TYPES + "." + FD_TYPE_ID + " "
                        + " left outer join " + TB_DEPARTMENTS + " "
                        + " on " + TB_EVENTLIST + "." + FD_EVENT_DEPID + " = "   + TB_DEPARTMENTS + "." + FD_DEPT_ID + " "
                        + " left outer join " + TB_LOCATIONS + " "
                        + " on " + TB_EVENTLIST + "." + FD_EVENT_LOCID + " = " + TB_LOCATIONS + "." + FD_LOCA_ID + " ";
        if (!flagDebug) {
            vSql = vSql +" where " + FD_EVENT_UPST + " < 1";
        }
        return this.getReadableDatabase().rawQuery(vSql, null);
    }

    public Cursor getCheckNote1ForUpload()
    {
        String vSql =
                "select * from  CheckNote1 where upst < 1 ";
        return this.getReadableDatabase().rawQuery(vSql, null);
    }

    //標示該筆事件記錄已完成上傳或更新
    public int signHasUpdate(long pId)
    {
        return signHasUpdate(pId, HAS_UPLOAD);
    }

    //標示該筆事件記錄已完成上傳或更新
    //參數：1-已上傳過 0-未上傳過
    public int signHasUpdate(long pId, int pStatus)
    {
        ContentValues vValue = new ContentValues();
        vValue.put("[" + FD_EVENT_UPST + "]", pStatus );
        return
                this.getWritableDatabase().update(
                        TB_EVENTLIST,
                        vValue,
                        FD_EVENT_ID + " = " + String.valueOf(pId),
                        null);
    }

    public int signHasUpload_CheckNote1(long pId, int pStatus)
    {
        ContentValues vValue = new ContentValues();
        vValue.put("[upst]", pStatus );
        return
                this.getWritableDatabase().update(
                        "CheckNote1",
                        vValue,
                        " _id = " + String.valueOf(pId),
                        null);
    }

    public Cursor getCheckNote1(long DayId)
    {
        return this.getReadableDatabase().rawQuery(
                "select * from CheckNote1 where dayid = " + String.valueOf(DayId) + "; ", null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (newVersion > oldVersion)
        {
            db.beginTransaction();//建立交易
            boolean isSuccess = true;//判斷參數

            if (isSuccess && oldVersion < 4) {
                isSuccess = onUpgrade1To2(db);
            }

            if (isSuccess && oldVersion < 5) {
                isSuccess = onUpgrade1To5(db);
            }

            if (isSuccess && oldVersion < 7) {
                isSuccess  = onUpgradeTo7(db);
            }

            if (isSuccess && oldVersion < 8) {
                isSuccess  = onUpgradeTo8(db);
            }

            if (isSuccess && oldVersion < 9) {
                isSuccess  = onUpgradeTo9(db);
            }

            if (isSuccess && oldVersion < 10) {
                isSuccess  = onUpgradeTo10_1(db);
            }

            if (isSuccess && oldVersion < 11) {
                isSuccess  = onUpgradeTo11_1(db);
            }

            if (isSuccess && oldVersion < 12) {
                isSuccess  = onUpgradeTo12(db);
            }

            if (isSuccess && oldVersion < 13) {
                isSuccess  = onUpgradeTo13(db);
            }

            if (isSuccess && oldVersion < 14) {
                isSuccess  = onUpgradeTo14(db);
            }

            if (isSuccess && oldVersion < 15) {
                isSuccess  = onUpgradeTo15(db);
            }

            if (isSuccess)
            {
                db.setTransactionSuccessful();//正確交易才成功
            }
            db.endTransaction();

            if (!isSuccess)
            {
                db.setVersion(oldVersion);
               // throw new Exception("資料庫初始失敗!!");
            }

        }


    }

    public boolean onUpgrade1To2(SQLiteDatabase db)
    {
        boolean vRet = false;
        try {
            String tmpTable = TB_DEPARTMENTS + "_1";

            db.execSQL(
                    "CREATE  TABLE " + tmpTable +
                            " (\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                            "\"" + FD_DEPT_DEPARTMENT + "\" NVARCHAR(128) )"
            );

            db.execSQL(
                    "insert into " + tmpTable
                    + " ( " + FD_DEPT_ID + "," + FD_DEPT_DEPARTMENT  + ") "
                    + " select " + FD_DEPT_ID + "," + FD_DEPT_DEPARTMENT  + " from  " + TB_DEPARTMENTS + "; "
            );

            db.execSQL("DROP TABLE " + TB_DEPARTMENTS);
            db.execSQL("Alter table " + tmpTable + " rename to " + TB_DEPARTMENTS);

            //建立地點資料表
            tmpTable = TB_LOCATIONS + "_1";
            db.execSQL(
                    "CREATE  TABLE " + tmpTable +
                            " (\"" + FD_LOCA_ID + "\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                            "\"" + FD_LOCA_DEPID + "\" INTEGER NO NULL, " +
                            "\"" + FD_LOCA_LOCA + "\" NVARCHAR(128) )"
            );

            db.execSQL(
                    " insert into " + tmpTable
                    + " (" + FD_LOCA_ID + "," + FD_LOCA_DEPID + "," + FD_LOCA_LOCA + ") "
                    + " select " + FD_LOCA_ID + "," + FD_LOCA_DEPID + "," + FD_LOCA_LOCA + " from " + TB_LOCATIONS + "; "
            );
            db.execSQL("DROP TABLE " + TB_LOCATIONS);
            db.execSQL("Alter table " + tmpTable + " rename to " + TB_LOCATIONS);


            //建立項目料別資料表
            tmpTable = TB_TYPES + "1";
            db.execSQL(
                    "CREATE  TABLE " + tmpTable +
                            " (\"" + FD_TYPE_ID + "\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                            "\"" + FD_TYPE_TYPE  + "\" NVARCHAR(128) )"
            );

            db.execSQL(
                    "insert into " + tmpTable
                    + " (" + FD_TYPE_ID + "," + FD_TYPE_TYPE + ") "
                    + " select " + FD_TYPE_ID + "," + FD_TYPE_TYPE + " from " + TB_TYPES + "; "
            );
            db.execSQL("DROP TABLE " + TB_TYPES);
            db.execSQL("Alter table " + tmpTable + " rename to " + TB_TYPES);

            //建立 日期清單 資料表
            tmpTable = TB_DAYLIST + "1";
            db.execSQL(
                    "CREATE  TABLE " + tmpTable +
                            " (\"" + FD_DAYLIST_ID + "\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                            "\"" + FD_DAYLIST_TITLE + "\" NVARCHAR(128) )"
            );
            db.execSQL(
                    "insert into " + tmpTable
                    + " (" + FD_DAYLIST_ID + "," + FD_DAYLIST_TITLE + ") "
                    + " select " + FD_DAYLIST_ID + "," + FD_DAYLIST_TITLE + " from " + TB_DAYLIST + "; "
            );
            db.execSQL("DROP TABLE " + TB_DAYLIST);
            db.execSQL("Alter table " + tmpTable + " rename to " + TB_DAYLIST);



            db.execSQL(
                    "CREATE  TABLE " + TB_EVENTLIST + "_1 " +
                            " (\"" + FD_EVENT_ID + "\" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "\"" + FD_EVENT_DAYID + "\" INTEGER NOT NULL , " +
                            "\"" + FD_EVENT_TYPID + "\" INTEGER NOT NULL , " +
                            "\"" + FD_EVENT_DEPID + "\" INTEGER NOT NULL , " +
                            "\"" + FD_EVENT_LOCID + "\" INTEGER NOT NULL , " +
                            "\"" + FD_EVENT_ABNORMAL + "\" NVARCHAR(1024), " +
                            "\"" + FD_EVENT_SUGGEST + "\" NVARCHAR(1024), " +
                            "\"" + FD_EVENT_REMARK + "\" NVARCHAR(1024), " +
                            "\"" + FD_EVENT_UPST + "\" INTEGER DEFAULT 0 , " +
                            "\"" + FD_EVENT_CREATEAT + "\" DATETIME DEFAULT CURRENT_TIMESTAMP)"
            );


            db.execSQL(
                    "insert into " + TB_EVENTLIST + "_1 "
                            + " ( " + FD_EVENT_ID + ", "
                            + FD_EVENT_DAYID + ", "
                            + FD_EVENT_TYPID + ", "
                            + FD_EVENT_DEPID + ", "
                            + FD_EVENT_LOCID + ", "
                            + FD_EVENT_ABNORMAL + ", "
                            + FD_EVENT_SUGGEST + ", "
                            + FD_EVENT_REMARK + ", "
                            + FD_EVENT_UPST + ", "
                            + FD_EVENT_CREATEAT + ") "
                            + " select "
                            + FD_EVENT_ID + ", "
                            + FD_EVENT_DAYID + ", "
                            + FD_EVENT_TYPID + ", "
                            + FD_EVENT_DEPID + ", "
                            + FD_EVENT_LOCID + ", "
                            + FD_EVENT_ABNORMAL + ", "
                            + FD_EVENT_SUGGEST + ", "
                            + FD_EVENT_REMARK + ", "
                            + FD_EVENT_UPST + ", "
                            + FD_EVENT_CREATEAT + " "
                            + " from " + TB_EVENTLIST + " "
            );

            db.execSQL("DROP TABLE " + TB_EVENTLIST);
            db.execSQL("Alter table " + TB_EVENTLIST + "_1 rename to " + TB_EVENTLIST);
            vRet = true;
        }
        catch(Exception ex) {
            vRet = false;
        }
        return vRet;
    }

    //建立設備定義資料表
    public boolean onUpgrade1To5(SQLiteDatabase db)
    {
        boolean vRet = false;
        try {
            String tmpTable = TB_DEPARTMENTS + "_1";

            db.execSQL(
                    "CREATE  TABLE DEVICEINF " +
                            " ( \"_id\"      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                            "\"DeviceId\" NVARCHAR(128), " +   //設備的ID
                            "\"IdInServer\" NVARCHAR(128), " + //註冊於伺服器上的ID
                               "\"DEVNAME\" NVARCHAR(128) )"   //設備的名稱
            );
            vRet = true;

        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
        }
        return vRet;
    }


    public static final String TB_LIST_ABN = "TB_LIST_ABN";
    public static final String FD_LIST_ABN_ID = "_id";
    public static final String FD_LIST_ABN_STR = "FD_LIST_ABN_STR";
    public static final String FD_LIST_ABN_TypeID = "FD_LIST_ABN_TypeID";

    public static final String TB_LIST_SUG = "TB_LIST_SUG";
    public static final String FD_LIST_SUG_LSTABN_ID = "LSTABN_ID";
    public static final String FD_LIST_SUG_ID = "_id";
    public static final String FD_LIST_SUG_STR = "FD_LIST_SUG_STR";

    //建立 異常說明、建議處置 下拉選單用
    public boolean onUpgradeTo7(SQLiteDatabase db)
    {
        boolean vRet = false;
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TB_LIST_ABN + ";");
            db.execSQL(
                    "CREATE TABLE " + TB_LIST_ABN + " " +
                            " ( " +  FD_LIST_ABN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
                            ", "  + FD_LIST_ABN_STR + "  NVARCHAR(1024) " + " )"
            );
            db.execSQL("DROP TABLE IF EXISTS " + TB_LIST_SUG + ";");
            db.execSQL(
                    "CREATE TABLE " + TB_LIST_SUG + " " +
                            " ( " + FD_LIST_SUG_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  " +
                            ", "  + FD_LIST_SUG_STR + " NVARCHAR(1024) " + " )"
            );
            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
        }
        return vRet;
    }


    public static int TOTAL_COUNT_CheckNote1_FIELD = 99;
    //建立檢查表
    public boolean onUpgradeTo8(SQLiteDatabase db)
    {
        boolean vRet = false;
        try {
            String tSql = "DROP TABLE IF EXISTS " + TB_CheckNote1 + ";";
            db.execSQL(tSql);

            tSql = "CREATE TABLE " + TB_CheckNote1 + " ( "
                 + "  " + FD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL "
                 + ", " + FD_CheckNote1_DayId + " integer ";

            for(int i = 1; i <= TOTAL_COUNT_CheckNote1_FIELD; i++)
            {
                tSql = tSql + ", et" + String.valueOf(i) + " nvarchar(512) ";
            }

            tSql += " , " + FD_UPST + " int default 0 ); ";
            db.execSQL(tSql);
            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
        }
        return vRet;
    }

    //建立 參數資料表，修改 設備定義資料表
    public boolean onUpgradeTo9(SQLiteDatabase db)
    {
        boolean vRet = false;
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TB_CONFIG + ";");
            db.execSQL(
                    "CREATE TABLE " + TB_CONFIG + " " +
                            " ( " +  FD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
                            ", " + FD_CONFIG_parKey + " NVARCHAR(512) " +
                            ", " + FD_CONFIG_parVal + " NVARCHAR(512) " +
                            ", " + FD_UPST  + " INTEGER DEFAULT 0 " + " )"
            );

            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
            return false;
        }

        //給預設廠別：南崁廠
        //p1:廠別 {0 南崁、1 龍潭}
        try {
            db.execSQL("INSERT INTO " + TB_CONFIG + "(" + FD_CONFIG_parKey + ", " + FD_CONFIG_parVal + ") VALUES ('p1', '0') ;");
            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
            return false;
        }

        //String tsql = "ALTER TABLE  CheckNote1 ADD COLUMN upst int default 0;";
        //修改 設備定義資料表，新增欄位
        try {
            db.execSQL(
                    "ALTER TABLE " + TB_DeviceINF + " " +
                    " ADD COLUMN " + FD_UPST  + " INTEGER DEFAULT 0; "
            );

            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
            return false;
        }
        if (vRet) {
            vRet = onUpgradeTo9_1(db);
        }

        if (vRet)
        {
            vRet = onUpgradeTo9_2(db);
        }

        if (vRet)
        {
            vRet = onUpgradeTo9_3(db);
        }

        if (vRet)
        {
            vRet = onUpgradeTo9_4(db);
        }

        if (vRet)
        {
            vRet = onUpgradeTo9_5(db);
        }

        if (vRet)
        {
            vRet = onUpgradeTo9_6(db);
        }

        return vRet;
    }

    //更新資料表：加入與檢查項目連動
    public boolean onUpgradeTo11_1(SQLiteDatabase db)
    {
        boolean vRet = false;

        try {
            db.execSQL("ALTER TABLE " + TB_DAYLIST + " ADD COLUMN " + FD_UPST + " INTEGER DEFAULT 0; ");
            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
            return false;
        }
        return vRet;
    }

    //更新資料表：變更廠別組織
    public boolean onUpgradeTo12(SQLiteDatabase db)
    {
        boolean vRet = false;

        try {
            db.execSQL("Update " + TB_DEPARTMENTS + " set " + FD_DEPT_DEPARTMENT + " = '廠區外圍'  Where " + FD_DEPT_NL + " = 1 and " + FD_DEPT_DEPARTMENT +  " = '廠區外圍與工務系統' " );
            //db.execSQL("insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ", FD_DEPT_NL ) values ('工務系統', 1);");
            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
            return false;
        }
        return vRet;
    }


    //更新資料表：變更廠別組織
    public boolean onUpgradeTo13(SQLiteDatabase db)
    {
        boolean vRet = false;

        try {
            db.execSQL(
                "insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ", " + FD_DEPT_NL + " ) " +
                " select '中央廚房', 0 " +
                " where not exists " +
                "( select 1 from " + TB_DEPARTMENTS  + " where " + FD_DEPT_DEPARTMENT + " = '中央廚房' and " + FD_DEPT_NL + " = 0 );"
            );
            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
            return false;
        }
        return vRet;
    }

    public boolean onUpgradeTo14(SQLiteDatabase db)
    {
        boolean vRet = false;

        try {
            db.execSQL("update " + TB_DEPARTMENTS + " set " + FD_DEPT_DEPARTMENT + " = '生機(醫)園區' where " + FD_DEPT_DEPARTMENT + " = '龍潭三廠'");
            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
            return false;
        }
        return vRet;
    }

    public boolean onUpgradeTo15(SQLiteDatabase db)
    {
        boolean vRet = false;

        try {
            db.execSQL("insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ") values ('蛋卷廠');");
            db.execSQL("insert into " + TB_DEPARTMENTS + " (" + FD_DEPT_DEPARTMENT + ") values ('囍餅廠');");
            vRet = true;
        }
        catch(Exception ex) {
            Log.d("CongError:", ex.getMessage());
            return false;
        }
        return vRet;
    }

}
