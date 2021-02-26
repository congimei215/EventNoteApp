package tw.com.imeifoods.cong.eventnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.R.id.text1;
import static tw.com.imeifoods.cong.eventnote.MyDBHelper.FD_Works_DayId;
import static tw.com.imeifoods.cong.eventnote.R.id.edt_NewItem;


//version-tag: 2017/07/21
public class ListItemActivity extends AppCompatActivity {
    public static final String Bundle_DayId = "Bundle_DayId";
    public static final String Bundle_EventId = "Bundle_EventId";
    public static final String Bundle_NewFunc = "Bundle_NewFunc";
    public static final String Bundle_DeptId = "Bundle_DeptId";
    public static final String Bundle_LocId = "Bundle_LocId";
    public static final String Bundle_WorkId = "Bundle_WorkId";
    public static final String Bundle_TypeId = "Bundle_TypeId";
    public static final String Bundle_ListAbnormal_Id = "Bundle_ListAbnormal_Id";

    private String _Android_ID = "";
    public String getAndroid_ID()
    {
        if (_Android_ID.equals("")) {
            _Android_ID = MyUtil.getAndroid_ID(this);
        }
        return _Android_ID;
    }




    private int mNowFunc = enumFunc.DayList;
    private MyDBHelper mDb;
    private ListView lsv_ListView;
    private Button btn_NewList, BtnUpload, BtnCheckNote, BtnCancer;
    private long mDayId, mDepId, mEventId, mListAbnormal_Id, mTypeId, mLocId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        init();
    }

    //建立選單
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mNowFunc == enumFunc.DayList)
        {
            getMenuInflater().inflate(R.menu.daylist_menu, menu);
        }
        else if (mNowFunc ==  enumFunc.EventList)
        {
            getMenuInflater().inflate(R.menu.eventlist_menu, menu);
        }
        else if (mNowFunc == enumFunc.WorkNoteList)
        {
            getMenuInflater().inflate(R.menu.worklist_menu, menu);
        }
        return true;
    }


    //使用者選擇選單事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mNowFunc == enumFunc.DayList)
        {
            onMenuSelected_DayList(item);
        }
        else if (mNowFunc == enumFunc.EventList)
        {
            onMenuSelected_EventList(item);
        }
        else if (mNowFunc == enumFunc.WorkNoteList)
        {
            onMenuSelected_WorkNoteList(item);
        }
        return true;
    }



    public void onMenuSelected_DayList(MenuItem item)
    {
        int item_id = item.getItemId();
        if (item_id == R.id.action_settings)
        {
            //轉跳到參數設定
            Intent vIntent = new Intent(ListItemActivity.this, FirstSettingActivity.class);
            startActivity(vIntent);
            //不結束 清單 使用者可以按返回
        }
        else if (item_id == R.id.action_Upload)
        {
            UploadDayLog();
        }
        else if (item_id == R.id.action_addNewItem)
        {
            addNewItem_DayList();
        }
        else if (item_id == R.id.action_exit_daylist)
        {
            finish();
        }
    }

    public void onMenuSelected_EventList(MenuItem item)
    {
        int item_id = item.getItemId();

        //檢查表
        if (item_id == R.id.action_CheckNote)
        {
            startActivity_CheckNote(mDayId, mDepId, mLocId, mTypeId);
            finish();
        }


        //轉跳到 生產記錄清單
        else if (item_id == R.id.action_WorkNoteList)
        {
            startActivity_WorkNoteList(mDayId, mDepId,mLocId, mTypeId, this);
            finish();
        }


        //新增事件
        else if (item_id == R.id.action_addNewItem)
        {
            addNewItem_EventList(mDayId, mDepId, mLocId, mTypeId);
        }

    }

    public void onMenuSelected_WorkNoteList(MenuItem item)
    {
        int item_id = item.getItemId();

        //檢查表
        if (item_id == R.id.action_CheckNote)
        {
            startActivity_CheckNote(mDayId, mDepId, mLocId, mTypeId);
            finish();

        }


        //轉跳到 事件記錄清單
        else if (item_id == R.id.action_EventNoteList)
        {
            startActivity_EventNoteList(mDayId, mDepId, mLocId, mTypeId, this);
            finish();
        }


        //新增事件
        else if (item_id == R.id.action_addNewItem)
        {
            addNewItem_WorkList(mDayId, mDepId, mLocId, mTypeId);
        }

    }

    //畫面初次載入或從別的active切回來的事件
    @Override
    protected void onResume() {
        BtnUpload.setVisibility(View.GONE);
        BtnCheckNote.setVisibility(View.GONE);
        btn_NewList.setVisibility(View.VISIBLE);
        BtnCancer.setVisibility(View.VISIBLE);

        switch (mNowFunc) {
            case enumFunc.DayList:
                loadDayList();
                //BtnUpload.setVisibility(View.VISIBLE); //移到選單列中
                BtnCancer.setVisibility(View.GONE);
                btn_NewList.setVisibility(View.GONE);
                break;
            case enumFunc.DepartmentList:
                loadDepartmentList();
                btn_NewList.setVisibility(View.GONE);
                break;
            case enumFunc.LocationList:
                loadLocationList(mDepId);
                break;
            case enumFunc.AbnList:
                loadAbnormalList(mTypeId);
                break;
            case enumFunc.SugList:
                loadSuggestList(mListAbnormal_Id);
                break;
            case enumFunc.EventList:
                loadEventList(mDayId);
                //BtnCheckNote.setVisibility(View.VISIBLE); //移到選單列中
                break;
            case enumFunc.WorkNoteList:
                loadWorkList(mDayId);
                break;
            case enumFunc.WorkItemList:
                loadWorkItemList(mDepId);
                break;
            case enumFunc.WorkItemStatusList:
                loadWorkItemStatusList();
                break;
            case enumFunc.AbnTypeList:
                loadAbnormalTypeList();
                btn_NewList.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        super.onResume();
    }

    //使用者按返回鍵的事件
    @Override
    public void onBackPressed() {

        switch (mNowFunc) {
            case enumFunc.DayList:
                break; //不要直接跳出
            case enumFunc.WorkNoteList:
                startActivity_EventNoteList(mDayId, mDepId, mLocId, mTypeId, this);//事件清單是主要功能
                finish();
                break;
            default:
                super.onBackPressed();
               // setResult(0);
               // finish();
               // break;
        }

    }


    protected void init() {
        //初始化變數
        mDb = new MyDBHelper(this);
        if (!CheckHasFirstSetting()) {return;} //檢查是否有做初次參數設定

        //取得Bundle參數
        getBundleData();

        //取得元件
        lsv_ListView = (ListView) findViewById(R.id.lsv_ListView);
        btn_NewList = (Button) findViewById(R.id.btn_NewList);
        BtnUpload = (Button) findViewById(R.id.BtnUpload);
        BtnCheckNote = (Button) findViewById(R.id.BtnCheckNote);
        BtnCancer = (Button) findViewById(R.id.BtnCancer);
        BtnCancer.setOnClickListener(btnCancer_OnClickListener());
        //事件綁定
        btn_NewList_setOnClickListener(); //綁定 新增按鈕按下事件
        lsv_ListView_setOnItemClickListener(); //綁定 點選清單項目事件
        setListView_setOnItemLongClickListener(lsv_ListView);  //綁定 長按點選清單項目事件
        setUploadOnClick(BtnUpload);
        //setCheckNoteOnClick(BtnCheckNote);
    }

    //檢查是否有做初次參數設定
    protected Boolean CheckHasFirstSetting()
    {
        Cursor vCursor = mDb.getDeviceInf(this.getAndroid_ID());
        Boolean vHasData =  vCursor.moveToFirst();
        vCursor.close();

        if (!vHasData)
        {
            //轉跳到參數設定
            Intent vIntent = new Intent(ListItemActivity.this, FirstSettingActivity.class);
            startActivity(vIntent);
            finish();
        }
        return vHasData;
    }

    //取得Bundle參數
    protected void getBundleData() {
        beforeGetBundle();

        Bundle vBundle = this.getIntent().getExtras();
        if (vBundle != null) {
            mNowFunc = vBundle.getInt(Bundle_NewFunc);
            mDayId = vBundle.getLong(Bundle_DayId, 0);
            mDepId = vBundle.getLong(Bundle_DeptId, 0);
            mLocId = vBundle.getLong(Bundle_LocId, 0);
            mTypeId = vBundle.getLong(Bundle_TypeId, 0);
            mListAbnormal_Id = vBundle.getLong(Bundle_ListAbnormal_Id, 0);

            //mEventId = vBundle.getLong(Bundle_EventId);
        }

        afterGetBundle();
    }

    protected void beforeGetBundle()
    {

    }

    //取得 Bundle 後要做的事
    protected void afterGetBundle()
    {
        //此處主要做修改標題
        //資料載入是寫在 onResume() 事件

        String tmpStr;

        //設定標頭名稱  title
        switch (mNowFunc) {
            case enumFunc.DayList:
                this.setTitle("【義美食品夜間值勤日誌】");
                break;
            case enumFunc.DepartmentList:
                this.setTitle("【廠別】" );
                break;
            case enumFunc.LocationList:
                String dep = mDb.getDepartmentName(mDepId);
                this.setTitle("【廠別】" + dep);//帶入廠別名稱
                break;
            case enumFunc.AbnList:
                String vTypeName = mDb.getTypeName(mTypeId);
                this.setTitle("【異常說明】" + vTypeName);
                break;
            case enumFunc.SugList:
                this.setTitle("【處置/建議】");
                break;
            case enumFunc.EventList:
                tmpStr = mDb.getDayName(mDayId);
                this.setTitle("【事件記錄】" + tmpStr) ;//帶入日誌名稱
                break;
            case enumFunc.WorkNoteList:
                tmpStr = mDb.getDayName(mDayId);
                this.setTitle("【生產記錄】" + tmpStr) ;//帶入日誌名稱
                break;
            case enumFunc.WorkItemList:
                this.setTitle("【生產作業】") ;
                break;
            case enumFunc.WorkItemStatusList:
                this.setTitle("【生產狀況】") ;
                break;
            case enumFunc.AbnTypeList:
                this.setTitle("【類型】");
                break;
        }
    }

    //綁定 新增按鈕按下事件
    protected void btn_NewList_setOnClickListener() {
        if (mNowFunc == enumFunc.Nan)
        {
            mNowFunc = enumFunc.DayList;
        }
        switch (mNowFunc) {
            case enumFunc.DayList:
                btn_NewList.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) { addNewItem_DayList();}
                        } );
                break;

            case enumFunc.DepartmentList:
                btn_NewList.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {addNewItem_Department();}
                });
                break;

            case enumFunc.LocationList:
                btn_NewList.setOnClickListener( new Button.OnClickListener(){
                    @Override
                    public void onClick(View view) {addNewItem_Location();}
                 });
                break;
            case enumFunc.AbnList:
                btn_NewList.setOnClickListener( new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) { addNewItem_Abnormal();  }
                 });
                break;
            case enumFunc.SugList:
                btn_NewList.setOnClickListener( new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {addNewItem_Suggest();}
                 });
                break;
            case enumFunc.EventList:
                //設定處理事件：使用者按下新增異常事件
                btn_NewList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { addNewItem_EventList(mDayId, mDepId, mLocId, mTypeId); }
                });
                break;
            case enumFunc.WorkNoteList:
                //設定處理工作清單：使用者按下新增工作內容
                btn_NewList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {addNewItem_WorkList(mDayId, mDepId, mLocId, mTypeId); }
                });
                break;
            case enumFunc.WorkItemList:
                btn_NewList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {addNewItem_WorkItemList(); }
                });
                break;
            case enumFunc.WorkItemStatusList:
                btn_NewList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {addNewItem_WorkItemStatusList(); }
                });
                break;
        }
    }

    //綁定 點選清單項目事件
    protected void lsv_ListView_setOnItemClickListener() {
        if (mNowFunc == enumFunc.WorkNoteList)
        {
            //設定處理事件：使用者按下工作清單的項目，開啟 工作記錄 編輯畫面
            lsv_ListView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            Bundle vData = new Bundle();
                            vData.putLong(FD_Works_DayId, mDayId);
                            vData.putLong(MyDBHelper.FD_ID, id);
                            Intent vIntent = new Intent(ListItemActivity.this, WorkNoteActivity.class);
                            vIntent.putExtras(vData);
                            startActivity(vIntent);
                            finish();
                        }
                    }
            );
        }
        else if (mNowFunc == enumFunc.EventList) //異常事件清單按一下，跳到異常事件明細編輯畫面
        {
            //設定處理事件：使用者按下異常清單的項目，開啟 異常事件記錄 編輯畫面
            lsv_ListView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            Bundle vData = new Bundle();
                            vData.putLong(Bundle_DayId, mDayId);
                            vData.putLong(Bundle_EventId, id);
                            Intent vIntent = new Intent(ListItemActivity.this, EventNoteActivity.class);
                            vIntent.putExtras(vData);
                            startActivity(vIntent);
                            finish();
                        }
                    }
            );
        }
        else if (mNowFunc == enumFunc.DayList) //日誌清單按一下會跳到異常事件清單
        {
            lsv_ListView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            Bundle vBundle = new Bundle();
                            vBundle.putLong(Bundle_DayId, id);
                            vBundle.putInt(Bundle_NewFunc, enumFunc.EventList);
                            Intent vIntent = new Intent(ListItemActivity.this, ListItemActivity.class);
                            vIntent.putExtras(vBundle);
                            startActivity(vIntent);
                            //startActivityForResult(vIntent, 0);
                        }
                    }
            );
        }
        else
        {
            //切回原畫面
            lsv_ListView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            Bundle vBundle = new Bundle();
                            vBundle.putInt("position", position);
                            vBundle.putLong("id", id);
                            Intent vIntent = getIntent();
                            vIntent.putExtras(vBundle);
                            setResult(Activity.RESULT_OK, vIntent);
                            finish(); //這個畫面不需要再讓使用者按回上頁回到這頁來。
                        }
                    }
            );
        }
    }

    //綁定 長按點選清單項目事件
    protected void setListView_setOnItemLongClickListener(ListView pLsv) {
        //Daylist 顯示 修改、刪除、取消，檢查是否event有使用？
        //Dep 顯示 修改、刪除、取消，檢查是否event與loc都有使用？
        //loc 顯示 修改、刪除、取消，檢查是否event有使用？
        //event 顯示 刪除、取消

        pLsv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View pView, int pPoistion, long pId) {
                View vDialogView = LayoutInflater.from(ListItemActivity.this).inflate(R.layout.add_item_dialog, null);
                EditText vEdt_Item = vDialogView.findViewById(edt_NewItem);

                AlertDialog.Builder vAlertDialog =
                        new AlertDialog.Builder(ListItemActivity.this)
                                .setView(vDialogView)
                                .setNegativeButton("取消", null);

                //顯示原始值
                if (pView instanceof TextView) {

                    vEdt_Item.setText(((TextView) pView).getText());
                    vEdt_Item.requestFocus();
                    vEdt_Item.selectAll();
                }
                else if (pView instanceof LinearLayout)
                {

                    String vItemValue = "";
                    TextView vTextView = (TextView) ((LinearLayout) pView).getChildAt(0); //hardcode
                    vItemValue += vTextView.getText();
                    vTextView = (TextView) ((LinearLayout) pView).getChildAt(1); //hardcode
                    vItemValue += "/" + vTextView.getText();
                    vEdt_Item.setText(vItemValue);
                }

                switch (mNowFunc) {
                    case enumFunc.DayList:
                        vAlertDialog.setTitle("請輸入新的日誌名稱：")
                                .setNeutralButton("刪除", deleteDayListListener(pId))
                                .setPositiveButton("更新", updateDayListListener(pId, vEdt_Item));
                        break;
                    case enumFunc.EventList:
                        vAlertDialog.setTitle("是否刪除本筆事件記錄？")
                                .setNeutralButton("刪除", deleteEventListener(pId));
                        break;
                    case enumFunc.LocationList:
                        vAlertDialog.setTitle("請輸入新的地點/樓層名稱：")
                                .setNeutralButton("刪除", deleteLocationListener(pId))
                                .setPositiveButton("更新", updateLocationListener(pId, vEdt_Item));
                        break;
                    case enumFunc.DepartmentList:
                        /*
                        vAlertDialog.setTitle("請輸入新的部門名稱：")
                                .setNeutralButton("刪除", deleteDepartmentListener(pId))
                                .setPositiveButton("更新", updateDepartmentListener(pId, vEdt_Item));
                        */
                        vAlertDialog = null;
                        break;
                    case enumFunc.AbnList:
                        vAlertDialog.setTitle("請輸入新的異常原因：")
                                .setNeutralButton("刪除",  deleteAbnormalListener (pId))
                                .setPositiveButton("更新", updateAbnormalListener(pId, vEdt_Item));
                        break;
                    case enumFunc.SugList:
                        vAlertDialog.setTitle("請輸入新的建議或處置：")
                                .setNeutralButton("刪除", deleteSuggestListener(pId))
                                .setPositiveButton("更新", updateSuggestListener(pId, vEdt_Item));
                        break;
                    case enumFunc.WorkNoteList:
                        vAlertDialog.setTitle("是否刪除本筆生產記錄？")
                                .setNeutralButton("刪除", deleteWorkListener(pId));
                        break;
                    case enumFunc.WorkItemList:
                        vAlertDialog.setTitle("請輸入新的生產作業名稱：")
                                .setNeutralButton("刪除", deleteWorkItemListener (pId))
                                .setPositiveButton("更新", updateWorkItemListener (pId, vEdt_Item));
                        break;
                    case enumFunc.WorkItemStatusList:
                        vAlertDialog.setTitle("請輸入新的生產狀況：")
                                .setNeutralButton("刪除", deleteWorkItemStatusListener (pId))
                                .setPositiveButton("更新", updateWorkItemStatusListener (pId, vEdt_Item));
                        break;
                    case enumFunc.AbnTypeList:
                        vAlertDialog = null;
                        break;
                    default:
                        vAlertDialog = null;
                        break;
                }

                if (vAlertDialog != null) {
                    vAlertDialog.show();
                }
                return true;
            }
        });


    }

    protected View.OnClickListener btnCancer_OnClickListener()
    {
        View.OnClickListener vRet =
        new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNowFunc == enumFunc.WorkNoteList)
                {
                    startActivity_EventNoteList(mDayId, mDepId, mLocId, mTypeId, ListItemActivity.this);
                }

                ListItemActivity.this.finish();
            }
        };
        return vRet;
    }

    //綁定 上傳按鈕 點選事件
    protected void setUploadOnClick(Button pBtn) {
        pBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                UploadDayLog();
            }
        });
    }
