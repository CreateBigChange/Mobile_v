package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity  implements SensorEventListener,View.OnClickListener {

    // 存放布局文件text
    public TextView Light;

    // 存放传感器管理
    private SensorManager sm;

    //用于计算采样使用的时间
    public float last_time = 0;

    //用于计算采样次数
    public int count_s = 0;

    // 用于存储light value
    public StringBuffer LightValue = new StringBuffer("");

    // 用于保存按钮
    private Button btnsave;
    private Button btnstop;

    // 定义全局变量 写文件和文件夹
    Writer writer = null;
    File folder = null;
    File LightSensorFile = null;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获得布局的Text
        Light = findViewById(R.id.Light);

        //获得当前应用的文件夹
        String dir3 =this.getExternalFilesDir(
                Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath();

        // 在当前应用的文件夹中创建Light Sensor 的文件用于保存数据
        String outputDir = dir3 + File.separator+"Light_sensors";

        folder = new File(outputDir);


        // 创建该文件夹
        if (!folder.exists()) {
            //(new File(outputDir)).mkdirs();
            if (!folder.mkdirs()) {
                Log.i("Error_yxs", "FileDir creat error");
            }
        }


        //获得传感器管理器
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        //获得光传感器
        Sensor mSensorLight = sm.getDefaultSensor(Sensor.TYPE_LIGHT);

        //注册传感器事件
        sm.registerListener((SensorEventListener) this, mSensorLight,
                SensorManager.SENSOR_DELAY_FASTEST);

        //获取按钮
        btnsave = (Button) findViewById(R.id.btnsave);
        btnstop = (Button) findViewById(R.id.btnstop);
        //给点击事件赋予函数

        btnsave.setOnClickListener(this);

        btnstop.setOnClickListener(this);

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        //实现传感器改变事件
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {

            Float light_strength = event.values[0]; //获得传感器的值

            String name = event.sensor.getName(); //获得传感器名称
            float type = event.sensor.getType(); //获得传感器种类
            String ventor = event.sensor.getVendor();  //获得传感器供应商
            int version = event.sensor.getVersion();  //获得传感器版本
            float range = event.sensor.getMaximumRange(); //获得最大范围
            float pow = event.sensor.getPower();  //传感器使用时的耗电量
            float resolution = event.sensor.getResolution(); //获得传感器版本

            //计算采样时间间隔
            float current_time = event.timestamp;
            float time_interval = current_time - last_time;

            //计算采样次数
            count_s = count_s + 1;
            LightValue.append(current_time);
            LightValue.append(",");

            LightValue.append(light_strength);

            LightValue.append("\n");

            //打印信息到相关viewer中
            Light.setText("\n当前光线强度为" + light_strength+"；传感器名字"+name+";测量范围"+range+";功率"+pow+";分辨率"+resolution+";型号"+type+";厂商"+ventor+";版本"+version+";Time step:"+time_interval+"计数器："+count_s);

            //Light.setText(LightValue);
            last_time = current_time;

            if (writer!=null){

            try {
                //追加写数据
                    writer.append(LightValue);
                    //writer.append(light_strength.toString()+",");
                    writer.flush();

            } catch (IOException e) {
                Log.i("Error_yxs", "File error");
                e.printStackTrace();
            }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnsave:

                // 定义写的文件名使用时间

                SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss"); //设置时间格式


                formatter.setTimeZone(TimeZone.getTimeZone("GMT+08")); //设置时区



                Date curDate = new Date(System.currentTimeMillis()); //获取当前时间



                String createDate = formatter.format(curDate);   //格式转换

                //float current_time = event.timestamp;

                String filename = createDate+".txt";

                LightSensorFile = new File(folder, File.pathSeparator + filename);

                // 打开写文件
                try {
                    writer = new FileWriter(LightSensorFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btnstop:
                //关闭写文件
                try {
                    writer.close();
                    writer = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

    }
}