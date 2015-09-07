package com.whinc.apksignaturedigest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by wuhui on 8/31/15.
 */
public class ApkSignatureActivity extends AppCompatActivity {
    public static final String TAG = ApkSignatureActivity.class.getSimpleName();

    @Bind(R.id.pkg_name_editText)
    AutoCompleteTextView mPkgNameEditText;
    @Bind(R.id.signature_textView)
    TextView mSignatureTextView;

    private boolean mUpperCase = true;

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

        List<String> pkgNames = PackageUtils.newInstance(this).installedPkgNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pkgNames);
        mPkgNameEditText.setAdapter(adapter);
    }

    private void initAppBar() {
    }

    @OnClick({R.id.retrieve_signature_btn})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.retrieve_signature_btn:
                retrieveApkSignature();
                break;
        }
    }

    private void retrieveApkSignature() {
        String pkgName = mPkgNameEditText.getText().toString();
        if (TextUtils.isEmpty(pkgName)) {
            Toast.makeText(this, "Package name cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        PackageUtils pkgUtils = PackageUtils.newInstance(this, pkgName);
        if (pkgUtils != null) {
            String[] digests = pkgUtils.signatureDigest();
            if (digests.length > 0) {
                String digest = digests[0];
                digest = mUpperCase ? digest.toUpperCase() : digest.toLowerCase();
                mSignatureTextView.setText(digest);
            }
        } else {
            Toast.makeText(this, "Package name not found!", Toast.LENGTH_SHORT).show();
        }
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
        SystemServiceUtils.copyToClipboard(this, textView.getText());

        String tip = textView.getText() + " has been copied to clipboard!";
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
        return true;
    }
}
