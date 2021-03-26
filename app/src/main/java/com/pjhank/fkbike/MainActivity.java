package com.pjhank.fkbike;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.os.SystemClock;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.components.Legend;
//import com.github.mikephil.charting.components.LegendEntry;
//import com.github.mikephil.charting.components.LimitLine;
//import com.github.mikephil.charting.components.XAxis;
//import com.github.mikephil.charting.components.YAxis;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.LineData;
//import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shawnlin.numberpicker.NumberPicker;
//import com.shawnlin.numberpicker.NumberPicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public int i;
    public int t;
    public int k;
    public int j;
    public int p;
    public int xt;
    public int sleepms;
    public int Age;
    public int Height;
    public int Weight;
    public float a = 1;
    public float cal ;
    //float cal_T = 0;
    public Float cal_T = new Float(0);
    private String SSID,Setmode,Gender;
    private Socket socket;
    private Button info_in,confirm_b;
    private TextView g_in,y_in,h_in,k_in,t_cal,g_zt2,textView2;
    private Drawable setdraw,ong;
    private ImageView wifi_img;
    private Handler WifiHandler = new Handler();
    private EditText age,height,weight;
    private ExecutorService WifiThreadPoll,RecvThreadPoll;
    private InetSocketAddress socketAdr;
    private OutputStream SetmodeStream;

    private boolean WIST = false;
    private boolean errocon = true;
    private boolean connjiu = true;
    private boolean connok = true;
    private boolean Recv_st = true;

    private volatile boolean WI = false;
    private volatile boolean IsConnt = false;
    private volatile boolean Socket_IsConnt = false;
    private WifiInfo wifiInfo;
    private static WifiManager mWifiManager;
    private Context context ;
    private Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        t = 1;
        RecvThreadPoll = Executors.newCachedThreadPool();
        WifiThreadPoll = Executors.newCachedThreadPool();
        getGPSpermission();
        context =  getApplicationContext();
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        ///////////////////////////////////////////
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.number_picker);
        String[] data = {"Bike", "High", "Mid", "Low"};//String[] data = {"Bike", "High", "Mid", "Low_D","X_E", "Y_F", "Z", "A", "B", "C"};
        numberPicker.setValue(1);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(data.length);
        numberPicker.setDisplayedValues(data);
        //numberPicker.setOnValueChangedListener(onValueChangeListener);

        //////////////////////////////////////////

        //
        WifiThreadPoll.execute(new SocketConnTime());
        RecvThreadPoll.submit(new Recv_T());
        //

        //
        ong = getResources().getDrawable(R.drawable.ic_baseline_wifi_24);
        setdraw = getResources().getDrawable(R.drawable.ic_erro_wifi_24);
        wifi_img = findViewById(R.id.wifi_ok_img);
        g_in = findViewById(R.id.G_in);
        y_in = findViewById(R.id.Y_in);
        k_in = findViewById(R.id.K_in);
        h_in = findViewById(R.id.H_in);
        t_cal = findViewById(R.id.T_cal);
        //

        info_in = findViewById(R.id.setinfo);
        info_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Showdialog();
            }
        });

        numberPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker picker, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    WifiThreadPoll.submit(new Setmode(picker.getValue()));
                    Log.d("MainActivity", String.format(Locale.US, "newVal: %d", picker.getValue()));
                }
            }
        });


    }

    public void getGPSpermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void Showdialog(){
        AlertDialog.Builder alert;
        System.out.println("ConnFaile");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            alert = new AlertDialog.Builder(this);
        }
        else {
            alert = new AlertDialog.Builder(this);
        }

        View View = getLayoutInflater().inflate(R.layout.layout_dialog,null);
        Spinner gender = (Spinner) View.findViewById(R.id.Gender);

        age = View.findViewById(R.id.Edit_age);
        height = View.findViewById(R.id.Edit_height);
        weight = View.findViewById(R.id.Edit_weight);
        confirm_b = View.findViewById(R.id.confirm_in);

        alert.setView(View);
        alert.setCancelable(true);
        final AlertDialog dialog = alert.create();

        confirm_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gender = gender.getSelectedItem().toString();
                Age = Integer.parseInt(age.getText().toString());
                Height = Integer.parseInt(height.getText().toString());
                Weight = Integer.parseInt(weight.getText().toString());

                if(!age.getText().toString().isEmpty() && !height.getText().toString().isEmpty() && !weight.getText().toString().isEmpty()){
                    g_in.setText(Gender);
                    y_in.setText(Age+"y");
                    k_in.setText(Weight+"KG");
                    h_in.setText(Height+"CM");
                    Toast.makeText(getApplicationContext(),"已儲存",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }else{
                    Toast.makeText(MainActivity.this,"ERROR",Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();

    }

    public void dd(View view) {
        if (cal_T.isNaN()){
            cal_T = new Float(0);
        }
        float h = Height;
        float w = Weight;
        float wh =  w/h;
        float K = wh*a;
        float ca = 1*K;
        cal =  2 * ca; //0.7
        cal_T += cal+cal;
        Float finalCal_T = cal_T;
        WifiHandler.post(new Runnable() {
            @Override
            public void run() {
                //String.format(getResource().getString(R.id.delay_time), mDay, mHour, mSecond)
                t_cal.setText(String.format(getResources().getString(R.string.cal_text),Math.round(finalCal_T)));
                //t_cal.setText(Integer.toString(Math.round(finalCal_T))+"卡");
            }
        });
    }

    public class SocketConnTime implements Runnable{
        @Override
        public void run() {
            String Wifi_s = '"'+"mServerSocket"+'"';//NTUT_G_SENSOR ankleCuff_test
            //String Wifi_s = '"'+"bike"+'"';
            WIST = true;
            xt = 0;

            float cal ;
            //float cal_T = 0;
            Float cal_T = new Float(0);

            while (true){
                try {
                    xt++;
                    mWifiManager = (WifiManager) MainActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiInfo = mWifiManager.getConnectionInfo();
                    SSID = wifiInfo.getSSID();
                    if(Wifi_s.equals(Wifi_s)){
                        errocon = false;
                        connjiu = true;
                        //Socket_IsConnt = true;
                        if(Socket_IsConnt){
                            k = 1;
                            if (cal_T.isNaN()){
                                cal_T = new Float(0);
                            }
                            float h = Height;
                            float w = Weight;
                            float wh =  w/h;
                            float K = wh*a;
                            float ca = 1*K;
                            cal =  2 * ca; //0.7
                            cal_T += cal+cal;
                            Float finalCal_T = cal_T;
                            WifiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //String.format(getResource().getString(R.id.delay_time), mDay, mHour, mSecond)
                                    t_cal.setText(String.format(getResources().getString(R.string.cal_text),Math.round(finalCal_T)));
                                    //t_cal.setText(Integer.toString(Math.round(finalCal_T))+"卡");
                                }
                            });
                            System.out.println("OK SSID "+SSID+" = "+Wifi_s+" "+Socket_IsConnt + " " + cal_T+" "+ a);


                            connjiu = false;
                            connok = true;
                            IsConnt = true;
                        }else {
                            //errocon = false;
                            //connjiu = true;
                            IsConnt = false;
                            WifiThreadPoll.submit(new Wifisuteji());
                            socketAdr = new InetSocketAddress("192.168.0.18",8000);//4567//5678//49152 192.168.0.18 8000
                            socket = new Socket();
                            socket.connect(socketAdr,5000);
                            Socket_IsConnt = socket.isConnected();

                            connjiu = false;
                            connok = true;
                            WifiThreadPoll.submit(new Wifisuteji());
                        }
                    }else {
                        WifiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //WIST = true;
                                IsConnt = false;
                                errocon = true;
                                connjiu = false;
                                connok = false;
                                Socket_IsConnt = false;
                                WifiThreadPoll.submit(new Wifisuteji());

                                if (toast == null) {
                                    toast = Toast.makeText(context, "Not Conneted ", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }
                        });

                        if(xt >= 25){
                            toast = null;
                            xt = 0;
                        }

                    }
                }catch (Exception e){
                    System.out.println("ConnFaile");
                }
                //System.out.println("DEBUG: "+k+" "+p+" Debug: "+SSID);
                SystemClock.sleep(1000);
            }
        }
    }
    public class Wifisuteji implements Runnable{
        @Override
        public void run() {
            //for (j = 0; j<100000; j++)
            while (WIST){
                if(IsConnt){
                    sleepms = 5000;
                    System.out.println("isconn");
                    return;
                }
                SystemClock.sleep(sleepms);
                k++;
                p++;
                if(errocon){
                    sleepms = 200;
                    if (k == 3) {
                        WifiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                wifi_img.setVisibility(View.VISIBLE);
                            }
                        });
                        wifi_img.setImageDrawable(getDrawable(R.drawable.ic_erro_wifi_24));
                        k = 1;

                    }else {
                        WifiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                wifi_img.setVisibility(View.GONE);
                            }
                        });
                    }
                    System.out.println("DEBUG: "+k+" "+" Debug: "+SSID +" "+xt);
                    return;
                }else if(connjiu){
                    sleepms = 800;
                    if (k == 3) {
                        WifiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                wifi_img.setVisibility(View.VISIBLE);
                            }
                        });
                        wifi_img.setImageDrawable(getDrawable(R.drawable.ic_con_wifi_24));
                        k = 1;

                    }else {
                        WifiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                wifi_img.setVisibility(View.GONE);
                            }
                        });
                    }
                    System.out.println("DEBUG: "+k+" "+" Debug: "+SSID +" "+xt);
                    return;
                }else if(connok){
                    sleepms = 1000;
                    wifi_img.setImageDrawable(getDrawable(R.drawable.ic_baseline_wifi_24));
                    WifiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            wifi_img.setVisibility(View.VISIBLE);
                        }
                    });
                }
                IsConnt = true;
                System.out.println("++: "+k+" "+" Debug: "+SSID +" "+xt);
                return;
            }

        }
    }

    public class Recv_T implements Runnable{
        @Override
        public void run() {
            String ret = "";
            int Rang = 0;
            boolean end = false;
            Float cal_T = new Float(0);
            float cal;
            //float a1 = (float) 1.5; float a2 = (float) 2.0; float a3 = (float) 2.5;

            /*float h = 170;
            float w = 60;
            float wh =  w/h;
            float K = wh*a;
            float ca = 1*K;
            float cal = 0;
            float cal_T = 0;*/

            /*float K = wh*a;
            float ca = 1*K;
            float cal = 0;
            float cal_T = 0;*/

            //byte[] bytes = new byte[delint];

            while (Recv_st){
                try {
                    if(Socket_IsConnt){
                        InputStream sis = socket.getInputStream();
                        //sis.read(bytes);
                        //System.out.println("not Message:++ "+ Arrays.toString(bytes)+" "+Socket_IsConnt);

                        if(sis == null){
                            end = true;
                        }else {
                            end = false;
                            while (!end){
                                byte[] bytes = new byte[2];
                                sis.read(bytes);

                                float h = Height;
                                float w = Weight;
                                float wh =  w/h;
                                float K = wh*a;
                                float ca = 1*K;
                                cal =  bytes[0] * ca; //0.7
                                cal_T += cal+cal;
                                Float finalCal_T = cal_T;
                                WifiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //String.format(getResource().getString(R.id.delay_time), mDay, mHour, mSecond)
                                        t_cal.setText(String.format(getResources().getString(R.string.cal_text),Math.round(finalCal_T)));
                                        //t_cal.setText(Integer.toString(Math.round(finalCal_T))+"卡");
                                    }
                                });

                                System.out.println("Message: "+Arrays.toString(bytes)+" " + cal_T+" "+ a);
                                SystemClock.sleep(900);
                            }
                        }
                    }
                } catch (IOException e) {
                    //Recv_st=false;
                    System.out.println("Message: ");
                    e.printStackTrace();
                }
            }
        }
    }




    public class Setmode implements Runnable{
        int setmode;
        Setmode(int setmode){this.setmode = setmode;}
        @Override
        public void run() {
            switch (setmode){
                case 1:
                    try {
                        a = 1;

                        SetmodeStream = socket.getOutputStream();
                        Setmode = "Mode1";
                        SetmodeStream.write((Setmode).getBytes());
                        SetmodeStream.flush();
                        Log.e("MainActivity",Setmode+SetmodeStream+(Setmode).getBytes());
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        a = (float) 1.5;
                        SetmodeStream = socket.getOutputStream();
                        Setmode = "Mode2";
                        SetmodeStream.write((Setmode).getBytes());
                        SetmodeStream.flush();
                        Log.e("MainActivity",Setmode+SetmodeStream+(Setmode).getBytes());
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        a = (float) 2.0;
                        SetmodeStream = socket.getOutputStream();
                        Setmode = "Mode3";
                        SetmodeStream.write((Setmode).getBytes());
                        SetmodeStream.flush();
                        Log.e("MainActivity",Setmode+SetmodeStream+(Setmode).getBytes());
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    try {
                        a = (float) 2.5;
                        SetmodeStream = socket.getOutputStream();
                        Setmode = "Mode4";
                        SetmodeStream.write((Setmode).getBytes());
                        SetmodeStream.flush();
                        Log.e("MainActivity",Setmode+SetmodeStream+(Setmode).getBytes());
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    }

}