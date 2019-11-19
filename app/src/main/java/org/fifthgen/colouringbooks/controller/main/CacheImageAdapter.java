package org.fifthgen.colouringbooks.controller.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import org.fifthgen.colouringbooks.MyApplication;
import org.fifthgen.colouringbooks.R;
import org.fifthgen.colouringbooks.controller.paint.PaintActivity;
import org.fifthgen.colouringbooks.model.AsynImageLoader;
import org.fifthgen.colouringbooks.model.bean.CacheImageBean;

import java.util.ArrayList;
import java.util.List;

//import com.gamegfx.colmoana.util.UmengUtil;

/**
 * Created by GameGFX Studio on 2015/9/9.
 */
public class CacheImageAdapter extends RecyclerView.Adapter<CacheImageAdapter.ViewHolder> {
    List<CacheImageBean> cacheImageBeans;
    Context context;

    public CacheImageAdapter(Context context, List<CacheImageBean> cacheImageBeans) {
        if (cacheImageBeans == null) {
            cacheImageBeans = new ArrayList<>();
        }
        this.cacheImageBeans = cacheImageBeans;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.view_cacheimage_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (cacheImageBeans.get(position).getWvHRadio() != 0) {
            holder.image.setLayoutParams(new LinearLayout.LayoutParams(MyApplication.getScreenWidth(context) / 2, (int) (MyApplication.getScreenWidth(context) / 2 / cacheImageBeans.get(position).getWvHRadio())));
        } else {
            holder.image.setLayoutParams(new LinearLayout.LayoutParams(MyApplication.getScreenWidth(context) / 2, (int) (MyApplication.getScreenWidth(context) / 2 / 0.71)));
        }
        AsynImageLoader.showImageAsynWithoutCache(holder.image, cacheImageBeans.get(position).getUrl());
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoPaintActivity(cacheImageBeans.get(position).getUrl());
            }
        });
    }

    private void gotoPaintActivity(String s) {
        //UmengUtil.analysitic(context, UmengUtil.MODELNUMBER, s);
        Intent intent = new Intent(context, PaintActivity.class);
        intent.putExtra(MyApplication.BIGPIC, s);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return cacheImageBeans.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }

    }
}
