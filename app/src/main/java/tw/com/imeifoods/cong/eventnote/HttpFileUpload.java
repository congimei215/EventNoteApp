package tw.com.imeifoods.cong.eventnote;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//參數1：Params   -- 要執行 doInBackground() 時傳入的參數，數量可以不止一個
//參數2：Progress -- doInBackground() 中，呼叫 publishProgress() 來傳送資料給 onProgressUpdate()，傳入的參數，數量可以不止一個
//參數3：Rsesult  -- doInBackground() 傳回執行結果， 若您沒有參數要傳入，則填入 Void (注意 V 為大寫)。
public class HttpFileUpload extends AsyncTask<String, Integer, Integer> {
    File[] mFiles;
    URL connectURL;
    String mEventId;
    int mDeviceId;


    HttpFileUpload(File[] pFiles,int pDeviceId, long pEventId, String pUrl)
    {
        try{
            connectURL = new URL(pUrl);
            mFiles = pFiles;
            mEventId = String.valueOf(pEventId);
            mDeviceId = pDeviceId;
        }catch(Exception ex){
            Log.i("HttpFileUpload","URL Malformatted");
        }
    }


    @Override
    protected Integer doInBackground(String... strings) {
        Log.d("Cong:", "run doInBackground");
        int vCountFile = 0;
        if (mFiles == null || mFiles.length == 0) return vCountFile;

        while(vCountFile < mFiles.length) {
            try {
                FileInputStream fstrm = new FileInputStream(mFiles[vCountFile]);
                if (Sending(fstrm,mDeviceId, mEventId, mFiles[vCountFile].getName()  ) > -1)
                {
                    ++vCountFile;
                }
                else
                {
                    vCountFile = -1;
                    break;
                }
            } catch (FileNotFoundException e) {
                vCountFile = -1;
                break;
            }
            catch (Exception ex) {
                vCountFile = -1;
                break;
            }
        }
        return vCountFile;
    }

   @Override
    protected void onProgressUpdate(Integer... progress)
    {
        // 這裡接收傳入的 progress 值, 並更新進度表畫面
        // 參數是 Integer 型態的陣列
        // 但是因為在 doInBackground() 只傳一個參數
        // 所以以 progress[0] 取得傳入參數
        //setProgressPercent(progress[0]);
    }

    @Override
    protected void onPostExecute(Integer result)
    {
        //showDialog("Downloaded " + result + " bytes");
    }

    protected int Sending(FileInputStream fStream,int pDeviceId, String pEventId, String pFileName)
    {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "----WebKitFormBoundaryrGKCBY7qhFd3TrwA";
        String Tag="fSnd";
        int vRet = 0;
        try
        {
            // Open a HTTP connection to the URL
            HttpURLConnection conn = (HttpURLConnection)connectURL.openConnection();

            // Allow Inputs
            conn.setDoInput(true);

            // Allow Outputs
            conn.setDoOutput(true);

            // Don't use a cached copy.
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"eventid\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(pDeviceId) + "_" + pEventId);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"filename\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(  pFileName);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);


            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pFileName +"\"" + lineEnd);
            dos.writeBytes(lineEnd);


            // create a buffer of maximum size
            int bytesAvailable = fStream.available();

            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[ ] buffer = new byte[bufferSize];

            // read file and write it into form...
            int bytesRead = fStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fStream.available();
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                bytesRead = fStream.read(buffer, 0,bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            fStream.close();

            dos.flush();

            Log.e(Tag,"File Sent, Response: "+String.valueOf(conn.getResponseCode()));

            InputStream is = conn.getInputStream();

            // retrieve the response from server
            int ch;

            StringBuffer b =new StringBuffer();
            while( ( ch = is.read() ) != -1 ){ b.append( (char)ch ); }
            String s=b.toString();
            Log.i("Response",s);
            dos.close();
            vRet += 1;
        }
        catch (MalformedURLException ex)
        {
            Log.e(Tag, "URL error: " + ex.getMessage(), ex);
            vRet = -1;
        }
        catch (IOException ioe)
        {
            Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
            vRet = -1;
        }
        catch (Exception ex)
        {
            vRet = -1;
        }
        return vRet;
    }


}