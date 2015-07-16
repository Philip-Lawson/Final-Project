package com.example.appproofofconcept;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import finalproject.poc.calculationclasses.WorkPacketList;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Process;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	

	private EditText textBox;
	private Button button;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.activity_main);

		this.registerReceiver(new BatteryInfoReceiver(), new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		button = (Button) findViewById(R.id.button1);
		textBox = (EditText) findViewById(R.id.editText1);

		button.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		/*
		 * Toast.makeText(getApplicationContext(), textBox.getText(),
		 * Toast.LENGTH_LONG).show(); textBox.getText().clear();
		 */
		Thread clientThread = new Thread(new RegisterClientThread(getApplicationContext()));
		clientThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
		clientThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
		
}
