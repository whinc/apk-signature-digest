package com.whinc.apksignaturedigest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static com.whinc.util.SystemServiceUtils.copyToClipboard;

/**
 * Created by wuhui on 8/31/15.
 */
public class ApkSignatureActivity extends AppCompatActivity {
    public static final String TAG = ApkSignatureActivity.class.getSimpleName();

    @Bind(R.id.pkg_name_editText)
    AutoCompleteTextView mPkgNameEdt;
    @Bind(R.id.signature_textView)
    TextView mSignDigestTxt;
    @Bind(R.id.version_info_layout)
    GridLayout mVersionInfoLayout;

    private boolean mUpperCase = true;
    private List<PackageInfo> mPkgInfoList;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ApkSignatureActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_signature);
        ButterKnife.bind(this);
        initAppBar();
        initView();
    }

    private void initAppBar() {
    }

    private void initView() {
        mVersionInfoLayout.setVisibility(View.INVISIBLE);

        mPkgInfoList = PackageUtils.getInstance().getInstalledPackages(this);
        List<String> pkgNameList = new ArrayList<>(mPkgInfoList.size());
        for (PackageInfo v : mPkgInfoList) {
            pkgNameList.add(v.packageName);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pkgNameList);
        mPkgNameEdt.setAdapter(adapter);
        mPkgNameEdt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updatePkgInfo(mPkgInfoList.get(position));
            }
        });
        mPkgNameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                mSignDigestTxt.setText(R.string.sign_digest_hint);
                mVersionInfoLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    @OnClick({R.id.retrieve_signature_btn})
    protected void onBtnClick() {
        String pkgName = mPkgNameEdt.getText().toString();
        if (TextUtils.isEmpty(pkgName)) {
            Toast.makeText(this, "Package name cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        PackageInfo pkgInfo = null;
        for (PackageInfo v : mPkgInfoList) {
            if (v.packageName.equals(pkgName)) {
                pkgInfo = v;
                break;
            }
        }
        if (pkgInfo != null) {
            updatePkgInfo(pkgInfo);
        } else {
            Toast.makeText(this, "Invalid package name!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePkgInfo(PackageInfo pkgInfo) {
        String digest = PackageUtils.getInstance().getSignatureDigest(pkgInfo);
        digest = mUpperCase ? digest.toUpperCase() : digest.toLowerCase();
        mSignDigestTxt.setText(digest);
        mPkgNameEdt.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mPkgNameEdt.getWindowToken(), 0);

        TextView versionCodeTxt = (TextView) mVersionInfoLayout.getChildAt(1);
        TextView versionNameTxt = (TextView) mVersionInfoLayout.getChildAt(3);
        versionCodeTxt.setText(String.valueOf(pkgInfo.versionCode));
        versionNameTxt.setText(pkgInfo.versionName);
        mVersionInfoLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.signature_textView)
    void convertDigestCase(TextView textView) {
        mUpperCase = !mUpperCase;
        String digest = textView.getText().toString();
        digest = mUpperCase ? digest.toUpperCase() : digest.toLowerCase();
        textView.setText(digest);
    }

    @OnLongClick(R.id.signature_textView)
    boolean copyDigest(TextView textView) {
        copyToClipboard(this, textView.getText());

        String tip = textView.getText() + " has been copied to clipboard!";
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
        return true;
    }
}
