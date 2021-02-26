package tw.com.imeifoods.cong.eventnote;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static tw.com.imeifoods.cong.eventnote.ListItemActivity.Bundle_DayId;

public class CheckNote1 extends AppCompatActivity {
    //git test version 1
    String spnItemString[] = {"1. 原料處理中心","2. 料理餐食總廠","3. 西點麵包廠","4. 冰品奶品廠","5. 飲料廠", "6. LPG 巡檢","7. 中央系統巡檢","8. 廢水處理場巡檢","9. 自來水桶槽"};
    Spinner Spn1;
    LinearLayout[] LinearLayouts = new LinearLayout[11];
    public static int MAX_VALUE_ADD1 = 61;
    EditText[] EditTexts = new EditText[MAX_VALUE_ADD1];
    Button btnSave, btnCancer;
    private MyDBHelper mDb;
    private  long mDayId = 0, mDepId = 0, mLocId = 0, mTypId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_note1);
        init();
    }


    //region 選單
    //建立選單
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.checknote_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();

        if (item_id == R.id.action_saveButton)
        {
            //存檔
            saveCheckNote();
        }
        else if (item_id == R.id.action_EventNoteList)
        {
            ListItemActivity.startActivity_EventNoteList(mDayId, mDepId, mLocId, mTypId, this);
            finish();
        }
        else if (item_id == R.id.action_WorkNoteList)
        {
            ListItemActivity.startActivity_WorkNoteList(mDayId, mDepId, mLocId, mTypId, this);
            finish();
        }

        return true;
    }
    //endregion



    //使用者按返回鍵的事件
    @Override
    public void onBackPressed() {
        ListItemActivity.startActivity_EventNoteList(mDayId, mDepId, mLocId, mTypId, this);
        finish();
    }

    protected void init()
    {
        mDb = new MyDBHelper(this);

        Spn1 = (Spinner) findViewById(R.id.spn1);
        Resources resources = getResources();
        String packageName = getPackageName();
        for(int i = 1; i < LinearLayouts.length ; i++)
        {
            int resID = resources.getIdentifier("Layer" + String.valueOf(i), "id", packageName);
            LinearLayouts[i] = (LinearLayout)findViewById(resID);
        }

        for(int i = 1; i < EditTexts.length ; i++)
        {
            try {


                int resID = resources.getIdentifier("et" + String.valueOf(i), "id", packageName);
                EditTexts[i] = (EditText) findViewById(resID);
            }
            catch(Exception ex) {
                Log.d("Err:", String.valueOf(i));
            }

        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,  R.layout.mysample_list_item, spnItemString);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.mysimple_spinner_dropdown_item); // The drop down view
        Spn1.setAdapter(spinnerArrayAdapter);
        Spn1.setOnItemSelectedListener(Spn1_OnItemSelectedListener());
        btnSave = (Button) findViewById(R.id.btnCheckNote1Save);
        btnSave_setOnClickListener();
        btnCancer = (Button) findViewById(R.id.BtnCancer);
        btnCancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListItemActivity.startActivity_EventNoteList(mDayId, mDepId, mLocId, mTypId, CheckNote1.this);
                CheckNote1.this.finish();
            }
        });

        //switchLinearVisible(1);
        loadCheckNote();
    }

    //檢查類型下拉選單 選取事件
    protected AdapterView.OnItemSelectedListener Spn1_OnItemSelectedListener()
    {
        return
                new Spinner.OnItemSelectedListener(){

                    @Override
                    public void onItemSelected(AdapterView parent, View v, int position, long id) {
                        // parent = 事件發生的母體 spinner_items
                        // position = 被選擇的項目index = parent.getSelectedItemPosition()
                        // id = row id，通常給資料庫使用
                        switchLinearVisible(position + 1);
                    }

                    @Override
                    public void onNothingSelected(AdapterView parent) {}
                };
    }


    //載入
    protected void loadCheckNote()
    {
        try {
            final Bundle vBundle = this.getIntent().getExtras();
            mDayId = -1;
            if (vBundle != null) {
                mDayId = vBundle.getLong(Bundle_DayId, -1);
                mDepId = vBundle.getLong(ListItemActivity.Bundle_DeptId, 0);
                mLocId = vBundle.getLong(ListItemActivity.Bundle_LocId, 0);
                mTypId = vBundle.getLong(ListItemActivity.Bundle_TypeId, 0);
                Cursor cursor = mDb.getCheckNote1(mDayId);

                if (cursor.moveToNext()) {
                    //0:checknote_id, 1:day_id, 2:startValue
                    for(int i = 1; i < EditTexts.length; i++)
                    {
                        EditTexts[i].setText(cursor.getString(i + 1));
                    }
                }
                cursor.close();
            }
        }
        catch(Exception ex) {
            Log.d("Err:" , ex.getMessage());
        }

        setTitle();
    }

    protected void setTitle()
    {
        String day = mDb.getDayName(mDayId);
        this.setTitle("【檢查表】" + day);
    }

    protected boolean insertUpdateCheckNote()
    {
        Boolean isFinish = false;
        try {
            String tSql = "";
            Cursor cursor = mDb.getCheckNote1(mDayId);    // TODO: 2017/8/17 mDayId測試旋轉畫面是否資料仍存在
            Boolean needInsert =! cursor.moveToNext();
            cursor.close();

            if (needInsert)
            {
                tSql = "insert into checknote1 (dayid) values (" + mDayId + ") ;";
                mDb.getWritableDatabase().execSQL(tSql);
            }

            tSql = "update checknote1 set ";
            for(int i = 1; i < EditTexts.length; i++)
            {
                tSql += "et" + String.valueOf(i) + " = '" + EditTexts[i].getText().toString() + "', ";
            }
            tSql = tSql.substring(0, tSql.length() - 2);
            tSql +=  " , upst = 0  where dayid = " + mDayId + ";";
            mDb.getWritableDatabase().execSQL(tSql);
            isFinish = true;
        }
        catch(Exception ex) {
            Log.d("Err:", ex.getMessage());
        }
        return isFinish;
    }

    protected void btnSave_setOnClickListener()
    {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            saveCheckNote();
            }
        });
    }

    protected void saveCheckNote()
    {
        if (insertUpdateCheckNote())
        {
            mDb.signHasUpdate(mDayId); //todo:注意mDayId是否空值。
            Toast.makeText(getApplicationContext(), "存檔成功", Toast.LENGTH_LONG).show();
            ListItemActivity.startActivity_EventNoteList(mDayId, mDepId, mLocId, mTypId, this);
            this.finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "存檔失敗", Toast.LENGTH_LONG).show();
        }
    }

    //顯示指定的 LinearLayout
    protected void switchLinearVisible(int index)
    {
        int visibility;

        for(int i = 1; i < LinearLayouts.length ; i++)
        {
            visibility = (i == index) ? View.VISIBLE: View.GONE;

            if (LinearLayouts[i].getVisibility() != visibility)
            {
                LinearLayouts[i].setVisibility(visibility);
            }
        }
    }

}
