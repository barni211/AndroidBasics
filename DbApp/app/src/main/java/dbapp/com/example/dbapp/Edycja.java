package dbapp.com.example.dbapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Edycja extends AppCompatActivity {
    private DatabaseHelper myDb = new DatabaseHelper(this);
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView mImageView;
    private String mCurrentPhotoPath="";
    private Boolean editing = false;
    private String message;
    private String idPerson;
    static final int REQUEST_TAKE_PHOTO = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edycja);

        mImageView = (ImageView) findViewById(R.id.photoView);
        Intent intent = getIntent();
        message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        StringBuilder text = new StringBuilder(message);//.length());

        try
        {
            String photoPath = savedInstanceState.getString("MyPath");
            mCurrentPhotoPath=photoPath;
            Bitmap imageBitmap = BitmapFactory.decodeFile(photoPath);
            mImageView.setImageBitmap(imageBitmap);
        }
        catch(Exception ex)
        {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        EditText imieView = (EditText) findViewById(R.id.imieView);
        EditText nazwiskoView = (EditText) findViewById(R.id.nazwiskoView);
        EditText wiekView = (EditText) findViewById(R.id.wiekView);

        final String[] values = message.split("/");
        idPerson = values[1];

        if (values[0].equals("edycja")) {
            editing = true;
            Cursor res = myDb.getAllData();
            while (res.moveToNext()) {
                if (res.getString(0).equals(values[1])) {
                    imieView.setText(res.getString(1));
                    nazwiskoView.setText(res.getString(2));
                    wiekView.setText(res.getString(3));
                    mCurrentPhotoPath = res.getString(4);
                    Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                    mImageView.setImageBitmap(imageBitmap);
                }
            }
        }
    }

    public void zrobZdjecie(View v) {
        dispatchTakePictureIntent();
    }

    public void zapiszZdjecie(View v) {

        EditText imieView = (EditText) findViewById(R.id.imieView);
        EditText nazwiskoView = (EditText) findViewById(R.id.nazwiskoView);
        EditText wiekView = (EditText) findViewById(R.id.wiekView);



        if (editing == false && validate()) {
            Boolean isInserted = myDb.insertData(imieView.getText().toString(),
                    nazwiskoView.getText().toString(),
                    Integer.parseInt(wiekView.getText().toString()),
                    mCurrentPhotoPath);
            if (isInserted = true && validate()) {
                myDb.addToHistory("Add", imieView.getText().toString(), nazwiskoView.getText().toString(), wiekView.getText().toString());
                Toast.makeText(Edycja.this, "Wstawiono dane.", Toast.LENGTH_LONG).show();
                done();
            } else {
                Toast.makeText(Edycja.this, "Nie wstawiono danych", Toast.LENGTH_LONG).show();
            }
        } else if(editing==true && validate()){
            boolean isUpdate = myDb.updateData(idPerson,
                    imieView.getText().toString(),
                    nazwiskoView.getText().toString(),
                    Integer.parseInt(wiekView.getText().toString()),
                    mCurrentPhotoPath);
            if (isUpdate == true) {
                myDb.addToHistory("Edit", imieView.getText().toString(), nazwiskoView.getText().toString(), wiekView.getText().toString());
                Toast.makeText(Edycja.this, "Dane zaktualizowane", Toast.LENGTH_LONG).show();
                done();
            } else
                Toast.makeText(Edycja.this, "Nie zaktualizowano danych", Toast.LENGTH_LONG).show();
        }
    }

    public void done() {
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);//, bmOptions); //(Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    private File createImageFile() throws IOException {
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
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "dbapp.com.example.dbapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public boolean validate()
    {
        EditText imieView = (EditText) findViewById(R.id.imieView);
        EditText nazwiskoView = (EditText) findViewById(R.id.nazwiskoView);
        EditText wiekView = (EditText) findViewById(R.id.wiekView);
        if(imieView.getText().toString().equals(""))
        {
            Toast.makeText(Edycja.this, "Pole imie nie może być puste.", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(nazwiskoView.getText().toString().equals(""))
        {
            Toast.makeText(Edycja.this, "Pole nazwisko nie może być puste.", Toast.LENGTH_LONG).show();
            return false;
        }

        //String wiek;

        try{
            Integer wiekInt = Integer.parseInt(wiekView.getText().toString());
        }
        catch(Exception ex)
        {
            Toast.makeText(Edycja.this, "Wiek musi być liczbą.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {


        savedInstanceState.putString("MyPath", mCurrentPhotoPath);

        super.onSaveInstanceState(savedInstanceState);
    }

}
