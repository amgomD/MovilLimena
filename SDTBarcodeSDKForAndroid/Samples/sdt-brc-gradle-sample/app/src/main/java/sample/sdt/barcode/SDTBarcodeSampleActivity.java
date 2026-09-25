package sample.sdt.barcode;

import sdt.brc.android.BarcodeReader;
import sdt.brc.android.BarcodeReaderResult;
import sdt.brc.android.BarcodeReaderUtil;
import sdt.brc.android.BarcodeScanActivity;
import sdt.brc.android.BarcodeScanActivityScanParameters;
import sdt.brc.android.BarcodeScanDialog;
import sdt.brc.android.OnRecognitionListener;
import sdt.brc.android.OnBarcodeScanActivityRecognitionListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SDTBarcodeSampleActivity extends Activity {

    private static String LICENSE_KEY = "YOUR DEVELOPER LIENSE KEY";
    private static String UPGRADE_LICENSE_KEY = "YOUR UPGRADE LICENSE KEY IF DEVELOPER LIENSE KEY IS OLDER THAN 12 MONTHS";
    
    /** Barcode Read Dialog */
    private BarcodeScanDialog mCamDlg = null;

    /** UI */
    private Button mShowCamBut = null;
    /** UI */
    private Button mShowScanActivityBut = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mShowCamBut = (Button) findViewById(R.id.readBarcode);
        if (mShowCamBut != null) {
            mShowCamBut.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    showCameraDlg();
                }
            });
        }

        mShowScanActivityBut = (Button) findViewById(R.id.readBarcodeByScanActivity);
        if (mShowScanActivityBut != null) {
            mShowScanActivityBut.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    showScanActivity();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        hideCameraDialog();
        super.onStop();
    }

    protected void hideCameraDialog() {
        // It is important to hide Scan preview dialog in order to release
        // locked Camera resource.
        if (mCamDlg != null) {
            mCamDlg.hide();
            mCamDlg.dismiss();
            mCamDlg = null;
        }
    }

    protected void showScanActivity() {
        View l_oOverlayView = null;

        LayoutInflater l_oLoInflater = (LayoutInflater) SDTBarcodeSampleActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (l_oLoInflater != null) {
            l_oOverlayView = l_oLoInflater.inflate(R.layout.camera_overlay,
                    null);
        }

        OnBarcodeScanActivityRecognitionListener listener = new OnBarcodeScanActivityRecognitionListener() {

            @Override
            public boolean onActivityRecognitionResults(List results,
                    YuvImage srcImage) {
                // Populate results
                LinearLayout resultsLayout = (LinearLayout) findViewById(R.id.resultsValues);
                if (resultsLayout != null) {
                    resultsLayout.removeAllViews();
                    for (BarcodeReaderResult barcodeReaderResult : (List<BarcodeReaderResult>) results) {
                        String value = "[" + barcodeReaderResult.getTypeName()
                                + "]" + barcodeReaderResult.getValue() + "\n";
                        TextView tv = new TextView(getApplicationContext());
                        if (tv != null) {
                            tv.setText(value);
                            resultsLayout.addView(tv);
                        }
                    }

                    // Show the image
                    if (srcImage != null) {
                        ImageView iv = (ImageView) findViewById(R.id.resultsImage);
                        if (iv != null) {
                            Bitmap bm = BarcodeReaderUtil
                                    .decodeImageToBitmap(srcImage);
                            iv.setImageBitmap(bm);
                        }
                    }
                }
                // returning true will close scan activity
                return true;
            }
        };

        BarcodeScanActivityScanParameters params = new BarcodeScanActivityScanParameters();
        params.setLicenseKey(LICENSE_KEY);
        // remove next comment to pass upgrade license key to the engine
        // params.setLicenseUpgradeKey(UPGRADE_LICENSE_KEY);
        params.setEnableFlashLight(false);
        params.setOverlayView(l_oOverlayView);
        params.setShowScanArea(true);
        params.setBarcodeTypes(BarcodeReader.SDTBARCODE_ALL_1D | BarcodeReader.SDTBARCODE_ALL_2D);
        params.setRecognitionListener(listener);
        sdt.brc.android.BarcodeScanActivity.showBarcodeScanActivityForResult(this, 222, params);   
    }

    protected void showCameraDlg() {
        View overlayView = null;
        LayoutInflater layoutInflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            overlayView = layoutInflater.inflate(R.layout.camera_overlay, null);
        }
        mCamDlg = new BarcodeScanDialog(this, LICENSE_KEY, overlayView);
        if (mCamDlg != null) {
            mCamDlg.setBarcodeTypes(BarcodeReader.SDTBARCODE_ALL_1D);
            mCamDlg.showActiveArea(true);
            mCamDlg.setRecognitionListener(new OnRecognitionListener() {

                public void onRecognitionResults(List results, YuvImage srcImage) {
                    // Populate results
                    LinearLayout resultsLayout = (LinearLayout) findViewById(R.id.resultsValues);
                    if (resultsLayout != null) {
                        resultsLayout.removeAllViews();

                        for (BarcodeReaderResult barcodeReaderResult : (List<BarcodeReaderResult>) results) {
                            String value = "["
                                    + barcodeReaderResult.getTypeName() + "]"
                                    + barcodeReaderResult.getValue() + "\n";
                            TextView tv = new TextView(getApplicationContext());
                            if (tv != null) {
                                tv.setText(value);
                                resultsLayout.addView(tv);
                            }
                        }
                    }

                    // Hide scan dialog
                    mCamDlg.hide();

                    // Show the image
                    if (srcImage != null) {
                        ImageView iv = (ImageView) findViewById(R.id.resultsImage);
                        if (iv != null) {
                            Bitmap bm = BarcodeReaderUtil
                                    .decodeImageToBitmap(srcImage);
                            iv.setImageBitmap(bm);
                        }
                    }
                }
            });
            mCamDlg.show();
        }
    }
}