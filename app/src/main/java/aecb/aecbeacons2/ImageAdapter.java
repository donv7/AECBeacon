package aecb.aecbeacons2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    List<AecbImage> mAecbImageList;


    public ImageAdapter(Context c) {
        mContext = c;
        mAecbImageList = new ArrayList<AecbImage>();
    }

    public int getCount() {
        //return mThumbIds.length;
        return mAecbImageList.size()+1;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public void setBeaconList(List<AecbImage> aecbImageList){
        mAecbImageList = aecbImageList;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(dpToPx(120), dpToPx(120)));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        if(position==0) {
            imageView.setImageResource(R.drawable.addphoto);
            //imageView.setBackgroundColor(Color.TRANSPARENT);
        }
        else {
            //imageView.setImageURI(new Uri(mAecbImageList.get(position).getThumbnail_image_url()));
            //Picasso.with(mContext).load("http://i.imgur.com/DvpvklR.png").into(imageView);
            Picasso.with(mContext).load(mAecbImageList.get(position-1).getThumbnail_image_url()).into(imageView);
            //imageView.setImageResource(Color.TRANSPARENT);
        }

        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            Color.RED, Color.WHITE,
            Color.YELLOW,
            Color.BLUE, Color.CYAN,
            Color.RED, Color.WHITE,
            Color.YELLOW,
            Color.BLUE, Color.CYAN,
            Color.RED, Color.WHITE,
            Color.YELLOW,
            Color.BLUE, Color.CYAN,
            Color.RED, Color.WHITE,
            Color.YELLOW,
            Color.BLUE, Color.CYAN
    };

    private static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}