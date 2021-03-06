package com.example.onlineshop.view.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.onlineshop.R;
import com.example.onlineshop.databinding.FragmentLoginBinding;
import com.example.onlineshop.model.User;
import com.example.onlineshop.view.MainActivity;
import com.example.onlineshop.viewmodel.LoginSignupViewModel;
import com.example.onlineshop.viewmodel.LoginSignupViewModelFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Response;


public class LoginFragment extends Fragment {

    FragmentLoginBinding binding;
    LoginSignupViewModel viewModel;
    Context context;
    public static final String TAG = "LoginFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.context = getActivity();
        LoginFragmentEventListener loginFragmentEventListener = new LoginFragmentEventListener();

        if (getActivity() != null)
            viewModel = new ViewModelProvider(getActivity(), new LoginSignupViewModelFactory(getActivity())).get(LoginSignupViewModel.class);

        binding.setEventListener(loginFragmentEventListener);
        binding.setViewModel(viewModel);


    }

    public void showMessage(String code, User user) {

        String message;

        switch (code) {
            case "212":
                message = "???????? ???????? ???????? ?????? ??????????";
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                SharedPreferences sharedPreferences =
                        context.getApplicationContext().getSharedPreferences(context.getString(R.string.logged_in_shared_preferences), Context.MODE_PRIVATE);

                sharedPreferences.edit().putString(context.getString(R.string.logged_in_number_KEY), user.getNumber()).apply();
                sharedPreferences.edit().putString(context.getString(R.string.logged_in_name_KEY), user.getName()).apply();
//                sharedPreferences.edit().putString(context.getString(R.string.logged_in_name_KEY), "hossein").apply();

                Log.i(TAG, "showMessage:user "+sharedPreferences.getString(context.getString(R.string.logged_in_name_KEY), "0"));
                Log.i(TAG, "showMessage:number "+sharedPreferences.getString(context.getString(R.string.logged_in_number_KEY), "0"));
                context.startActivity(new Intent(context, MainActivity.class));
                ((Login_Signup_Activity) context).finish();
                break;
            case "213":
                message = "?????????? ???? ?????? ?????????? ???????? ??????????.";
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                break;

            case "214":
                message = "?????? ???????? ???????????? ??????.";
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                break;
            case "220":
                message = "???????? ???? ?????????????? ??????????";
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                break;
            default:
                message = "Unexpected value: " + code;
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }

    }

    public class LoginFragmentEventListener {

        public void onSignInClick(View view, LoginSignupViewModel viewModel) {

            if (viewModel.isSigningFormValid()) viewModel.login().observe((LifecycleOwner) context,
                    new Observer<Response<JsonObject>>() {
                        @Override
                        public void onChanged(Response<JsonObject> response) {

                            Log.i(TAG, "onChanged: user :::: "+response.body().toString());
                            showMessage(String.valueOf(response.code()),
                                    new Gson().fromJson(response.body(),User.class));
                        }
                    });
        }

        public void LoginToSignupFragment(View view) {
            Navigation.findNavController(view).navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment());

        }


    }


}
