package org.fifthgen.colouringbooks.controller.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.fifthgen.colouringbooks.R;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GameGFX Studio on 2015/8/20.
 */
@SuppressWarnings("unused")
public class ImageWallListAdapter extends RecyclerView.Adapter<ImageWallListAdapter.ViewHolder> {

    List<String> imageWall;
    private Context mContext;

    public ImageWallListAdapter(Context context, List<String> imageWall) {
        mContext = context;

        if (imageWall != null) {
            this.imageWall = imageWall;
        } else {
            this.imageWall = new ArrayList<>();
        }
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.view_imagewall_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return imageWall.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView imageName;
        TextView author;
        ImageView image;
        Button like;
        Button dontLike;
        Button comment;

        public ViewHolder(View itemView) {
            super(itemView);
            imageName = itemView.findViewById(R.id.imagename);
            author = itemView.findViewById(R.id.author);
            image = itemView.findViewById(R.id.image);
            like = itemView.findViewById(R.id.like);
            dontLike = itemView.findViewById(R.id.dontlike);
            comment = itemView.findViewById(R.id.comment);
        }
    }
}
