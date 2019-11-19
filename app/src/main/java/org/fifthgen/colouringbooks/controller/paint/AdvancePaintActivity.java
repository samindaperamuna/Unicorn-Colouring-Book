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
import org.fifthgen.colouringbooks.view.DragedTextView;
import org.fifthgen.colouringbooks.view.ImageButton_define;
import org.fifthgen.colouringbooks.view.MyProgressDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

//import com.gamegfx.colmoana.util.UmengUtil;

/**
 * Created by macpro001 on 20/8/15.
 */
public class AdvancePaintActivity extends BaseActivity {

    public static int Offest = MyApplication.screenWidth / 40;
    @BindView(R.id.addwords)
    ImageButton_define addwords;
    @BindView(R.id.addvoice)
    Button addvoice;
    @BindView(R.id.current_image)
    ImageView currentImage;
    @BindView(R.id.share)
    ImageButton_define share;
    @BindView(R.id.repaint)
    ImageButton_define repaint;
    @BindView(R.id.paintview)
    FrameLayout paintview;
    String imageUri;
    MyDialogFactory myDialogFactory;
    @BindView(R.id.addborder)
    ImageButton_define addborder;
    @BindView(R.id.border)
    ImageView border;
    @BindView(R.id.cloudgallery)
    ImageButton_define cloudgallery;
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
        addwords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addwordsDialog();
            }
        });
        addborder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBorderDialog();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareDrawable();
            }
        });
        repaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repaintPictureDialog();
            }
        });
        cloudgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialogFactory.dismissDialog();
                setResult(MyApplication.RepaintResult);
                finish();
            }
        };
        myDialogFactory.showRepaintDialog(onClickListener);
    }

    private void shareDrawable() {
        paintview.setDrawingCacheEnabled(true);
        //UmengUtil.analysitic(AdvancePaintActivity.this, UmengUtil.SHAREIMAGE, imageUri);
        paintview.destroyDrawingCache();
        paintview.buildDrawingCache();
        MyProgressDialog.show(AdvancePaintActivity.this, null, getString(R.string.savingimage));
        SaveImageAsyn saveImageAsyn = new SaveImageAsyn();
        saveImageAsyn.execute(paintview.getDrawingCache(), MyApplication.SHAREWORK);
        saveImageAsyn.setOnSaveSuccessListener(new SaveImageAsyn.OnSaveFinishListener() {
            @Override
            public void onSaveFinish(String path) {
                MyProgressDialog.DismissDialog();
                if (path == null) {
                    Toast.makeText(AdvancePaintActivity.this, getString(R.string.saveFailed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdvancePaintActivity.this, getString(R.string.saveSuccess) + path, Toast.LENGTH_SHORT).show();
                    ShareImageUtil.getInstance(AdvancePaintActivity.this).shareImg(path);
                }
            }
        });
    }

    private void addwordsDialog() {
        OnAddWordsSuccessListener addwordssuccess = new OnAddWordsSuccessListener() {
            @Override
            public void addWordsSuccess(DragedTextView dragedTextView) {
                ((ViewGroup) currentImage.getParent()).addView(dragedTextView);
            }
        };
        myDialogFactory.showAddWordsDialog(addwordssuccess);
    }

    private void addBorderDialog() {
        OnChangeBorderListener addborderlistener = new OnChangeBorderListener() {
            @Override
            public void changeBorder(int drawableid, int pt, int pd, int pl, int pr) {
                if (drawableid != 0) {
                    border.setBackgroundResource(drawableid);
                    currentImage.setPadding(pl, pt, pr, pd);
                    currentImage.requestLayout();
                }
                paintview.requestLayout();
            }
        };
        myDialogFactory.showAddBorderDialog(addborderlistener);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
