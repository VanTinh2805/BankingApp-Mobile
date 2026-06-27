package com.example.proiectmobilebanking;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.proiectmobilebanking.network.model.UserInfo;

public class SharedPreferencesUser {
    private final static String SHARED_PREF_NAME = "sharedPrefName";
    private final static String IS_LOGGED = "is_logged";
    private final static String SET_USER = "user";
    private final static String TOKEN = "token";
    private final static String EMAIL = "email";
    private final static String NAME = "name";
    private final static String CARD_NUMBER = "card_number";
    private final static String BANK = "bank";
    private final static String BALANCE = "balance";

    private final SharedPreferences preferences;

    public SharedPreferencesUser(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public void isLogged(boolean status) {
        preferences.edit().putBoolean(IS_LOGGED, status).apply();
    }

    public void setUser(long id) {
        preferences.edit().putLong(SET_USER, id).apply();
    }

    public long getUser() {
        return preferences.getLong(SET_USER, 0);
    }

    public void setToken(String token) {
        preferences.edit().putString(TOKEN, token).apply();
    }

    public String getToken() {
        return preferences.getString(TOKEN, "");
    }

    public String getAuthorizationHeader() {
        String token = getToken();
        if (token == null || token.isEmpty()) {
            return "";
        }
        return "Bearer " + token;
    }

    public void saveUserInfo(UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(NAME, valueOrEmpty(userInfo.getName()));
        editor.putString(EMAIL, valueOrEmpty(userInfo.getEmail()));
        editor.putString(CARD_NUMBER, valueOrEmpty(userInfo.getCardNumber()));
        editor.putString(BANK, valueOrEmpty(userInfo.getBank()));
        editor.putString(BALANCE, userInfo.getBalance() == null ? "" : String.valueOf(userInfo.getBalance()));
        editor.apply();
    }

    public void setEmail(String email) {
        preferences.edit().putString(EMAIL, email).apply();
    }

    public String getEmail() {
        return preferences.getString(EMAIL, "");
    }

    public void setName(String name) {
        preferences.edit().putString(NAME, name).apply();
    }

    public String getName() {
        return preferences.getString(NAME, "");
    }

    public void setCardNumber(String cardNumber) {
        preferences.edit().putString(CARD_NUMBER, cardNumber).apply();
    }

    public String getCardNumber() {
        return preferences.getString(CARD_NUMBER, "");
    }

    public void setBank(String bank) {
        preferences.edit().putString(BANK, bank).apply();
    }

    public String getBank() {
        return preferences.getString(BANK, "");
    }

    public void setBalance(Double balance) {
        preferences.edit().putString(BALANCE, balance == null ? "" : String.valueOf(balance)).apply();
    }

    public String getBalance() {
        return preferences.getString(BALANCE, "");
    }

    public void clearSession() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(IS_LOGGED);
        editor.remove(SET_USER);
        editor.remove(TOKEN);
        editor.remove(EMAIL);
        editor.remove(NAME);
        editor.remove(CARD_NUMBER);
        editor.remove(BANK);
        editor.remove(BALANCE);
        editor.apply();
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
