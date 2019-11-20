package org.fifthgen.colouringbooks.controller.main;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.fifthgen.colouringbooks.MyApplication;
import org.fifthgen.colouringbooks.R;
import org.fifthgen.colouringbooks.model.OnRecycleViewItemClickListener;
import org.fifthgen.colouringbooks.model.bean.ThemeBean;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by GameGFX Studio on 2015/8/14.
 */
public class ThemeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_ITEM = 1;
    private final int TYPE_FOOTER = 2;

    List<ThemeBean.Theme> themeList;

    Context context;
    FrameLayout footer;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;

    public ThemeListAdapter(Context context, List<ThemeBean.Theme> themeList, View footer) {
        this.context = context;
        this.themeList = themeList;
        this.footer = (FrameLayout) footer.getParent();
    }

    public void setOnRecycleViewItemClickListener(OnRecycleViewItemClickListener onRecycleViewItemClickListener) {
        this.onRecycleViewItemClickListener = onRecycleViewItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == themeList.size()) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(context).inflate(R.layout.view_list_item, parent, false);
            return new VHItem(v);
        }

        return new VHFooter(footer);
    }

    @Override
    public void onBindViewHolder(@NotNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof VHItem) {
            if (themeList.get(position) != null) {
                ((VHItem) holder).name.setText(themeList.get(position).getN());

                try {
                    AssetManager assetManager = ((VHItem) holder).parent.getContext().getAssets();
                    String ar = Objects.requireNonNull(assetManager
                            .list(String.format(Locale.getDefault(), "%s%s",
                                    MyApplication.PATHIMG, themeList.get(position).getFolder())))[0];

                    // First image.
                    InputStream ims = assetManager.open(String.format(Locale.getDefault(),
                            "%s%s/%s", MyApplication.PATHIMG,
                            themeList.get(position).getFolder(), ar));

                    Bitmap bitmap = BitmapFactory.decodeStream(ims);

                    ((VHItem) holder).image.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ((VHItem) holder).parent.setOnClickListener(view -> {
                    if (onRecycleViewItemClickListener != null)
                        onRecycleViewItemClickListener
                                .recycleViewItemClickListener(((VHItem) holder).parent, position);
                });
            }
        } else if (holder instanceof VHFooter) {
            ((VHFooter) holder).footer.setLayoutParams(
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }

    }

    @Override
    public int getItemCount() {
        return themeList.size() + 1;
    }

    public List<ThemeBean.Theme> getList() {
        return themeList;
    }

    public void updateListView(List<ThemeBean.Theme> list) {
        this.themeList = list;
        notifyDataSetChanged();
    }

    static class VHItem extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public View parent;

        public VHItem(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            parent = itemView;
        }
    }

    class VHFooter extends RecyclerView.ViewHolder {
        FrameLayout footer;

        public VHFooter(View itemView) {
            super(itemView);
            footer = (FrameLayout) itemView;
        }
    }
}
