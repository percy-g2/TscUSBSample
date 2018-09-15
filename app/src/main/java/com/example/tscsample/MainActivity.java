package com.example.tscsample;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;

import java.util.HashMap;
import java.util.Iterator;

import com.example.tscdll.TSCUSBActivity;


public class MainActivity extends Activity {

	TSCUSBActivity TscUSB = new TSCUSBActivity();
	
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	private static UsbManager mUsbManager;
	private static PendingIntent mPermissionIntent;
	private static boolean hasPermissionToCommunicate = false;
	
	private Button test;
	private TextView tv1;
	private static UsbDevice device;
	
	
	IntentFilter filterAttached_and_Detached = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
	// Catches intent indicating if the user grants permission to use the USB device
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            hasPermissionToCommunicate = true;
                        }
                    }
                }
            }
        }
    };
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	   	 mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
	     mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
	     IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
	     registerReceiver(mUsbReceiver, filter);
	     
	     
	     UsbAccessory[] accessoryList = mUsbManager.getAccessoryList();
         HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
         Log.d("Detect ", deviceList.size()+" USB device(s) found");
         Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
         while(deviceIterator.hasNext())
         {
        	device = deviceIterator.next();
        	if(device.getVendorId() == 4611)
        	{
	        	//Toast.makeText(MainActivity.this, device.toString(), 0).show();
	        	break;
        	}
         }

	     
	     
         //-----------start-----------
       	 PendingIntent mPermissionIntent;
       	 mPermissionIntent = PendingIntent.getBroadcast(MainActivity.this, 0, 
       	 new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_ONE_SHOT);
       	 mUsbManager.requestPermission(device, mPermissionIntent);

       	 tv1 = (TextView) findViewById(R.id.textView1);
		 test = (Button) findViewById(R.id.button1);

		 
		 test.setOnClickListener(new OnClickListener() {

             public void onClick(View v) {

        	 if(mUsbManager.hasPermission(device))
        	 {
            	 TscUSB.openport(mUsbManager,device);
            	 
            	 TscUSB.sendcommand("SIZE 3,1\r\n");
            	 TscUSB.sendcommand("GAP 0,0\r\n");
            	 TscUSB.sendcommand("CLS\r\n");
            	 TscUSB.sendcommand("TEXT 100,100,\"3\",0,1,1,\"123456\"\r\n");
            	 TscUSB.sendcommand("PRINT 1\r\n");
            	 TscUSB.closeport(3000);
            	 
        	 }

         }
        	
        	
             
        });

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}


}
