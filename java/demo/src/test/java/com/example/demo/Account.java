package com.example.demo;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

public class Account {

   

    public String userSteamAccount;
    public String userSteamPassword;
    public String userOutlookAccount;
    public String userOutlookPassword;
    public BrowserContext userContext;
    public Page userSteamPage;
    public Page userOutlookPage;

    public Account(String userSteamAccount, String userSteamPassword, String userOutlookAccount,
            String userOutlookPassword) {
        this.userSteamAccount = userSteamAccount;
        this.userSteamPassword = userSteamPassword;
        this.userOutlookAccount = userOutlookAccount;
        this.userOutlookPassword = userOutlookPassword;

    }
    // public String getUserSteamAccount() {
    // return userSteamAccount;
    // }

    // public String getUserSteamPassword() {
    // return userSteamPassword;
    // }
    // public String getUserOutlookAccount() {
    // return userOutlookAccount;
    // }
    // public String getUserOutlookPassword() {
    // return userOutlookPassword;
    // }
    // public BrowserContext getUserContext() {
    // return userContext;
    // }
    // public Page getUserSteamPage() {
    // return userSteamPage;
    // }
    // public Page getUserOutlookPage() {
    // return userOutlookPage;
    // }
    public void setUserContext(BrowserContext userContext) {
        this.userContext = userContext;
    }

    public void setUserSteamPage(Page userSteamPage) {
        this.userSteamPage = userSteamPage;
    }

}
