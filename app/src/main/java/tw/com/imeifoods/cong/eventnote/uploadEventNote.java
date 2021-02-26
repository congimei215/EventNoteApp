package tw.com.imeifoods.cong.eventnote;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 上傳日誌到遠端資料庫伺服器
 */

public class uploadEventNote extends AsyncTask<String, Void, Integer> {
    public static final String UPLOAD_URL = "http://172.17.0.132/qm/power/EventNodeUpload.aspx";
    private Context mContext;
    private MyDBHelper mAndroidDb;
    private ServerDAL mServerDb;
    private String mAndroid_Id;



    public uploadEventNote(Context context, String Android_Id)
    {
        mContext = context;
        mAndroidDb = new MyDBHelper(context);

        mAndroid_Id = Android_Id;

        mServerDb = new ServerDAL();
    }

    @Override
    protected Integer doInBackground(String... urls) {
        Integer vCount = -1;

        if (getDeviceIdInServer() > -1) {
            Update_DeviceInfo_ifChanged(); //更新設備檔資料
            Update_Config_ifChanged();
            UploadDayList();
            vCount = UploadEventWithImage(); //上傳日誌檔資料
            if (vCount != -1) {
                UploadCheckNote1();//上傳檢查表資料
                UploadWorkNote(); //上傳生產記錄
            }
        }

        return vCount;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }





    //取得註冊於伺服器上的設備ID碼
    public int getDeviceIdInServer() {

        if (_DeviceIdInServer == -1)
        {
            //從本機上的DB找
            _DeviceIdInServer = mAndroidDb.getDeviceIdOnServer(mAndroid_Id);

            if (_DeviceIdInServer == -1)
            {
                //本機資料庫沒有id，從伺服器找
                _DeviceIdInServer = getDeviceIdInServerFromServerDB(mAndroid_Id);

                //找不到就向伺服器註冊
                if (_DeviceIdInServer == -1)
                {
                    String vAndroid_Name = mAndroidDb.getDeviceName(mAndroid_Id);
                    _DeviceIdInServer = this.RegisterDevice(mAndroid_Id, vAndroid_Name);
                }

                if (_DeviceIdInServer != -1)
                {
                    mAndroidDb.UpdateDeviceIdOnServer(_DeviceIdInServer, mAndroid_Id); //將從server端取得的id回寫到設備中
                }
            }
        }

        return _DeviceIdInServer;
    }
    private int _DeviceIdInServer = -1;

    public int getDeviceIdInServerFromServerDB(String pAndroid_Id)
    {
        int id = -1;
        Connection vConnection = mServerDb.createConnection();
        String vSql = "select [did] from [device] where [devid] = '" + pAndroid_Id + "';";
        try {
            PreparedStatement statement = vConnection.prepareStatement(vSql);
            ResultSet st = statement.executeQuery();
            if (st.next())
            {
                id = st.getInt(1);
            }
        }
        catch(Exception ex)
        {
            Log.d("FATAL EXCEPTION:", ex.getMessage());
        }
        finally {
            try {
                if (vConnection != null && !vConnection.isClosed()) {
                    vConnection.close();
                }
            } catch (SQLException ex) {
            }
        }
        return id;
    }

    //region 操作 註冊於伺服器上的設備資訊
    public void Update_DeviceInfo_ifChanged()
    {
        try {
            Cursor cursor = mAndroidDb.getDeviceInf(mAndroid_Id);
            if (cursor.moveToNext()) {
                int status = cursor.getInt(cursor.getColumnIndex(mAndroidDb.FD_UPST));
                if (status == 0) {
                    int affectRows =
                    updateRegisterDeviceInfo(
                            getDeviceIdInServer(),
                            cursor.getString(cursor.getColumnIndex(mAndroidDb.FD_DeviceName)));

                    if (affectRows > 0)
                    {
                        mAndroidDb.signDeviceInfoStatus(mAndroid_Id, true);
                    }
                }
            }
            cursor.close();
        }
        catch(Exception ex) {
            Log.d("FATAL EXCEPTION:", ex.getMessage());
        }
    }

