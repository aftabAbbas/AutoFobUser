package com.idialogics.autofobuser.Utils;


import java.util.HashMap;

public class Constants {
    public static final int SPLASH_TIME_OUT = 1000;
    public static final String KEY_PREFERENCE_NAME = "AutofobAdmin";
    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_DP = "dp";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_EMAIL = "email";
    public static final String ID = "id";
    public static final String STATUS = "status";
    public static final String PHONE = "phone";
    public static final int PICK_CONTACT = 1;
    public static final String BANNERS = "banners";
    public static final String SHIPPED = "Shipped";
    public static final String ADMIN = "admin";
    public static final String USER_TYPE = "userType";
    public static final String MANUFECTURER = "manufacturer";
    public static final String MANUFACTURER_INTENT = "manufacturerIntent";
    public static final String PRODUCT_DETAIL_INTENT = "productDetailIntent";
    public static final String MODEL_INTENT = "modelIntent";
    public static final String MODEL = "model";
    public static final String PARENT_ID = "parentId";
    public static final String YEAR = "year";
    public static final String PRODUCT_INTENT = "productIntent";
    public static final String ADD_PRODUCT_INTENT = "addProductIntent";
    public static final String PRODUCTS = "products";
    public static final String EDIT_PRODUCT_INTENT = "editProduct";
    public static final String USER = "user";
    public static final String CART = "cart";
    public static final String PENDING = "Pending";
    public static final String ORDERS = "orders";
    public static final String TYPE = "type";
    public static final String FROM_NOTIFICATION = "fromNotifications";
    public static final String NOTIFICATION = "notifications";
    public static final String USER_ID = "userId";
    public static final String CANCELED = "Canceled";
    public static final String COMPLETED = "Completed";
    public static final String CHECK_IS_NOTIFICATION_ON = "isNotificationOn";
    public static final String IS_MULTIPLE = "isMultiple";
    public static final String BUSINESS_NAME = "businessName";
    public static final String BUY = "buy";
    public static final String BILLING_INFO = "billingInfo";
    public static final String PRICE = "price";
    public static String App_Version = "App Version";
    public static final String VERSION = "version";
    public static final String APP_VERSION_CODE = "1";
    public static final CharSequence EMPTY_ERROR = "Field cannot be empty";
    public static final CharSequence NOT_VALID_ADDRESS = "Invalid Email address";
    public static final String EMAIL_VALID_REGEX = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";


    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String KEY_BODY = "body";
    public static final String KEY_TITLE = "title";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";


    public static HashMap<String, String> getRemoteMessageHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(
                Constants.REMOTE_MSG_AUTHORIZATION,
                "key=AAAAlh7RXHs:APA91bHMRhraufy5ZY6tpQXvoYoQEcIwkGj_hFliRbKK8-QNldOHOUjtVpD1ZH6rcXbOAnSJ_UrqlsxZZmDWn7cOpQ8u_rCEzPZZw8p5k5LW5b7Y8iQ6ZisApW3sKWMQYUX3hijLb0uh"
        );
        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE, "application/json");
        return headers;
    }
}
