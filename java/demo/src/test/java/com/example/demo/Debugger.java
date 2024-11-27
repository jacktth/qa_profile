package com.example.demo;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

@SpringBootTest
public class Debugger {

     private Playwright playwright;
     private Browser browser;
    @Value("${qa3.steam.account}")
    private String qa3SteamAccount;

    @Value("${qa3.steam.password}")
    private String qa3SteamPassword;

    @Test
    public void debugger() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false)
                        .setArgs(List.of("--start-maximized")));
        BrowserContext userContext = browser
                .newContext(new Browser.NewContextOptions().setViewportSize(1366, 768));
        Page userSteamPage = userContext.newPage();
        userSteamPage.navigate("https://store.steampowered.com/login");
        userSteamPage.fill(
                "xpath=//*[@id=\"responsive_page_template_content\"]/div[3]/div[1]/div/div/div/div[2]/div/form/div[1]/input",
                qa3SteamAccount);
        userSteamPage.fill(
                "xpath=/html/body/div[1]/div[7]/div[6]/div[3]/div[1]/div/div/div/div[2]/div/form/div[2]/input",
                qa3SteamPassword);
        userSteamPage
                .click("xpath=/html/body/div[1]/div[7]/div[6]/div[3]/div[1]/div/div/div/div[2]/div/form/div[4]/button"); // Submit
        userSteamPage.pause();
    }
}
