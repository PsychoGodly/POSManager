package com.app.smartpos;

public class Constant {

    Constant() {
        //write your action here if need
    }

    //We will use this to store the user token number into shared preference
    public static final String SHARED_PREF_NAME = "com.app.smartpos"; //pcakage name+ id


    public static final String ORDER_STATUS = "order_status";


    //order status
    public static final String PENDING = "Pending";
    public static final String PROCESSING = "Processing";
    public static final String COMPLETED = "Completed";
    public static final String CANCEL = "Cancel";


    //all table names
    public static String customers = "customers";
    public static String users = "users";
    public static String suppliers = "suppliers";
    public static String productCategory = "product_category";
    public static String products = "products";
    public static String paymentMethod = "payment_method";
    public static String expense = "expense";
    public static String productCart = "product_cart";
    public static String orderList = "order_list";
    public static String orderDetails = "order_details";


}
