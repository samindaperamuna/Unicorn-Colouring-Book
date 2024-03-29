package org.fifthgen.colouringbooks.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.fifthgen.colouringbooks.util.L;

/**
 * Created by GameGFX Studio on 2015/8/5.
 */
@SuppressWarnings("unused")
public class FCDBHelper extends SQLiteOpenHelper {
    public static final String FCTABLE = "fc_table";
    public static final String FCIMAGETABLE = "fc_imagetable";
    public static final String FCTABLE_COL_0 = "ThemeID";
    public static final String FCTABLE_COL_1 = "ThemeName";
    public static final String FCTABLE_COL_2 = "Status";
    public static final String FCIMAGETABLE_COL_0 = "theme_id";
    public static final String FCIMAGETABLE_COL_1 = "pic_id";
    public static final String FCIMAGETABLE_COL_2 = "Status";
    public static final String FCIMAGETABLE_COL_3 = "WvHRadio";
    private static final String DBNAME = "fillcolor.db";
    private static final int version = 5;

    public FCDBHelper(Context context) {
        super(context, DBNAME, null, version);
    }

    public FCDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + FCTABLE + "( " +
                FCTABLE_COL_0 + " INTEGER, " +
                FCTABLE_COL_1 + " varchar(30), " +
                FCTABLE_COL_2 + " INTEGER DEFAULT 0" + " );");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + FCIMAGETABLE + "( " +
                FCIMAGETABLE_COL_0 + " INTEGER, " +
                FCIMAGETABLE_COL_1 + " INTEGER, " +
                FCIMAGETABLE_COL_2 + " INTEGER DEFAULT 0" + " );");
        onUpgrade(sqLiteDatabase, 3, version);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        switch (i) {
            case 0:
            case 1:
            case 2:
                L.e("upgrade to" + i);
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FCTABLE);
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FCIMAGETABLE);
                onCreate(sqLiteDatabase);
            case 3:
            case 4:
                L.e("upgrade to" + i);

                try {
                    sqLiteDatabase.execSQL("ALTER TABLE " + FCIMAGETABLE + " ADD COLUMN " +
                            FCIMAGETABLE_COL_3 + " REAL;");
                } catch (Exception e) {
                    L.e(e.toString());
                }
        }
    }
}
