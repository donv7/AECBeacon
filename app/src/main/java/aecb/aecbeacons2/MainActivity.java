package aecb.aecbeacons2;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;

import butterknife.Bind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

@TargetApi(21)
public class MainActivity extends Activity {

    // region bluetooth vars
    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1;
    private int REQUEST_CAMERA = 2;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    private HashMap<String, Integer> mBeacons;


    // endregion

    Uri imageUri;

    String beaconName;

    AecbRetrofit.AecbApiService AecbApi;

    //List<AecbImage> currentBeaconList;
    ImageAdapter mImageAdapter;

    @Bind(R.id.ivPic) ImageView ivPic;
    @Bind(R.id.btnOne) Button btnOne;
    @Bind(R.id.gvGrid) GridView gvGrid;

    // region create, resume, pause, destroy
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "2MQu7HMiThB2CaN0pEZuKCV2O794eV4zMJw0RR5y", "thl1PCaGgOqIjHM74TZhg72FXOijSmg7SzZbY6ck");

        ButterKnife.bind(this);

        mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mImageAdapter = new ImageAdapter(this);
        gvGrid.setAdapter(mImageAdapter);

        gvGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        beaconName = "this is and invaaaaaalid beacon..a;jkfsd;lkfjasd;lfj";

        AecbApi = new AecbRetrofit().getService(getApplication());

    }

    @Override
    protected void onResume() {
        super.onResume();
        mBeacons = new HashMap<String, Integer>();
        mBeacons.put("D5:4E:CC:37:29:F5", -1000);
        mBeacons.put("F2:C1:3A:BB:AD:D9", -1000);
        mBeacons.put("E0:60:E2:5A:9E:29", -1000);
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                filters = new ArrayList<ScanFilter>();
            }
            scanLeDevice(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
    }

    @Override
    protected void onDestroy() {
        if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;
        super.onDestroy();
    }
    // endregion

    // region ui
    @OnClick(R.id.btnOne)
    public void onClick_submit(View v) {
        //take a picture with the camera
        Intent getCameraImage = new Intent("android.media.action.IMAGE_CAPTURE");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        String timeStamp = dateFormat.format(new Date());
        String imageFileName = "aecb_" + timeStamp + ".jpg";

        // get the path to save the file
        File path = MainActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File photo = new File(path, imageFileName);
        getCameraImage.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);

        startActivityForResult(getCameraImage, REQUEST_CAMERA);
    }

    protected void imageOnActivityResult(Intent data) {
        ivPic.setImageURI(imageUri);

        File f = new File(imageUri.getPath());

        TypedFile tf = new TypedFile("image/jpeg", f);

        //upload image to parse
        AecbApi.postImage(tf, beaconName, new Callback<Map<String, String>>() {
            @Override
            public void success(Map<String, String> tokenResponse, retrofit.client.Response response) {
                Toast.makeText(MainActivity.this, "Successfully uploaded!",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MainActivity.this, "Shit.",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    // endregion

    // region bluetooth stuff
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
                finish();
                return;
            }
        }
        else if(requestCode == REQUEST_CAMERA && resultCode == RESULT_OK){
            imageOnActivityResult(data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mLEScanner.startScan(filters, settings, mScanCallback);
            }
        } else {
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                mLEScanner.stopScan(mScanCallback);
            }
        }
    }

    public void filterBeaconList(List<AecbImage> beaconList, String beaconName){

        for (int i=0; i<beaconList.size(); i++) {
            if(!beaconList.get(i).getBeacon().equals(beaconName)){
                beaconList.remove(i);
            }
        }
    }


    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String newBeaconName = beaconInProximity(result);

            if(newBeaconName == null){
                //make everything blank or something like that
            }
            else if(beaconName != newBeaconName){
                beaconName = newBeaconName;

                // get the images for this new beacon from the server
                AecbApi.getImages(new Callback<List<AecbImage>>() {
                    @Override
                    public void success(List<AecbImage> beaconList, retrofit.client.Response response) {
                        Toast.makeText(MainActivity.this, "Successfully gotten?...!",
                                Toast.LENGTH_SHORT).show();

                        filterBeaconList(beaconList, beaconName);

                        //currentBeaconList = beaconList;
                        mImageAdapter.setBeaconList(beaconList);
                        gvGrid.invalidateViews();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(MainActivity.this, "Fuck.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                // draw the images for the new beacon!
            }
            // otherwise, same beacon... do nothing

            btnOne.setText(beaconName);
            Log.i("result", mBeacons.toString());
//            BluetoothDevice btDevice = result.getDevice();
//            connectToDevice(btDevice);
        }

        private String beaconInProximity(ScanResult result) {
            if (mBeacons.keySet().contains(result.getDevice().getAddress())) {
                mBeacons.put(result.getDevice().getAddress(), result.getRssi());
            }

            String closest = null;
            for (String bId : mBeacons.keySet()) {
                if (mBeacons.get(bId) > -85) {
                    if (closest == null || mBeacons.get(bId) > mBeacons.get(closest)) {
                        closest = bId;
                    }
                }
            }
            return closest;
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("onLeScan", device.toString());
                            Log.i("rssi", ""+rssi);
                            connectToDevice(device);
                        }
                    });
                }
            };

    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
            scanLeDevice(false);// will stop after first device detection
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());
            gatt.readCharacteristic(services.get(1).getCharacteristics().get
                    (0));
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());
            gatt.disconnect();
        }
    };

    // endregion
}
