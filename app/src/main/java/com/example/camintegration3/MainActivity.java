package com.example.camintegration3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int req = 1;
    String Ppath;
    Button bt;
    ImageView iv;
    String ex ="";
    boolean isFragOpen;
    String nameSelected;
    //edit

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        nameSelected = null;
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        bt = (Button) findViewById(R.id.button);
        iv = (ImageView) findViewById(R.id.image);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //set ppath in button!!!

                try{   File f = createImage();
                    Ppath = f.getCanonicalPath();
                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, req);
                    int id = getResources().getIdentifier("sdcard/camera_app/cpic.jpg:drawable/", null, null);
                    iv.setImageResource(id);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    Log.e("IO","IO"+e);
                    ex="IOException";
                    return;
                }




            }
        });
    }





    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImage();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, req);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == req && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
           if (extras != null) {
               Bitmap imageBitmap = (Bitmap) extras.get("data");
               iv.setImageBitmap(imageBitmap);
           }
           else{
               setPic();
           }

        }
    }
    String exceptionName="";


//calls extras but no extras
    private File createImage() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        Ppath = image.getAbsolutePath();
        return image;
    }
    private void addpic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(Ppath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    private void setPic() {
        int tH = iv.getHeight();
        int tW = iv.getWidth();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(Ppath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW/tW, photoH/tH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(Ppath, bmOptions);
        iv.setImageBitmap(bitmap);
    }

    public void closeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        Frag firstFragment = (Frag) fragmentManager
                .findFragmentById(R.id.image);

        if (firstFragment != null) {
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(firstFragment).commit();
        }

        isFragOpen = false;
    }

    public void goClicked(View v)
    {
        if (!isFragOpen)
        {
            showFragment();
        }
        else
        {
            closeFragment();
        }
    }




    public void showFragment()
    {

        Frag firstFragment = Frag.newInstance(nameSelected);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.add(R.id.image, firstFragment).commit();

        isFragOpen = true;
    }

}
