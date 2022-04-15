package com.example.onlineshop.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.onlineshop.NetworkStatus;
import com.example.onlineshop.R;
import com.example.onlineshop.model.Account;
import com.example.onlineshop.model.Attribute;
import com.example.onlineshop.model.CartItemModel;
import com.example.onlineshop.model.Category;
import com.example.onlineshop.model.Comment;
import com.example.onlineshop.model.Group;
import com.example.onlineshop.model.Image;
import com.example.onlineshop.model.Order;
import com.example.onlineshop.model.Product;
import com.google.gson.JsonObject;

import java.util.List;

import hu.akarnokd.rxjava3.retrofit.Result;
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
    private final Context context;

    private static AppDatabase databaseInstance;

    public Repository(Context context) {
        this.context = context;
        databaseInstance = AppDatabase.getInstance(context);


    }

    MutableLiveData<Integer> errorLiveData = new MutableLiveData<>(R.string.no_error);

    public MutableLiveData<Integer> getErrorLiveData() {
        return errorLiveData;
    }

    public void addError(Throwable e) {

        if (e instanceof java.net.ConnectException | e instanceof java.net.SocketTimeoutException) {
            //If we can connect to other sites(for example google) then the problem is from the server
            NetworkStatus.hasInternetConnection().subscribe(aBoolean -> {
                int error = aBoolean ? (R.string.server_connection_error) : (R.string.internet_connection_error);
                if (!errorLiveData.getValue().equals(error)) errorLiveData.setValue(error);
            });
        }
    }

    public static Repository getInstance(Context context) {
        if (repository == null) {
            repository = new Repository(context);
        }
        return repository;

    }


    public LiveData<String> submitComment(Comment comment, CompositeDisposable disposable) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        RetrofitInstance.getAPI().submitComment(comment).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<JsonObject>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Response<JsonObject> jsonObjectResponse) {
                        liveData.setValue(String.valueOf(jsonObjectResponse.code()));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }
                });

        return liveData;
    }

    public LiveData<String> submitOrder(Order order, CompositeDisposable disposable) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        RetrofitInstance.getAPI().submitOrder(order).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JsonObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);

                    }

                    @Override
                    public void onSuccess(@NonNull JsonObject jsonObject) {
                        liveData.setValue(jsonObject.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        addError(e);


                    }
                });
        return liveData;
    }

    public List<Product> getHistory() {
        return databaseInstance.historyDAO().getItems();
    }

    public void addHistoryItem(Product product) {

        databaseInstance.historyDAO().deleteItem(product.getName());

        if (databaseInstance.historyDAO().getItems().size() > 9) {
            databaseInstance.historyDAO().deleteFirstItem();
        }

        databaseInstance.historyDAO().insertItem(product);


    }


    public LiveData<List<CartItemModel>> getCartItems() {
        return databaseInstance.itemCartDao().getItems();
    }

    public boolean isItemExist(CartItemModel model) {
        List<String> items = databaseInstance.itemCartDao().isItemExist(model.getName());
        return items.size() > 0;
    }


    public boolean addCartItem(CartItemModel itemModel) {

        if (isItemExist(itemModel)) {

            return false;
        } else {
            databaseInstance.itemCartDao().insertItem(itemModel);

            return true;
        }


    }


    public void deleteCartItem(CartItemModel itemModel) {
        databaseInstance.itemCartDao().deleteItem(itemModel.getName());
    }


    public void increaseItemCount(CartItemModel model) {
        databaseInstance.itemCartDao().increaseItemCount(model.getName());

    }

    public void decreaseItemCount(CartItemModel model) {
        databaseInstance.itemCartDao().decreaseItemCount(model.getName());

    }


    public String getUserNumber(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.logged_in_shared_preferences), Context.MODE_PRIVATE);
        return sharedPreferences.getString(context.getString(R.string.logged_in_number_KEY), "0");
    }

    public String getUserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.logged_in_shared_preferences), Context.MODE_PRIVATE);
        return sharedPreferences.getString(context.getString(R.string.logged_in_name_KEY), "0");
    }


    public LiveData<List<Product>> searchProducts(String search_text) {
        MutableLiveData<List<Product>> liveData = new MutableLiveData<>();
        RetrofitInstance.getAPI().searchProducts(search_text).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Product>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<Product> products) {
                        liveData.setValue(products);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        addError(e);
                    }
                });
        return liveData;

    }

    public LiveData<List<Group>> getGroups() {
        MutableLiveData<List<Group>> liveData = new MutableLiveData<>();
        RetrofitInstance.getAPI().getGroups().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Group>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<Group> groups) {
                        liveData.setValue(groups);

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        addError(e);

                    }
                });
        return liveData;
    }

    public LiveData<List<Category>> getCategories(int groupID) {
        MutableLiveData<List<Category>> liveData = new MutableLiveData<>();
        RetrofitInstance.getAPI().getCategories(groupID).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Category>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<Category> categories) {
                        liveData.setValue(categories);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        addError(e);

                    }
                });
        return liveData;
    }

    public LiveData<Account> updateAccount(Account account) {
        MutableLiveData<Account> liveData = new MutableLiveData<>();
        RetrofitInstance.getAPI().updateAccount(account.getNumber(), account.getName(), account.getNumber(), account.getAddress(), account.getEmail(), account.getPassword())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Account>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Account account) {
                        liveData.setValue(account);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        addError(e);
                    }
                });


        return liveData;
    }

    public LiveData<Account> getAccountDetails(String number, CompositeDisposable disposable) {
        MutableLiveData<Account> liveData = new MutableLiveData<>();

        RetrofitInstance.getAPI().getAccountDetails(number)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Account>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Account account) {
                        liveData.setValue(account);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        addError(e);
                    }
                });
        return liveData;

    }

    public LiveData<List<Comment>> getComments(int id, CompositeDisposable disposable) {
        MutableLiveData<List<Comment>> liveData = new MutableLiveData<>();
        RetrofitInstance.getAPI().getComments(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Comment>>() {
                               @Override
                               public void onSubscribe(@NonNull Disposable d) {
                                   disposable.add(d);
                               }

                               @Override
                               public void onSuccess(@NonNull List<Comment> comments) {
                                   liveData.setValue(comments);
                               }

                               @Override
                               public void onError(@NonNull Throwable e) {
                                   addError(e);

                               }
                           }
                );
        return liveData;
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
                        addError(e);

                    }
                });

        return liveData;


    }

    public LiveData<List<Product>> getSameProducts(int id, CompositeDisposable disposable) {
        MutableLiveData<List<Product>> liveData = new MutableLiveData<>();

        RetrofitInstance.getAPI().getSameProducts(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Product>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);

                    }

                    @Override
                    public void onSuccess(@NonNull List<Product> products) {
                        liveData.setValue(products);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        addError(e);

                    }
                });


        return liveData;


    }

    public LiveData<List<Product>> getBestselling(CompositeDisposable disposable) {
        MutableLiveData<List<Product>> liveData = new MutableLiveData<>();

        RetrofitInstance.getAPI().getBestselling().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Product>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull List<Product> products) {
                        liveData.setValue(products);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        addError(e);
                    }
                });

        return liveData;
    }

    public LiveData<List<Product>> getSpecialDiscounts(CompositeDisposable disposable) {
        MutableLiveData<List<Product>> liveData = new MutableLiveData<>();

        RetrofitInstance.getAPI().GetSpecialDiscounts().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Product>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);

                    }

                    @Override
                    public void onSuccess(@NonNull List<Product> products) {
                        liveData.setValue(products);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        addError(e);

                    }
                });

        return liveData;
    }

    public LiveData<Product> getProduct(int id, CompositeDisposable disposable) {
        MutableLiveData<Product> liveData = new MutableLiveData<>();


        RetrofitInstance.getAPI().getProduct(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Product>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Product product) {
                        liveData.setValue(product);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        addError(e);
                    }
                });

        return liveData;

    }

    public LiveData<List<Attribute>> getProductAttributes(int id, CompositeDisposable disposable) {
        MutableLiveData<List<Attribute>> liveData = new MutableLiveData<>();

        RetrofitInstance.getAPI().getProductAttributes(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Attribute>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);

                    }

                    @Override
                    public void onSuccess(@NonNull List<Attribute> attributes) {
                        liveData.setValue(attributes);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        addError(e);

                    }
                });

        return liveData;

    }

    public LiveData<List<Product>> getAll(CompositeDisposable disposable) {
        MutableLiveData<List<Product>> productsList = new MutableLiveData<>();

        RetrofitInstance.getAPI().getAllItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Product>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull List<Product> homeItems) {
                        productsList.setValue(homeItems);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        addError(e);
                    }
                });
        return productsList;

    }

    public LiveData<List<Product>> getProductsByCategory(String category, CompositeDisposable disposable) {
        MutableLiveData<List<Product>> productsList = new MutableLiveData<>();

        RetrofitInstance.getAPI().getProductsByCategory(category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Product>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull List<Product> homeItems) {
                        productsList.setValue(homeItems);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        addError(e);
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
                        Log.i(TAG, "onSuccess: " + jsonObjectResponse.body());

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        addError(e);
                        Log.i(TAG, "onError: " + e.toString());
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
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Response<JsonObject> jsonObjectResponse) {
                        signupLiveData.setValue(jsonObjectResponse.code());
                        Log.i(TAG, "onSuccess: " + jsonObjectResponse.body());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        addError(e);
                        Log.i(TAG, "onError: " + e.toString());
                    }
                });

        return signupLiveData;
    }


    public LiveData<List<Product>> getProductsByGroup(String group, CompositeDisposable disposable) {
        MutableLiveData<List<Product>> liveData = new MutableLiveData<>();

        RetrofitInstance.getAPI().getProductsByGroup(group).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Product>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull List<Product> products) {
                        liveData.setValue(products);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        addError(e);
                    }
                });

        return liveData;

    }
}
