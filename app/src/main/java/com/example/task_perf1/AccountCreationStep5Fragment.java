package com.example.task_perf1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.lifecycle.ViewModelProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountCreationStep5Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_creation_step5, container, false);

        Button loginButton = view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            // Add the signal to the Intent
            intent.putExtra("FROM_ACCOUNT_CREATION", true);
            sendAccountInfo();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    public void sendAccountInfo(){
        databaseHelper db = new databaseHelper(getContext());

        AccountCreationDataStorage ACA = new ViewModelProvider(requireActivity()).get(AccountCreationDataStorage.class);

        db.addNewUser(ACA.firstName,ACA.lastName,ACA.birthDate,ACA.username,ACA.password, ACA.interests);
    }
}