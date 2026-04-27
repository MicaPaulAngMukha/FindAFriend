package com.example.task_perf1;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class AccountCreationStep4Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_creation_step4, container, false);

        TextView termsTextView = view.findViewById(R.id.terms_text_view);
        CheckBox agreeCheckbox = view.findViewById(R.id.agree_checkbox);
        Button backButton = view.findViewById(R.id.back_button);
        Button nextButton = view.findViewById(R.id.next_button);
        ViewPager2 viewPager = getActivity().findViewById(R.id.view_pager);

        // This line enables HTML formatting
        termsTextView.setText(Html.fromHtml(getString(R.string.terms_and_conditions), Html.FROM_HTML_MODE_COMPACT));

        nextButton.setEnabled(false);

        agreeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            nextButton.setEnabled(isChecked);
        });

        backButton.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1));
        nextButton.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1));

        return view;
    }
}