    public int RegisterDevice(String pAndroid_id, String pAndroid_name)
    {
        int vDeviceIdInServer = -1;
        Connection vConnection = mServerDb.createConnection();

        //新的設備，伺服器端尚未建立資料，進行建檔
        String vSql = "insert into [device] ([devid], [devnm], [Dord] )  select '" + pAndroid_id + "', '" +  pAndroid_name + "',  max(dord) +1 from DEVICE ;";
        try {
            PreparedStatement statement = vConnection.prepareStatement(
                    vSql,
                    Statement.RETURN_GENERATED_KEYS);
            int affectedRows = statement.executeUpdate();

            if (affectedRows != 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    vDeviceIdInServer = generatedKeys.getInt(1);
                    //mAndroidDb.UpdateDeviceIdOnServer(vDeviceIdInServer, pAndroid_id); //將從server端取得的id回寫到設備中
                }
            }
        }
        catch(Exception ex)
        {
            Log.d("FATAL EXCEPTION:", ex.getMessage());
        }
        finally {
            try {
                if (vConnection != null && !vConnection.isClosed()) {
                    vConnection.close();
                }
            } catch (SQLException ex) {
            }
        }
        return vDeviceIdInServer;
    }

    public int updateRegisterDeviceInfo(int deviceIdInServer, String pAndroid_name)
    {
        int affectedRows = 0;
        Connection vConnection = mServerDb.createConnection();

        String vSql = "update [device] set [devnm] = '" +  pAndroid_name + "' where [did] = " + deviceIdInServer + "; ";
        try {
            PreparedStatement statement = vConnection.prepareStatement(vSql);
            affectedRows = statement.executeUpdate();
        }
        catch(Exception ex)
        {
            Log.d("FATAL EXCEPTION:", ex.getMessage());
        }
        finally {
            try {
                if (vConnection != null && !vConnection.isClosed()) {
                    vConnection.close();
                }
            } catch (SQLException ex) {
            }
        }
        return affectedRows;
    }
    //endregion

    //region 操作 參數檔
    public void Update_Config_ifChanged()
    {
        try {
            Cursor cursor = mAndroidDb.getConfig_ForUpdate();

            while (cursor.moveToNext()) {
                String parmName = cursor.getString(cursor.getColumnIndex(MyDBHelper.FD_CONFIG_parKey));
                String parmValue = cursor.getString(cursor.getColumnIndex(MyDBHelper.FD_CONFIG_parVal));
                int affectRows = 0;
                if (isConfigExistInServer(getDeviceIdInServer(), parmName ))
                {
                    affectRows = Update_Config(getDeviceIdInServer(), parmName, parmValue);
                }
                else
                {
                    affectRows = Insert_Config(getDeviceIdInServer(), parmName, parmValue);
                }

                if (affectRows > 0)
                {
                    int id = cursor.getInt(cursor.getColumnIndex(MyDBHelper.FD_ID));
                    mAndroidDb.signConfigStatus(id, true);
                }

            }
            cursor.close();
        }
        catch(Exception ex) {
            Log.d("FATAL EXCEPTION:", ex.getMessage());
        }
    }

    public int Update_Config(int pDeviceIdInServer, String parmKey, String parmVal ) throws Exception
    {
        Connection vConnection = mServerDb.createConnection();

        //修改用command
        String vSqlUpdate = " update " + mAndroidDb.TB_CONFIG + " "
                + " set " + MyDBHelper.FD_CONFIG_parVal + " = '" + parmVal + "' "
                + " where [did] = " + pDeviceIdInServer
                + " and " + MyDBHelper.FD_CONFIG_parKey + " = '" + parmKey + "'; ";

        PreparedStatement vStmUpdate = vConnection.prepareStatement(vSqlUpdate);
        return vStmUpdate.executeUpdate();
    }

    public int Insert_Config(int pDeviceIdInServer, String parmKey, String parmVal ) throws Exception
    {
        Connection vConnection = mServerDb.createConnection();

        //新增用command
        String vSqlInsert = " insert into " + mAndroidDb.TB_CONFIG + " ( did, " + mAndroidDb.FD_CONFIG_parKey + ", " + mAndroidDb.FD_CONFIG_parVal + ") "
                + " values (" + pDeviceIdInServer + ", '" + parmKey + "', '" + parmVal + "'); ";
        PreparedStatement vStmInset = vConnection.prepareStatement(vSqlInsert);
        return vStmInset.executeUpdate();
    }

    public Boolean isConfigExistInServer(int pDeviceIdInServer, String pKeyName) throws Exception
    {
        Boolean vRet = false;
        String vSqlCount = "select count(*) as count1 from " + mAndroidDb.TB_CONFIG
                + " where [did] = " + pDeviceIdInServer + " "
                + " and " + mAndroidDb.FD_CONFIG_parKey + " = '" + pKeyName + "'; ";
        Connection vConnection = mServerDb.createConnection();

        PreparedStatement vStmCount = vConnection.prepareStatement(vSqlCount);
        ResultSet vDsCount = vStmCount.executeQuery();
        vDsCount.next();
        int vRowCount = vDsCount.getInt("count1");
        vDsCount.close();

        vRet = (vRowCount > 0);
        return vRet;
    }
    //endregion

    public Boolean isEventExistInServer(int eventId/* AndroidDB_Event_ID */) throws Exception
    {
        Boolean vRet = false;
        String vSqlCount = "select count(*) as count1 from [EVENT] where [EDEVID] = " + getDeviceIdInServer() + " and [EId] = " + eventId;
        Connection vConnection = mServerDb.createConnection();

        PreparedStatement vStmCount = vConnection.prepareStatement(vSqlCount);
        ResultSet vDsCount = vStmCount.executeQuery();
        vDsCount.next();
        int vRowCount = vDsCount.getInt("count1");
        vDsCount.close();

        vRet = (vRowCount > 0);
        return vRet;
    }

    //上傳 事件資料與圖片 到 伺服器
    public int UploadEventWithImage()
    {
        Integer vCount = 0;

        try {
            Cursor vCursor = mAndroidDb.getEventListForUpdate(); //取得需要上傳的日誌

            if (vCursor.moveToNext()) {
                Connection vConnection = mServerDb.createConnection();

                //新增用command
                String vSqlInsert = " insert into [EVENT] ( [EID], [EDayId], [EType], [EDEP], [ELOC], [EABN], [ESUG], [ERMK], [CREATEAT], [EDEVID]) values (?, ?, ?, ?, ?, ?, ?, ? , ?, " +  getDeviceIdInServer() + ")";
                PreparedStatement vStmInset = vConnection.prepareStatement(vSqlInsert);

                //修改用command
                String vSqlUpdate = "update [event] set  [EType] = ?, [EDEP] = ?, [ELOC] = ?, [EABN] = ?, [ESUG] = ?, [ERMK] = ? where [EDEVID] = ? and  [EID] = ? ";
                PreparedStatement vStmUpdate = vConnection.prepareStatement(vSqlUpdate);

                do {
                    Boolean isWriteDb = false;
                    int vPi = 0;
                    if (isEventExistInServer(vCursor.getInt(0)))
                    {
                        //update
                        vStmUpdate.clearParameters();
                        vStmUpdate.setString(++vPi, vCursor.getString(2)); //EType
                        vStmUpdate.setString(++vPi, vCursor.getString(3)); //EDEP
                        vStmUpdate.setString(++vPi, vCursor.getString(4)); //ELOC
                        vStmUpdate.setString(++vPi, vCursor.getString(5)); //EABN
                        vStmUpdate.setString(++vPi, vCursor.getString(6)); //ESUG
                        vStmUpdate.setString(++vPi, vCursor.getString(7)); //ERMK

                        //where parameter'

                        vStmUpdate.setLong(++vPi,  getDeviceIdInServer()); //EDEVID
                        vStmUpdate.setInt(++vPi, vCursor.getInt(0)); //EID
                        isWriteDb = vStmUpdate.execute();
                    }
                    else
                    {
                        //insert
                        vStmInset.clearParameters();
                        vStmInset.setInt(++vPi, vCursor.getInt(0)); //EID
                        vStmInset.setString(++vPi, vCursor.getString(1)); //EDay
                        vStmInset.setString(++vPi, vCursor.getString(2)); //EType
                        vStmInset.setString(++vPi, vCursor.getString(3)); //EDEP
                        vStmInset.setString(++vPi, vCursor.getString(4)); //ELOC
                        vStmInset.setString(++vPi, vCursor.getString(5)); //EABN
                        vStmInset.setString(++vPi, vCursor.getString(6)); //ESUG
                        vStmInset.setString(++vPi, vCursor.getString(7)); //ERMK
                        //hard code CREATEAT
                        vStmInset.setString(++vPi, vCursor.getString(8)); //EDEVID
                        isWriteDb =  vStmInset.execute();
                    }
                    if ( UploadFile(vCursor.getInt(0)) > -1) {
                        mAndroidDb.signHasUpdate(vCursor.getInt(0));
                        ++vCount;
                    }

                }
                while (vCursor.moveToNext());

                vConnection.close();
            }
            vCursor.close();
        } catch (Exception e) {
            vCount = -1;
            Log.d("congError:" , e.getMessage());
        }
        return vCount;
    }

    //上傳 生產記錄
    public int UploadWorkNote()
    {
        int vCount = 0;
        String tSql = "";
        try {
            Cursor vCursor = mAndroidDb.getWorkNoteForUpload();

            if (vCursor.moveToNext()) {
                Connection vConnection = mServerDb.createConnection();

                do {
                    tSql = " delete from " + MyDBHelper.TB_Works + " where [Did] = " + getDeviceIdInServer()
                            + " and " +  "[wrkId] = " + vCursor.getInt(vCursor.getColumnIndex(MyDBHelper.FD_ID))   + "; ";

                    tSql += " insert into " + MyDBHelper.TB_Works + " (did, dayid, wrkid "
                            + ", " + MyDBHelper.FD_DEPT_DEPARTMENT
                            + ", " + MyDBHelper.FD_LOCA_LOCA
                            + ", " + MyDBHelper.FD_Works_WorkItem
                            + ", " + MyDBHelper.FD_Works_WorkStatus +  ") values ("
                            + getDeviceIdInServer()
                            + ", " + vCursor.getInt(vCursor.getColumnIndex(MyDBHelper.FD_Works_DayId))
                            + ", " + vCursor.getInt(vCursor.getColumnIndex(MyDBHelper.FD_ID))
                            + ", '" + vCursor.getString(vCursor.getColumnIndex(MyDBHelper.FD_DEPT_DEPARTMENT))  + "' "
                            + ", '" + vCursor.getString(vCursor.getColumnIndex(MyDBHelper.FD_LOCA_LOCA)) + "' "
                            + ", '" + vCursor.getString(vCursor.getColumnIndex(MyDBHelper.FD_Works_WorkItem)) + "' "
                            + ", '" + vCursor.getString(vCursor.getColumnIndex(MyDBHelper.FD_Works_WorkStatus)) + "' "
                            + "); ";

                    PreparedStatement preparedStatement = vConnection.prepareStatement(tSql);
                    Boolean isWriteDb = preparedStatement.execute();
                    mAndroidDb.signStatus_Work(vCursor.getInt(vCursor.getColumnIndex(MyDBHelper.FD_ID)), true);
                    ++vCount;

                }
                while (vCursor.moveToNext());

                vConnection.close();
            }
            vCursor.close();
        } catch (Exception e) {
            vCount = -1;
            Log.d("congError:" , e.getMessage());
        }
        return vCount;
    }

    //上傳 檢查表 (南崁與龍潭目前是寫在同一個表)
    public int UploadCheckNote1()
    {
        Integer vCount = 0;

        try {
            Cursor vCursor = mAndroidDb.getCheckNote1ForUpload();

            if (vCursor.moveToNext()) {
                Connection vConnection = mServerDb.createConnection();

                do {
                    String tSql = " delete from CheckNote1 where devid = " + getDeviceIdInServer()
                            + " and dayid = "  + vCursor.getInt(vCursor.getColumnIndex("dayid")) + " "
                            + " and noteid = " + vCursor.getInt(vCursor.getColumnIndex("_id"))   + "; ";
                    tSql += " insert into CheckNote1 (devid, dayid, noteid) values ("
                            + getDeviceIdInServer()
                            + ", " + vCursor.getInt(vCursor.getColumnIndex("dayid"))
                            + ", " + vCursor.getInt(vCursor.getColumnIndex("_id"))  + ");";
                    tSql += " update CheckNote1 set ";

                    for(int i = 1 ; i <= MyDBHelper.TOTAL_COUNT_CheckNote1_FIELD ; i++)
                    {
                        String fieldName = "et" + String.valueOf(i);
                        int vColumnIndex = vCursor.getColumnIndex(fieldName);
                        if (vCursor.isNull(vColumnIndex))
                        {
                            tSql += " " + fieldName + " = NULL, ";
                        }
                        else
                        {
                            tSql += " " + fieldName + " = '" + vCursor.getString(vColumnIndex) + "', ";
                        }
                    }

                    tSql = tSql.substring(0, tSql.length() - 2);
                    tSql += " "
                            + " where devid = " + getDeviceIdInServer()
                            + " and dayid = "  + vCursor.getInt(vCursor.getColumnIndex("dayid")) + " "
                            + " and noteid = " + vCursor.getInt(vCursor.getColumnIndex("_id"))   + "; ";

                    PreparedStatement preparedStatement = vConnection.prepareStatement(tSql);
                    Boolean isWriteDb = preparedStatement.execute();
                    mAndroidDb.signHasUpload_CheckNote1(vCursor.getInt(vCursor.getColumnIndex("_id")), mAndroidDb.HAS_UPLOAD);
                    ++vCount;
                }
                while (vCursor.moveToNext());

                vConnection.close();
            }
            vCursor.close();
        } catch (Exception e) {
            vCount = -1;
            Log.d("congError:" , e.getMessage());
        }
        return vCount;
    }

    //上傳 生產記錄
    public int UploadDayList()
    {
        int vCount = 0;
        String tSql = "";
        try {
            Cursor vCursor = mAndroidDb.getDayListForUpload();

            if (vCursor.moveToNext()) {
                Connection vConnection = mServerDb.createConnection();

                do {
                    tSql = " delete from TDAY where [Did] = " + getDeviceIdInServer()
                            + " and [DayId] = " + vCursor.getInt(vCursor.getColumnIndex(MyDBHelper.FD_ID))   + "; ";

                    tSql += " insert into TDAY (did, DayId, DayNM ) values ("
                            + getDeviceIdInServer()
                            + ", " + vCursor.getInt(vCursor.getColumnIndex(MyDBHelper.FD_ID))
                            + ", '" + vCursor.getString(vCursor.getColumnIndex(MyDBHelper.FD_DAYLIST_TITLE)) + "' "
                            + "); ";

                    PreparedStatement preparedStatement = vConnection.prepareStatement(tSql);
                    Boolean isWriteDb = preparedStatement.execute();
                    mAndroidDb.signStatus_DayList(vCursor.getInt(vCursor.getColumnIndex(MyDBHelper.FD_ID)), true);
                    ++vCount;
                }
                while (vCursor.moveToNext());

                vConnection.close();
            }
            vCursor.close();
        } catch (Exception e) {
            vCount = -1;
            Log.d("congError:" , e.getMessage());
        }
        return vCount;
    }

    //上傳相片
    public int UploadFile(long pEventId) {
        int vRet = 0;
        File[] vFiles = FileUtil.getImagesPath(mContext, pEventId).listFiles(FileUtil.buildExtendNameFilter(".jpg"));

        if (vFiles != null && vFiles.length > 0) {
            HttpFileUpload vUpload = new HttpFileUpload(vFiles,  getDeviceIdInServer(), pEventId, UPLOAD_URL);
            vRet =  vUpload.doInBackground();
            //已在另一個執行序中執行了，不可再建立第二層執行序執行。
            /*
            vUpload.execute().get();
            try {
                vUpload.execute().get();
            } catch (ExecutionException ex) {
                Log.d("Error:", ex.toString());
            } catch (InterruptedException ex) {
                Log.d("Error:", ex.toString());
            }
            */


        }
        return vRet;
    }

}
