package com.detail.gzjr.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.detail.gzjr.R;
import com.detail.gzjr.activity.utils.DataParse;
import com.detail.gzjr.activity.utils.NetworkUtils;
import com.detail.gzjr.activity.utils.Tools;
import com.detail.gzjr.activity.view.CommonProgressDialog;
import com.detail.gzjr.activity.view.MaterialDialog;
import com.detail.gzjr.activity.view.OnBtnClickL;
import com.yanzhenjie.alertdialog.AlertDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends Activity {

    private CommonProgressDialog pBar;
    private ProgressBar pbWebBase;
    private WebView mWebview;
    private long exitTime=0;
    private static final String DOWNLOAD_NAME = "DownFile";

    private static final int A=11;
    private static final int B=12;
    private static final int C=13;

    private TextView mTextNetState;

    @Bind(R.id.Image)
    ImageView mImageView;

    @Bind(R.id.progress_load)
    TextView mText;

    @Bind(R.id.bu)
    TextView mNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        if(NetworkUtils.isConnected(MainActivity.this)){
            String vision = Tools.getVersion(MainActivity.this);
            getVersion(vision);
            //handler.sendEmptyMessage(B);
            initData();
        }else {
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mNet.setVisibility(View.VISIBLE);
            mNet.setText("网络不给力");
        }
    }

    /*初始化控件*/
    private void initView(){
        mWebview = findViewById(R.id.webView1);
        pbWebBase = findViewById(R.id.progress);
    }

    /*初始化控件的参数*/
    private void initData(){
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setTextSize(WebSettings.TextSize.NORMAL);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSavePassword(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);

        /*设置背景透明*/
        mWebview.setBackgroundColor(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//软件解码
        }
        mWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);    //硬件解码
        handler.sendEmptyMessage(A);
    }


    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what){
                case A:{

                    mWebview.loadUrl("http://gdjy.fuziche.com/");
                    // 设置setWebChromeClient对象
                    mWebview.setWebChromeClient(new WebChromeClient() {
                        @Override
                        public void onReceivedTitle(WebView view, String title) {
                            super.onReceivedTitle(view, title);
                        }

                        @Override
                        public void onProgressChanged(WebView view, int newProgress) {
                            // TODO Auto-generated method stub

                            pbWebBase.setProgress(newProgress);
                            super.onProgressChanged(view, newProgress);
                        }
                    });
                    //设置此方法可在WebView中打开链接，反之用浏览器打开
                    mWebview.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            if (!mWebview.getSettings().getLoadsImagesAutomatically()) {
                                mWebview.getSettings().setLoadsImagesAutomatically(true);
                            }
                            pbWebBase.setVisibility(View.GONE);
                            mText.setVisibility(View.GONE);
                        }

                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            // TODO Auto-generated method stub
                            pbWebBase.setVisibility(View.VISIBLE);
                            super.onPageStarted(view, url, favicon);
                        }

                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            if (url.startsWith("http:") || url.startsWith("https:")) {
                                view.loadUrl(url);
                                return false;
                            }
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    });
                    break;
                }
                case B:{

                    break;
                }
                case C:{
                    break;
                }
            }
        }
    };

    private void getVersion(final String vision) {
        //String vision = Tools.getVersion(MainActivity.this);
        String NewVersion = DataParse.version;
        Log.e("MainActivity",NewVersion);
        String content = "\n" +
                "温馨提示：\n" +"\n" +
                "1,优化了访问速度\n" +
                "2,调试与修改了多个bug\n";
        String url = DataParse.updateUrl;
        if(NewVersion == null || url == null){
            Toast.makeText(this,"请连接网络！", Toast.LENGTH_LONG).show();
        }else if (!NewVersion.equals(vision)) {
            ShowDialog(vision, NewVersion, content, url);
        }
    }

    /**
     * 升级系统
     *
     * @param content
     * @param url
     */
    private void ShowDialog(String vision, String newversion, String content,final String url) {

        final MaterialDialog dialog = new MaterialDialog(this);//自定义的对话框，可以呀alertdialog
        if(DataParse.Isforced.equals("0")) {
            dialog.content(content).btnText("取消", "更新").title("版本更新 ").titleTextSize(15f).show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnBtnClickL(new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    dialog.dismiss();
                }
            }, new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    dialog.dismiss();
                    pBar = new CommonProgressDialog(MainActivity.this);
                    pBar.setCanceledOnTouchOutside(false);
                    pBar.setTitle("正在下载");
                    pBar.setCustomTitle(LayoutInflater.from(MainActivity.this).inflate(R.layout.title_dialog, null));
                    pBar.setMessage("正在下载");
                    pBar.setIndeterminate(true);
                    pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pBar.setCancelable(true);
                    // downFile(URLData.DOWNLOAD_URL);
                    final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
                    downloadTask.execute(url);
                    pBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            downloadTask.cancel(true);
                        }
                    });
                }
            });
        }else {
            new android.app.AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("版本更新")
                    .setMessage(content)
                    .setPositiveButton("立即体验", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            pBar = new CommonProgressDialog(MainActivity.this);
                            pBar.setCanceledOnTouchOutside(false);
                            pBar.setTitle("正在下载");
                            pBar.setCustomTitle(LayoutInflater.from( MainActivity.this).inflate( R.layout.title_dialog, null));
                            pBar.setMessage("正在下载");
                            pBar.setIndeterminate(true);
                            pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            pBar.setCancelable(true);
                            // downFile(URLData.DOWNLOAD_URL);
                            final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
                            downloadTask.execute(url);
                            pBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    downloadTask.cancel(true);
                                }
                            });
                        }
                    }).show();
        }

          /*
        new android.app.AlertDialog.Builder(this)
                .setTitle("版本更新")
                .setMessage(content)
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        pBar = new CommonProgressDialog(MainActivity.this);
                        pBar.setCanceledOnTouchOutside(false);
                        pBar.setTitle("正在下载");
                        pBar.setCustomTitle(LayoutInflater.from( MainActivity.this).inflate( R.layout.title_dialog, null));
                        pBar.setMessage("正在下载");
                        pBar.setIndeterminate(true);
                        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        pBar.setCancelable(true);
                        // downFile(URLData.DOWNLOAD_URL);
                        final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
                        downloadTask.execute(url);
                        pBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                downloadTask.cancel(true);
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();  */

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if(keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()){
            mWebview.goBack();
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_BACK){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
               finish();
            }
            /*此处必须要返回true，不然连续点击返回键无法退出*/
            return true;
        }
        /*不能返回true,否则会屏蔽掉音量键*/
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume(){
        super.onResume();
        mWebview.resumeTimers();
    }

    @Override
    public void onPause(){
        super.onPause();
        mWebview.pauseTimers();
    }

    //销毁Webview
    @Override
    protected void onDestroy(){
        if (mWebview != null) {
            mWebview.clearHistory();
            ((ViewGroup) mWebview.getParent()).removeView(mWebview);
            mWebview.destroy();
            mWebview = null;
        }
        super.onDestroy();
    }


    /**
     * 下载应用
     * @author Administrator
     */
    class DownloadTask extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            File file = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP "+ connection.getResponseCode() + " "+ connection.getResponseMessage();
                }
                int fileLength = connection.getContentLength();
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    file = new File(Environment.getExternalStorageDirectory(),DOWNLOAD_NAME);
                    if (!file.exists()) {
                        // 判断父文件夹是否存在
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "sd卡未挂载",Toast.LENGTH_LONG).show();
                }
                input = connection.getInputStream();
                output = new FileOutputStream(file);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                System.out.println(e.toString());
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,getClass().getName());
            mWakeLock.acquire();
            pBar.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            pBar.setIndeterminate(false);
            pBar.setMax(100);
            pBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            pBar.dismiss();
            if (result != null) {
                /*申请多个权限。*/
                AndPermission.with(MainActivity.this)
                        .requestCode(REQUEST_CODE_PERMISSION_SD)
                        .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
                        .rationale(rationaleListener)
                        .send();
                Toast.makeText(context, "请允许打开SD卡权限", Toast.LENGTH_LONG).show();
            } else {
                update(MainActivity.this);
            }
        }
    }

    private static final int REQUEST_CODE_PERMISSION_SD = 101;
    private static final int REQUEST_CODE_SETTING = 300;


    private RationaleListener rationaleListener = new RationaleListener() {
        @Override
        public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
            // 这里使用自定义对话框，如果不想自定义，用AndPermission默认对话框：
            //AndPermission.rationaleDialog(Context, Rationale).show();
            // 自定义对话框。
            AlertDialog.build(MainActivity.this)
                    .setTitle(R.string.title_dialog)
                    .setMessage(R.string.message_permission_rationale)
                    .setPositiveButton(R.string.btn_dialog_yes_permission, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            rationale.resume();
                        }
                    })

                    .setNegativeButton(R.string.btn_dialog_no_permission, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            rationale.cancel();
                        }
                    }).show();

        }
    };

    /*SD权限,7.0用户点击允许，直接下载新Apk*/
    @PermissionYes(REQUEST_CODE_PERMISSION_SD)
    private void getMultiYes(List<String> grantedPermissions) {
        Toast.makeText(this, R.string.message_post_succeed, Toast.LENGTH_SHORT).show();
        //getVersion(Tools.getVersion(MainActivity.this));
        pBar = new CommonProgressDialog(MainActivity.this);
        pBar.setCanceledOnTouchOutside(false);
        pBar.setTitle("正在下载");
        pBar.setCustomTitle(LayoutInflater.from(MainActivity.this).inflate(R.layout.title_dialog, null));
        pBar.setMessage("正在下载");
        pBar.setIndeterminate(true);
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setCancelable(true);
        // downFile(URLData.DOWNLOAD_URL);
        final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
        downloadTask.execute(DataParse.updateUrl);
    }

    @PermissionNo(REQUEST_CODE_PERMISSION_SD)
    private void getMultiNo(List<String> deniedPermissions) {
        Toast.makeText(this, R.string.message_post_failed, Toast.LENGTH_SHORT).show();

        /*用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。*/
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING)
                    .setTitle(R.string.title_dialog)
                    .setMessage(R.string.message_permission_failed)
                    .setPositiveButton(R.string.btn_dialog_yes_permission)
                    .setNegativeButton(R.string.btn_dialog_no_permission, null)
                    .show();
        }
    }

    /*权限回调处理*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /**
         * 转给AndPermission分析结果。
         *
         * @param object     要接受结果的Activity、Fragment。
         * @param requestCode  请求码。
         * @param permissions  权限数组，一个或者多个。
         * @param grantResults 请求结果。
         */
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SETTING: {
                Toast.makeText(this, R.string.message_setting_back, Toast.LENGTH_LONG).show();
                //设置成功，再次请求更新
                getVersion(Tools.getVersion(MainActivity.this));
                break;
            }
        }
    }

    private void update(Context mContext) {
        //安装应用
        File apkfile = new File(Environment.getExternalStorageDirectory(), DOWNLOAD_NAME);
        if (!apkfile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = mContext.getPackageName() + ".provider";
            Uri contentUri = FileProvider.getUriForFile(mContext, authority, apkfile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            startActivity(intent);
        }else{
        intent.setDataAndType(Uri.fromFile(apkfile),"application/vnd.android.package-archive");
        startActivity(intent);
        }
    }
}
