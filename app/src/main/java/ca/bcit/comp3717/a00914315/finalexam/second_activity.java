package ca.bcit.comp3717.a00914315.finalexam;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class second_activity extends AppCompatActivity {
    private Bitmap imageToSave;
    private ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        final Intent intent;
        final String value;


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        value = intent.getStringExtra("value");
        setTitle(value);

        doSomething(value);

        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);


    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
            imageToSave = resizeImage(progress);
            image.setImageBitmap(imageToSave);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }
    });

}

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    public void doSomething(String value)
    {
        image = (ImageView) findViewById(R.id.imageView);
        if(value.equals("england")){
            image.setImageResource(R.drawable.england);
        }else if(value.equals("germany")){
            image.setImageResource(R.drawable.germany);
        } else {
            image.setImageResource(R.drawable.france);
        }
        imageToSave = ((BitmapDrawable)image.getDrawable()).getBitmap();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    public void onClickSaveImage(final View view){
        String title = String.valueOf(System.currentTimeMillis());
        String yourDescription = "Final";
        insertImage(getContentResolver(), imageToSave, title , yourDescription);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageToSave.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        new SaveImageTask().execute(byteArray);

    }

    public Bitmap resizeImage(int Size) {
        int width = imageToSave.getWidth();
        int height = imageToSave.getHeight();
        float scale = (float)Size/50;
        float newWidth = width * scale;
        float newHeight = height * scale;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                imageToSave, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }

    //https://stackoverflow.com/questions/8560501/android-save-image-into-gallery?answertab=votes#tab-top
    public static final String insertImage(ContentResolver cr,
                                           Bitmap source,
                                           String title,
                                           String description) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                } finally {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                //storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }

    private class SaveImageTask
            extends AsyncTask<byte[], Void, Void>
    {
        @Override
        protected Void doInBackground(final byte[]... data)
        {
            FileOutputStream outStream;
            final File       sdCard;
            final File       dir;
            final String     fileName;
            final File       outFile;

            sdCard = Environment.getExternalStorageDirectory();
            dir    = new File (sdCard.getAbsolutePath() + "/finalexam");
            dir.mkdirs();

            fileName  = String.format("%d.jpg", System.currentTimeMillis());
            outFile   = new File(dir, fileName);
            outStream = null;

            try
            {
                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();

            }
            catch(final FileNotFoundException ex)
            {
                //Log.e(TAG, "File not found", ex);
            }
            catch(final IOException ex)
            {
                //Log.e(TAG, "IOException", ex);
            }
            finally
            {
                try
                {
                    if(outStream != null)
                    {
                        outStream.close();
                    }
                }
                catch(final IOException ex)
                {

                }
            }

            return null;
        }

    }
}
