package com.example.loginform2;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.RelativeLayout.ALIGN_PARENT_RIGHT;
import static java.lang.System.loadLibrary;

public class MainActivity extends Activity {
    public static UserLoginTask mAuthTask;
    public String sGameActivity = "com.unity3d.player.UnityPlayerActivity";
    public String sPass = "";
    public String sUrl = "https://example.com/login.php";
    public String sUser = "";

    SharedPreferences sharedPreferences;
    CheckBox rememberChkBox;
    EditText userEditText, passEditText;
    Button loginBtn;
    ProgressBar progressBar;

    public native void LoginCheck();

    //Login async task
    public class UserLoginTask extends AsyncTask<Void, Void, String> {
        private final String mPassword;
        private final String mUser;

        UserLoginTask(String str, String str2) {
            mUser = str;
            mPassword = str2;
        }

        public String doInBackground(Void... voidArr) {
            if (mUser.isEmpty() || mPassword.isEmpty()) {
                return "";
            }
            MainActivity mainActivity = MainActivity.this;
            return mainActivity.urlRequest(MainActivity.this.sUrl + "?username=" + MainActivity.this.base64Encode(this.mUser) + "&password=" + MainActivity.this.base64Encode(this.mPassword));
        }

        public void onPostExecute(String str) {
            MainActivity.this.loginBtn.setVisibility(View.INVISIBLE);
            MainActivity.this.progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(str);
            if (!str.trim().isEmpty()) {
                if (str.trim().contains("\"code\":\"1\"")) {
                    Toast.makeText(MainActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                    String user = userEditText.getText().toString().trim();
                    String pass = passEditText.getText().toString().trim();

                    boolean isChecked = MainActivity.this.rememberChkBox.isChecked();
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    if (isChecked) {
                        edit.putString("User", user);
                        edit.putString("Pass", pass);
                    } else {
                        edit.clear();
                    }
                    edit.putBoolean("RememberMe", isChecked);
                    edit.apply();

                    //Launch game activity
                    try {
                        MainActivity.this.startActivity(new Intent(MainActivity.this, Class.forName(MainActivity.this.sGameActivity)));
                        //Start service
                    } catch (ClassNotFoundException e) {
                        Toast.makeText(MainActivity.this, "Error. Game's main activity does not exist", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        return;
                    }

                    LoginCheck();

                    finish();
                    return;
                } else if (str.trim().contains("\"code\":\"0\"")) {
                    Toast.makeText(MainActivity.this, "Login failed!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "An unknown error. Please contact the modder", Toast.LENGTH_LONG).show();
                }
                MainActivity.this.loginBtn.setVisibility(View.VISIBLE);
                MainActivity.this.progressBar.setVisibility(View.INVISIBLE);
            } else {
                Toast.makeText(MainActivity.this, "An unknown error. Please contact the modder", Toast.LENGTH_LONG).show();
                MainActivity.this.loginBtn.setVisibility(View.VISIBLE);
                MainActivity.this.progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void SetupForm() {
        //Create shared preferences to remember username and password
        sharedPreferences = getSharedPreferences("SavePref", 0);
        String struser = sharedPreferences.getString("User", null);
        String strpass = sharedPreferences.getString("Pass", null);
        Boolean rememberMe = sharedPreferences.getBoolean("RememberMe", false);

        //Add relative layout
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        linearLayout.setBackgroundColor(Color.parseColor("#ff1f2b3f"));
        linearLayout.setPadding(convertDipToPixels(15.0f), convertDipToPixels(15.0f), convertDipToPixels(15.0f), convertDipToPixels(15.0f));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //Big text
        TextView teamName = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.setMargins(convertDipToPixels(10.0f), convertDipToPixels(10.0f), convertDipToPixels(10.0f), convertDipToPixels(10.0f));
        teamName.setLayoutParams(layoutParams);
        teamName.setGravity(1);
        teamName.setText(Html.fromHtml("<font face='monospace'><b><font color='#ffffff'>LGL</font><font color='#57c4aa'> TEAM</font></b></font>"));
        teamName.setTextSize(45.0f);

        //Login to play note text
        TextView note = new TextView(this);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.setMargins(convertDipToPixels(10.0f), convertDipToPixels(10.0f), convertDipToPixels(10.0f), convertDipToPixels(10.0f));
        note.setLayoutParams(layoutParams2);
        note.setGravity(1);
        note.setText(Html.fromHtml("<font face='fantasy'><b><font color='#57c4aa'>LOGIN TO PLAY</b></font>"));
        note.setTextSize(20.0f);

        //Username text
        TextView userTextView = new TextView(this);
        userTextView.setText("Username");
        userTextView.setTextColor(Color.parseColor("#ddddd1"));
        userTextView.setTextSize(12.0f);

        //Username form
        userEditText = new EditText(this);
        if (struser != null && !struser.isEmpty()) {
            userEditText.setText(struser);
        }
        userEditText.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        userEditText.setHint("Username");
        userEditText.setHintTextColor(Color.parseColor("#6d7582"));
        userEditText.setTextColor(Color.parseColor("#ffffff"));
        userEditText.setSingleLine(true);
        userEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});

        //Password text
        TextView passTextView = new TextView(this);
        passTextView.setText("Password");
        passTextView.setTextColor(Color.parseColor("#ddddd1"));
        passTextView.setTextSize(12.0f);

        //Password form
        passEditText = new EditText(this);
        if (strpass != null && !strpass.isEmpty()) {
            passEditText.setText(strpass);
        }
        passEditText.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        passEditText.setHint("Password");
        passEditText.setHintTextColor(Color.parseColor("#6d7582"));
        passEditText.setTextColor(Color.parseColor("#ffffff"));
        passEditText.setSingleLine(true);
        passEditText.setInputType(129);

        //Framelayout
        FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(-1, -2);
        layoutParams3.topMargin = convertDipToPixels(15.0f);
        layoutParams3.bottomMargin = convertDipToPixels(15.0f);
        layoutParams3.gravity = 17;

        frameLayout.setLayoutParams(layoutParams3);
        loginBtn = new Button(this);
        loginBtn.setBackgroundColor(Color.parseColor("#12a56b"));
        loginBtn.setText("Login");
        loginBtn.setTextColor(Color.parseColor("#e8f8f4"));

        //Spinning progress bar
        progressBar = new ProgressBar(this);
        FrameLayout.LayoutParams layoutParams4 = new FrameLayout.LayoutParams(-2, -2);
        layoutParams4.gravity = 17;
        progressBar.setLayoutParams(layoutParams4);
        progressBar.setVisibility(View.INVISIBLE);

        frameLayout.addView(loginBtn);
        frameLayout.addView(progressBar);

        RelativeLayout relativelayout = new RelativeLayout(this);
        relativelayout.setLayoutParams(new RelativeLayout.LayoutParams(-2, -1));
        relativelayout.setPadding(convertDipToPixels(5.0f), convertDipToPixels(5.0f), convertDipToPixels(5.0f), convertDipToPixels(5.0f));
        relativelayout.setBackgroundColor(Color.parseColor("#ff1f2b3f"));
        relativeLayout.setVerticalGravity(16);

        //Create remember checkbox
        rememberChkBox = new CheckBox(this);
        rememberChkBox.setChecked(rememberMe);
        rememberChkBox.setPadding(0, 5, 0, 5);
        rememberChkBox.setTextSize(18);
        rememberChkBox.setTextColor(Color.rgb(255, 255, 255));
        rememberChkBox.setText("Remember me");

        //Clickable registration text
        TextView regTextView = new TextView(this);
        regTextView.setText("Registration");
        regTextView.setTextColor(Color.parseColor("#ffffff"));
        regTextView.setTypeface(null, Typeface.BOLD);
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lp2.addRule(ALIGN_PARENT_RIGHT);
        regTextView.setLayoutParams(lp2);

        //Footer text
        LinearLayout linearLayout3 = new LinearLayout(this);
        RelativeLayout.LayoutParams layoutParams5 = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams5.addRule(12);
        linearLayout3.setLayoutParams(layoutParams5);
        linearLayout3.setOrientation(LinearLayout.VERTICAL);

        TextView footerText = new TextView(this);
        footerText.setText(Html.fromHtml("<font face='monospace'>Modded by <font color='#57c4aa'>LGL Team</font></font>"));
        footerText.setTextColor(Color.parseColor("#ffffff"));
        footerText.setGravity(17);
        linearLayout3.addView(footerText);

        //Description with scroll view
        ScrollView scrollView = new ScrollView(this);
        RelativeLayout.LayoutParams rlsv = new RelativeLayout.LayoutParams(-1, -1);
        rlsv.topMargin = 100;
        rlsv.bottomMargin = 50;
        scrollView.setLayoutParams(rlsv);
        scrollView.setScrollBarSize(convertDipToPixels(5.0f));

        TextView desc = new TextView(this);
        desc.setText(Html.fromHtml("Hello Friend!<br/>We are happy because you using MOD of <b><font color='red'>LGL Team</b>.<br/><br/>To use mod you must login with your account registered at <b><font color='red'>LGL Team</font></b>.<br/><br/>If you don't have an account, please click to \"<b><font color='yellow'>Registration</font></b>\" below the <b>\"<font color='yellow'>LOGIN</font>\"</b> button.<br/><br/>Visit the <b><font color='red'>LGL Team</font></b> website to download more <b><font color='yellow'>mods</font></b></font>"));
        desc.setTextColor(Color.parseColor("#ffffff"));

        //Add views
        scrollView.addView(desc);
        relativelayout.addView(rememberChkBox);
        relativelayout.addView(regTextView);
        relativelayout.addView(scrollView);
        linearLayout.addView(teamName);
        linearLayout.addView(note);
        linearLayout.addView(userTextView);
        linearLayout.addView(userEditText);
        linearLayout.addView(passTextView);
        linearLayout.addView(passEditText);
        linearLayout.addView(frameLayout);
        linearLayout.addView(relativelayout);
        relativeLayout.addView(linearLayout);
        relativeLayout.addView(linearLayout3);

        //OnClick listeners
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (userEditText.getText().toString().matches("") || passEditText.getText().toString().matches("")) {
                    Toast.makeText(MainActivity.this, "Input \"Username\" or \"Password\" please!", Toast.LENGTH_SHORT).show();
                    return;
                }
                sUser = userEditText.getText().toString();
                sPass = passEditText.getText().toString();
                mAuthTask = new UserLoginTask(sUser, sPass);
                mAuthTask.execute();
                loginBtn.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        regTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(sUrl + "/register/")));
            }
        });
        teamName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(sUrl)));
            }
        });
        setContentView(relativeLayout);
    }

    private int convertDipToPixels(float f) {
        return (int) ((f * getResources().getDisplayMetrics().density) + 0.5f);
    }

    public String base64Encode(String str) {
        return new String(Base64.encode(str.getBytes(), 0)).trim();
    }

    public String urlRequest(String str) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(str).openConnection().getInputStream()));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                sb.append(readLine);
                sb.append("\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLibrary("MyLib");
        SetupForm();
    }
}
