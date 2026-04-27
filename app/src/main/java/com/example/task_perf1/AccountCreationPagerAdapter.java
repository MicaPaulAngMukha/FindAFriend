package com.example.task_perf1;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AccountCreationPagerAdapter extends FragmentStateAdapter {

    public AccountCreationPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AccountCreationStep1Fragment();
            case 1:
                return new AccountCreationStep2Fragment();
            case 2:
                return new AccountCreationStep3Fragment();
            case 3:
                return new AccountCreationStep4Fragment();
            case 4:
                return new AccountCreationStep5Fragment();
            default:
                // You can either return the first step or create a final, empty fragment
                return new AccountCreationStep1Fragment();
        }
    }

    @Override
    public int getItemCount() {
        // We now have 5 steps
        return 5;
    }
}