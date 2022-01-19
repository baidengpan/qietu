package com.example.video.processor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.video.processor.library.ProcessCallback;
import com.video.processor.library.ProcessConfig;
import com.video.processor.library.ProcessTask;
import com.video.processor.library.ProcessorError;
import com.video.processor.library.ProviderUtils;

import java.util.concurrent.Executors;

/**
 * 此class的所有实现均只用作demo演示
 */
public class MainActivity extends AppCompatActivity implements ProcessCallback {
    private static final String TAG = "MainActivity";
    private static final String OUTPUT_DIR = "video";
    private static final String SDCARD_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_SELECT_VIDEO = 1000;
    private ProgressBar progressBar;
    private TextView inputTv, outputTv, progressTv, resultTv;
    private EditText widthEt, heightEt;
    private String videoPath;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.select_btn).setOnClickListener(v -> {
            chooseVideo();
        });
        findViewById(R.id.start).setOnClickListener(v -> {
            if (TextUtils.isEmpty(videoPath)) {
                Toast.makeText(MainActivity.this, "请先选择视频文件", Toast.LENGTH_LONG).show();
                return;
            }
            startProcess();
        });
        progressBar = findViewById(R.id.progress);
        inputTv = findViewById(R.id.input_tv);
        outputTv = findViewById(R.id.output_tv);
        progressTv = findViewById(R.id.progress_tv);
        resultTv = findViewById(R.id.cost_tv);
        widthEt = findViewById(R.id.video_width_et);
        heightEt = findViewById(R.id.video_height_et);
    }

    private void chooseVideo() {
        if (ContextCompat.checkSelfPermission(this, SDCARD_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{SDCARD_PERMISSION}, 1000);
            return;
        }
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_SELECT_VIDEO);
    }

    private void startProcess() {
        //config 需要根据外部传过来的Uri设置
        ProcessConfig config = new ProcessConfig();
        config.inputVideoPath = videoPath;
        config.outputPath = Environment.getExternalStoragePublicDirectory(OUTPUT_DIR).getAbsolutePath();
        config.taskId = videoPath.substring(videoPath.lastIndexOf('/') + 1, videoPath.lastIndexOf('.'));
        int w = getIntText(widthEt);
        int h = getIntText(heightEt);
        if (w == 0) {
            config.outputWidth = 720;
        } else {
            config.outputWidth = w;
        }
        if (h == 0) {
            config.outputHeight = 1280;
        } else {
            config.outputHeight = h;
        }
        config.idx = "";
        config.layoutType = 2;
        config.source = "";
        Log.d(TAG, "startProcess: " + config);
        ProcessTask task = new ProcessTask(this, config, this);
        Executors.newSingleThreadExecutor().execute(task);
        startTime = System.currentTimeMillis();
    }

    private int getIntText(EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            return 0;
        }
        int val;
        try {
            val = Integer.parseInt(editText.getText().toString());
        } catch (Throwable t) {
            val = 0;
        }
        return val;
    }

    private void reset() {
        inputTv.setText("");
        outputTv.setText("");
        resultTv.setText("");
        progressBar.setProgress(0);
        progressTv.setText("");
    }

    @Override
    public void progress(int frameFinished, float progress) {
        runOnUiThread(() -> {
            progressBar.setProgress((int) progress);
            String s = String.format("%.2f%%", progress);
            progressTv.setText(s);
        });
    }

    @Override
    public void finish(int code, String message) {
        runOnUiThread(() -> {
            boolean success = code == ProcessorError.Code.SUCCESS;
            if (success) {
                outputTv.setText(Environment.getExternalStoragePublicDirectory(OUTPUT_DIR).getAbsolutePath());
            }
            String result = String.format("result:%s, 耗时%d秒", success ? "success" : "failed," + message, (System.currentTimeMillis() - startTime) / 1000);
            resultTv.setText(result);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 选取图片的返回值
        if (requestCode == REQUEST_SELECT_VIDEO) {
            if (resultCode == RESULT_OK) {
                reset();
                Uri uri = data.getData();
                Log.d(TAG, "onActivityResult, uri:" + uri);
                videoPath = ProviderUtils.getFilePath(this, uri);
                Log.d(TAG, "onActivityResult: " + videoPath);
                inputTv.setText(videoPath);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}