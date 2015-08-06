package com.ramasubbaiya.restfulwebservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidexample.restfulwebservice.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RestFulWebserviceActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedState) {

		super.onCreate(savedState);
		setContentView(R.layout.rest_ful_webservice);

		final Button ServerData = (Button) findViewById(R.id.GetServerData);

		ServerData.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// WebServer Request URL
				String serverURL = "http://rama.thoughtbox.ca/JsonData.php";

				// Use AsyncTask execute Method To Prevent ANR Problem
				new LongOperation().execute(serverURL);
			}
		});

	}

	// Class with extends AsyncTask class

	private class LongOperation extends AsyncTask<String, Void, Void> { 

		// Required initialization

		private String Content;    
		private String Error = null;
		private ProgressDialog Dialog = new ProgressDialog(
				RestFulWebserviceActivity.this);
		String data = "";
		TextView serverResponse = (TextView) findViewById(R.id.output);
		TextView parsedJSON = (TextView) findViewById(R.id.jsonParsed);
		EditText userInput = (EditText) findViewById(R.id.serverText);

		protected void onPreExecute() {
			// NOTE: You can call UI Element here.

			// Start Progress Dialog (Message)

			Dialog.setMessage("Please wait...Data is being Parsed!");
			Dialog.show();

			try {
				// Set Request parameter
				data += "&" + URLEncoder.encode("data", "UTF-8") + "="
						+ userInput.getText();

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// Call after onPreExecute method
		protected Void doInBackground(String... urls) {

			/************ Make Post Call To Web Server ***********/
			BufferedReader reader = null;

			// Send data
			try {

				// Defined URL where to send data
				URL url = new URL(urls[0]);

				// Send POST data request

				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(
						connection.getOutputStream());
				wr.write(data);
				wr.flush();

				// Get the server response

				reader = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = null;

				// Read Server Response
				while ((line = reader.readLine()) != null) {
					// Append server response in string
					sb.append(line + " ");
				}

				// Append Server Response To Content String
				Content = sb.toString();
			} catch (Exception ex) {
				Error = ex.getMessage();
			} finally {
				try {

					reader.close();
				}

				catch (Exception ex) {
				}
			}

			/*****************************************************/
			return null;
		}

		protected void onPostExecute(Void unused) {
			// NOTE: You can call UI Element here.

			// Close progress dialog
			Dialog.dismiss();

			if (Error != null) {

				serverResponse.setText("Output : " + Error);

			} else {

				// Show Response Json On Screen (activity)
				serverResponse.setText(Content);

				/****************** Start Parse Response JSON Data *************/

				String OutputData = "";

				JSONObject jsonResponse;

				try {

					/******
					 * Creates a new JSONObject with name/value mappings from
					 * the JSON string.
					 ********/
					jsonResponse = new JSONObject(Content);

					/*****
					 * Returns the value mapped by name if it exists and is a
					 * JSONArray.
					 ***/
					/******* Returns null otherwise. *******/
					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("Android");
 
					/*********** Process each JSON Node ************/

					int lengthJsonArr = jsonMainNode.length();

					for (int i = 0; i < lengthJsonArr; i++) {
						/****** Get Object for each JSON node. ***********/
						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);

						/******* Fetch node values **********/
						String name = jsonChildNode.optString("name")
								.toString();
						String number = jsonChildNode.optString("description")
								.toString();
						String dateAdded = jsonChildNode
								.optString("album").toString();

						OutputData += "Name              : " + name;
						OutputData += "\n";
						OutputData += "Description    : " + number;
						OutputData += "\n";
						OutputData += "Album             : " + dateAdded;
						OutputData += "\n";
						OutputData += "--------------------------------------------------";
						OutputData += "\n";

					}

					/****************** End Parse Response JSON Data *************/

					// Show Parsed Output on screen (activity)
					parsedJSON.setText(OutputData);

				} catch (JSONException e) {

					e.printStackTrace();

				}

			}
		}

	}
}
