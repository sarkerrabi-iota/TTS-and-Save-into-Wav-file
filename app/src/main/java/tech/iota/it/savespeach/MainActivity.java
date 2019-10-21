package tech.iota.it.savespeach;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.audiofx.PresetReverb;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener {

    private String tag = Activity.class.getSimpleName();
    private EditText et;
    private TextToSpeech tts = null;
    private Button btPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tts = new TextToSpeech(this, this);
        et = findViewById(R.id.editText);
        btPlay = findViewById(R.id.bt_play);
        btPlay.setOnClickListener(this);

        if (!checkPermission()) {
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
        String destFileName = Environment.getExternalStoragePublicDirectory("/Audio007/") + "wakeUp.wav";
        Toast.makeText(this, "" + destFileName, Toast.LENGTH_SHORT).show();
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, et.getText().toString());
        tts.synthesizeToFile(et.getText().toString(), myHashRender, destFileName);
    }


    SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    boolean isSoundLoaded = false;
    int soundID;
    float frequencyPitch = 1.3f; // tweak this. it accepts any number between 0.5f and 2.0f
    public void modifyNow( View view){


        soundID = soundPool.load( Environment.getExternalStoragePublicDirectory("/Audio007/") + "wakeUp.wav", 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                isSoundLoaded = true;
                if(isSoundLoaded)
                {
                    soundPool.play(soundID, 1f, 1f, 1, 0, frequencyPitch);
                }
            }
        });
    }

    public void onInit(int status) {
        Log.i(tag, "onInit [" + status + "]");
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS);
        if (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MODIFY_AUDIO_SETTINGS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                }).check();

    }


    @Override
    public void onClick(View v) {
//        modifyNow(v);
        playPreverbNow();
    }

    public void playPreverbNow(){
        MediaPlayer mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(this, Uri.parse(Environment.getExternalStoragePublicDirectory("/Audio007/") + "wakeUp.wav"));

            PresetReverb mReverb = new PresetReverb(0,mMediaPlayer.getAudioSessionId());//<<<<<<<<<<<<<
            mReverb.setPreset(PresetReverb.PRESET_LARGEHALL);
            mReverb.setEnabled(true);
            mMediaPlayer.setAuxEffectSendLevel(1.0f);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



