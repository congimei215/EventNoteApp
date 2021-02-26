package tw.com.imeifoods.cong.eventnote;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static tw.com.imeifoods.cong.eventnote.ListItemActivity.Bundle_NewFunc;
import static tw.com.imeifoods.cong.eventnote.MyDBHelper.FD_ID;
import static tw.com.imeifoods.cong.eventnote.MyDBHelper.FD_Works_DayId;
import static tw.com.imeifoods.cong.eventnote.MyUtil.getSelectItemKey;
import static tw.com.imeifoods.cong.eventnote.MyUtil.setSpinnerSelectionById;

public class WorkNoteActivity extends AppCompatActivity {


    private MyDBHelper mDb;
    private Button btnWorkOk, btnWorkCancer;
    private EditText etWork, etStatus;
    private Spinner  mSpnDept, mSpnLoca;
    private TextView TvDep, TvLoc, tvWrkItm, tvWrkSts;
    private  long mDayID = 0, mDepId = 0, mLocId = 0, mTypId = 0,  mWorkId = 0;
    boolean isSkipOneTime_ReloadLocation = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_note);
        init();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
    }

    //建立選單
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.worknote_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();

        if (item_id == R.id.action_saveFile)
        {
            //存檔
            insertUpdateWork(true );
        }
        else if (item_id == R.id.action_CheckNote)
        {
            //轉跳到 檢查表
            String config_Location = mDb.getConfig_Location();
            ListItemActivity.startActivity_CheckNote(config_Location, mDayID, mDepId, mLocId, mTypId, this);
            finish();
        }
        else if (item_id == R.id.action_EventNoteList)
        {
            ListItemActivity.startActivity_EventNoteList(mDayID,mDepId, mLocId, mTypId, this);
            finish();
        }
        else if (item_id == R.id.action_WorkNoteList)
        {
            ListItemActivity.startActivity_WorkNoteList(mDayID, mDepId, mLocId, mTypId, this);
            finish();
        }

        return true;
    }



    @Override //從別的頁面返回
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        int vFunc = requestCode;

        if (vFunc == enumFunc.DepartmentList)
        {
            KeyValuePart vDep = (KeyValuePart) mSpnDept.getSelectedItem();
            loadSpnDept(); //載入最新的選單

            if (! setSpinnerSelectionByBundleArgPosotion(mSpnDept, data)) {
                if (vDep != null) {
                    setSpinnerSelectionById(mSpnDept, vDep.Key);  //將原本的選取值選起來
                }
            }
        }
        else if (vFunc == enumFunc.LocationList)
        {
            KeyValuePart vDept = (KeyValuePart) mSpnDept.getSelectedItem();
            KeyValuePart vLoca = (KeyValuePart) mSpnLoca.getSelectedItem();

            if (vDept != null) {
                loadSpnLoca(vDept.Key);
            }

            if (! setSpinnerSelectionByBundleArgPosotion(mSpnLoca, data)) {
                if (vLoca != null) {
                    setSpinnerSelectionById(mSpnLoca, vLoca.Key);
                }
            }
        }
        else if (vFunc == enumFunc.WorkItemList)
        {
            KeyValuePart vDept = (KeyValuePart) mSpnDept.getSelectedItem();
            long vDeptId = 0;
            if (vDept != null) {
               vDeptId = vDept.Key;
            }
            ArrayList<KeyValuePart> vListItems = mDb.getListWorkItem(vDeptId);  //載入最新的選單
            String selectItemText = getSelectFromBundle(vListItems, data);

            if (  selectItemText != null )
            {
                etWork.setText(selectItemText);
            }

        }
        else if (vFunc == enumFunc.WorkItemStatusList)
        {
            ArrayList<KeyValuePart> vListItems = mDb.getListWorkItemStatus(); //載入最新的選單
            String selectItemText = getSelectFromBundle(vListItems, data);
            if (selectItemText != null )
            {
                etStatus.setText(selectItemText);
            }
        }

    }

    @Override
    public void onBackPressed() {
        ListItemActivity.startActivity_WorkNoteList(mDayID, mDepId, mLocId, mTypId, this);
        finish();
    }


    //初始化變數
    protected void init()
    {

        mDb = new MyDBHelper(this);
        final Bundle vBundle = this.getIntent().getExtras();

        if (vBundle != null)
        {
            mDayID = vBundle.getLong(FD_Works_DayId);
            mWorkId = vBundle.getLong(FD_ID);
            mDepId = vBundle.getLong(ListItemActivity.Bundle_DeptId, 0);
            mLocId = vBundle.getLong(ListItemActivity.Bundle_LocId, 0);
            mTypId = vBundle.getLong(ListItemActivity.Bundle_TypeId, 0);
            String day = mDb.getDayName(mDayID);
            this.setTitle("【生產記錄】" + day) ;//帶入日誌名稱
        }
        else
        {
            mWorkId = 0;
        }

        //沒有取得日誌ID，就退出事件記錄畫面
        if (mDayID == 0)
        {
            WorkNoteActivity.this.finish();
            return;
        }


        btnWorkOk = (Button) findViewById(R.id.btnWorkSave);
        btnWorkOk.setOnClickListener(btnWorkOk_OnClickListener());
        btnWorkCancer = (Button) findViewById(R.id.btnWorkCancer);
        btnWorkCancer.setOnClickListener(btnWorkCancer_OnClickListener());

        mSpnDept = (Spinner)findViewById(R.id.wrk_spnDept);
        loadSpnDept();
        mSpnDept.setOnItemSelectedListener(mSpnDept_setOnItemSelectedListener());


        mSpnLoca = (Spinner)findViewById(R.id.spnLoca);
        mSpnLoca.setLongClickable(true);
        mSpnLoca.setOnLongClickListener(mSpnLoca_setOnLongClickListener());
        mSpnLoca.setOnItemSelectedListener(mSpnLoc_OnItemSelectedListener());

        etWork = (EditText)findViewById(R.id.edtWorkItem);
        etStatus = (EditText)findViewById(R.id.edtWorkStatus);

        TvDep = (TextView) findViewById(R.id.TvDep);
        TvDep.setOnClickListener(TvDep_OnClickListener());

        TvLoc = (TextView) findViewById(R.id.TvLoc);
        TvLoc.setOnClickListener(TvLoc_OnClickListener());

        tvWrkItm = (TextView) findViewById(R.id.tvWrkItm);
        tvWrkItm_setOnClickListener();


        tvWrkSts = (TextView) findViewById(R.id.tvWrkSts);
        tvWorkItemStatus_setOnClickListener();

        if (mWorkId > 0 && mDayID > 0)
        {
            loadWorkNote(mWorkId);
        }
        else
        {
            loadPerviouslySelected();
        }

    }

    //not found return null
    protected String getSelectFromBundle(ArrayList<KeyValuePart> sourceArray, Intent data)
    {
        String ret = null;
        if (data != null)
        {
            Bundle bundle = data.getExtras();
            if (bundle != null && sourceArray != null)
            {
                int position = bundle.getInt("position", -1);

                if (position > -1 && position < sourceArray.size())
                {
                    ret = sourceArray.get(position).Value;
                }
            }
        }
        return ret;
    }

    protected Boolean setSpinnerSelectionByBundleArgPosotion(Spinner spinner, Intent data)
    {
        Boolean ret = false ;
        if (data != null) {
            Bundle bundle = data.getExtras(); //從 Intent 取出 Bundle
            if (bundle != null) {
                int position = bundle.getInt("position", -1);
                if (position > -1) {
                    spinner.setSelection(position);
                    ret = true;
                }
            }
        }
        return ret;
    }



    //載入指定生產記錄
    protected void loadWorkNote(Long pWorkID)
    {
        Cursor vCursor = mDb.getWorkNoteCursorByID(pWorkID);

        if (vCursor != null && vCursor.moveToFirst())
        {
            isSkipOneTime_ReloadLocation = true;

            int vDepId = vCursor.getInt(vCursor.getColumnIndex(MyDBHelper.FD_Works_DepId));
            mDepId = vDepId;
            setSpinnerSelectionById(mSpnDept, vDepId);

            loadSpnLoca(vDepId);
            int vLocId = vCursor.getInt(vCursor.getColumnIndex(MyDBHelper.FD_Works_LocId));
            mLocId = vLocId;
            setSpinnerSelectionById(mSpnLoca, vLocId);

            etWork.setText( vCursor.getString( vCursor.getColumnIndex(MyDBHelper.FD_Works_WorkItem) ).toString());
            etStatus.setText( vCursor.getString( vCursor.getColumnIndex(MyDBHelper.FD_Works_WorkStatus) ).toString());

        }
        vCursor.close();

    }

    //載入指定生產記錄
    protected void loadPerviouslySelected()
    {
        if (mDepId > 0) {
            isSkipOneTime_ReloadLocation = true;
            mDepId = setSpinnerSelectionById(mSpnDept, mDepId);

            if (mDepId > 0)
            {
                loadSpnLoca(mDepId);
                mLocId = setSpinnerSelectionById(mSpnLoca, mLocId);
            }
        }
    }




    //region 有關 部門 的操作

    //載入 部門 選項清單
    protected void loadSpnDept()
    {
        SpinnerAdapter vAdapter =
                new ArrayAdapter<>(this, R.layout.mysimple_spinner_dropdown_item, mDb.getDepartmentList());
        mSpnDept.setAdapter(vAdapter);
    }

    //部門變動，連動更新 地點清單
    protected AdapterView.OnItemSelectedListener  mSpnDept_setOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isSkipOneTime_ReloadLocation)
                {
                    isSkipOneTime_ReloadLocation = false;
                }
                else
                {
                    KeyValuePart vDep = (KeyValuePart) adapterView.getItemAtPosition(i);
                    mDepId = vDep.Key;
                    loadSpnLoca(vDep.Key);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    //部門清單 長按事件，轉跳到 部門編輯
    protected View.OnLongClickListener mSpnDept_setOnLongClickListener() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                openEditDept();
                return true;
            }
        };
    }

    //部門標題，轉跳到 部門編輯
    protected View.OnClickListener TvDep_OnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditDept();
            }
        };

    }

    //轉跳到 部門編輯
    protected void openEditDept()
    {
        int vNewFunc = enumFunc.DepartmentList;
        Bundle vData = new Bundle();
        vData.putInt(Bundle_NewFunc, vNewFunc);
        Intent vIntent = new Intent(WorkNoteActivity.this, ListItemActivity.class);
        vIntent.putExtras(vData);
        //vIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //重要的旗標，避免開啟另一個intent後且尚未結束時，就立即返回 onActivityResult
        startActivityForResult(vIntent, vNewFunc);
    }

    //endregion


    //region 有關 地點 的操作

    //載入 地點 選項清單
    protected void loadSpnLoca(long argDepId)
    {
        SpinnerAdapter vAdapter =
                new ArrayAdapter<>(this, R.layout.mysimple_spinner_dropdown_item,mDb.getLocationList(argDepId));
        mSpnLoca.setAdapter(vAdapter);
    }

    //部門變動，連動更新 地點清單
    protected AdapterView.OnItemSelectedListener  mSpnLoc_OnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    KeyValuePart vLoc = (KeyValuePart) adapterView.getItemAtPosition(i);
                    mLocId = vLoc.Key    ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }


    //地點清單 長按事件，轉跳到 地點編輯
    public View.OnLongClickListener mSpnLoca_setOnLongClickListener() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                openEditLoc();
                return true;
            }
        };
    }

    //地點清單 按事件，轉跳到 地點編輯
    protected View.OnClickListener TvLoc_OnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditLoc();

            }
        };
    }

    //地點清單 長按事件，轉跳到 地點編輯
    protected void openEditLoc()
    {
        int vNewFunc = enumFunc.LocationList;
        KeyValuePart vDept = (KeyValuePart) mSpnDept.getSelectedItem();
        Bundle vData = new Bundle();
        vData.putInt(Bundle_NewFunc, vNewFunc);
        vData.putLong(ListItemActivity.Bundle_DeptId, vDept.Key);
        Intent vIntent = new Intent(WorkNoteActivity.this, ListItemActivity.class);
        vIntent.putExtras(vData);
        startActivityForResult(vIntent, vNewFunc);
    }
    //endregion



    //region 有關 生產項目 操作
    //綁定 生產項目 標題 按下事件
    protected void tvWrkItm_setOnClickListener()
    {
        View.OnClickListener holder = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditListWorkItem();
            }
        };
        tvWrkItm.setOnClickListener(holder);
    }


    //開啟 生產項目 列表 編輯 選取
    protected void openEditListWorkItem()
    {
        int vNewFunc = enumFunc.WorkItemList;
        long vDepId = getSelectItemKey(mSpnDept);
        Bundle vData = new Bundle();
        vData.putInt(Bundle_NewFunc, vNewFunc);
        vData.putLong(ListItemActivity.Bundle_DeptId, vDepId);
        Intent vIntent = new Intent(WorkNoteActivity.this, ListItemActivity.class);
        vIntent.putExtras(vData);
        startActivityForResult(vIntent, vNewFunc);
    }
    //endregion



    //region 有關 生產狀況 操作
    //綁定 生產狀況 標題 按下事件
    protected void tvWorkItemStatus_setOnClickListener()
    {
        View.OnClickListener holder = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditListWorkItemStatus();
            }
        };
        tvWrkSts.setOnClickListener(holder);
    }

    ///開啟 生產況狀 列表 編輯 選取
    protected void openEditListWorkItemStatus()
    {
        int vNewFunc = enumFunc.WorkItemStatusList;
        Bundle vData = new Bundle();
        vData.putInt(Bundle_NewFunc, vNewFunc);
        Intent vIntent = new Intent(WorkNoteActivity.this, ListItemActivity.class);
        vIntent.putExtras(vData);
        startActivityForResult(vIntent, vNewFunc);
    }
    //endregion




    //使用者按下 ok 按鈕，執行 新增/更新/存檔
    protected Button.OnClickListener btnWorkOk_OnClickListener()
    {
        return
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (   insertUpdateWork(true) > 0)
                        {
                            ListItemActivity.startActivity_WorkNoteList(mDayID, mDepId, mLocId, mTypId, WorkNoteActivity.this);
                            WorkNoteActivity.this.finish();
                        }
                    }
                };
    }


    protected long insertUpdateWork(Boolean ShowMessage)
    {
        long id = 0;
        long vDepId = getSelectItemKey(mSpnDept);
        long vLocId = getSelectItemKey(mSpnLoca);
        String vWrkItm = etWork.getText().toString();
        String vWrkSts = etStatus.getText().toString();

        if (TextUtils.isEmpty(vWrkItm) && TextUtils.isEmpty(vWrkSts))
        {
            Toast.makeText(WorkNoteActivity.this, "未輸入任何資料!!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (mWorkId == 0) {
                //新增
                id = mDb.insertWorkList(mDayID, vDepId, vLocId, vWrkItm, vWrkSts);
                mWorkId = id;

                if (ShowMessage) {
                    if (id > 0) {
                        Toast.makeText(WorkNoteActivity.this, "新增成功。", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(WorkNoteActivity.this, "新增失敗。", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                //修改
                id = mWorkId;
                int vUpdateCount = mDb.updateWorkList(mWorkId, vDepId, vLocId, vWrkItm, vWrkSts);
                if (ShowMessage) {
                    if (vUpdateCount > 0) {
                        Toast.makeText(WorkNoteActivity.this, "更新成功。", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WorkNoteActivity.this, "更新失敗。", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        return id;
    }

    //使用者按下 cancer 按鈕 關閉頁面
    protected Button.OnClickListener btnWorkCancer_OnClickListener()
    {
        return new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListItemActivity.startActivity_WorkNoteList(mDayID,mDepId,mLocId,mTypId, WorkNoteActivity.this );
                WorkNoteActivity.this.finish();
            }
        };
    }






}
