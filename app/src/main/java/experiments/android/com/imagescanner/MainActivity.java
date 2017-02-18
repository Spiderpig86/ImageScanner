package experiments.android.com.imagescanner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button test;
    private ImageView mImageView;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private static String root = null; // Stores the temp file path of our image
    private String imageName = null;
    private static String imageFolderPath = null;
    private Uri fileUri = null;

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
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            root = Environment.getExternalStorageDirectory().toString() +
                    "/Image_Capture";

            // Creating folders for image
            imageFolderPath = root + "/temp";
            File imagesFolder = new File(imageFolderPath);
            imagesFolder.mkdirs();

            // Create our file for the image
            imageName = "temp.png";

            File image = new File(imageFolderPath, imageName);
            fileUri = Uri.fromFile(image;
            mImageView.setTag(imageFolderPath + File.separator + imageName);

            // Start Camera intent
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

        } else {
            Toast.makeText(this, "Something went wrong...", Toast
                    .LENGTH_SHORT).show();
        }

    }

}
