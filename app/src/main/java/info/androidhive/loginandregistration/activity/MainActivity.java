package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import com.google.zxing.WriterException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;


import java.util.HashMap;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.helper.SQLiteHandler;
import info.androidhive.loginandregistration.helper.SessionManager;

public class MainActivity extends AppCompatActivity{

	private TextView txtName;
	private TextView txtEmail;
	private TextView txtUid;
	private Button btnLogout;
	private Button scan;
	ImageView qrCodeImageview;
	String QRcode;
	public final static int WIDTH =500;
	private SQLiteHandler db;
	private SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtName = (TextView) findViewById(R.id.name);
		txtEmail = (TextView) findViewById(R.id.email);
		txtUid = (TextView) findViewById(R.id.uid);
		btnLogout = (Button) findViewById(R.id.btnLogout);
		scan = (Button) findViewById(R.id.scan);

		// SqLite database handler
		db = new SQLiteHandler(getApplicationContext());

		// session manager
		session = new SessionManager(getApplicationContext());

		if (!session.isLoggedIn()) {
			logoutUser();
		}

		// Fetching user details from SQLite
		HashMap<String, String> user = db.getUserDetails();

		String name = user.get("name");
		String email = user.get("email");
		String unique_id = user.get("uid");

		// Displaying the user details on the screen
		txtName.setText(name);
		txtEmail.setText(email);
		txtUid.setText(unique_id);

		final String imgUrl = txtUid.getText().toString();
		final Bitmap[] bmp = new Bitmap[1];
		ImageView image = (ImageView) findViewById(R.id.imageView);


		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					bmp[0] = getCode("https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + imgUrl + "!&size=100x100");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		thread.start();
		while (bmp[0] == null) {

		}
		image.setImageBitmap(bmp[0]);
		System.out.println("Done");



		// Logout button click event
		btnLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutUser();
			}
		});

		// Scan button click event
		scan.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						DecoderActivity.class);
				startActivity(i);
				finish();
			}
		});


	}
	private Bitmap getCode(String qrUrl) throws IOException{
		URL url = new URL(qrUrl);
		InputStream is = url.openStream();

		Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
		is.close();
		return bmp;
	}





	/**
	 * Logging out the user. Will set isLoggedIn flag to false in shared
	 * preferences Clears the user data from sqlite users table
	 * */
	private void logoutUser() {
		session.setLogin(false);

		db.deleteUsers();

		// Launching the login activity
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
}
