package com.kcirqueit.playandearn.viewModel;

import android.app.Application;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.kcirqueit.playandearn.model.PaymentRequest;
import com.kcirqueit.playandearn.repository.PaymentRequestsRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class PaymentRequestViewModel extends AndroidViewModel {

    private PaymentRequestsRepository paymentRequestsRepository;

    public PaymentRequestViewModel(@NonNull Application application) {
        super(application);
        paymentRequestsRepository = PaymentRequestsRepository.getInstance();
    }


    public Task addPaymentRequest(PaymentRequest paymentRequest) {
        return paymentRequestsRepository.addPaymentRequest(paymentRequest);
    }

    public LiveData<DataSnapshot> getPaymentRequestByUserId(String userId) {
        return paymentRequestsRepository.getPaymentRequestByUserId(userId);
    }



}
