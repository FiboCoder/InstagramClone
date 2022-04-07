package Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import Helper.SquareImageView;

public class Grid extends ArrayAdapter<String> {

    private Context context;
    private int layoutResource;
    private List<String> urlImages;


    public Grid(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);

        this.context = context;
        this.layoutResource = resource;
        this.urlImages = objects;
    }

    public class ViewHolder{

        SquareImageView ivGrid;
        ProgressBar pbGrid;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView == null){

            viewHolder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResource, parent, false);

            viewHolder.pbGrid = convertView.findViewById(R.id.pbGrid);
            viewHolder.ivGrid = convertView.findViewById(R.id.ivGrid);

            convertView.setTag(viewHolder);
        }else{

            viewHolder = (ViewHolder) convertView.getTag();

        }

        //recover image data
        String urlImage = getItem(position);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(urlImage, viewHolder.ivGrid,
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                        viewHolder.pbGrid.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        viewHolder.pbGrid.setVisibility(View.GONE);

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                        viewHolder.pbGrid.setVisibility(View.GONE);

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                        viewHolder.pbGrid.setVisibility(View.GONE);
                    }
                });

        return convertView;
    }
}
