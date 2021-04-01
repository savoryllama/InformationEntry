package com.example.informationentry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final int CHOOSE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.btn);
        EditText et_name = findViewById(R.id.editText_name);

        //头像
        ImageView touxiang = findViewById(R.id.touxiang);
        touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTouxiang();
            }
        });

        //民族
        EditText et_nation = findViewById(R.id.editText_nation);
        Spinner spn_nation = findViewById(R.id.spinner_nation);
        //将可选内容与ArrayAdapter连接起来
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.nation, android.R.layout.simple_spinner_item);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_nation.setAdapter(adapter);
        spn_nation.setOnItemSelectedListener(new MyOnItemSelectedListener());

        //身份证号
        EditText ed_id = findViewById(R.id.editText_id_number);
        ed_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Drawable d = getResources().getDrawable(R.drawable.error);
                d.setBounds(0, 0, 60, 60); //必须设置大小，否则不显示
                int len = ed_id.length();
                String c = String.valueOf(ed_id.getText().charAt(len - 1));
                if (len < 18 && (c.equals("x") || c.equals("X"))) {
                    ed_id.setError("输入格式错误,x为最后一位", d);
                    btn.setEnabled(false);
                }
                if (len > 18) {
                    ed_id.setError("身份证号不能超过18位数", d);
                    btn.setEnabled(false);
                }
                if (len < 18) {
                    ed_id.setError("身份证号不足18位数", d);
                    btn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Email
        EditText ed_email = findViewById(R.id.editText_email);
        ed_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Drawable d = getResources().getDrawable(R.drawable.error);
                d.setBounds(0, 0, 60, 60); //必须设置大小，否则不显示
                if (!emailValidate(ed_email.getText().toString())) {
                    ed_email.setError("邮箱格式错误", d);
                    btn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //出生日期
        EditText ed_date = findViewById(R.id.editText_date);
        ed_date.setOnFocusChangeListener(new MyOnFocusChangeListener());
        ImageButton btn_date = findViewById(R.id.btn_date);
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateSelect();
            }
        });
    }
    //申请打开相册权限
    private void setTouxiang() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)  {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },1);
        }else{
            openAlbum();
        }
    }
    //打开相册
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    //  邮箱验证（正则表达式）
    private Boolean emailValidate(String s) {
        Pattern p = Pattern.compile("^[a-zA-Z0-9]+([\\_|\\-|\\.]?[a-zA-Z0-9])*\\@[a-zA-Z0-9]+([\\_|\\-|\\.]?[a-zA-Z0-9])*\\.[a-zA-Z]{2,3}$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    //  民族editText随spinner更改
    private class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            EditText et_nation = findViewById(R.id.editText_nation);

            et_nation.setText(parent.getItemAtPosition(position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    // 日期选择
    private class MyOnFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                dateSelect();
            }
        }
    }
    private void dateSelect() {
        EditText ed_date = findViewById(R.id.editText_date);
        //实例化Calendar对象，通过getInstance()获得时间操作类型
        Calendar c = Calendar.getInstance();
        //获得时间的年月日
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_YEAR);

        DatePickerDialog dpd = new DatePickerDialog(ed_date.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.i("test", "当前日期：" + year + "/" + month + "/" + dayOfMonth);
            }
        }, year, month, day);
        //调用日期（一定要记得）
        dpd.show();
        ed_date.setText(year + "_" + month + "_" + day);
    }
    //  民族检测editText中的数据是否为spinner中的内容
    //  输入一个字查询arryas中有无这个，有则把输入的查询结果做一个下拉框
}
