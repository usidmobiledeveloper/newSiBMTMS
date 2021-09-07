package com.usid.mobilebmt.mandirisejahtera.registrasi;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.usid.mobilebmt.mandirisejahtera.model.User;
import com.usid.mobilebmt.mandirisejahtera.repository.AuthRepository;
import com.usid.mobilebmt.mandirisejahtera.repository.Resource;


public class RegistrasiViewModel extends ViewModel {

    private AuthRepository loginRepo;
    private MutableLiveData<Resource<User>> mutableLiveData;


    public RegistrasiViewModel() {
        loginRepo = new AuthRepository();
    }

    public LiveData<Resource<User>> getLogin(String datetime) {
        if (mutableLiveData == null) {
            mutableLiveData = loginRepo.requestLogin(datetime);
        }
        return mutableLiveData;
    }

    public LiveData<Resource<User>> relogin(String datetime) {
        if (mutableLiveData == null) {
            mutableLiveData = loginRepo.requestReLogin(datetime);
        }
        return mutableLiveData;
    }


}