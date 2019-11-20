package org.fifthgen.colouringbooks.controller.paint;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.fifthgen.colouringbooks.MyApplication;
import org.fifthgen.colouringbooks.R;
import org.fifthgen.colouringbooks.controller.BaseActivity;
import org.fifthgen.colouringbooks.factory.AnimateFactory;
import org.fifthgen.colouringbooks.factory.MyDialogFactory;
import org.fifthgen.colouringbooks.factory.SharedPreferencesFactory;
import org.fifthgen.colouringbooks.model.AsynImageLoader;
import org.fifthgen.colouringbooks.model.SaveImageAsyn;
import org.fifthgen.colouringbooks.util.FileUtils;
import org.fifthgen.colouringbooks.util.ImageSaveUtil;
import org.fifthgen.colouringbooks.util.L;
import org.fifthgen.colouringbooks.util.ShareImageUtil;
import org.fifthgen.colouringbooks.view.ColorPicker;
import org.fifthgen.colouringbooks.view.MyProgressDialog;
import org.fifthgen.colouringbooks.view.imageButtonDefine;
import org.fifthgen.colouringbooks.view.imageButtonDefineSecondLay;
import org.fifthgen.colouringbooks.view.imageCheckBoxDefine;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import uk.co.senab.photoview.ColourImageView;
import uk.co.senab.photoview.OnDrawLineListener;
import uk.co.senab.photoview.PhotoViewAttacher;

@SuppressWarnings("unused")
public class PaintActivity extends BaseActivity implements View.OnClickListener {

