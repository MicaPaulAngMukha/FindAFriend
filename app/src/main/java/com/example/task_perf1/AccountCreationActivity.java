package com.example.task_perf1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class AccountCreationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        AccountCreationPagerAdapter adapter = new AccountCreationPagerAdapter(this);
        viewPager.setAdapter(adapter);
    }
}