package com.app.smartpos.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.pos.PosActivity;
import com.app.smartpos.product.EditProductActivity;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class PosProductAdapter extends RecyclerView.Adapter<PosProductAdapter.MyViewHolder> {


    private List<HashMap<String, String>> productData;
    private Context context;
    MediaPlayer player;
    public static int count;


    public PosProductAdapter(Context context, List<HashMap<String, String>> productData) {
        this.context = context;
        this.productData = productData;
        player = MediaPlayer.create(context, R.raw.delete_sound);

    }


    @NonNull
    @Override
    public PosProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pos_product_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final PosProductAdapter.MyViewHolder holder, int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        final String product_id = productData.get(position).get("product_id");
        String name = productData.get(position).get("product_name");
        final String product_weight = productData.get(position).get("product_weight");
        final String product_stock = productData.get(position).get("product_stock");
        final String product_price = productData.get(position).get("product_sell_price");
        final String weight_unit_id = productData.get(position).get("product_weight_unit_id");
        String base64Image = productData.get(position).get("product_image");




        databaseAccess.open();
        final String weight_unit_name = databaseAccess.getWeightUnitName(weight_unit_id);

        holder.txtProductName.setText(name);

        //Low stock marked as RED color
        int getStock=Integer.parseInt(product_stock);
        if (getStock>5) {
            holder.txtStock.setText(context.getString(R.string.stock) + " : " + product_stock);
        }
        else
        {
            holder.txtStock.setText(context.getString(R.string.stock) + " : " + product_stock);
            holder.txtStock.setTextColor(Color.RED);

        }
        holder.txtWeight.setText(product_weight + " " + weight_unit_name);
        holder.txtPrice.setText(currency + product_price);


        holder.cardProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                player.start();
                Intent intent=new Intent(context, EditProductActivity.class);
                intent.putExtra("product_id",product_id);
                context.startActivity(intent);
            }
        });



        if (base64Image != null) {
            if (base64Image.length() < 6) {
                Log.d("64base", base64Image);
                holder.product_image.setImageResource(R.drawable.image_placeholder);
                holder.product_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {


                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.product_image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }


        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (getStock<=0)
                {

                    Toasty.warning(context, R.string.stock_is_low_please_update_stock, Toast.LENGTH_SHORT).show();
                }
                else {

                    Log.d("w_id", weight_unit_id);
                    databaseAccess.open();

                    int check = databaseAccess.addToCart(product_id, product_weight, weight_unit_id, product_price, 1, product_stock);

                    databaseAccess.open();
                    int count=databaseAccess.getCartItemCount();
                    if (count==0)
                    {
                        PosActivity.txtCount.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        PosActivity. txtCount.setVisibility(View.VISIBLE);
                        PosActivity.txtCount.setText(String.valueOf(count));
                    }

                    if (check == 1) {
                        Toasty.success(context, R.string.product_added_to_cart, Toast.LENGTH_SHORT).show();
                        player.start();
                    } else if (check == 2) {

                        Toasty.info(context, R.string.product_already_added_to_cart, Toast.LENGTH_SHORT).show();

                    } else {
                        Toasty.error(context, R.string.product_added_to_cart_failed_try_again, Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return productData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardProduct;
        TextView txtProductName,txtStock, txtWeight, txtPrice;
        Button btnAddToCart;
        ImageView product_image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtWeight = itemView.findViewById(R.id.txt_weight);
            txtPrice = itemView.findViewById(R.id.txt_price);
            txtStock = itemView.findViewById(R.id.txt_stock);
            product_image = itemView.findViewById(R.id.img_product);
            btnAddToCart = itemView.findViewById(R.id.btn_add_cart);
            cardProduct=itemView.findViewById(R.id.card_product);


        }
    }


}
