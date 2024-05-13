package com.app.smartpos.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
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
import androidx.recyclerview.widget.RecyclerView;

import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseAccess;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {


    private List<HashMap<String, String>> cart_product;
    private Context context;
    MediaPlayer player;
    TextView txt_total_price, txt_no_product;
    public static Double total_price;
    Button btnSubmitOrder;
    ImageView imgNoProduct;
    DecimalFormat f;



    public CartAdapter(Context context, List<HashMap<String, String>> cart_product, TextView txt_total_price, Button btnSubmitOrder, ImageView imgNoProduct, TextView txt_no_product) {
        this.context = context;
        this.cart_product = cart_product;
        player = MediaPlayer.create(context, R.raw.delete_sound);
        this.txt_total_price = txt_total_price;
        this.btnSubmitOrder = btnSubmitOrder;
        this.imgNoProduct = imgNoProduct;
        this.txt_no_product = txt_no_product;
        f = new DecimalFormat("#0.00");

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_product_items, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();

        final String cart_id = cart_product.get(position).get("cart_id");
        String product_id = cart_product.get(position).get("product_id");
        String product_name = databaseAccess.getProductName(product_id);


        final String price = cart_product.get(position).get("product_price");
        final String weight_unit_id = cart_product.get(position).get("product_weight_unit");
        final String weight = cart_product.get(position).get("product_weight");
        final String qty = cart_product.get(position).get("product_qty");
        final String stock = cart_product.get(position).get("stock");

        int getStock=Integer.parseInt(stock);


      //  Log.d("unit_ID ", weight_unit_id);

        databaseAccess.open();
        String base64Image = databaseAccess.getProductImage(product_id);

        databaseAccess.open();
        String weight_unit_name = databaseAccess.getWeightUnitName(weight_unit_id);


        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        databaseAccess.open();
        total_price = databaseAccess.getTotalPrice();
        txt_total_price.setText(context.getString(R.string.total_price) + currency + f.format(total_price));


        if (base64Image != null) {
            if (base64Image.isEmpty() || base64Image.length() < 6) {
                holder.imgProduct.setImageResource(R.drawable.image_placeholder);
            } else {


                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.imgProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }



        final double getPrice = Double.parseDouble(price) * Integer.parseInt(qty);


        holder.txtItemName.setText(product_name);
        holder.txtPrice.setText(currency + f.format(getPrice));
        holder.txtWeight.setText(weight + " " + weight_unit_name);
        holder.txtQtyNumber.setText(qty);

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                boolean deleteProduct = databaseAccess.deleteProductFromCart(cart_id);

                if (deleteProduct) {
                    Toasty.success(context, context.getString(R.string.product_removed_from_cart), Toast.LENGTH_SHORT).show();

                    // Calculate Cart's Total Price Again
                    //  setCartTotal();


                    player.start();

                    //for delete cart item dynamically
                    // Remove CartItem from Cart List
                    cart_product.remove(holder.getAdapterPosition());

                    // Notify that item at position has been removed
                    notifyItemRemoved(holder.getAdapterPosition());


                    databaseAccess.open();
                    total_price = databaseAccess.getTotalPrice();
                    txt_total_price.setText(context.getString(R.string.total_price) + currency + f.format(total_price));


                } else {
                    Toasty.error(context, context.getString(R.string.failed), Toast.LENGTH_SHORT).show();
                }


                databaseAccess.open();
                int itemCount = databaseAccess.getCartItemCount();
                Log.d("itemCount", "" + itemCount);
                if (itemCount <= 0) {
                    txt_total_price.setVisibility(View.GONE);
                    btnSubmitOrder.setVisibility(View.GONE);

                    imgNoProduct.setVisibility(View.VISIBLE);
                    txt_no_product.setVisibility(View.VISIBLE);
                }

            }
        });


        holder.txtPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                String qty1 = holder.txtQtyNumber.getText().toString();
                int get_qty = Integer.parseInt(qty1);

                if (get_qty>=getStock)
                {
                    Toasty.error(context, context.getString(R.string.available_stock)+ " " +getStock, Toast.LENGTH_SHORT).show();
                }

                else {
                    get_qty++;


                    double cost = Double.parseDouble(price) * get_qty;


                    holder.txtPrice.setText(currency + f.format(cost));
                    holder.txtQtyNumber.setText("" + get_qty);


                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    databaseAccess.updateProductQty(cart_id, "" + get_qty);

                    total_price = total_price + Double.valueOf(price);
                    txt_total_price.setText(context.getString(R.string.total_price) + currency + f.format(total_price));

                }
            }
        });


        holder.txtMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String qty = holder.txtQtyNumber.getText().toString();
                int get_qty = Integer.parseInt(qty);


                if (get_qty >= 2) {
                    get_qty--;

                    double cost = Double.parseDouble(price) * get_qty;

                    holder.txtPrice.setText(currency + f.format(cost));
                    holder.txtQtyNumber.setText("" + get_qty);


                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    databaseAccess.updateProductQty(cart_id, "" + get_qty);

                    total_price = total_price - Double.valueOf(price);
                    txt_total_price.setText(context.getString(R.string.total_price) + currency + f.format(total_price));


                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return cart_product.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtItemName, txtPrice, txtWeight, txtQtyNumber, txtPlus, txtMinus;
        ImageView imgProduct, imgDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtItemName = itemView.findViewById(R.id.txt_item_name);
            txtPrice = itemView.findViewById(R.id.txt_price);
            txtWeight = itemView.findViewById(R.id.txt_weight);
            txtQtyNumber = itemView.findViewById(R.id.txt_number);
            imgProduct = itemView.findViewById(R.id.cart_product_image);
            imgDelete = itemView.findViewById(R.id.img_delete);
            txtMinus = itemView.findViewById(R.id.txt_minus);
            txtPlus = itemView.findViewById(R.id.txt_plus);

        }


    }


}
