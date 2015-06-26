package com.example.testingagainnnn;

import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressWarnings("deprecation")
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
		Toast.makeText(getApplicationContext(), textBox.getText(),
				Toast.LENGTH_LONG).show();
		textBox.getText().clear();
		;
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
