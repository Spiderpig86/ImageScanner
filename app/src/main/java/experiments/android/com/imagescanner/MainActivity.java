package experiments.android.com.imagescanner;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button test;
    private ImageView mImageView;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private static String root = null; // Stores the temp file path of our image
    private String imageName = null;
    private static String imageFolderPath = null;
    private Uri fileUri = null;

    public static final String TAG = "main";
    final int REQUEST_WRITE_EXTERNAL_STORAGE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkCameraHardware(this)) {
            Toast.makeText(this, "Camera not supported on this device.",
                    Toast.LENGTH_LONG);
        }

        // Assign controls
        test = (Button) findViewById(R.id.btn_test);
        mImageView = (ImageView) findViewById(R.id.mImageView);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture();
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = (String) v.getTag();

                if (path != null) {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri imgUri = Uri.parse("file://" + path);
                    intent.setDataAndType(imgUri, "image/*");
                    startActivity(intent);

                }
            }
        });
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void capture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            root = Environment.getExternalStorageDirectory().toString() +
                    "/Image_Capture";

            // Creating folders for image
            imageFolderPath = root + "/temp";
            //File imagesFolder = new File(imageFolderPath);
            File imagesFolder = new File(Environment
                    .getExternalStorageDirectory().getAbsolutePath() + "/temp");
            if (!imagesFolder.mkdirs()) {
                Toast.makeText(this, "Unable to make dir", Toast
                        .LENGTH_SHORT).show();
            }

            // Create our file for the image
            imageName = "temp.png";

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    requestPermission(this);

                    //Toast.makeText(this, "Unable to make dir2", Toast
                    //.LENGTH_SHORT).show();

                } else {
                    File image = new File(imageFolderPath, imageName);
                    fileUri = Uri.fromFile(image);
                    Toast.makeText(this, fileUri.toString(), Toast.LENGTH_SHORT).show();
                    mImageView.setTag(imageFolderPath + File.separator + imageName);

                    // Start Camera intent
                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

            }

        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // If the request is good
            Bitmap bitmap = null;

            try {
                GetThumb getThumb = new GetThumb();
                bitmap = getThumb.getThumbnail(fileUri, this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Show the image on the image view.
            mImageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "Something went wrong...", Toast
                    .LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    private void requestPermission(final Context context){
        if(ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.

            new AlertDialog.Builder(context)
                    .setMessage("Need storage permission")
                    .setPositiveButton("yes", new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    }).show();

        }else {
            // permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    // Needed since Android introduced runtime permissions or something
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            getResources().getString(R.string.permission_storage_success),
                            Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this,
                            getResources().getString(R.string
                                    .permission_storage_error),
                            Toast.LENGTH_SHORT).show();
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                return;
            }
        }
    }

}