    ColourImageView colourImageView;
    PhotoViewAttacher mAttacher;
    TableLayout tableLayout;
    ImageView currentColor, cColor1, cColor2, cColor3, cColor4;
    ColorPicker colorPickerSeekBar, largecolorpicker;
    ImageView advanceColor;
    imageButtonDefine save, share, more, delete;
    imageButtonDefineSecondLay undo, redo;
    imageCheckBoxDefine pick, drawLine, jianbian_color;
    String URL;
    int PAINTNAME;
    LinearLayout advanceLay, largecolorpickerlay;
    MyDialogFactory myDialogFactory;
    View.OnClickListener checkCurrentColor = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.current_pen1:
                    //change background
                    view.setBackgroundResource(R.drawable.main_bg);
                    setWhiteRoundBg(R.id.current_pen2, R.id.current_pen3, R.id.current_pen4);
                    //set currentimageview
                    currentColor = (ImageView) view;
                    //change colourImageview color
                    changeCurrentColor((ImageView) view);
                    break;
                case R.id.current_pen2:
                    //change background
                    view.setBackgroundResource(R.drawable.main_bg);
                    setWhiteRoundBg(R.id.current_pen1, R.id.current_pen3, R.id.current_pen4);
                    //set currentimageview
                    currentColor = (ImageView) view;
                    //change colourImageview color
                    changeCurrentColor((ImageView) view);
                    break;
                case R.id.current_pen3:
                    //change background
                    view.setBackgroundResource(R.drawable.main_bg);
                    setWhiteRoundBg(R.id.current_pen2, R.id.current_pen1, R.id.current_pen4);
                    //set currentimageview
                    currentColor = (ImageView) view;
                    //change colourImageview color
                    changeCurrentColor((ImageView) view);
                    break;
                case R.id.current_pen4:
                    //change background
                    view.setBackgroundResource(R.drawable.main_bg);
                    setWhiteRoundBg(R.id.current_pen2, R.id.current_pen3, R.id.current_pen1);
                    //set currentimageview
                    currentColor = (ImageView) view;
                    //change colourImageview color
                    changeCurrentColor((ImageView) view);
                    break;
            }
        }
    };
    private Bitmap cachedBitmap;
    private int isShowing = 2;
    private boolean fromSDCard = false;
    private int isInterstial = 0; // count to show Interstial
    private InterstitialAd mInterstitialAd;

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(MyApplication.INTERSTITIAL_AD);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        initViews();
        addEvents();
        if (getIntent().hasExtra(MyApplication.BIGPIC)) {
            fromSDCard = false;
            URL = Objects.requireNonNull(getIntent().getExtras()).getString(MyApplication.BIGPIC);
            loadLargeImage();
        } else if (getIntent().hasExtra(MyApplication.BIGPICFROMUSER)) {
            fromSDCard = true;
            URL = Objects.requireNonNull(getIntent().getExtras()).getString(MyApplication.BIGPICFROMUSER);
            PAINTNAME = getIntent().getExtras().getInt(MyApplication.BIGPICFROMUSERPAINTNAME);
            loadLargeImageFromSDCard();
        } else {
            finish();
        }

        if (shouldAskPermissions()) {
            askPermissions();
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void loadLargeImageFromSDCard() {
        MyProgressDialog.show(this, null, getString(R.string.loadpicture));
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                AsynImageLoader.showLargeImageAsynWithNoCacheOpen(colourImageView, URL, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        MyProgressDialog.DismissDialog();
                        Toast.makeText(PaintActivity.this, getString(R.string.loadpicturefailed), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        mAttacher = new PhotoViewAttacher(colourImageView, bitmap);
                        //rotate screen load bitmap saved in the savestates
                        if (cachedBitmap != null) {
                            colourImageView.setImageBT(cachedBitmap);
                        }
                        MyProgressDialog.DismissDialog();
                        //show advancelay
                        advanceLayToggle();
                        showInterstials();
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                        MyProgressDialog.DismissDialog();
                        Toast.makeText(PaintActivity.this, getString(R.string.loadpicturefailed), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadLargeImage() {
        MyProgressDialog.show(this, null, getString(R.string.loadpicture));
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                AsynImageLoader.showLargeImageAsynWithAllCacheOpen(colourImageView, URL, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        MyProgressDialog.DismissDialog();
                        Log.e("fail", Objects.requireNonNull(failReason.getCause().getMessage()));
                        Toast.makeText(PaintActivity.this, getString(R.string.loadpicturefailed), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        mAttacher = new PhotoViewAttacher(colourImageView, bitmap);
                        //rotate screen load bitmap saved in the savestates
                        if (cachedBitmap != null) {
                            colourImageView.setImageBT(cachedBitmap);
                        } else {
                            openSaveImage(ImageSaveUtil.convertImageLargeUrl(URL).hashCode());
                            showHintDialog();
                        }
                        MyProgressDialog.DismissDialog();
                        //show advancelay
                        advanceLayToggle();

                        showInterstials();

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                        MyProgressDialog.DismissDialog();
                        Toast.makeText(PaintActivity.this, getString(R.string.loadpicturefailed), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }.execute();
    }

    private void showHintDialog() {
        myDialogFactory.showPaintHintDialog();
    }

    private void initViews() {
        myDialogFactory = new MyDialogFactory(this);
        advanceLay = findViewById(R.id.topfirstlay);
        colourImageView = findViewById(R.id.fillImageview);
        cColor1 = findViewById(R.id.current_pen1);
        cColor2 = findViewById(R.id.current_pen2);
        cColor3 = findViewById(R.id.current_pen3);
        cColor4 = findViewById(R.id.current_pen4);
        tableLayout = findViewById(R.id.colortable);
        colorPickerSeekBar = findViewById(R.id.seekcolorpicker);
        largecolorpicker = findViewById(R.id.largepicker);
        largecolorpickerlay = findViewById(R.id.largepickerlay);
        advanceColor = findViewById(R.id.advance_color);
        undo = findViewById(R.id.undo);
        redo = findViewById(R.id.redo);
        save = findViewById(R.id.save);
        share = findViewById(R.id.share);
        more = findViewById(R.id.more);
        delete = findViewById(R.id.delete);
        pick = findViewById(R.id.pickcolor);
        drawLine = findViewById(R.id.drawline);
        jianbian_color = findViewById(R.id.jianbian_color);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addEvents() {
        advanceColor.setOnClickListener(view -> advanceLayToggle());

        undo.setOnClickListener(view -> {

            colourImageView.undo();
            isInterstial++;

            if (isInterstial == 4) {
                // show ads.
                isInterstial = 0;
                showInterstials();
            }
        });

        redo.setOnClickListener(view -> colourImageView.redo());

        currentColor = cColor1;

        getSavedColors(cColor1, cColor2, cColor3, cColor4);

        cColor1.setOnClickListener(checkCurrentColor);
        cColor2.setOnClickListener(checkCurrentColor);
        cColor3.setOnClickListener(checkCurrentColor);
        cColor4.setOnClickListener(checkCurrentColor);

        changeCurrentColor(currentColor);

        colorPickerSeekBar.setOnChangedListener(this::changeCurrentColor);

        colorPickerSeekBar.setOnTouchListener((view, motionEvent) -> {
            int action = motionEvent.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    largecolorpickerlay.setVisibility(View.VISIBLE);
                    largecolorpickerlay.startAnimation(AnimateFactory.getInstance().popupAnimation(PaintActivity.this));
                case MotionEvent.ACTION_MOVE:
                    largecolorpicker.setColor(colorPickerSeekBar.getColor());
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    largecolorpickerlay.setVisibility(View.GONE);
                    break;
            }
            return false;
        });

        colorPickerSeekBar.setColor(getResources().getColor(R.color.maincolor));
        colourImageView.setOnRedoUndoListener((undoSize, redoSize) -> {
            if (undoSize != 0) {
                undo.setEnabled(true);
                undo.setImageSrc(R.drawable.blueundo);
            } else {
                undo.setEnabled(false);
                undo.setImageSrc(R.drawable.greyundo);
            }
            if (redoSize != 0) {
                redo.setEnabled(true);
                redo.setImageSrc(R.drawable.blueredo);
            } else {
                redo.setEnabled(false);
                redo.setImageSrc(R.drawable.greyredo);
            }
        });

        save.setOnClickListener(view -> saveToLocal());

        share.setOnClickListener(view -> shareImage());

        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            for (int j = 0; j < ((TableRow) tableLayout.getChildAt(i)).getChildCount(); j++) {
                if (((TableRow) tableLayout.getChildAt(i)).getChildAt(j) instanceof Button) {
                    ((TableRow) tableLayout.getChildAt(i)).getChildAt(j).setOnClickListener(this);
                }
            }
        }

        pick.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                drawLine.setChecked(false);
                myDialogFactory.showPickColorHintDialog();
                colourImageView.setModel(ColourImageView.Model.PICKCOLOR);
                colourImageView.setOnColorPickListener((status, color) -> {
                    if (status) {
                        changeCurrentColor(color);
                        pick.setChecked(false);
                    } else {
                        Toast.makeText(PaintActivity.this, getString(R.string.pickcolorerror), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                backToColorModel();
            }
        });

        drawLine.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                pick.setChecked(false);
                myDialogFactory.showBuxianButtonClickDialog();
                colourImageView.setModel(ColourImageView.Model.DRAW_LINE);
                colourImageView.setOnDrawLineListener(new OnDrawLineListener() {
                    @Override
                    public void OnDrawFinishedListener(boolean drawed, int startX, int startY, int endX, int endY) {
                        if (!drawed) {
                            Toast.makeText(PaintActivity.this, getString(R.string.drawLineHint_finish), Toast.LENGTH_SHORT).show();
                        } else {
                            myDialogFactory.showBuxianNextPointSetDialog();
                        }
                    }

                    @Override
                    public void OnGivenFirstPointListener(int startX, int startY) {
                        myDialogFactory.showBuxianFirstPointSetDialog();
                    }

                    @Override
                    public void OnGivenNextPointListener(int endX, int endY) {

                    }
                });
            } else {
                colourImageView.clearPoints();
                backToColorModel();
            }
        });

        delete.setOnClickListener(v -> {
            View.OnClickListener listener = v1 -> {
                myDialogFactory.dismissDialog();
                repaint();
            };
            myDialogFactory.showRepaintDialog(listener);
        });

        more.setOnClickListener(v -> gotoAdvancePaintActivity());

        jianbian_color.setOnCheckedChangeListener((view, checked) -> {
            if (checked) {
                myDialogFactory.showGradualHintDialog();
                colourImageView.setModel(ColourImageView.Model.FILLGRADUALCOLOR);
                jianbian_color.setText(R.string.jianbian_color);
            } else {
                colourImageView.setModel(ColourImageView.Model.FILLCOLOR);
                jianbian_color.setText(R.string.normal_color);
            }
        });
    }

    private void backToColorModel() {
        colourImageView.setModel(ColourImageView.Model.FILLCOLOR);
        jianbian_color.setChecked(false);
    }

    private void setWhiteRoundBg(int view1, int view2, int view3) {
        findViewById(view1).setBackgroundResource(R.drawable.white_bg);
        findViewById(view2).setBackgroundResource(R.drawable.white_bg);
        findViewById(view3).setBackgroundResource(R.drawable.white_bg);
    }


    private void gotoAdvancePaintActivity() {
        L.e("advancepaint");
        SaveImageAsyn.OnSaveFinishListener finishlistener = path -> {
            MyProgressDialog.DismissDialog();
            Intent intent = new Intent(PaintActivity.this, AdvancePaintActivity.class);
            intent.putExtra("imagepath", path);
            startActivityForResult(intent, MyApplication.PaintActivityRequest);
        };

        saveToLocal(finishlistener);
    }

    private void advanceLayToggle() {
        if (isShowing == 2) {
            advanceColor.setImageResource(R.drawable.hidebutton);
            AnimateFactory.getInstance().BounceInDownAnimation(advanceLay, findViewById(R.id.advancelay2), findViewById(R.id.colorpicklay));
            isShowing = 1;
        } else if (isShowing == 1) {
            if (advanceLay.getVisibility() == View.VISIBLE) {
                advanceColor.setImageResource(R.drawable.hidebutton);
                AnimateFactory.getInstance().SlideOutUpAnimation(advanceLay);
                isShowing = 0;
            } else {
                advanceColor.setImageResource(R.drawable.hidebutton);
                AnimateFactory.getInstance().BounceInDownAnimation(findViewById(R.id.advancelay2), findViewById(R.id.colorpicklay));
                isShowing = 0;
            }
        } else {
            advanceColor.setImageResource(R.drawable.showbutton);
            AnimateFactory.getInstance().SlideOutUpAnimation(advanceLay, findViewById(R.id.advancelay2), findViewById(R.id.colorpicklay));
            isShowing = 2;
        }
    }

    private void shareImage() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        MyProgressDialog.show(PaintActivity.this, null, getString(R.string.savingimage));
        SaveImageAsyn saveImageAsyn = new SaveImageAsyn();

        if (fromSDCard) {
            saveImageAsyn.execute(colourImageView.getmBitmap(), PAINTNAME);
        } else {
            saveImageAsyn.execute(colourImageView.getmBitmap(), ImageSaveUtil.convertImageLargeUrl(URL).hashCode());
        }

        saveImageAsyn.setOnSaveSuccessListener(path -> {
            MyProgressDialog.DismissDialog();

            if (path == null) {
                Toast.makeText(PaintActivity.this, getString(R.string.saveFailed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PaintActivity.this, getString(R.string.saveSuccess) + path, Toast.LENGTH_SHORT).show();
                ShareImageUtil.getInstance(PaintActivity.this).shareImg(path);
            }
        });
    }

    private void saveToLocal() {
        MyProgressDialog.show(PaintActivity.this, null, getString(R.string.savingimage));
        SaveImageAsyn saveImageAsyn = new SaveImageAsyn();

        if (fromSDCard) {
            saveImageAsyn.execute(colourImageView.getmBitmap(), PAINTNAME);
        } else {
            saveImageAsyn.execute(colourImageView.getmBitmap(), ImageSaveUtil.convertImageLargeUrl(URL).hashCode());
        }

        saveImageAsyn.setOnSaveSuccessListener(path -> {
            MyProgressDialog.DismissDialog();
            if (path == null) {
                Toast.makeText(PaintActivity.this, getString(R.string.saveFailed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PaintActivity.this, getString(R.string.saveSuccess) + path, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToLocal(SaveImageAsyn.OnSaveFinishListener onSaveFinishListener) {
        MyProgressDialog.show(PaintActivity.this, null, getString(R.string.savingimage));
        SaveImageAsyn saveImageAsyn = new SaveImageAsyn();

        if (fromSDCard) {
            saveImageAsyn.execute(colourImageView.getmBitmap(), PAINTNAME);
        } else {
            saveImageAsyn.execute(colourImageView.getmBitmap(), ImageSaveUtil.convertImageLargeUrl(URL).hashCode());
        }

        saveImageAsyn.setOnSaveSuccessListener(onSaveFinishListener);
    }

    private void openSaveImage(int hashCode) {
        try {
            File extDir = this.getApplicationContext().getExternalFilesDir(null);
            String root = String.format("%s/MyFCWorks/", Objects.requireNonNull(extDir).getPath());
            // String root = Environment.getExternalStorageDirectory().getPath() + ;
            String path = root + hashCode + ".png";
            File file = new File(path);
            if (!file.exists()) {
                throw new Exception("open image failed");
            }
            Bitmap bMap = BitmapFactory.decodeFile(path);
            colourImageView.setImageBT(bMap);
        } catch (Exception e) {
            L.e(e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int color;
        color = ((ColorDrawable) view.getBackground()).getColor();
        L.e(color + "");
        colorPickerSeekBar.setColor(color);
        changeCurrentColor(color);
    }

    private void changeCurrentColor(int color) {
        setFillColorModel();
        colourImageView.setColor(color);
        currentColor.setImageDrawable(new ColorDrawable(color));
    }

    private void setFillColorModel() {
        pick.setChecked(false);
        drawLine.setChecked(false);
    }


    private void changeCurrentColor(ImageView currentColor) {
        setFillColorModel();
        colorPickerSeekBar.setColor(((ColorDrawable) currentColor.getDrawable()).getColor());
        colourImageView.setColor(((ColorDrawable) currentColor.getDrawable()).getColor());
    }

    @Override
    public void onBackPressed() {
        View.OnClickListener savelistener = view -> {
            DialogInterface.OnDismissListener onCancelListener = dialogInterface -> saveToLocalAndFinish();
            myDialogFactory.dismissDialog(onCancelListener);
        };

        View.OnClickListener quitlistener = view -> {
            myDialogFactory.dismissDialog();
            finish();
        };

        myDialogFactory.FinishSaveImageDialog(savelistener, quitlistener);
    }

    private void saveToLocalAndFinish() {
        MyProgressDialog.show(PaintActivity.this, null, getString(R.string.savingimage));
        SaveImageAsyn saveImageAsyn = new SaveImageAsyn();

        if (fromSDCard) {
            saveImageAsyn.execute(colourImageView.getmBitmap(), PAINTNAME);
        } else {
            saveImageAsyn.execute(colourImageView.getmBitmap(), ImageSaveUtil.convertImageLargeUrl(URL).hashCode());
        }

        saveImageAsyn.setOnSaveSuccessListener(path -> {
            MyProgressDialog.DismissDialog();

            if (path == null) {
                Toast.makeText(PaintActivity.this, getString(R.string.saveFailed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PaintActivity.this, getString(R.string.saveSuccess) + path, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        if (colourImageView != null && colourImageView.getmBitmap() != null) {
            outState.putParcelable("bitmap", (colourImageView.getmBitmap().copy(colourImageView.getmBitmap().getConfig(), true)));
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.getParcelable("bitmap") != null)
            cachedBitmap = savedInstanceState.getParcelable("bitmap");

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MyApplication.PaintActivityRequest) {
            if (resultCode == MyApplication.RepaintResult) {
                repaint();
            }
        }

    }

    private void repaint() {
        if (fromSDCard) {
            if (FileUtils.deleteFile(URL)) {
                finish();
            } else {
                Toast.makeText(this, getString(R.string.deletePaintFailed), Toast.LENGTH_SHORT).show();
            }
        } else {
            MyProgressDialog.show(this, null, getString(R.string.loadpicture));
            colourImageView.clearStack();

            AsynImageLoader.showLargeImageAsynWithAllCacheOpen(colourImageView, URL, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    MyProgressDialog.DismissDialog();
                    Toast.makeText(PaintActivity.this, getString(R.string.loadpicturefailed), Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    mAttacher = new PhotoViewAttacher(colourImageView, bitmap);
                    MyProgressDialog.DismissDialog();
                    showInterstials();

                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                    MyProgressDialog.DismissDialog();
                    Toast.makeText(PaintActivity.this, getString(R.string.loadpicturefailed), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    private void setSavedColors(ImageView cColor1, ImageView cColor2, ImageView cColor3, ImageView cColor4) {
        SharedPreferencesFactory.saveInteger(this, SharedPreferencesFactory.SavedColor1, ((ColorDrawable) cColor1.getDrawable()).getColor());
        SharedPreferencesFactory.saveInteger(this, SharedPreferencesFactory.SavedColor2, ((ColorDrawable) cColor2.getDrawable()).getColor());
        SharedPreferencesFactory.saveInteger(this, SharedPreferencesFactory.SavedColor3, ((ColorDrawable) cColor3.getDrawable()).getColor());
        SharedPreferencesFactory.saveInteger(this, SharedPreferencesFactory.SavedColor4, ((ColorDrawable) cColor4.getDrawable()).getColor());
    }

    private void getSavedColors(ImageView cColor1, ImageView cColor2, ImageView cColor3, ImageView cColor4) {
        cColor1.setImageDrawable(new ColorDrawable(SharedPreferencesFactory.getInteger(this, SharedPreferencesFactory.SavedColor1, getResources().getColor(R.color.red))));
        cColor2.setImageDrawable(new ColorDrawable(SharedPreferencesFactory.getInteger(this, SharedPreferencesFactory.SavedColor2, getResources().getColor(R.color.yellow))));
        cColor3.setImageDrawable(new ColorDrawable(SharedPreferencesFactory.getInteger(this, SharedPreferencesFactory.SavedColor3, getResources().getColor(R.color.skyblue))));
        cColor4.setImageDrawable(new ColorDrawable(SharedPreferencesFactory.getInteger(this, SharedPreferencesFactory.SavedColor4, getResources().getColor(R.color.green))));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        colourImageView.onRecycleBitmaps();
    }

    private void showInterstials() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}