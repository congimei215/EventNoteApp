package tw.com.imeifoods.cong.eventnote;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class FirstSettingActivity extends AppCompatActivity {
    private EditText EdtName;
    private MyDBHelper mDb;
    private String _Android_ID = "";
    public String getAndroid_ID()
    {
        if (_Android_ID.equals("")) {
            _Android_ID = MyUtil.getAndroid_ID(this);
        }
        return _Android_ID;
    }

    private Button BtnOk;
    private RadioButton rdb0, rdb1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_setting);
        Init();
    }

    //region 建立選單
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.firstsetting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();

        switch (item_id){
            case R.id.action_BackItem:
                //轉跳到日誌檔
                //ListItemActivity.startActivity_DayList(this);
                finish();

                break;
            default: return false;
        }
        return true;
    }
    //endregion 建立選單


    protected void Init()
    {

        this.setTitle("【設定】");

        mDb = new MyDBHelper(this);
        EdtName =  (EditText)findViewById(R.id.EdtName);
        BtnOk = (Button)findViewById(R.id.BtnCreateNewDevice);
        rdb0 = (RadioButton) findViewById(R.id.rdb0);
        rdb1 = (RadioButton) findViewById(R.id.rdb1);

        Cursor vCursor = mDb.getDeviceInf(getAndroid_ID());
        Boolean vHasData =  vCursor.moveToFirst();

        if (vHasData)
        {
            //載入設備名稱
            String deviceName = vCursor.getString(vCursor.getColumnIndex(MyDBHelper.FD_DeviceName));
            EdtName.setText(deviceName);
            vCursor.close();

            //讀取隸屬廠別設定
            String location = mDb.getConfig("p1");
            if (location.equals("0"))
            {
                rdb0.setChecked(true); //南崁
            }
            else
            {
                rdb1.setChecked(true); //龍潭
            }

        }
        else
        {
            vCursor.close();
        }

        //綁定 存檔按鈕 按下事件
        BtnOk.setOnClickListener(BtnOk_setOnClickListener());

    }

    protected View.OnClickListener BtnOk_setOnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSetting();
            }
        };
    }



    protected void saveSetting()
    {
        Boolean vRet = false;
        String deviceName = EdtName.getText().toString();
        deviceName = deviceName.trim();

        if (! TextUtils.isEmpty(deviceName))
        {
            if (mDb.UpdateDeviceInf(deviceName, getAndroid_ID()) < 1) {
                mDb.InsertDeviceInf(deviceName, getAndroid_ID());
            }

            //讀取隸屬廠別設定
            if (rdb0.isChecked())
            {
                vRet = mDb.updateConfig("p1", "0"); //南崁
            }
            else
            {
                vRet = mDb.updateConfig("p1", "1"); //龍潭
            }
        }

        if (vRet)
        {
            Toast.makeText(this , "參數存檔完畢!!", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(this , "參數存檔失敗!!", Toast.LENGTH_LONG).show();
        }
        if (vRet) {
            //ListItemActivity.startActivity_DayList(this);
            finish();
        }

    }




}
