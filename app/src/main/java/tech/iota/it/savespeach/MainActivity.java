package tech.iota.it.savespeach;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private String tag = Activity.class.getSimpleName();
    private EditText et;
    private TextToSpeech tts = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tts = new TextToSpeech(this, this);
        et = findViewById(R.id.editText);

        if (!checkPermission()){
            requestPermission();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }
    public void speakNow(View v) {
        Log.i(tag, "speakNow [" + et.getText().toString() + "]");
        HashMap<String, String> myHashRender = new HashMap();

        tts.speak(et.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
        String destFileName = Environment.getExternalStoragePublicDirectory("/Audio007/")+"wakeUp.wav";
        Toast.makeText(this, ""+destFileName, Toast.LENGTH_SHORT).show();
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, et.getText().toString());
        tts.synthesizeToFile(et.getText().toString(), myHashRender, destFileName);
    }

    public void onInit(int status) {
        Log.i(tag, "onInit [" + status + "]");
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED && result1==PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                }).check();

    }


    }



