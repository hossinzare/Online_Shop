package com.example.onlineshop.utils;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.onlineshop.model.HomeItem;
import com.example.onlineshop.model.Image;
import com.example.onlineshop.model.Product;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Response;

public class Repository {

    private static Repository repository = null;
    public static final String TAG = "repository";

    public Repository() {


    }

    public static final Repository getInstance() {
        if (repository == null) {
            repository = new Repository();
        }

        return repository;

    }

    public LiveData<List<Image>> getImages(int id, CompositeDisposable disposable) {
        MutableLiveData<List<Image>> liveData = new MutableLiveData<>();
        RetrofitInstance.getAPI().getImages(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Image>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull List<Image> images) {
                        liveData.setValue(images);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });

        return liveData;


    }

    public LiveData<Product> getDetails(int id, CompositeDisposable disposable) {
        MutableLiveData<Product> liveData = new MutableLiveData<>();

        Log.i(TAG, "getDetails: log ok");

        RetrofitInstance.getAPI().getDetails(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Product>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Product product) {
                        Log.i(TAG, "onSuccess: "+product.getName());
                        liveData.setValue(product);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i(TAG, "onError: "+e.getMessage());
                    }
                });

        return liveData;

    }

    public LiveData<List<HomeItem>> getAll(CompositeDisposable disposable) {
        MutableLiveData<List<HomeItem>> productsList = new MutableLiveData<>();

        RetrofitInstance.getAPI().getAllItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<HomeItem>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull List<HomeItem> homeItems) {
                        productsList.setValue(homeItems);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
                    }
                });
        return productsList;

    }

    public LiveData<Integer> login(String number, String password, CompositeDisposable disposable) {
        MutableLiveData<Integer> loginLiveData = new MutableLiveData<>();


        RetrofitInstance.getAPI().login(number, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<JsonObject>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Response<JsonObject> jsonObjectResponse) {
                        loginLiveData.setValue(jsonObjectResponse.code());

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });


        return loginLiveData;


    }

    public LiveData<Integer> signup(String number, String password, String name, CompositeDisposable disposable) {
        MutableLiveData<Integer> signupLiveData = new MutableLiveData<>();
        RetrofitInstance.getAPI().signup(name, number, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<JsonObject>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.i(TAG, "onSubscribe: " + d.toString());
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Response<JsonObject> jsonObjectResponse) {
                        signupLiveData.setValue(jsonObjectResponse.code());
                        Log.i(TAG, "onSuccess: " + jsonObjectResponse.code());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
                    }
                });

        return signupLiveData;
    }


}