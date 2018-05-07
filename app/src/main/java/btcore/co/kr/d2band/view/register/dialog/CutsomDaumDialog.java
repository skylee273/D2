package btcore.co.kr.d2band.view.register.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TableRow;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.DialogAddressBinding;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by leehaneul on 2018-02-09.
 */

public class CutsomDaumDialog extends Dialog {
    DialogAddressBinding mBinding;
    private Handler handler;
    private String address;
    public CutsomDaumDialog(Context context) { super(context); }


    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_address, null, false);
        setContentView(mBinding.getRoot());

        handler = new Handler();
        initView();

    }



    private void initView(){
        mBinding.webView.getSettings().setJavaScriptEnabled(true);
        mBinding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mBinding.webView.addJavascriptInterface(new AndroidBridge(), "CutsomDaumDialog");
        mBinding.webView.setWebChromeClient(new WebChromeClient());
        mBinding.webView.loadUrl("http://103.60.125.37/getAddress.php");

    }

    private class AndroidBridge{
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    address = String.format("(%s) %s %s",arg1,arg2,arg3);
                    initView();
                    dismiss();
                }
            });
        }
    }

    public String getAddressStr() {
        return address;
    }
}
