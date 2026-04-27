package com.example.task_perf1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class AccountCreationStep3Fragment extends Fragment {

    private int selectedChipCount = 0;
    private static final int MAX_CHIPS = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_creation_step3, container, false);

        ChipGroup chipGroup = view.findViewById(R.id.interest_chip_group);
        Button backButton = view.findViewById(R.id.back_button);
        Button nextButton = view.findViewById(R.id.next_button);
        TextView skipButton = view.findViewById(R.id.skip_button);
        ViewPager2 viewPager = getActivity().findViewById(R.id.view_pager);

        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            chip.setOnClickListener(v -> {
                if (chip.isChecked()) {
                    if (selectedChipCount < MAX_CHIPS) {
                        selectedChipCount++;
                    } else {
                        chip.setChecked(false);
                        Toast.makeText(getContext(), "You can only select up to 5 interests.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    selectedChipCount--;
                }
            });
        }

        backButton.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1));

        nextButton.setOnClickListener(v -> {
            String[] selectedInterests = new String[selectedChipCount];
            int index = 0;

            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    selectedInterests[index] = chip.getText().toString();
                    index++;
                }
            }

            AccountCreationDataStorage ACA = new ViewModelProvider(requireActivity()).get(AccountCreationDataStorage.class);
            ACA.interests = selectedInterests;

            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        });

        skipButton.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1));

        return view;
    }
}