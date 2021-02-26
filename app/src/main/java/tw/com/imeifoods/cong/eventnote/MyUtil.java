package tw.com.imeifoods.cong.eventnote;

import android.content.ContextWrapper;
import android.provider.Settings;
import android.widget.Spinner;

/**
 * Created by user on 2017/7/20.
 */

public  class MyUtil
{
    public static String getAndroid_ID(ContextWrapper context)
    {
        return Settings.Secure.getString(context.getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static int tryParseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    //取得Spinner元件選取的key值
    public static long getSelectItemKey(Spinner argSpn)
    {
        long vRet = 0;
        Object vItem = argSpn.getSelectedItem();
        if (vItem != null)
        {
            vRet = ((KeyValuePart)vItem).Key;
        }
        return vRet;
    }

    //設定Spinner元件預設值
    public static long setSpinnerSelectionById(Spinner argSpinner, long argId)
    {
        for(int i = 0; i < argSpinner.getCount(); i++)
        {
            KeyValuePart vSelectItem =  (KeyValuePart)argSpinner.getItemAtPosition(i);

            if (vSelectItem.Key == argId)
            {
                argSpinner.setSelection(i);
                return argId;
            }
        }

        if (argSpinner.getCount() > 0)
        {
            return argSpinner.getItemIdAtPosition(1);
        }
        return -1;
    }
}
