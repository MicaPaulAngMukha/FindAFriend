package com.example.task_perf1;

import androidx.lifecycle.ViewModel;

//this class is needed to store data so it won't disappear. ViewModel and .requireActivity makes it
//so that it doesn't forget the passed on memory
public class AccountCreationDataStorage extends ViewModel {
    String firstName = "";
    String lastName = "";
    String birthDate = "";
    String username = "";
    String password = "";
    String[] interests = new String[5];
}