/*
    //綁定 檢驗表 點選事件
    private void setCheckNoteOnClick(Button pBtn)
    {
        pBtn.setOnClickListener(
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle vData = new Bundle();
                    vData.putLong(Bundle_DayId, mDayId);
                    Intent vIntent = new Intent(ListItemActivity.this, CheckNote1.class);
                    vIntent.putExtras(vData);
                    startActivity(vIntent);
                    finish();
                }
        });
    }

*/

    //載入日期清單
    protected void loadDayList() {
        Cursor vCursor = mDb.getDayListCursor();

        SimpleCursorAdapter vAdapter =
                new SimpleCursorAdapter(this, R.layout.mysample_list_item  , vCursor,
                        new String[]{"Title"},
                        new int[]{text1}, 0);
        lsv_ListView.setAdapter(vAdapter);
    }

    protected void loadAbnormalTypeList()
    {
        Cursor vCursor = mDb.getTypeCursor();

        SimpleCursorAdapter vAdapter =
                new SimpleCursorAdapter(this, R.layout.mysample_list_item, vCursor,
                        new String[]{MyDBHelper.FD_TYPE_TYPE},
                        new int[]{text1}, 0);
        lsv_ListView.setAdapter(vAdapter);

    }

    //載入 部門清單
    protected void loadDepartmentList() {
        Cursor vCursor = mDb.getDepartmentCursor();

        SimpleCursorAdapter vAdapter =
                new SimpleCursorAdapter(this, R.layout.mysample_list_item, vCursor,
                        new String[]{"Department"},
                        new int[]{text1}, 0);
        lsv_ListView.setAdapter(vAdapter);
    }

    //載入 地點清單
    protected void loadLocationList(long argDepId) {
        Cursor vCursor = mDb.getLocationCursor(argDepId);

        SimpleCursorAdapter vAdapter =
                new SimpleCursorAdapter(this, R.layout.mysample_list_item, vCursor,
                        new String[]{"Location"},
                        new int[]{text1}, 0);
        lsv_ListView.setAdapter(vAdapter);
    }

    //載入 異常說明清單
    protected void loadAbnormalList(long pTypeId) {
        Cursor vCursor = mDb.getAbnormalCursor(pTypeId);

        SimpleCursorAdapter vAdapter =
                new SimpleCursorAdapter(this, R.layout.mysample_list_item, vCursor,
                        new String[]{mDb.FD_LIST_ABN_STR},
                        new int[]{text1}, 0);
        lsv_ListView.setAdapter(vAdapter);
    }

    //載入 異常說明清單
    protected void loadSuggestList(long pAbnormal_ID) {

        Cursor vCursor = mDb.getSuggestCursor(pAbnormal_ID);

        SimpleCursorAdapter vAdapter =
                new SimpleCursorAdapter(this, R.layout.mysample_list_item, vCursor,
                        new String[]{mDb.FD_LIST_SUG_STR},
                        new int[]{text1}, 0);
        lsv_ListView.setAdapter(vAdapter);
    }

    //載入事件清單
    protected void loadEventList(long pDayId) {
        Cursor vCursor = mDb.getEventListCursorByDay(pDayId);
        SimpleCursorAdapter vAdapter =
                new SimpleCursorAdapter(this, R.layout.mysample_list_item2, vCursor,
                        new String[]{MyDBHelper.FD_DEPT_DEPARTMENT, MyDBHelper.FD_EVENT_ABNORMAL},
                        new int[]{text1, android.R.id.text2}, 0);
        lsv_ListView.setAdapter(vAdapter);
    }

    //載入 生產作業清單
    protected void loadWorkItemList(long pDepId) {
        Cursor vCursor = mDb.getWorkItemCursor(pDepId);

        SimpleCursorAdapter vAdapter =
                new SimpleCursorAdapter(this, R.layout.mysample_list_item, vCursor,
                        new String[]{MyDBHelper.FD_LIST_WorkItem_STR},
                        new int[]{text1}, 0);
        lsv_ListView.setAdapter(vAdapter);
    }

    //載入 生產狀況清單
    protected void loadWorkItemStatusList() {
        Cursor vCursor = mDb.getWorkItemStatusCursor();

        SimpleCursorAdapter vAdapter =
                new SimpleCursorAdapter(this, R.layout.mysample_list_item, vCursor,
                        new String[]{MyDBHelper.FD_LIST_WorkStatus_STR},
                        new int[]{text1}, 0);
        lsv_ListView.setAdapter(vAdapter);
    }

    protected void addNewItem_DayList()
    {
        View vDialogView = LayoutInflater.from(this).inflate(R.layout.add_item_dialog, null);
        final EditText vEdt_NewItem = vDialogView.findViewById(edt_NewItem);

        //日誌名稱預設為日期
        vEdt_NewItem.setText(android.text.format.DateFormat.format("yyyy/MM/dd", new java.util.Date()).toString());
        vEdt_NewItem.requestFocus();
        vEdt_NewItem.selectAll();
        //使用對話框執行新增程序
        new AlertDialog.Builder(ListItemActivity.this)
                .setTitle("請輸入日誌名稱：").setView(vDialogView)
                .setNegativeButton("取消", null)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String vNewItemString = vEdt_NewItem.getText().toString();

                        if (vNewItemString != null && !vNewItemString.isEmpty()) {
                            long id = mDb.insertDayList(vNewItemString);
                            loadDayList();
                        }
                    }
                })
                .show();
    }

    protected void addNewItem_Department()
    {
        View vDialogView = LayoutInflater.from(this).inflate(R.layout.add_item_dialog, null);
        final EditText vEdt_NewItem = vDialogView.findViewById(edt_NewItem);
        //設定預設值
        //vEdt_NewItem.setText(android.text.format.DateFormat.format("yyyy/MM/dd", new java.util.Date()).toString());
        vEdt_NewItem.requestFocus();
        vEdt_NewItem.selectAll();

        //使用對話框執行新增程序
        new AlertDialog.Builder(ListItemActivity.this)
                .setTitle("請輸入部門名稱：").setView(vDialogView)
                .setNegativeButton("取消", null)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String vNewItemString = vEdt_NewItem.getText().toString();

                        if (vNewItemString != null && !vNewItemString.isEmpty()) {
                            mDb.insertDepartments(vNewItemString);
                            loadDepartmentList();
                        }
                    }
                })
                .show();
    }

    protected void addNewItem_Location()
    {
        View vDialogView = LayoutInflater.from(this).inflate(R.layout.add_item_dialog, null);
        final EditText vEdt_NewItem = vDialogView.findViewById(edt_NewItem);
        //設定預設值
        //vEdt_NewItem.setText(android.text.format.DateFormat.format("yyyy/MM/dd", new java.util.Date()).toString());
        vEdt_NewItem.requestFocus();
        vEdt_NewItem.selectAll();

        //使用對話框執行新增程序
        new AlertDialog.Builder(ListItemActivity.this)
                .setTitle("請輸入地點/樓層：").setView(vDialogView)
                .setNegativeButton("取消", null)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String vNewItemString = vEdt_NewItem.getText().toString();

                        if (vNewItemString != null && !vNewItemString.isEmpty()) {
                            mDb.insertLocations(mDepId, vNewItemString);
                            loadLocationList(mDepId);
                        }
                    }
                })
                .show();
    }


    protected void addNewItem_Abnormal()
    {
        View vDialogView = LayoutInflater.from(this).inflate(R.layout.add_item_dialog, null);
        final EditText vEdt_NewItem = vDialogView.findViewById(edt_NewItem);

        //設定預設值
        //vEdt_NewItem.setText(android.text.format.DateFormat.format("yyyy/MM/dd", new java.util.Date()).toString());
        vEdt_NewItem.requestFocus();
        vEdt_NewItem.selectAll();

        //使用對話框執行新增程序
        new AlertDialog.Builder(ListItemActivity.this)
                .setTitle("請輸入異常原因：").setView(vDialogView)
                .setNegativeButton("取消", null)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String vNewItemString = vEdt_NewItem.getText().toString();

                        if (vNewItemString != null && !vNewItemString.isEmpty()) {
                            mDb.insertAbnormals(mTypeId, vNewItemString);
                            loadAbnormalList(mTypeId);
                        }
                    }
                })
                .show();
    }


    protected void addNewItem_Suggest() {
        View vDialogView = LayoutInflater.from(this).inflate(R.layout.add_item_dialog, null);
        final EditText vEdt_NewItem = vDialogView.findViewById(edt_NewItem);

        vEdt_NewItem.requestFocus();
        vEdt_NewItem.selectAll();

        //使用對話框執行新增程序
        new AlertDialog.Builder(ListItemActivity.this)
                .setTitle("請輸入建議或處理：").setView(vDialogView)
                .setNegativeButton("取消", null)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String vNewItemString = vEdt_NewItem.getText().toString();

                        if (vNewItemString != null && !vNewItemString.isEmpty()) {
                            mDb.insertSuggests(mListAbnormal_Id, vNewItemString);
                            loadSuggestList(mListAbnormal_Id);
                        }
                    }
                })
                .show();

    }


    protected void addNewItem_EventList(long pDayId, long pDepId, long pLocId, long pTypId)
    {
        Bundle vData = new Bundle();
        vData.putLong(Bundle_DayId, pDayId);
        vData.putLong(Bundle_DeptId, pDepId);
        vData.putLong(Bundle_LocId, pLocId);
        vData.putLong(Bundle_TypeId, pTypId);
        Intent vIntent = new Intent(ListItemActivity.this, EventNoteActivity.class);
        vIntent.putExtras(vData);
        startActivity(vIntent);
        finish();
    }

    protected void addNewItem_WorkList(long pDayId, long pDepId, long pLocId, long pTypId)
    {
        Bundle vData = new Bundle();
        vData.putLong(FD_Works_DayId, pDayId);
        vData.putLong(Bundle_DayId, pDayId);
        vData.putLong(Bundle_DeptId, pDepId);
        vData.putLong(Bundle_LocId, pLocId);
        vData.putLong(Bundle_TypeId, pTypId);
        Intent vIntent = new Intent(ListItemActivity.this, WorkNoteActivity.class);
        vIntent.putExtras(vData);
        startActivity(vIntent);
        finish();
    }

    protected void addNewItem_WorkItemList()
    {
        View vDialogView = LayoutInflater.from(this).inflate(R.layout.add_item_dialog, null);
        final EditText vEdt_NewItem = vDialogView.findViewById(edt_NewItem);

        vEdt_NewItem.requestFocus();
        vEdt_NewItem.selectAll();

        //使用對話框執行新增程序
        new AlertDialog.Builder(ListItemActivity.this)
                .setTitle("請輸入作業名稱：").setView(vDialogView)
                .setNegativeButton("取消", null)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String vNewItemString = vEdt_NewItem.getText().toString();

                        if (vNewItemString != null && !vNewItemString.isEmpty()) {
                            long id = mDb.insertWorkItem(mDepId,  vNewItemString);
                            loadWorkItemList(mDepId );
                        }
                    }
                })
                .show();
    }

    protected void addNewItem_WorkItemStatusList()
    {
        View vDialogView = LayoutInflater.from(this).inflate(R.layout.add_item_dialog, null);
        final EditText vEdt_NewItem = vDialogView.findViewById(edt_NewItem);

        vEdt_NewItem.requestFocus();
        vEdt_NewItem.selectAll();

        //使用對話框執行新增程序
        new AlertDialog.Builder(ListItemActivity.this)
                .setTitle("請輸入生產狀況：").setView(vDialogView)
                .setNegativeButton("取消", null)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String vNewItemString = vEdt_NewItem.getText().toString();

                        if (vNewItemString != null && !vNewItemString.isEmpty()) {
                            long id = mDb.insertWorkItemStatus(vNewItemString);
                            loadWorkItemStatusList();
                        }
                    }
                })
                .show();
    }

    // Dialog中選擇 更新 按鈕，更新日誌名稱
    protected DialogInterface.OnClickListener updateDayListListener(final long pId, final EditText pEdt) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String vNewValue = pEdt.getText().toString();
                if (!TextUtils.isEmpty(vNewValue)) {
                    mDb.updateDayList(pId, vNewValue);
                    ListItemActivity.this.onResume();
                }
            }
        };
    }

    // Dialog中選擇 刪除 按鈕，刪除日誌
    protected DialogInterface.OnClickListener deleteDayListListener(final long pId) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Cursor vCursor = mDb.getEventListCursorByDay(pId);
                int vCount = vCursor.getCount();
                if (vCount > 0) { //日誌裡有異常事件記錄，再確認是否刪除
                    new AlertDialog.Builder(ListItemActivity.this)
                            .setTitle("您所選擇的日誌含有記錄，若按確定，將一併刪所有記錄。")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ArrayList<Long> vEventIds =  mDb.deleteDayList(pId);
                                    DeleteEventFile(vEventIds);
                                    ListItemActivity.this.onResume();
                                }
                            })
                            .show();
                } else {
                    mDb.deleteDayList(pId);
                    ListItemActivity.this.onResume();
                }
            }
        };
    }

    // Dialog中選擇 更新 按鈕，更新部門
    protected DialogInterface.OnClickListener updateDepartmentListener(final long pId, final EditText pEdt) {

        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String vNewValue = pEdt.getText().toString();
                if (!TextUtils.isEmpty(vNewValue)) {
                    mDb.updateDepartments(pId, vNewValue);
                    ListItemActivity.this.onResume();
                }
            }
        };
    }

    // Dialog中選擇 更新 按鈕，更新異常原因
    protected DialogInterface.OnClickListener updateAbnormalListener(final long pId, final EditText pEdt) {

        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String vNewValue = pEdt.getText().toString();
                if (!TextUtils.isEmpty(vNewValue)) {
                    mDb.updateAbnroml(pId, vNewValue);
                    ListItemActivity.this.onResume();
                }
            }
        };
    }

    // Dialog中選擇 更新 按鈕，更新建議或處置
    protected DialogInterface.OnClickListener updateSuggestListener(final long pId, final EditText pEdt) {

        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String vNewValue = pEdt.getText().toString();
                if (!TextUtils.isEmpty(vNewValue)) {
                    mDb.updateSuggest(pId, vNewValue);
                    ListItemActivity.this.onResume();
                }
            }
        };
    }

    protected DialogInterface.OnClickListener updateWorkItemListener(final long pId, final EditText pEdt) {

        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String vNewValue = pEdt.getText().toString();
                if (!TextUtils.isEmpty(vNewValue)) {
                    mDb.updateWorkItem(pId, vNewValue);
                    ListItemActivity.this.onResume();
                }
            }
        };
    }

    protected DialogInterface.OnClickListener updateWorkItemStatusListener(final long pId, final EditText pEdt) {

        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String vNewValue = pEdt.getText().toString();
                if (!TextUtils.isEmpty(vNewValue)) {
                    mDb.updateWorkItemStatus(pId, vNewValue);
                    ListItemActivity.this.onResume();
                }
            }
        };
    }

    // Dialog中選擇 刪除 按鈕，刪除部門
    protected DialogInterface.OnClickListener deleteDepartmentListener(final long pId) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int vCount = mDb.countDepartmentUsed(pId);
                if (vCount > 0) {
                    Toast.makeText(getApplicationContext(), "此部門已被使用，不可刪除。", Toast.LENGTH_SHORT).show();
                } else {
                    mDb.deleteDepartment(pId);
                    ListItemActivity.this.onResume();
                }
            }
        };
    }

    // Dialog中選擇 刪除 按鈕，刪除異常原因
    protected DialogInterface.OnClickListener deleteAbnormalListener(final long pId) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDb.deleteAbnormal(pId);
                ListItemActivity.this.onResume();
            }
        };
    }

    // Dialog中選擇 刪除 按鈕，刪除建議處置清單
    protected DialogInterface.OnClickListener deleteSuggestListener(final long pId) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDb.deleteSuggest(pId);
                ListItemActivity.this.onResume();
            }
        };
    }

    protected DialogInterface.OnClickListener deleteWorkItemListener(final long pId)
    {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDb.deleteWorkItem(pId);
                ListItemActivity.this.onResume();
            }
        };
    }

    protected DialogInterface.OnClickListener deleteWorkItemStatusListener(final long pId)
    {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDb.deleteWorkItemStatus(pId);
                ListItemActivity.this.onResume();
            }
        };
    }

    // Dialog中選擇 更新 按鈕，更新地點/樓層
    protected DialogInterface.OnClickListener updateLocationListener(final long pId, final EditText pEdt) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String vNewValue = pEdt.getText().toString();

                if (!TextUtils.isEmpty(vNewValue)) {
                    mDb.updateLocation(pId, vNewValue);
                    ListItemActivity.this.onResume();
                }
            }
        };
    }

    // Dialog中選擇 刪除 按鈕，刪除地點/樓層
    protected DialogInterface.OnClickListener deleteLocationListener(final long pId) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int vCount = mDb.countLocationUsed(pId);
                if (vCount > 0) { //事件裡使用到此樓層的記錄，不允許刪除
                    Toast.makeText(getApplicationContext(), "此地點/樓層已被使用，不可刪除。", Toast.LENGTH_SHORT).show();
                } else {
                    mDb.deleteLocation(pId);
                    ListItemActivity.this.onResume();
                }
            }
        };
    }

    // Dialog中選擇 刪除 按鈕，刪除事件記錄
    protected DialogInterface.OnClickListener deleteEventListener(final long pId) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDb.deleteEventlist(pId);
                FileUtil.deleteFile(ListItemActivity.this, pId);
                ListItemActivity.this.onResume();
            }
        };
    }

    // Dialog中選擇 刪除 按鈕，刪除生產記錄
    protected DialogInterface.OnClickListener deleteWorkListener(final long pId) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDb.deleteWorkList(pId);
                ListItemActivity.this.onResume();
            }
        };
    }

    //刪除事件內的照片
    public void DeleteEventFile(ArrayList<Long> pEventIds)
    {
        for (long iEventId : pEventIds )
        {
            FileUtil.deleteFile(this, iEventId );
        }
    }


    //上傳日誌檔
    protected void UploadDayLog()
    {
        Toast.makeText(getApplicationContext(), "開始上傳!!", Toast.LENGTH_SHORT).show();

        Boolean isDone = false;
        uploadEventNote vUpload1 = new uploadEventNote(ListItemActivity.this, getAndroid_ID() );
        try
        {
            int vRestul =  vUpload1.execute("").get();
            isDone = vRestul != -1;
        } catch (ExecutionException ex) {
            Log.d("Error:", ex.toString());
        } catch (InterruptedException ex) {
            Log.d("Error:", ex.toString());
        }

        //UploadFile(13);
        if (isDone) {
            Toast.makeText(getApplicationContext(), "上傳完畢!!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "上傳失敗!!", Toast.LENGTH_SHORT).show();
        }
    }


    //載入工作狀況清單
    protected void loadWorkList(long pDayId) {
        Cursor vCursor = mDb.getWorkListCursorByDay(pDayId);
        SimpleCursorAdapter vAdapter =
                new SimpleCursorAdapter(this, R.layout.mysample_list_item2, vCursor,
                        new String[]{MyDBHelper.FD_DEPT_DEPARTMENT,  MyDBHelper.FD_Works_WorkItem},
                        new int[]{text1, android.R.id.text2}, 0);
        lsv_ListView.setAdapter(vAdapter);
    }


    protected static void startActivity_EventNoteList(long pDayId, long pDepId, long pLocId, long pTypId,  Activity pActivity)
    {
        Intent vIntent;
        vIntent = new Intent(pActivity, ListItemActivity.class);
        Bundle vData = new Bundle();
        vData.putInt(Bundle_NewFunc,  enumFunc.EventList);
        vData.putLong(Bundle_DayId, pDayId);
        vData.putLong(Bundle_DeptId, pDepId);
        vData.putLong(Bundle_LocId, pLocId);
        vData.putLong(Bundle_TypeId, pTypId);
        vIntent.putExtras(vData);
        pActivity.startActivity(vIntent);
    }

    //轉跳至 檢查表
    protected void startActivity_CheckNote(long pDayId, long pDepId, long pLocId, long pTypId)
    {
        Intent vIntent;
        if (mDb.getConfig_Location().equals("0")) {
            vIntent = new Intent(ListItemActivity.this, CheckNote1.class);
        }
        else
        {
            vIntent = new Intent(ListItemActivity.this, CheckNote2.class);
        }
        Bundle vData = new Bundle();
        vData.putLong(Bundle_DayId, pDayId);
        vData.putLong(Bundle_DeptId, pDepId);
        vData.putLong(Bundle_LocId, pLocId);
        vData.putLong(Bundle_TypeId, pTypId);
        vIntent.putExtras(vData);
        startActivity(vIntent);
    }


    public static void startActivity_WorkNoteList(long pDayId, long pDepId, long pLocId, long pTypId, Activity pActivity)
    {
        Intent vIntent;
        vIntent = new Intent(pActivity, ListItemActivity.class);
        Bundle vData = new Bundle();
        vData.putInt(Bundle_NewFunc,  enumFunc.WorkNoteList);
        vData.putLong(Bundle_DeptId, pDepId);
        vData.putLong(Bundle_LocId, pLocId);
        vData.putLong(Bundle_TypeId, pTypId);
        vData.putLong(Bundle_DayId, pDayId);
        vIntent.putExtras(vData);
        pActivity.startActivity(vIntent);
    }

    public static void startActivity_DayList(Activity pActivity, long pDepId, long pLocId, long pTypId)
    {
        Intent vIntent = new Intent(pActivity, ListItemActivity.class);
        Bundle vData = new Bundle();
        vData.putInt(Bundle_NewFunc,  enumFunc.DayList);
        vData.putLong(Bundle_DeptId, pDepId);
        vData.putLong(Bundle_LocId, pLocId);
        vData.putLong(Bundle_TypeId, pTypId);

        pActivity.startActivity(vIntent);
    }

    public static void startActivity_CheckNote(String pConfig_Location, long pDayId,long pDepId, long pLocId, long pTypId, Activity pActivity)
    {
        //轉跳到 檢查表
        Intent vIntent;

        if (pConfig_Location.equals("0") ) {
            vIntent = new Intent(pActivity, CheckNote1.class);
        }
        else
        {
            vIntent = new Intent(pActivity, CheckNote2.class);
        }

        Bundle vData = new Bundle();
        vData.putLong(Bundle_DayId, pDayId);
        vData.putLong(Bundle_DeptId, pDepId);
        vData.putLong(Bundle_LocId, pLocId);
        vData.putLong(Bundle_TypeId, pTypId);
        vIntent.putExtras(vData);
        pActivity.startActivity(vIntent);
    }
}
