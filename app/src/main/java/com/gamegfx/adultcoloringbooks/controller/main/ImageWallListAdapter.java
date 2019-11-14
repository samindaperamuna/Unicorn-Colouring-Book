package com.gamegfx.adultcoloringbooks.controller.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gamegfx.adultcoloringbooks.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GameGFX Studio on 2015/8/20.
 */
public class ImageWallListAdapter extends RecyclerView.Adapter<ImageWallListAdapter.ViewHolder> {

    List<String> imagewall;
    private Context mContext;

    public ImageWallListAdapter(Context context, List<String> imagewall) {
        mContext = context;
        if (imagewall != null) {
            this.imagewall = imagewall;
        } else {
            this.imagewall = new ArrayList<>();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.view_imagewall_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return imagewall.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView imagename;
        TextView author;
        ImageView image;
        Button like;
        Button dontlike;
        Button comment;

        public ViewHolder(View itemView) {
            super(itemView);
            imagename = itemView.findViewById(R.id.imagename);
            author = itemView.findViewById(R.id.author);
            image = itemView.findViewById(R.id.image);
            like = itemView.findViewById(R.id.like);
            dontlike = itemView.findViewById(R.id.dontlike);
            comment = itemView.findViewById(R.id.comment);
        }

    }
}
