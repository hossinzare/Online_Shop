package com.example.onlineshop.utils.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlineshop.R;
import com.example.onlineshop.databinding.CartItemCardBinding;
import com.example.onlineshop.model.CartItemModel;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {
    CartItemCardBinding binding;
    List<CartItemModel> cartItemModels = new ArrayList<>();


OnCartProductData onCartProductData;

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.cart_item_card, parent, false);
        return new CartHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        CartItemModel model = cartItemModels.get(position);
        binding.setModel(model);
    }


    @Override
    public int getItemCount() {
        return cartItemModels.size();
    }


    public class CartHolder extends RecyclerView.ViewHolder {
        CartItemCardBinding binding;

        public CartHolder(@NonNull CartItemCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onCartProductData!=null && getAdapterPosition()!=RecyclerView.NO_POSITION){
                        onCartProductData.onAddClickListener(getAdapterPosition());


                    }

                }
            });

            binding.minesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(onCartProductData!=null && getAdapterPosition()!=RecyclerView.NO_POSITION){
                        onCartProductData.onReduceClickListener(getAdapterPosition());


                    }

                }
            });

            binding.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onCartProductData!=null && getAdapterPosition()!=RecyclerView.NO_POSITION){

                        onCartProductData.onDeleteClickListener(getAdapterPosition());


                    }
                }
            });

        }
    }

    public void setCartItemModels(List<CartItemModel> cartItemModels) {
        this.cartItemModels = cartItemModels;

        notifyDataSetChanged();
    }

    public interface OnCartProductData{
        void onAddClickListener(int position);
        void onReduceClickListener(int position);
        void onDeleteClickListener(int position);

    }

    public void setOnCartProductData(OnCartProductData onCartProductData) {
        this.onCartProductData = onCartProductData;
    }
}