package com.manishjangra.mosam;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView, weatherText;
    EditText editText;
    Button searchButton;

    String url;
    String[] temp = new String[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        imageView = findViewById(R.id.image_view);
        textView = findViewById(R.id.text_view);
        weatherText = findViewById(R.id.weather_text);
        editText = findViewById(R.id.edit_text);
        searchButton = findViewById(R.id.search_btn);




        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editText.getText().toString();
                try {
                    if (!city.isEmpty()){

// -----------> Below line of code will not workk if you will not enter your api_key there. So do paste api_key below
                        
                        url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid={ Enter your own api_key here}";
                    }else {
                        Toast.makeText(MainActivity.this, "Enter City Name First", Toast.LENGTH_SHORT).show();
                    }
                    GetWeather task = new GetWeather();
                    temp[0] = task.execute(url).get();
                }catch (ExecutionException e){
                    e.printStackTrace();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                if(temp ==null){
                    weatherText.setText("No City Found");
                    Toast.makeText(MainActivity.this, "Cannot Find City", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class GetWeather extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line ="";

                while((line = bufferedReader.readLine()) != null){
                    result.append(line).append("\n");
                }
                return result.toString();
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            try{
                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("main");
                weatherInfo = weatherInfo.replace("temp","Temperature");
                weatherInfo = weatherInfo.replace("feels_like","Feels Like");
                weatherInfo = weatherInfo.replace("temp_min","Temperature Minimum");
                weatherInfo = weatherInfo.replace("temp_max","Temperature Maximum");
                weatherInfo = weatherInfo.replace("pressure","Pressure");
                weatherInfo = weatherInfo.replace("humidity","Humidity");
                weatherInfo = weatherInfo.replace("sea_level","Sea Level");
                weatherInfo = weatherInfo.replace("grnd_level","Ground Level");
                weatherInfo = weatherInfo.replace("{","");
                weatherInfo = weatherInfo.replace("}","");
                weatherInfo = weatherInfo.replace(":"," : ");
                weatherInfo = weatherInfo.replace(","," \n");
                weatherInfo = weatherInfo.replace("\""," ");


                weatherText.setText(weatherInfo);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

