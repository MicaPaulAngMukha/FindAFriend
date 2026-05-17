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
import java.util.regex.Pattern;

public class AccountCreationStep2Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_creation_step2, container, false);

        Button backButton = view.findViewById(R.id.back_button);
        Button nextButton = view.findViewById(R.id.next_button);
        ViewPager2 viewPager = getActivity().findViewById(R.id.view_pager);

        EditText username = view.findViewById(R.id.username_input);
        EditText password = view.findViewById(R.id.password_input);
        EditText confirmPassword = view.findViewById(R.id.confirm_password_input);

        backButton.setOnClickListener(v -> {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        });

        nextButton.setOnClickListener(v -> {
            AccountCreationDataStorage ACA = new ViewModelProvider(requireActivity()).get(AccountCreationDataStorage.class);

            String usn = username.getText().toString().trim();
            String pss = password.getText().toString();
            String cpss = confirmPassword.getText().toString();

            if(usn.isEmpty() || pss.isEmpty() || cpss.isEmpty()){
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pss.equals(cpss)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(pss)) {
                Toast.makeText(getContext(), "Password must be at least 8 characters, contain uppercase, lowercase, a number, and a special character (no spaces).", Toast.LENGTH_LONG).show();
                return;
            }

            ACA.username = usn;
            ACA.password = pss;
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        });

        return view;
    }

    private boolean isValidPassword(String password) {
        // Regex: 
        // ^(?=.*[0-9])        # a digit must occur at least once
        // (?=.*[a-z])         # a lower case letter must occur at least once
        // (?=.*[A-Z])         # an upper case letter must occur at least once
        // (?=.*[@#$%^&+=!_\\-?]) # a special character must occur at least once
        // (?=\S+$)            # no whitespace allowed in the entire string
        // .{8,}               # at least 8 characters
        // $
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_\\-?])(?=\\S+$).{8,}$";
        return Pattern.compile(passwordPattern).matcher(password).matches();
    }
}