package com.example.task_perf1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import java.util.Calendar;

public class AccountCreationStep1Fragment extends Fragment {

    private EditText birthDateInput;
    private Button nextButton;
    private Calendar birthDate = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_creation_step1, container, false);

        Button backButton = view.findViewById(R.id.back_button);
        nextButton = view.findViewById(R.id.next_button);
        birthDateInput = view.findViewById(R.id.birth_date_input);
        ViewPager2 viewPager = getActivity().findViewById(R.id.view_pager);

        // Initially disable the next button
        nextButton.setEnabled(false);
        nextButton.setAlpha(0.5f); // Make it look gray

        birthDateInput.setOnClickListener(v -> showDatePicker());

        backButton.setOnClickListener(v -> {
            // Show the quit confirmation dialog
            new QuitConfirmationDialogFragment().show(getParentFragmentManager(), "QuitConfirmationDialog");
        });

        nextButton.setOnClickListener(v -> {
            AccountCreationDataStorage ACA = new ViewModelProvider(requireActivity()).get(AccountCreationDataStorage.class);

            EditText firstNameInput = view.findViewById(R.id.first_name_input);
            EditText lastNameInput = view.findViewById(R.id.last_name_input);
            EditText birthDateInput = view.findViewById(R.id.birth_date_input);

            String firstName = firstNameInput.getText().toString();
            String lastName = lastNameInput.getText().toString();
            String birthDate = birthDateInput.getText().toString();

            if (firstName.isEmpty() || lastName.isEmpty() || birthDate.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                ACA.firstName = firstName;
                ACA.lastName = lastName;
                ACA.birthDate = birthDate;

                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        return view;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) -> {
            birthDate.set(year1, month1, dayOfMonth);
            String dateString = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            birthDateInput.setText(dateString);
            
            checkAgeRequirement();
        }, year, month, day);

        datePickerDialog.show();
    }

    private void checkAgeRequirement() {
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // Adjust if birthday hasn't happened yet this year
        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        if (age >= 18) {
            nextButton.setEnabled(true);
            nextButton.setAlpha(1.0f); // Restore full color
            birthDateInput.setError(null);
        } else {
            nextButton.setEnabled(false);
            nextButton.setAlpha(0.5f); // Keep it gray
            birthDateInput.setError("You must be at least 18 years old");
        }
    }
}