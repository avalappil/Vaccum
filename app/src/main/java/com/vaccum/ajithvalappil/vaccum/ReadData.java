package com.vaccum.ajithvalappil.vaccum;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.InputStream;

/**
 * Created by ajithvalappil2 on 1/6/15.
 */
public class ReadData extends Thread{

    public InputStream inStream = null;
    String inpMsg = null;
    BluetoothSocket btSocket;
    Handler handler = new Handler();
    boolean keepRunning = true;

    public void run(){
        while (true){
            try{
                inpMsg = "";

            }catch(Exception e1){
                e1.printStackTrace();
            }
            try {
                int i;
                char c;
                inpMsg = "";
                if (btSocket!=null && btSocket.isConnected() && inStream!=null && (i=inStream.read())!=-1){ //Check if there is an available byte to read
                    c = (char)i; //Conduct a serial read
                    inpMsg = inpMsg.concat(String.valueOf(c));
                }
                if (btSocket.isConnected() && inpMsg!=null && !"".equals(inpMsg)){
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("message", inpMsg);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    System.out.println(">>> " + inpMsg);
                    inpMsg = "";
                }

            }catch(Exception e){
                e.printStackTrace();
                break;
            }
        }
    }



    public InputStream getInStream() {
        return inStream;
    }

    public void setInStream(InputStream inStream) {
        this.inStream = inStream;
    }

    public String getInpMsg() {
        return inpMsg;
    }

    public void setInpMsg(String inpMsg) {
        this.inpMsg = inpMsg;
    }

    public void setBtSocket(BluetoothSocket btSocket) {
        this.btSocket = btSocket;
    }

    public BluetoothSocket getBtSocket() {
        return btSocket;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public boolean isKeepRunning() {
        return keepRunning;
    }

    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }
}
