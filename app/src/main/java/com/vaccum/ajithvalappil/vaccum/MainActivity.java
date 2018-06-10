package com.vaccum.ajithvalappil.vaccum;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spinner;
    static List<String> items = new ArrayList<String>();
    List<String> commands = new ArrayList<String>();
    String lastCommand = "f";
    static final String[]blueDevices = {"item 1", "item 2", "item 3"};
    ArrayAdapter<String> adapter;
    TableLayout tableLayout;

    BluetoothController aBluetoothController = new BluetoothController();
    public BluetoothAdapter btAdapter = null;
    public BluetoothSocket btSocket = null;
    public OutputStream outStream = null;
    public InputStream inStream = null;
    public static String address = "88:C9:D0:94:DE:3F";
    static boolean isDevicesConnected = false;
    //ReadData aReadData = new ReadData();
    Button connectBlu;
    static final int REQUEST_ENABLE_BT = 0;
    static boolean stillProcessing = false;
    private boolean isModeMobile = true;
    long distanceInSeconds = 0;
    long rotateInSeconds = 7290;
    String rotationSide = "l";
    Date d1 = null;
    boolean vaccumStart = false;
    boolean startAutoMode = false;
    String message = null;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();

            if (bundle.containsKey("connected")){
                String msgData  = bundle.getString("connected");
                System.out.println("Complete.....>> " + msgData);
                Button connectBlu=(Button)findViewById(R.id.button);
                if (msgData!=null && msgData.equalsIgnoreCase("Connected")){
                    connectBlu.setText("Disconnect");
                    btSocket = aBluetoothController.getBtSocket();
                    outStream = aBluetoothController.getOutStream();
                    inStream = aBluetoothController.getInStream();
                    /*aReadData.setHandler(handler);
                    aReadData.setBtSocket(btSocket);
                    aReadData.setInStream(inStream);
                    aReadData.start();*/
                }else if (msgData!=null && msgData.equalsIgnoreCase("Disconnected")){
                    connectBlu.setText("Connect");
                }
                System.out.println("Complete.....");
            }
            if (bundle.containsKey("message")){
                String msgData  = bundle.getString("message");
                System.out.println("Complete.....>> " + msgData);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner)findViewById(R.id.spinner);
        connectBlu=(Button)findViewById(R.id.button);
        tableLayout=(TableLayout)findViewById(R.id.tableLayout);

        items.add("Select Bluetooth");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        try {
            System.out.println("Starting.....");
            aBluetoothController.setProcessType("init");
            System.out.println("init.....");
            System.out.println("Thread started.....");
            aBluetoothController.start();
            System.out.println("wait for complete started.....");
            aBluetoothController.join();
            System.out.println("Complete.....");
            System.out.println("aBluetoothController.isDeviceHasBluetooth() >>" + aBluetoothController.isDeviceHasBluetooth());
            System.out.println("aBluetoothController.isDeviceBluetoothIsOn() >>" + aBluetoothController.isDeviceBluetoothIsOn());
            if (aBluetoothController.isDeviceHasBluetooth()){
                if (!aBluetoothController.isDeviceBluetoothIsOn()){
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    btAdapter = aBluetoothController.getBtAdapter();
                }else{
                    btAdapter = aBluetoothController.getBtAdapter();
                }
                aBluetoothController = new BluetoothController();
                aBluetoothController.setBtAdapter(btAdapter);
                aBluetoothController.setProcessType("getlist");
                System.out.println("init.....");
                System.out.println("Thread started.....");
                aBluetoothController.start();
                System.out.println("wait for complete started.....");
            }else{
                finish();
            }
        }catch (Exception ee){
            ee.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void connectBluetooth(View view) {
        if (btAdapter!=null){
            try {
                String selectedDevice  = spinner.getSelectedItem().toString();
                System.out.println("selectedDevice: " + selectedDevice);
                if (selectedDevice!=null && selectedDevice.contains("\n")) {
                    String data[] = selectedDevice.split("\n");
                    String deviceName = "";
                    String deviceAddress = "";
                    if (data.length == 2) {
                        deviceName = data[0];
                        deviceAddress = data[1];
                    }
                    address = deviceAddress;
                }
                if (!isDevicesConnected){
                    aBluetoothController = new BluetoothController();
                    aBluetoothController.setBtAdapter(btAdapter);
                    aBluetoothController.setProcessType("setup");
                    aBluetoothController.setHandler(handler);
                    aBluetoothController.address =  MainActivity.address;
                    System.out.println("init.....");
                    System.out.println("Thread started.....");
                    //aReadData.setKeepRunning(true);
                    aBluetoothController.start();
                    //readVoiceFromText();
                    Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show();
                }else{
                    connectBlu.setText("Connect");
                    //aReadData.setKeepRunning(false);
                    try {
                        if (btSocket!=null)
                            btSocket.close();
                        isDevicesConnected = false;
                    } catch (Exception e2) {
                        System.out.println("Fatal Error In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                        isDevicesConnected = false;
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                try {
                    if (btSocket!=null)
                        btSocket.close();
                    isDevicesConnected = false;
                } catch (IOException e2) {
                    System.out.println("Fatal Error In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                    isDevicesConnected = false;
                }
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }


    public void sendMessage(String message){
//        Toast.makeText(this, "Sending..." + message, Toast.LENGTH_SHORT).show();
        executeCommand(message);
    }

    public void executeCommand(String message){
        System.out.println("message: " + message);
        try {
            byte[] msgBuffer = message.getBytes("UTF-8");
            if (outStream!=null) {
                //
                outStream.write(msgBuffer);
                outStream.flush();
            }else{
                //Toast.makeText(this, "Please connect to a device...", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            System.out.println("In onResume() and an exception occurred during write: " + e.getMessage());
        }
    }

    public void front(View view){
        sendMessage("f");
    }
    public void back(View view){
        sendMessage("b");
    }
    public void left(View view){
        sendMessage("l");
        sleep();
    }
    public void right(View view){
        sendMessage("r");
        sleep();
    }
    public void stop(View view){
        sendMessage("s");
    }

    public void up(View view){
        sendMessage("u");
        sleep();
    }

    private void sleep(){
        try {
            Thread.sleep(100);
            sendMessage("s");
        }catch(Exception ee){

        }
    }

    public void down(View view){
        sendMessage("d");
        sleep();
    }

    public void processData(){
        Runnable runnable = new Runnable() {
            public void run() {
                while(vaccumStart){

                    sendMessage("a");
                    try {
                        Thread.sleep(distanceInSeconds * 1000);
                    }catch(Exception ee){

                    }
                    sendMessage("c");
                    try {
                        Thread.sleep(rotateInSeconds * 2);
                    }catch(Exception ee){

                    }
                    sendMessage("a");
                    try {
                        Thread.sleep(distanceInSeconds * 1000);
                    }catch(Exception ee){

                    }
                    sendMessage("e");
                    try {
                        Thread.sleep(rotateInSeconds * 2);
                    }catch(Exception ee){

                    }
                }
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
    }

}
