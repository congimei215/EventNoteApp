package tw.com.imeifoods.cong.eventnote;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION_CODES.M;
import static tw.com.imeifoods.cong.eventnote.ListItemActivity.Bundle_DayId;
import static tw.com.imeifoods.cong.eventnote.MyUtil.getSelectItemKey;
import static tw.com.imeifoods.cong.eventnote.MyUtil.setSpinnerSelectionById;


public class EventNoteActivity extends AppCompatActivity {
    private MyDBHelper mDb;
    private Button btnEventOk, btnEventCancer, btnTakePhoto;
    private EditText  mEdtAbnormal, mEdtSuggestion, mEdtRemark;
    private Spinner mSpnType, mSpnDept, mSpnLoca;
    private TextView TvType, TvDep, TvLoc, tvSug, tvAbn;
    private  long mDayID = 0 , mDepId = 0, mLocId = 0, mTypId = 0, mEventID = 0;
    private Boolean isSkipOneTime_ReloadLocation = false;


    //處理照相
    private static String mPhotoFileName;
    private static File mPhotoFile;
    private final int IMAGE_MAX_WIDTH = 640;
    private final int IMAGE_MAX_HEIGHT = IMAGE_MAX_WIDTH *  3 / 4;
    private final int IMAGE_QUALITY = 80;


