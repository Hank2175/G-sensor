package com.hank.gsensor;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


public class MainActivity extends Activity implements SensorEventListener {
    Button button_Calibration;

    ImageView iv_canvas;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;

    private TextView text_x;
    private TextView text_y;
    private TextView text_z;
    private SensorManager aSensorManager;
    private Sensor aSensor;
    private float gravity[] = new float[3];
    private float cali_gravity[] = new float[3];

    private boolean drawable = false;
    private int _CX = 0, _CY = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        iv_canvas = (ImageView) findViewById(R.id.iv_canvas);

        text_x = (TextView)findViewById(R.id.TextView01);
        text_y = (TextView)findViewById(R.id.TextView02);
        text_z = (TextView)findViewById(R.id.TextView03);
        button_Calibration = findViewById(R.id.calibration);

        for(int i = 0; i < 3;i++)
            cali_gravity[i] = 0;

        aSensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        aSensor = aSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        aSensorManager.registerListener(this, aSensor, aSensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
// TODO Auto-generated method stub

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
// TODO Auto-generated method stub
        button_Calibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawable = true;
                paint = new Paint();
                paint.setStrokeWidth(5);
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.STROKE);
                bitmap=Bitmap.createBitmap(iv_canvas.getWidth(), iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
                canvas = new Canvas(bitmap);

                _CX = iv_canvas.getWidth()/2;
                _CY = iv_canvas.getHeight()*2/3;
                canvas.drawCircle(_CX, _CY, iv_canvas.getWidth()/3, paint);
                canvas.drawCircle(_CX, _CY, iv_canvas.getWidth()/6, paint);
                canvas.drawLine(_CX, iv_canvas.getHeight()/3, _CX , iv_canvas.getHeight(), paint);
                canvas.drawLine(iv_canvas.getWidth()/6 - 15, _CY, iv_canvas.getWidth()*5/6 + 15, _CY, paint);
                iv_canvas.setImageBitmap(bitmap);
                canvas = null;
                paint.setColor(Color.RED);

                Toast toast = Toast.makeText( MainActivity.this, "calibration", Toast.LENGTH_SHORT);
                toast.show();
//                cali_gravity[0] = event.values[0];
//                cali_gravity[1] = event.values[1];
//                cali_gravity[2] = event.values[2];
            }
        });
        if(drawable){
            final double alpha = 0.8;
            gravity[0] = (float)(alpha * gravity[0] + (1 - alpha) * event.values[0]);
            gravity[1] = (float)(alpha * gravity[1] + (1 - alpha) * event.values[1]);
            gravity[2] = (float)(alpha * gravity[2] + (1 - alpha) * event.values[2]);
            gravity[0] = event.values[0] - cali_gravity[0];
            gravity[1] = event.values[1] - cali_gravity[1];
            gravity[2] = event.values[2] - cali_gravity[2];
            text_x.setText("X = "+gravity[0]);
            text_y.setText("Y = "+gravity[1]);
            text_z.setText("Z = "+gravity[2]);

            bitmap = bitmap.copy(bitmap.getConfig(), true);
            canvas = new Canvas(bitmap);
            int drawX = (int)(_CX - (gravity[0]/9.80665) * iv_canvas.getWidth()/3);
            int drawY = (int)(_CY + (gravity[1]/9.80665) * iv_canvas.getWidth()/3);
            canvas.drawLine(_CX, _CY, drawX, drawY, paint);
            iv_canvas.setImageBitmap(bitmap);
            canvas = null;
        }
    }
    @Override
    protected void onPause()
    {
// TODO Auto-generated method stub
        /* 取消註冊SensorEventListener */
        aSensorManager.unregisterListener(this);
        Toast.makeText(this, "Unregister accelerometerListener", Toast.LENGTH_LONG).show();
        super.onPause();
    }
}