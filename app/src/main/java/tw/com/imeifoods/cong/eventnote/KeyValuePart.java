package tw.com.imeifoods.cong.eventnote;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by user on 2017/7/3.
 */

public class KeyValuePart
{
    long Key;
    String Value;

    public KeyValuePart(){}

    public KeyValuePart(int argKey, String argValue)
    {
        this.Key = argKey;
        this.Value = argValue;
    }

    public static ArrayList<KeyValuePart> convertArrayList(Cursor argCursor, int argKeyIndex, int argValueIndex)
    {
        ArrayList<KeyValuePart> vRet = new ArrayList<>();
        if (argCursor.moveToFirst()) {
            do {
                KeyValuePart vItm = new KeyValuePart(argCursor.getInt(argKeyIndex), argCursor.getString(argValueIndex));
                vRet.add(vItm);
            } while (argCursor.moveToNext());
        }
        argCursor.close();
        return vRet;
    }

    public static int findPositionById(ArrayList<KeyValuePart> argArrayList, int argId)
    {
        for (int i = 0; i < argArrayList.size(); i++) {

            if (argId == argArrayList.get(i).Key)
            {
                return i;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return this.Value;
    }
}