    //處理相片瀏覽
    private  GridView mGridView1;
    private ImageView mImageView1;
    private File[] mImageFiles;
    private static final int REQUEST_EXTERNEL_PERMISSION = 200;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_note);
        init();
    }





    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override //從別的頁面返回
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        int vFunc = requestCode;

        if (vFunc == enumFunc.AbnTypeList)
        {
            KeyValuePart vSelectPart = (KeyValuePart) mSpnType.getSelectedItem();
            loadSpnType();
            Boolean hasSelect = setSpinnerSelectionByBundleArgPosotion(mSpnType, data);

            if (!hasSelect && vSelectPart != null) {
                setSpinnerSelectionById(mSpnType, vSelectPart.Key);  //將原本的選取值選起來
            }
        }
        //使用者從「部門」清單返回
        else if (vFunc == enumFunc.DepartmentList)
        {
            KeyValuePart vDep = (KeyValuePart) mSpnDept.getSelectedItem(); //取得原本「部門」下拉選項舊資料
            loadSpnDept(); //重新載入最新的「部門」下拉選項 (選取項目回到預設選取第一個選項)
            Boolean hasSelect = setSpinnerSelectionByBundleArgPosotion(mSpnDept, data); //將本頁部門下拉預設選取項改為使用者在上一頁清單中選取的值

            if (!hasSelect && vDep != null) {
                setSpinnerSelectionById(mSpnDept, vDep.Key);  //將原本的選取值選起來
            }

        }
        else if (vFunc == enumFunc.LocationList)
        {
            KeyValuePart vDept = (KeyValuePart) mSpnDept.getSelectedItem();
            KeyValuePart vLoca = (KeyValuePart) mSpnLoca.getSelectedItem();

            if (vDept != null) {
                loadSpnLoca(vDept.Key);
            }

            Boolean hasSelect = setSpinnerSelectionByBundleArgPosotion(mSpnLoca, data);

            if (!hasSelect && vLoca != null) {
                setSpinnerSelectionById(mSpnLoca, vLoca.Key);
            }
        }
        else if (vFunc == enumFunc.AbnList)
        {
            ArrayList<KeyValuePart> vListAbnormal =  mDb.getListAbnormal(mTypId);
            String selectItemText = getSelectValueFromBundle_position(vListAbnormal, data);
            if (selectItemText != null )
            {
                mEdtAbnormal.setText(selectItemText);
            }
        }
        else if (vFunc == enumFunc.SugList)
        {
            String vAbormalString = mEdtAbnormal.getText().toString();
            ArrayList<KeyValuePart> vListSuggest = mDb.getListSuggest(mTypId,  vAbormalString);
            String selectItemText = getSelectValueFromBundle_position(vListSuggest, data);

            if (selectItemText != null )
            {
                mEdtSuggestion.setText(selectItemText);
            }
        }
        else if (vFunc == enumFunc.CAMERA) {
            if ( resultCode == RESULT_OK) {
                try {
                    mDb.signHasUpdate(mEventID, mDb.NEED_UPLOAD);
                    mPhotoFile = FileUtil.compressImage(mPhotoFile, IMAGE_MAX_WIDTH, IMAGE_MAX_HEIGHT, IMAGE_QUALITY); //壓縮圖片
                    loadImageGridView(mEventID);
                }
                catch (Exception ex ){
                    Log.d("Cong:", ex.getMessage());
                }
            }
        }
    }

    //#region 建立選單
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.eventnote_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();

        if (item_id == R.id.action_saveFile)
        {
            //存檔
            insertUpdateEvent(true );
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
            //轉跳到事件記錄清單
            ListItemActivity.startActivity_EventNoteList(mDayID, mDepId, mLocId, mTypId, this);
            finish();
        }
        else if (item_id == R.id.action_WorkNoteList)
        {
            //轉跳到生產記錄清單
            ListItemActivity.startActivity_WorkNoteList(mDayID, mDepId, mLocId, mTypId, this);
            finish();
        }

        return true;
    }
    //#region 建立選單

    @Override // 覆寫請求授權後執行的方法
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 第一個參數是請求代碼
        // 第二個參數是請求授權的名稱
        // 第三個參數是請求授權的結果，PERMISSION_GRANTED或PERMISSION_DENIED
        // 讀取指定的照片檔案名稱設定給ImageView元件

        // 如果是寫入外部儲存設備授權請求
        if (requestCode == REQUEST_EXTERNEL_PERMISSION) {
            // 如果在授權請求選擇「允許」
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 拍攝照片
                takePicture(mEventID);
            }
            // 如果在授權請求選擇「拒絕」
            else {
                // 顯示沒有授權的訊息
                Toast.makeText(this, "您未同意授權寫入外部儲存設備。", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        ListItemActivity.startActivity_EventNoteList(mDayID, mDepId, mLocId, mTypId, this);
        finish();
    }

    //初始化變數
    protected void init()
    {

        mDb = new MyDBHelper(this);
        final Bundle vBundle = this.getIntent().getExtras();

        if (vBundle != null)
        {
            mDayID = vBundle.getLong(Bundle_DayId);
            mEventID = vBundle.getLong(ListItemActivity.Bundle_EventId);
            mDepId = vBundle.getLong(ListItemActivity.Bundle_DeptId);
            mLocId = vBundle.getLong(ListItemActivity.Bundle_LocId);
            mTypId = vBundle.getLong(ListItemActivity.Bundle_TypeId);
            String day = mDb.getDayName(mDayID);
            this.setTitle("【事件明細】" + day) ;//帶入日誌名稱
        }
        else {
            mEventID = 0;
        }

        //沒有取得日誌ID，就退出事件記錄畫面
        if (mDayID == 0)
        {
            ListItemActivity.startActivity_EventNoteList(mDayID, mDepId, mLocId, mTypId, this);
            return;
        }


        btnEventOk = (Button) findViewById(R.id.btnEventSave);
        btnEventCancer = (Button) findViewById(R.id.btnEventCancer);
        btnTakePhoto = (Button) findViewById(R.id.btnPhoto);

        mSpnType = (Spinner)findViewById(R.id.spnType);
        loadSpnType();
        mSpnType.setOnItemSelectedListener(mSpnType_setOnItemSelectedListener());

        mSpnDept = (Spinner)findViewById(R.id.spnDept);
        loadSpnDept();
        mSpnLoca = (Spinner)findViewById(R.id.spnLoca);
        mEdtAbnormal = (EditText)findViewById(R.id.edtAbnormal);
        mEdtSuggestion = (EditText)findViewById(R.id.edtSuggestion);
        mEdtRemark = (EditText)findViewById(R.id.edtRemark);

        mGridView1 = (GridView) findViewById(R.id.gridView1);
        mImageView1 = (ImageView) findViewById(R.id.imageView1);
        TvType = (TextView) findViewById(R.id.tvType);
        TvDep = (TextView) findViewById(R.id.TvDep);
        TvLoc = (TextView) findViewById(R.id.TvLoc);
        tvAbn = (TextView) findViewById(R.id.tvAbn);
        tvSug = (TextView) findViewById(R.id.tvSug);

        btnEventOk.setOnClickListener(btnEventOk_OnClickListener());
        btnEventCancer.setOnClickListener(btnEvnetCancer_OnClickListener());
        mSpnDept.setOnItemSelectedListener(mSpnDept_setOnItemSelectedListener());

        mSpnLoca.setLongClickable(true);
        mSpnLoca.setOnLongClickListener(mSpnLoca_setOnLongClickListener());
        mSpnLoca.setOnItemSelectedListener(mSpnLoc_OnItemSelectedListener());

        btnTakePhoto.setOnClickListener(btbTakePhoto_OnClickListener());
        mImageView1.setOnClickListener(mImageView1_OnClickListener());
        TvType.setOnClickListener(TvType_OnClickListener());
        TvDep.setOnClickListener(TvDep_OnClickListener());
        TvLoc.setOnClickListener(TvLoc_OnClickListener());
        tvSug_setOnClickListener();
        tvAbn_setOnClickListener();

        if (mEventID > 0 && mDayID > 0)
        {
            loadEventNote(mEventID);
        }
        else
        {
            loadPerviouslySelected();
        }
    }

    //載入指定生產記錄
    protected void loadPerviouslySelected()
    {
        if (mDepId > 0) {
            isSkipOneTime_ReloadLocation = true;
            mDepId = setSpinnerSelectionById(mSpnDept, mDepId);

            if (mDepId > 0) {
                loadSpnLoca(mDepId);
                mLocId = setSpinnerSelectionById(mSpnLoca, mLocId);
            }
        }

        if (mTypId > 0)
        {
            setSpinnerSelectionById(mSpnType, mTypId);
        }
    }


    //not found return null
    protected String getSelectValueFromBundle_position(ArrayList<KeyValuePart> sourceArray, Intent data)
    {
        String ret = null;
        KeyValuePart selectItem = getSelectFromBundle_position(sourceArray, data);

        if (selectItem != null)
        {
            ret = selectItem.Value;
        }
        return ret;
    }


    protected KeyValuePart getSelectFromBundle_position(ArrayList<KeyValuePart> sourceArray, Intent data)
    {
        KeyValuePart ret = null;
        if (data != null)
        {
            Bundle bundle = data.getExtras();
            if (bundle != null && sourceArray != null)
            {
                int position = bundle.getInt("position", -1);

                if (position > -1 && position < sourceArray.size())
                {
                    ret = sourceArray.get(position);
                }
            }
        }
        return ret;
    }

    //載入 類型 選項清單
    protected void loadSpnType()
    {
        SpinnerAdapter vAdapter =
                new ArrayAdapter<>(this, R.layout.mysimple_spinner_dropdown_item,  mDb.getTypesList());
        mSpnType.setAdapter(vAdapter);
    }

    //載入 部門 選項清單
    protected void loadSpnDept()
    {
        SpinnerAdapter vAdapter =
                new ArrayAdapter<>(this, R.layout.mysimple_spinner_dropdown_item, mDb.getDepartmentList());
        mSpnDept.setAdapter(vAdapter);
    }


    //載入 地點 選項清單
    protected void loadSpnLoca(long argDepId)
    {
        SpinnerAdapter vAdapter =
                new ArrayAdapter<>(this, R.layout.mysimple_spinner_dropdown_item,mDb.getLocationList(argDepId));
        mSpnLoca.setAdapter(vAdapter);
    }


    //載入指定事件記錄
    protected void loadEventNote(Long argEventID)
    {
        Cursor vCursor = mDb.getEventListCursorByEvent(argEventID);

        if (vCursor != null && vCursor.moveToFirst())
        {
            int vTypeId =  vCursor.getInt(vCursor.getColumnIndex("TypeId"));
            setSpinnerSelectionById(mSpnType, vTypeId);
            mTypId = vTypeId;

            int vDepId = vCursor.getInt(vCursor.getColumnIndex("DepartmentId"));
            mDepId = vDepId ;
            isSkipOneTime_ReloadLocation = true;
            setSpinnerSelectionById(mSpnDept, vDepId);

            loadSpnLoca(vDepId);
            int vLocId = vCursor.getInt(vCursor.getColumnIndex("LocationID"));
            mLocId = vLocId;
            setSpinnerSelectionById(mSpnLoca, vLocId);

            mEdtAbnormal.setText( vCursor.getString( vCursor.getColumnIndex("Abnormal") ).toString());
            mEdtSuggestion.setText( vCursor.getString( vCursor.getColumnIndex("Suggestion") ).toString());
            mEdtRemark.setText( vCursor.getString(vCursor.getColumnIndex("Remark")).toString());
            vCursor.close();

            loadImageGridView(argEventID);
        }
    }

    //類型清單 選取事件
    protected AdapterView.OnItemSelectedListener  mSpnType_setOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                KeyValuePart vType =  (KeyValuePart) adapterView.getItemAtPosition(position);
                mTypId = vType.Key;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    //部門清單 選取事件，連動更新 地點清單
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

    protected View.OnClickListener TvType_OnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditType();
            }
        };
    }

    //轉跳到 類型清單 只提供選取
    protected void openEditType()
    {
        int vNewFunc = enumFunc.AbnTypeList;
        Bundle vData = new Bundle();
        vData.putInt(ListItemActivity.Bundle_NewFunc, vNewFunc);
        Intent vIntent = new Intent(EventNoteActivity.this, ListItemActivity.class);
        vIntent.putExtras(vData);
        //vIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //重要的旗標，避免開啟另一個intent後且尚未結束時，就立即返回 onActivityResult
        startActivityForResult(vIntent, vNewFunc);
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
        vData.putInt(ListItemActivity.Bundle_NewFunc, vNewFunc);
        Intent vIntent = new Intent(EventNoteActivity.this, ListItemActivity.class);
        vIntent.putExtras(vData);
        //vIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //重要的旗標，避免開啟另一個intent後且尚未結束時，就立即返回 onActivityResult
        startActivityForResult(vIntent, vNewFunc);
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
        vData.putInt(ListItemActivity.Bundle_NewFunc, vNewFunc);
        vData.putLong(ListItemActivity.Bundle_DeptId, vDept.Key);
        Intent vIntent = new Intent(EventNoteActivity.this, ListItemActivity.class);
        vIntent.putExtras(vData);
        startActivityForResult(vIntent, vNewFunc);
    }

    //部門清單 選取事件，連動更新 地點清單
    protected AdapterView.OnItemSelectedListener  mSpnLoc_OnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    KeyValuePart vLoc = (KeyValuePart) adapterView.getItemAtPosition(i);
                    mLocId = vLoc.Key;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    //綁定 異常說明標題 按下事件
    protected void tvAbn_setOnClickListener()
    {
        View.OnClickListener holder = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditListAbn();
            }
        };
        tvAbn.setOnClickListener(holder);
    }

    //開啟 異常說明清單編輯
    protected void openEditListAbn()
    {
        int vNewFunc = enumFunc.AbnList;
        Bundle vData = new Bundle();
        vData.putInt(ListItemActivity.Bundle_NewFunc, vNewFunc);
        vData.putLong(ListItemActivity.Bundle_TypeId, mTypId);
        Intent vIntent = new Intent(EventNoteActivity.this, ListItemActivity.class);
        vIntent.putExtras(vData);
        startActivityForResult(vIntent, vNewFunc);
    }



    //綁定 建議與處置說明標題 按下事件
    protected void tvSug_setOnClickListener()
    {
        View.OnClickListener holder = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditListSug();
            }
        };
        tvSug.setOnClickListener(holder);
    }

    //開啟 建議與處置說明清單編輯
    protected void openEditListSug()
    {
        Bundle vData = new Bundle();
        int vNewFunc = enumFunc.SugList;
        vData.putInt(ListItemActivity.Bundle_NewFunc, vNewFunc);

        String AbnormalString = mEdtAbnormal.getText().toString();
        long vListAbrormal_ID = mDb.findAbnormalID(mTypId, AbnormalString);
        vData.putLong(ListItemActivity.Bundle_ListAbnormal_Id, vListAbrormal_ID);

        Intent vIntent = new Intent(EventNoteActivity.this, ListItemActivity.class);
        vIntent.putExtras(vData);
        startActivityForResult(vIntent, vNewFunc);
    }

    //使用者按下 ok 按鈕，執行 新增/更新/存檔
    protected Button.OnClickListener btnEventOk_OnClickListener()
    {
        return
        new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(  insertUpdateEvent(true) > 0) {
                    ListItemActivity.startActivity_EventNoteList(mDayID, mDepId, mLocId, mTypId, EventNoteActivity.this);
                    EventNoteActivity.this.finish();
                }
            }
        };
    }


    protected long insertUpdateEvent(Boolean ShowMessage)
    {
        long id = 0;
        long vTypId = getSelectItemKey(mSpnType);
        long vDepId = getSelectItemKey(mSpnDept);
        long vLocId = getSelectItemKey(mSpnLoca);
        String vAbormal = mEdtAbnormal.getText().toString();
        String vSuggestion = mEdtSuggestion.getText().toString();
        String vRemark = mEdtRemark.getText().toString();

        if (TextUtils.isEmpty(vAbormal) && TextUtils.isEmpty(vSuggestion) && TextUtils.isEmpty(vRemark))
        {
            if (ShowMessage) {
                Toast.makeText(EventNoteActivity.this, "未輸入任何資料!!", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            if (mEventID == 0) {
                //新增
                id = mDb.insertEventList(mDayID, vTypId, vDepId, vLocId, vAbormal, vSuggestion, vRemark);
                mEventID = id;
                /*
                if (id > 0)
                {
                    Bundle vData = new Bundle();
                    vData.putLong(Bundle_DayId, mDayID);
                    vData.putLong(Bundle_EventId, mEventID);
                    Intent vIntent = new Intent(EventNoteActivity.this, EventNoteActivity.class);
                    vIntent.putExtras(vData);
                    startActivity(vIntent);
                }
                */
                if (ShowMessage) {
                    if (id > 0) {
                        Toast.makeText(EventNoteActivity.this, "新增成功。", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(EventNoteActivity.this, "新增失敗。", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                //修改
                id = mDb.updateEventList(mEventID, vTypId, vDepId, vLocId, vAbormal, vSuggestion, vRemark);
                if (ShowMessage) {
                    //Toast.makeText(view.getContext(), "更新成功。", Toast.LENGTH_SHORT).show();
                    Toast.makeText(EventNoteActivity.this, "更新成功。", Toast.LENGTH_SHORT).show();
                }
                id = mEventID;
            }
        }
        return id;
    }

    //使用者按下 cancer 按鈕 關閉頁面
    protected Button.OnClickListener btnEvnetCancer_OnClickListener()
    {
        return new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
               ListItemActivity.startActivity_EventNoteList(mDayID, mDepId, mLocId,mTypId, EventNoteActivity.this   );
               EventNoteActivity.this.finish();
            }
        };
    }

    //使用者按下 照相 按鈕
    protected Button.OnClickListener btbTakePhoto_OnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //新增狀態時，要先存檔再開啟照像
                if (mEventID == 0)
                {
                    String vAbnor = mEdtAbnormal.getText().toString();

                    if (TextUtils.isEmpty(vAbnor)) {
                        mEdtAbnormal.setText(vAbnor + "暫存事件");
                    }

                    insertUpdateEvent(false);
                }
                if (mEventID == 0)
                {
                    Toast.makeText(view.getContext(), "資料暫存失敗，請重新操作。", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    takePicture(mEventID);
                }
            }
        };
    }

    //點選ImageView 關閉ImageView
     protected View.OnClickListener mImageView1_OnClickListener()
     {
         return
                 new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 mImageView1.setVisibility(View.GONE);
                 mGridView1.setVisibility(View.VISIBLE);
             }

         };
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




    //>> 顯示照片GridView >>
    protected void loadImageGridView(long  pEventId) {
        try {
            mImageView1.setVisibility(View.GONE);
            FilenameFilter vFileFilterJpg = FileUtil.buildExtendNameFilter(".jpg");
            mImageFiles = FileUtil.getImagesPath(this, pEventId).listFiles(vFileFilterJpg);

            ImageAdapter vImageAdapter = new ImageAdapter(EventNoteActivity.this, mImageFiles);
            mGridView1.setAdapter(vImageAdapter);
            vImageAdapter.notifyDataSetChanged();
            setDynamicHeight(mGridView1,4);

        }
        catch(Exception ex) {
            Log.d("cong:", ex.getMessage());
        }
    }

    //調整照片列表的高度，android不會自動變大
    private void setDynamicHeight(GridView gridView, int pColumnNumber) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
            // pre-condition
            return;
        }

        int items = gridViewAdapter.getCount();
        View listItem = gridViewAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        int totalHeight = listItem.getMeasuredHeight();

        if( items > pColumnNumber ){
            totalHeight =(int) (totalHeight  * Math.ceil(  items /(double) pColumnNumber));
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }

    //刪除照片 從 ImageAdapter / imageView.setOnLongClickListener 呼叫
    protected void deleteImage(final File[] pImageFiles, final int pPosition)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否刪除本張照片？")
                .setNegativeButton("否",null)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pImageFiles[pPosition].delete();
                        loadImageGridView(mEventID);
                        mDb.signHasUpdate(mEventID, mDb.NEED_UPLOAD);
                    }
                })
                .show();
    }

    //設定點選的照片到ImageView放大顯示
    public void setImageView(int position){

        Bitmap bm = BitmapFactory.decodeFile(mImageFiles[position].getAbsolutePath());
        mImageView1.setImageBitmap(bm);
        mImageView1.setVisibility(View.VISIBLE);
        mGridView1.setVisibility(View.GONE);

    }
    
    //執行 照像
    private void takePicture(long pEventId) {
        if (hasStoragePermission() && pEventId != 0) {
            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  // 啟動相機元件用的Intent物件
            intentCamera.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            mPhotoFileName = FileUtil.getUniqueFileName() + ".jpg"; //產生照片流水序號檔名
            mPhotoFile = new File(FileUtil.getImagesPath(this, mEventID) , mPhotoFileName); //                                                                                  // /storage/emulated/0/Android/data/tw.com.imeifoods.cong.eventnote/files/xxx/images/xxxxjpg

            Uri vContentUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                vContentUri = FileProvider.getUriForFile(EventNoteActivity.this, this.getPackageName() + ".fileprovider", mPhotoFile); // /tw.com.imeifoods.cong.eventnote.fileprovider/external_path/Android/data/tw.com.imeifoods.cong.eventnote/files/xxx/images/xxx.jpg
            }
            else {
                vContentUri = Uri.parse("file://" + mPhotoFile.getPath());
            }

            // 設定相片檔案
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, vContentUri);
            intentCamera.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //重要的旗標，避免開啟Camara後，就立即返回 onActivityResult

            // 啟動相機元件
            startActivityForResult(intentCamera, enumFunc.CAMERA);
        }
    }
    
    // 請求授權讀取與處理寫入外部儲存設備
    private boolean hasStoragePermission() {
        boolean vRet = true;

        // 如果裝置版本是6.0（包含）以上
        if (Build.VERSION.SDK_INT >= M) {

            // 取得授權狀態，參數是請求授權的名稱
            int hasPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            // 如果未授權
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                // 請求授權
                //     第一個參數是請求授權的名稱
                //     第二個參數是請求代碼
                requestPermissions(
                        new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNEL_PERMISSION);
                vRet = false; //在onRequestPermissionsResult會再執行一次啟動照像，因此先取消本次照像動作
            }
        }

        return vRet;
    }


}
