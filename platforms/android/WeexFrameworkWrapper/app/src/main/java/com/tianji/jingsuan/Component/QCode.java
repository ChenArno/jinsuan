package com.tianji.jingsuan.Component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.ui.view.WXFrameLayout;
import com.tianji.jingsuan.R;

import org.json.JSONObject;

import java.util.Hashtable;

/**
 * Created by chencentury on 2018/4/28.
 */

public class QCode extends WXVContainer<WXFrameLayout> {
    private static final String TAG = "======>";
    private WXSDKInstance mWXSDKInstance;
    private static Context mcontext;
    private ImageView img_test;
    private static final int BLACK = 0xff000000;

    public QCode(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
        super(instance, dom, parent);
        if (mWXSDKInstance == null) {
            mWXSDKInstance = instance;
        }
    }

    @Override
    protected WXFrameLayout initComponentHostView(@NonNull Context context) {
        if(mcontext == null){
            mcontext = context;
        }
        WXFrameLayout layout = new WXFrameLayout(context);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.image_code, null);
        img_test = (ImageView) view.findViewById(R.id.pic_test);
        layout.addView(view);
        return layout;
    }

    @WXComponentProp(name = "code")
    public void setCode(String code) {
//        Bitmap bitmap = createBitmap(code);
//        img_test.setImageBitmap(bitmap,400,400);
        try {
            JSONObject jsonObject = new JSONObject(code);
            String url = jsonObject.getString("url");
            int is = jsonObject.getInt("type");
            Bitmap mBitmap = createImage(url, 400, 400, BitmapFactory.decodeResource(mcontext.getResources(), (is==1)?R.mipmap.zhifubao:R.mipmap.weixin));
            img_test.setImageBitmap(mBitmap);
        }catch (Exception e){
            Log.i(TAG, "setCode: "+e);
        }
    }
//不带图片
    public static Bitmap createBitmap(String str,int w,int h){
        Bitmap bitmap = null;
        BitMatrix result = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            result = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, w, h);
            int width = result.getWidth();
            int height = result.getHeight();
            int[] pixels = new int[width * height];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (result.get(x, y)) {
                        pixels[y * width + x] = BLACK;
                    }
                }
            }
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);
        } catch (WriterException e){
            e.printStackTrace();
        } catch (IllegalArgumentException iae){ // ?
            return null;
        }
        return bitmap;
    }

    /**
     * 生成二维码图片
     * @param text
     * @param w
     * @param h
     * @param logo
     * @return
     */
    public static Bitmap createImage(String text,int w,int h,Bitmap logo) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        try {
            Bitmap scaleLogo = getScaleLogo(logo,w,h);

            int offsetX = w / 2;
            int offsetY = h / 2;

            int scaleWidth = 0;
            int scaleHeight = 0;
            if (scaleLogo != null) {
                scaleWidth = scaleLogo.getWidth();
                scaleHeight = scaleLogo.getHeight();
                offsetX = (w - scaleWidth) / 2;
                offsetY = (h - scaleHeight) / 2;
            }
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            //设置空白边距的宽度
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, w, h, hints);
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if(x >= offsetX && x < offsetX + scaleWidth && y>= offsetY && y < offsetY + scaleHeight){
                        int pixel = scaleLogo.getPixel(x-offsetX,y-offsetY);
                        if(pixel == 0){
                            if(bitMatrix.get(x, y)){
                                pixel = 0xff000000;
                            }else{
                                pixel = 0xffffffff;
                            }
                        }
                        pixels[y * w + x] = pixel;
                    }else{
                        if (bitMatrix.get(x, y)) {
                            pixels[y * w + x] = 0xff000000;
                        } else {
                            pixels[y * w + x] = 0xffffffff;
                        }
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap getScaleLogo(Bitmap logo,int w,int h){
        if(logo == null)return null;
        Matrix matrix = new Matrix();
        float scaleFactor = Math.min(w * 1.0f / 5 / logo.getWidth(), h * 1.0f / 5 /logo.getHeight());
        matrix.postScale(scaleFactor,scaleFactor);
        Bitmap result = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(),   logo.getHeight(), matrix, true);
        return result;
    }
}
