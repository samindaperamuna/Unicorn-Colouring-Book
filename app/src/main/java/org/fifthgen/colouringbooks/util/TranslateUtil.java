package org.fifthgen.colouringbooks.util;

import org.fifthgen.colouringbooks.MyApplication;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by GameGFX Studio on 2015/8/13.
 */
@SuppressWarnings("unused")
public class TranslateUtil {

    public static String translateByBaiduAPI(String h1) {
        MyHttpClient myHttpClient = new MyHttpClient();
        String getRest = MyApplication.BAIDUTRANSLATEAPI + "q=" + h1 + "&from=auto&to=auto";

        try {
            String ret = myHttpClient.executeGetRequest(getRest);
            if (ret != null) {
                JSONObject jsonObject = new JSONObject(ret);
                JSONArray jsonArray = jsonObject.getJSONArray("trans_result");
                return jsonArray.getJSONObject(0).getString("dst");
            } else {
                return h1;
            }
        } catch (Exception e) {
            L.e(e.getMessage());
            return h1;
        }
    }
}
