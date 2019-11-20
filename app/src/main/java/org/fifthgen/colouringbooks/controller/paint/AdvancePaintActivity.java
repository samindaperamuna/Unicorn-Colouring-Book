package org.fifthgen.colouringbooks.controller.paint;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import org.fifthgen.colouringbooks.MyApplication;
import org.fifthgen.colouringbooks.R;
import org.fifthgen.colouringbooks.controller.BaseActivity;
import org.fifthgen.colouringbooks.factory.MyDialogFactory;
import org.fifthgen.colouringbooks.listener.OnAddWordsSuccessListener;
import org.fifthgen.colouringbooks.listener.OnChangeBorderListener;
import org.fifthgen.colouringbooks.model.SaveImageAsyn;
import org.fifthgen.colouringbooks.util.ShareImageUtil;
import org.fifthgen.colouringbooks.view.MyProgressDialog;
import org.fifthgen.colouringbooks.view.imageButtonDefine;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * Created by macpro001 on 20/8/15.
 */
public class AdvancePaintActivity extends BaseActivity {

    @BindView(R.id.addwords)
    imageButtonDefine addwords;
    @BindView(R.id.addvoice)
    Button addvoice;
    @BindView(R.id.current_image)
    ImageView currentImage;
    @BindView(R.id.share)
    imageButtonDefine share;
    @BindView(R.id.repaint)
    imageButtonDefine repaint;
    @BindView(R.id.paintview)
    FrameLayout paintview;
    String imageUri;
    MyDialogFactory myDialogFactory;
    @BindView(R.id.addborder)
    imageButtonDefine addborder;
    @BindView(R.id.border)
    ImageView border;
    @BindView(R.id.cloudgallery)
    imageButtonDefine cloudgallery;
    @BindView(R.id.cancel)
    Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindows();
        setContentView(R.layout.activity_paint_advance);
        ButterKnife.bind(this);
        myDialogFactory = new MyDialogFactory(AdvancePaintActivity.this);
        imageUri = getIntent().getStringExtra("imagepath");
        currentImage.setImageBitmap(BitmapFactory.decodeFile(imageUri));
        addwords.setOnClickListener(view -> addwordsDialog());
        addborder.setOnClickListener(view -> addBorderDialog());
        share.setOnClickListener(view -> shareDrawable());
        repaint.setOnClickListener(view -> repaintPictureDialog());
        cloudgallery.setOnClickListener(view -> uploadImage());
        cancel.setOnClickListener(view -> finish());
    }

    private void uploadImage() {
        Toast.makeText(AdvancePaintActivity.this, getString(R.string.comingsoon), Toast.LENGTH_SHORT).show();
    }

    private void initWindows() {

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = MyApplication.screenWidth;
        this.getWindow().setAttributes(params);
    }

    private void repaintPictureDialog() {
        View.OnClickListener onClickListener = view -> {
            myDialogFactory.dismissDialog();
            setResult(MyApplication.RepaintResult);
            finish();
        };
        myDialogFactory.showRepaintDialog(onClickListener);
    }

    @SuppressWarnings({"deprecation"})
    private void shareDrawable() {
        paintview.setDrawingCacheEnabled(true);
        paintview.destroyDrawingCache();
        paintview.buildDrawingCache();
        MyProgressDialog.show(AdvancePaintActivity.this, null, getString(R.string.savingimage));
        SaveImageAsyn saveImageAsyn = new SaveImageAsyn();
        saveImageAsyn.execute(paintview.getDrawingCache(), MyApplication.SHAREWORK);
        saveImageAsyn.setOnSaveSuccessListener(path -> {
            MyProgressDialog.DismissDialog();
            if (path == null) {
                Toast.makeText(AdvancePaintActivity.this, getString(R.string.saveFailed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AdvancePaintActivity.this, getString(R.string.saveSuccess) + path, Toast.LENGTH_SHORT).show();
                ShareImageUtil.getInstance(AdvancePaintActivity.this).shareImg(path);
            }
        });
    }

    private void addwordsDialog() {
        OnAddWordsSuccessListener addwordssuccess = dragedTextView -> ((ViewGroup) currentImage.getParent()).addView(dragedTextView);
        myDialogFactory.showAddWordsDialog(addwordssuccess);
    }

    private void addBorderDialog() {
        OnChangeBorderListener addborderlistener = (drawableid, pt, pd, pl, pr) -> {
            if (drawableid != 0) {
                border.setBackgroundResource(drawableid);
                currentImage.setPadding(pl, pt, pr, pd);
                currentImage.requestLayout();
            }
            paintview.requestLayout();
        };
        myDialogFactory.showAddBorderDialog(addborderlistener);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
