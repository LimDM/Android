package com.example.lim.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.io.InputStreamReader;



import org.apache.http.HttpResponse;

import org.apache.http.client.methods.HttpPost;

import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.params.HttpConnectionParams;

import org.apache.http.params.HttpParams;

import org.json.JSONArray;

import org.json.JSONException;

import org.json.JSONObject;



import android.app.Activity;

import android.os.Bundle;

import android.util.Log;

import android.view.View;

import android.view.View.OnClickListener;

import android.widget.Button;

import android.widget.EditText;

import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private TextView txtResult;
    private EditText ID, PW;

    // URL 설정.
    String url = "http://huming.gonetis.com:9090/";
    String strFunc_Login = "member/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ID = (EditText)findViewById(R.id.editID);
        PW = (EditText)findViewById(R.id.editPW);
        txtResult = (TextView)findViewById(R.id.txtResult);

        Button btnLogin = (Button)findViewById(R.id.BtnLogIn);
        btnLogin.setOnClickListener(new View.OnClickListener(){
           public void onClick(View v)
           {
               Toast.makeText(getApplicationContext(), "Sending To Server", Toast.LENGTH_SHORT).show();

               String strLogIn = ID.getText().toString() + PW.getText().toString();

               // 서버작업
               String result = SendByHttp(strFunc_Login, strLogIn); // 메시지를 서버에 보냄
               //String[][] parsedData = jsonParserList(result); // 받은 메시지를 json 파싱

               //txtResult.setText(parsedData.toString());
           }
        });
    }

    private String SendByHttp(String func, String msg) {

        Toast toast = Toast.makeText(getApplicationContext(), "url : " + url + func +"\n"
                + "id : " + ID.getText().toString() + "\n"
                + "pw : " + PW.getText().toString(), Toast.LENGTH_SHORT);

        if(msg == null)
            msg = "";
        else
            toast.show();

        DefaultHttpClient client = new DefaultHttpClient();

        try {
            /* 체크할 id와 pwd값 서버로 전송 */
            HttpPost post = new HttpPost(url + func);

            /* 지연시간 최대 3초 */
            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 3000);

            /* 데이터 보낸 뒤 서버에서 데이터를 받아오는 과정 */
            HttpResponse response = client.execute(post);
            toast.cancel();
            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

            String line = null;
            String result = "";

            while ((line = bufreader.readLine()) != null) {
                result += line;
            }
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            client.getConnectionManager().shutdown();	// 연결 지연 종료
            return "";
        }
    }

    public String[][] jsonParserList(String pRecvServerPage) {
        try {
            JSONObject json = new JSONObject(pRecvServerPage);
            JSONArray jArr = json.getJSONArray("member");

            // 받아온 pRecvServerPage를 분석하는 부분
            String[] jsonName = {"userID", "userPwd"};
            String[][] parseredData = new String[jArr.length()][jsonName.length];

            for (int i = 0; i < jArr.length(); i++) {
                json = jArr.getJSONObject(i);

                if(json != null) {
                    for(int j = 0; j < jsonName.length; j++) {
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
            }
            return parseredData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
