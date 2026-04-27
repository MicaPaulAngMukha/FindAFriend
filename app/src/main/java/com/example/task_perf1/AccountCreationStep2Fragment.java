package com.example.task_perf1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import android.widget.EditText;
import android.widget.Toast;

public class AccountCreationStep2Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_creation_step2, container, false);

        Button backButton = view.findViewById(R.id.back_button);
        Button nextButton = view.findViewById(R.id.next_button);
        ViewPager2 viewPager = getActivity().findViewById(R.id.view_pager);

        backButton.setOnClickListener(v -> {
            // Go to the previous tab
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        });

        nextButton.setOnClickListener(v -> {
            AccountCreationDataStorage ACA = new ViewModelProvider(requireActivity()).get(AccountCreationDataStorage.class);

            EditText username = view.findViewById(R.id.username_input);
            EditText password = view.findViewById(R.id.password_input);

            String usn = username.getText().toString();
            String pss = password.getText().toString();

            if(usn.isEmpty() || pss.isEmpty()){
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }

            else{
                ACA.username = usn;
                ACA.password = pss;

                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        return view;
    }
}