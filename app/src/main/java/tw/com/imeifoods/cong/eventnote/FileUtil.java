package tw.com.imeifoods.cong.eventnote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {

    // 外部儲存設備是否可寫入
    public static boolean isExternalStorageWritable() {
        // 取得目前外部儲存設備的狀態
        String state = Environment.getExternalStorageState();

        // 判斷是否可寫入
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }

        return false;
    }

    // 外部儲存設備是否可讀取
    public static boolean isExternalStorageReadable() {
        // 取得目前外部儲存設備的狀態
        String state = Environment.getExternalStorageState();

        // 判斷是否可讀取
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }

        return false;
    }

    // 建立並傳回在公用相簿下參數指定的路徑
    public static File getPublicAlbumStorageDir(String albumName) {
        // 取得公用的照片路徑
        File pictures = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        // 準備在照片路徑下建立一個指定的路徑
        File file = new File(pictures, albumName);

        // 如果建立路徑不成功
        if (!file.mkdirs()) {
            Log.e("getAlbumStorageDir", "Directory not created");
        }

        return file;
    }

    // 建立並傳回在應用程式專用相簿下參數指定的路徑
    public static File getAlbumStorageDir(Context context, String albumName) {
        // 取得應用程式專用的照片路徑
        File pictures = context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);
        // 準備在照片路徑下建立一個指定的路徑
        File file = new File(pictures, albumName);

        // 如果建立路徑不成功
        if (!file.mkdirs()) {
            Log.e("getAlbumStorageDir", "Directory not created");
        }

        return file;
    }

    // 建立並傳回外部儲存媒體參數指定的路徑
    public static File getExternalStorageDir(String dir) {
        /*
            * 方法：getDataDirectory()
              解釋：返回 File ，得到 Android 資料目錄。
            * 方法：getDownloadCacheDirectory()
              解釋：返回 File ，得到 Android 下載/緩存內容目錄。
            * 方法：getExternalStorageDirectory()
              解釋：返回 File ，得到外部儲存目錄即 SDCard
            * 方法：getExternalStoragePublicDirectory(String type)
              解釋：返回 File ，取一個高端的公用的外部儲存目錄來擺放某些類型
         */
        File result = new File( Environment.getExternalStorageDirectory(), dir);

        if (!isExternalStorageWritable()) {
            return null;
        }

        if (!result.exists() && !result.mkdirs()) {
            return null;
        }

        return result;
    }

    // 讀取指定的照片檔案名稱設定給ImageView元件
    public static void fileToImageView(String fileName, ImageView imageView) {
        if (new File(fileName).exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(fileName);
            imageView.setImageBitmap(bitmap);
        }
        else {
            Log.e("fileToImageView", fileName + " not found.");
        }
    }

    // 產生唯一的檔案名稱
    public static String getUniqueFileName() {
        // 使用年月日_時分秒格式為檔案名稱
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }

    //縮圖
    public static File compressImage(File pPhotoFile, int IMAGE_MAX_WIDTH, int IMAGE_MAX_HEIGHT, int pQuality )
    {
        if (pPhotoFile == null  ) return pPhotoFile;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap sourceBitmap = BitmapFactory.decodeFile(pPhotoFile.getAbsolutePath(),bmOptions);
        Bitmap targetBitmap;


        if (sourceBitmap.getHeight() > sourceBitmap.getWidth()) {
            if (sourceBitmap.getHeight() == IMAGE_MAX_WIDTH) return pPhotoFile;
            targetBitmap = Bitmap.createScaledBitmap(sourceBitmap, IMAGE_MAX_HEIGHT, IMAGE_MAX_WIDTH, false); //縮圖

        }
        else {
            if (sourceBitmap.getWidth() == IMAGE_MAX_WIDTH) return pPhotoFile;
            targetBitmap = Bitmap.createScaledBitmap(sourceBitmap, IMAGE_MAX_WIDTH, IMAGE_MAX_HEIGHT, false); //縮圖
        }

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        targetBitmap.compress(Bitmap.CompressFormat.JPEG, pQuality, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        try {
            File newFile = new File(pPhotoFile.getParent(), "s" + pPhotoFile.getName());
            FileOutputStream fos = new FileOutputStream(newFile);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            pPhotoFile.delete();
            pPhotoFile = newFile;
        }
        catch (IOException ex){
            Log.d("haserror", ex.toString());
        }

        return pPhotoFile;
    }

    //副檔名過濾，例如".jpg"
    public static FilenameFilter buildExtendNameFilter(final String pExtendName)
    {
        return new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name.lastIndexOf('.')>0) {
                    int lastIndex = name.lastIndexOf('.');
                    String str = name.substring(lastIndex);
                    if(str.equals(pExtendName)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }


    public static  boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    public static void deleteIfExists(File file) {
        if (file != null) {
            if (file.exists() == true) {
                file.delete();
            }
        }
    }

    //取得照片檔路徑
    public static File getImagesPath(Context pContext, long pEventId)
    {
        return pContext.getExternalFilesDir(pEventId + "/images/");
    }

    //刪除相片
    public static void deleteFile(Context pContext,long pEventId)
    {
        File vFilePath = FileUtil.getImagesPath(pContext,pEventId).getParentFile();
        FileUtil.deleteDirectory(vFilePath);
    }
}