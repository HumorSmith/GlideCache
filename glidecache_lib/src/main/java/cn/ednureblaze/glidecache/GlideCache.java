package cn.ednureblaze.glidecache;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

public class GlideCache {
    private static Bitmap glideBitmap;

    public static boolean isHaveGlideCache(Context context, String url){
        FutureTarget<Bitmap> bitmap = Glide.with(context)
                .asBitmap()
                .load(url)
                .submit();
        try{
            glideBitmap = bitmap.get();
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public static Bitmap getGlideBitmap(Context context, String url) {
        FutureTarget<Bitmap> bitmap = Glide.with(context)
                .asBitmap()
                .load(url)
                .submit();
        try{
            glideBitmap = bitmap.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return glideBitmap;
    }

    public static void setNormalImageViaGlideCache(final Activity activity, final ImageView image, final String imageUrl) {
        new Thread(() -> {
            try {
                final Bitmap glideBitmap=GlideCache.getGlideBitmap(activity, imageUrl);

                activity.runOnUiThread(() -> image.setImageBitmap(glideBitmap));
            } catch (Exception ignored) {}
        }).start();
    }

    public static void setBlurImageViaGlideCache(final Activity activity, final ImageView blurImage, final String imageUrl, final String pattern) {

        new Thread(() -> {
            try{
                Bitmap glideBitmap=GlideCache.getGlideBitmap(activity, imageUrl);
                int scaleRatio = 0;
                if (TextUtils.isEmpty(pattern)) {
                    scaleRatio = 0;
                } else if (scaleRatio < 0) {
                    scaleRatio = 10;
                } else {
                    scaleRatio = Integer.parseInt(pattern);
                }
                //下面的这个方法必须在子线程中执行
                final Bitmap blurBitmap2 = FastBlur.toBlur(glideBitmap, scaleRatio);

                //刷新ui必须在主线程中执行
                activity.runOnUiThread(() -> {
                    blurImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    blurImage.setImageBitmap(blurBitmap2);
                });
            } catch (Exception ignored) {}
        }).start();
    }
}