package tw.com.imeifoods.cong.eventnote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private ViewGroup layout;
    private Context context;
    private List coll;
    private File[] mImageFiles;

    public ImageAdapter(Context context, /*List coll,*/ File[] pImageFiles) {

        super();
        this.context = context;
        //this.coll = pImageFiles.length;
        this.mImageFiles = pImageFiles;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowview = inflater.inflate(R.layout.item_photo, parent, false);
        layout = (ViewGroup) rowview.findViewById(R.id.rl_item_photo);
        ImageView imageView = (ImageView) rowview.findViewById(R.id.imageView1);

        try {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            float dd = dm.density;
            float px = 25 * dd;
            float screenWidth = dm.widthPixels;
            int newWidth = (int) (screenWidth - px) / 4; // 一行顯示四個縮圖
            int newHeight = newWidth * 3 / 4;
            layout.setLayoutParams(new GridView.LayoutParams(newWidth, newHeight));
            imageView.setId(position);

            File vImageFile = mImageFiles[position];
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(vImageFile.getAbsolutePath(), bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            imageView.setImageBitmap(bitmap);
            //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        catch(Exception ex) {}

        //點擊照片
        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "index:" + position, Toast.LENGTH_SHORT).show();
                ((EventNoteActivity)context).setImageView(position);
            }

        });

        //刪除
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((EventNoteActivity)context).deleteImage(mImageFiles, position);
                return true;
            }
        });

        return rowview;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        //return coll.size();
        return mImageFiles.length;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        //return coll.get(arg0);
        return arg0;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

}