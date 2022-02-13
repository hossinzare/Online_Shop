package com.example.onlineshop.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.onlineshop.R;
import com.example.onlineshop.databinding.FragmentProductDetailsBinding;
import com.example.onlineshop.model.Image;
import com.example.onlineshop.model.Product;
import com.example.onlineshop.utils.adapters.ImageSliderAdapter;
import com.example.onlineshop.viewmodel.MainActivityViewModel;

import java.util.List;

public class ProductDetailsFragment extends Fragment {
    FragmentProductDetailsBinding binding;
    ProductDetailsFragmentArgs args;
    MainActivityViewModel viewModel;

    ImageSliderAdapter adapter = new ImageSliderAdapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_details, container, false);
        args = ProductDetailsFragmentArgs.fromBundle(getArguments());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);

        ViewPager viewPager = binding.detailsViewPager;
        viewPager.setAdapter(adapter);
        //TODO convert int to string
        viewModel.getDetails(Integer.parseInt(args.getId())).observe(getViewLifecycleOwner(), new Observer<Product>() {
            @Override
            public void onChanged(Product product) {
                binding.setModel(product);
            }
        });

        //TODO convert int to string
        viewModel.getImages(Integer.parseInt(args.getId())).observe(getViewLifecycleOwner(), new Observer<List<Image>>() {
            @Override
            public void onChanged(List<Image> images) {
adapter.setImages(images);
            }
        });

    }
}
