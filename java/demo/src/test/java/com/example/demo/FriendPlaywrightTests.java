package com.example.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.MouseButton;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
public class FriendPlaywrightTests {

    static private Playwright playwright;
    static private Browser browser;
    static private List<Account> accounts;

    @Value("${qa1.steam.account}")
    public String qa1SteamAccount;

    @Value("${qa1.steam.password}")
    public String qa1SteamPassword;

    @Value("${qa1.outlook.account}")
    public String qa1OutlookAccount;

    @Value("${qa1.outlook.password}")
    public String qa1OutlookPassword;

    @Value("${qa2.steam.account}")
    public String qa2SteamAccount;

    @Value("${qa2.steam.password}")
    public String qa2SteamPassword;

    @Value("${qa2.outlook.account}")
    public String qa2OutlookAccount;

    @Value("${qa2.outlook.password}")
    public String qa2OutlookPassword;

    @Value("${qa3.steam.account}")
    public String qa3SteamAccount;

    @Value("${qa3.steam.password}")
    public String qa3SteamPassword;

    @Value("${qa3.outlook.account}")
    public String qa3OutlookAccount;

    @Value("${qa3.outlook.password}")
    public String qa3OutlookPassword;

    @Value("${qa4.steam.account}")
    public String qa4SteamAccount;

    @Value("${qa4.steam.password}")
    public String qa4SteamPassword;

    @Value("${qa4.outlook.account}")
    public String qa4OutlookAccount;

    @Value("${qa4.outlook.password}")
    public String qa4OutlookPassword;

    @BeforeAll
    public void setUbgp() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false)
                        .setArgs(List.of("--start-maximized")));

        accounts = new ArrayList<>();
        // Account 1
        // accounts.add(new Account(qa1SteamAccount, qa1SteamPassword,
        // qa1OutlookAccount, qa1OutlookPassword));
        // Account 2
        // accounts.add(new Account(qa2SteamAccount, qa2SteamPassword,
        // qa2OutlookAccount, qa2OutlookPassword));

        // initializeSteamAccountsLoginByOutlook("https://steamcommunity.com/chat/",
        // accounts);
        // --------------------------For no validation code
        // login//--------------------------
        // Account 3
        accounts.add(new Account(qa3SteamAccount, qa3SteamPassword,
        qa3OutlookAccount,qa3OutlookPassword));
        // // Account 4
        accounts.add(new Account(qa4SteamAccount, qa4SteamPassword,
        qa4OutlookAccount, qa4OutlookPassword));

        initializeSteamAccountsWithNoEmail("https://steamcommunity.com/chat/", accounts);
        //For check init states for testing are correct
        for (Account account : accounts) {
            account.userSteamPage.bringToFront();
            account.userSteamPage.pause();
        }
    }

    public void initializeSteamAccountsLoginByOutlook(String pathToGoAfterInit, List<Account> accounts) {
        if (accounts.size() > 0) {

            for (Account account : accounts) {
                BrowserContext userContext = browser
                        .newContext(new Browser.NewContextOptions().setViewportSize(null));
                userContext.setDefaultTimeout(10000);
                account.setUserContext(userContext);
                Page steamPage = loginToSteam(account.userSteamAccount,
                        account.userSteamPassword, account.userOutlookPage,
                        account.userOutlookAccount,
                        account.userOutlookPassword, userContext);
                account.setUserSteamPage(steamPage);
                // need to wait 5s before the next action otherwise login unsuccessful
                account.userSteamPage.waitForTimeout(6000);
                account.userSteamPage.navigate("https://steamcommunity.com/");
                account.userSteamPage.click("#account_pulldown");
                account.userSteamPage.hover("#account_language_pulldown");
                ;
                account.userSteamPage.click("a[href='?l=english']");

                account.userSteamPage.waitForTimeout(5000);
                account.userSteamPage.navigate(pathToGoAfterInit);
                account.userSteamPage.waitForTimeout(8000);

            }
        } else {
            System.err.println("No account found in the List<Account>");
        }
    }

    public void initializeSteamAccountsWithNoEmail(String pathToGoAfterInit, List<Account> accounts) {
        if (accounts.size() > 0) {

            for (Account account : accounts) {
                BrowserContext userContext = browser
                        .newContext(new Browser.NewContextOptions().setViewportSize(null));
                userContext.setDefaultTimeout(10000);
                account.setUserContext(userContext);
                Page userSteamPage = userContext.newPage();

                userSteamPage.bringToFront();
                userSteamPage.navigate("https://store.steampowered.com/login");
                userSteamPage.fill(
                        "xpath=//*[@id=\"responsive_page_template_content\"]/div[3]/div[1]/div/div/div/div[2]/div/form/div[1]/input",
                        account.userSteamAccount);
                userSteamPage.fill(
                        "xpath=/html/body/div[1]/div[7]/div[6]/div[3]/div[1]/div/div/div/div[2]/div/form/div[2]/input",
                        account.userSteamPassword);
                userSteamPage
                        .click("xpath=/html/body/div[1]/div[7]/div[6]/div[3]/div[1]/div/div/div/div[2]/div/form/div[4]/button"); // Submit
                // button
                userSteamPage.waitForTimeout(5000);
                account.setUserSteamPage(userSteamPage);
                // need to wait 5s before the next action otherwise login unsuccessful
                account.userSteamPage.waitForTimeout(6000);
                account.userSteamPage.navigate("https://steamcommunity.com/");
                account.userSteamPage.click("#account_pulldown");
                account.userSteamPage.hover("#account_language_pulldown");
                ;
                account.userSteamPage.click("a[href='?l=english']");
                account.userSteamPage.waitForTimeout(5000);
                account.userSteamPage.navigate(pathToGoAfterInit);

                account.userSteamPage.waitForTimeout(8000);
            }
        } else {
            System.err.println("No account found in the List<Account>");
        }
    }

    public Page loginToSteam(String userSteamAccount, String userSteamPassword,
            Page userOutlookPage, String userOutlookAccount, String userOutlookPassword,
            BrowserContext userContext) {

        Page userSteamPage = userContext.newPage();
        userOutlookPage = userContext.newPage();

        userSteamPage.bringToFront();
        userSteamPage.navigate("https://store.steampowered.com/login");
        userSteamPage.fill(
                "xpath=//*[@id=\"responsive_page_template_content\"]/div[3]/div[1]/div/div/div/div[2]/div/form/div[1]/input",
                userSteamAccount);
        userSteamPage.fill(
                "xpath=/html/body/div[1]/div[7]/div[6]/div[3]/div[1]/div/div/div/div[2]/div/form/div[2]/input",
                userSteamPassword);
        userSteamPage
                .click("xpath=/html/body/div[1]/div[7]/div[6]/div[3]/div[1]/div/div/div/div[2]/div/form/div[4]/button"); // Submit
        // button
        userSteamPage.waitForTimeout(5000);
        String userSteamCode = getValidationCodeFromOutlook(userOutlookPage, userOutlookAccount,
                userOutlookPassword);
        submitValidationCode(userSteamCode, userSteamPage);
        return userSteamPage;
    }

    public String getValidationCodeFromOutlook(Page page, String outlookAccount, String outlookPassword) {
        page.bringToFront();
        page.navigate(
                "https://login.live.com/login.srf?wa=wsignin1.0&rpsnv=165&ct=1731469307&rver=7.5.2211.0&wp=MBI_SSL&wreply=https%3a%2f%2foutlook.live.com%2fowa%2f%3fnlp%3d1%26cobrandid%3dab0455a0-8d03-46b9-b18b-df2f57b9e44c%26deeplink%3dowa%252f%253frealm%253doutlook.com%26RpsCsrfState%3db0a8aeb3-16da-087b-c863-d01129188f76&id=292841&aadredir=1&CBCXT=out&lw=1&fl=dob%2cflname%2cwld&cobrandid=ab0455a0-8d03-46b9-b18b-df2f57b9e44c");
        page.waitForTimeout(5000);
        // input account
        page.fill(
                "xpath=/html/body/div[1]/div/div/div/div[2]/div[1]/div/div/div/div[1]/div[2]/div/div/div/form/div[2]/div/div/input",
                outlookAccount);
        page.click(
                "xpath=/html/body/div[1]/div/div/div/div[2]/div[1]/div/div/div/div[1]/div[2]/div/div/div/form/div[4]/div/div/div/div/button");
        // input account password and submit
        page.fill(
                "xpath=/html/body/div[1]/div/div/div/div[2]/div[1]/div/div/div/div/div[2]/div[2]/div/form/div[3]/div/div/input",
                outlookPassword);
        page.click(
                "xpath=/html/body/div[1]/div/div/div/div[2]/div[1]/div/div/div/div/div[2]/div[2]/div/form/div[5]/div/div/div/div/button");
        // Reject auto login
        page.click(
                "xpath=/html/body/div[1]/div/div/div/div[2]/div[1]/div/div/div/div/div[2]/div[2]/div/form/div[3]/div[2]/div/div[1]/button");

        // focus to input
        page.click(
                ".rclHC");
        // input steam support email
        page.fill(
                ".uz227.GgHzM",
                "noreply@steampowered.com");
        // select search result of steam support
        page.click("#searchSuggestion-0");
        // select the top email from the list
        page.waitForTimeout(3000);

        page.locator(".jGG6V.gDC9O").first().click();
        // get the code
        String code = page.locator(".x_title-48.x_c-blue1.x_fw-b.x_a-center").innerText();
        return code;

    }

    // need to have error handling while wrong code gotten
    public void submitValidationCode(String code, Page steamPage) {
        steamPage.bringToFront();

        for (int i = 0; i < code.length(); i++) {
            char currentChar = code.charAt(i);
            String xpathSelector = String.format(
                    "xpath=/html/body/div[1]/div[7]/div[6]/div[3]/div[1]/div/div/div/div[2]/form/div/div[2]/div[1]/div/input[%d]",
                    i + 1);
            steamPage.fill(xpathSelector, "" + currentChar);

        }
    }

    @Test
    @Order(1)
    public void viewCheck() {
        List<String> elementSelectors = Arrays.asList(
                "dropTargetBox", // Replace with actual selectors
                "friendlist",
                "FriendsListContent ",
                "groupList",
                "ChatRoomListContainerParent");
        List<SelectorForText> elementsToTest = Arrays.asList(
                new SelectorForText(".groupChatSectionTitle", "GROUP CHATS"), // Replace with actual
                // selectors and
                new SelectorForText(".emptyChatDialogs", "Click a Friend or Group Chat to start!"));
        for (Account account : accounts) {
            Page page = account.userSteamPage;
            elementSelectors.forEach(selector -> {
                try {
                    Locator element = page.locator("." + selector);
                    assertTrue(element.count() > 0);
                } catch (NoSuchElementException e) {
                    assertTrue(false, "Element not found: " + selector);
                }
            });

        }
        for (Account account : accounts) {
            Page page = account.userSteamPage;
            page.waitForTimeout(5000);
            page.bringToFront();
            for (SelectorForText elementData : elementsToTest) {
                try {
                    Locator element = page.locator(elementData.selector);
                    assertEquals(element.innerText(), elementData.expectedText,
                            "Text should match for element: " + elementData.selector);
                } catch (NoSuchElementException e) {
                    assertTrue(false, "Element not found: " + elementData.selector);
                }
            }
            ;

        }

    }

    @Test
    @Order(2)
    public void testFriendsOnlineStatus() {
        Account qa_1 = accounts.get(0);
        Account qa_2 = accounts.get(1);
        String qa1UserName = qa_1.userSteamAccount;
        String qa2UserName = qa_2.userSteamAccount;
        Page user1Page = qa_1.userSteamPage;
        Page user2Page = qa_2.userSteamPage;

        Locator onlineFriendsList = user1Page.locator(".onlineFriends");
        String onlineFriendsListText = onlineFriendsList.textContent();
        // Check if the text "Online Friends" is present
        assertTrue(onlineFriendsListText.contains("Online Friends"),
                "'Online Friends' is displayed");

        Locator friendCategoryContainer = user1Page.locator(".friendCategoryContainer ");
        Locator onlineFriendInsidefriendCategoryContainer = friendCategoryContainer.locator(".friend.online");
        String qa2UserNameInList = onlineFriendInsidefriendCategoryContainer.textContent();

        assertTrue(qa2UserNameInList.contains(qa2UserName),
                "Testing friend account online");

        // Test 32
        changeOnlineStatus("Away", user1Page, user2Page, qa1UserName);
        // Test 33
        changeOnlineStatus("Invisible", user1Page, user2Page, qa1UserName);

        // Test 34
        changeOnlineStatus("Online", user1Page, user2Page, qa1UserName);

        // -------------------------User2 under "Do Not Disturb" to receive voice chat-------------------------!
        user2Page.bringToFront();

        user2Page.locator(".ContextMenuButton > .SVGIcon_Button").first().click();
        user2Page.getByText("Do Not Disturb").click();

        assertTrue(user2Page.getByTitle("Do Not Disturb").locator("#Layer_1").count() == 1,
                "Icon of 'Do Not Disturb' appears");
        user1Page.bringToFront();
        String regex = "^" + qa2UserName + "Online$";

        Locator onlineFriend = user1Page.locator("div")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile(regex))).nth(2);

        onlineFriend.click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
        user1Page.getByText("Start Voice Chat").click();
        Locator rejectCrossButtonInUser2 = user2Page.getByTitle("Decline");

        user2Page.waitForTimeout(5000);
        user2Page.bringToFront();

        user2Page.waitForTimeout(2000);

        assertTrue(rejectCrossButtonInUser2.count() == 0,
                "No notification of voice chat invitation");
        //Catch unexpected behaviour
        // if (user1Page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("End Voice Chat")).count() > 0) {
        //     user1Page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("End Voice Chat")).click();

        // }
        user2Page.locator(".ContextMenuButton > .SVGIcon_Button").first().click();
        user2Page.getByText("Do Not Disturb").click();
        user2Page.waitForTimeout(3000);

    }

    void changeOnlineStatus(String changedStatus, Page user1page, Page user2page, String qa1UserName) {
        user1page.bringToFront();
        Locator avatar = user1page.locator(".AvatarAndUser");
        Locator contextMenuButtonInAvatar = avatar.locator(".ContextMenuButton");

        // Click the context menu button to open the status options
        contextMenuButtonInAvatar.click();

        // Locate the list of online status options
        Locator onlineStatusList = user1page.locator(".contextMenuSectionContent");

        // Use Changedstatus to find the appropriate status option
        Locator statusOption = onlineStatusList.locator("div:has-text('" + changedStatus + "')");

        // Check if the status option exists and click it
        if (statusOption.count() > 0) {
            statusOption.first().click(); // Click the first matching element

            System.out.println("Clicked on the status: " + changedStatus);
        } else {
            System.out.println("No element with the text '" + changedStatus + "' was found.");
        }
        if (changedStatus == "Invisible") {
            user2page.bringToFront();
            user2page.waitForTimeout(5000);
            // Locator offlineTitleContainer =
            Locator clickExpandElement = user2page.locator(".DropTarget.friendGroup.offlineFriends");
            Locator containerOfExpandButton = clickExpandElement
                    .locator(".groupName.Collapsed.Panel.Focusable");
            containerOfExpandButton.click();

            Locator offlineFriendsList = user2page.locator(".offlineFriends");

            user1page.bringToFront();
            //Ensure no online user browser opened outside text browser
            user1page.getByText("Offline").click();
            Locator offlineQA1 = offlineFriendsList.locator(
                    "div.personanameandstatus_playerName_nOdcT:has-text('" + qa1UserName + "')");
            String offlineFriendsListText = offlineQA1.textContent();
            // Check if the text "Offline Friends" is present
            assertTrue(offlineFriendsListText.contains(qa1UserName),
                    "'Offline Friends' is displayed");

            // try to locate area and then locat element with class and text
            Locator friendCategoryContainer = user2page.locator(".friendCategoryContainer ");
            Locator offlineFriendInsidefriendCategoryContainer = friendCategoryContainer
                    .locator(".friend.offline");
            String qa2UserNameInList = offlineFriendInsidefriendCategoryContainer.textContent();

            assertTrue(qa2UserNameInList.contains(qa1UserName),
                    "Testing friend account offline");
        } else if (changedStatus == "Away") {
            // Locate the avatar and the context menu button
            user2page.bringToFront();
            System.out.println("Locate the avatar and the context menu button");

            Locator friendCategoryContainerOfQA2TextContent = user2page
                    .locator(".friend.online.awayOrSnooze.friendStatusHover.Panel.Focusable");
            String onlineStatusContent = friendCategoryContainerOfQA2TextContent
                    .locator(".personanameandstatus_richPresenceContainer_3sxE7").textContent();

            assertTrue(onlineStatusContent.contains(changedStatus),
                    "Testing friend account '" + changedStatus + "'");

        } else {
            // Locate the avatar and the context menu button
            user2page.bringToFront();
            System.out.println("Locate the avatar and the context menu button");

            Locator friendCategoryContainerOfQA2TextContent = user2page
                    .locator(".friend.online.friendStatusHover.Panel.Focusable");
            String onlineStatusContent = friendCategoryContainerOfQA2TextContent
                    .locator(".personanameandstatus_richPresenceContainer_3sxE7").textContent();

            assertTrue(onlineStatusContent.contains(changedStatus),
                    "Testing friend account '" + changedStatus + "'");
        }

    }

    @Test
    @Order(3)
    void testUserMenu() {
        Account qa_1 = accounts.get(0);
        Account qa_2 = accounts.get(1);
        String qa2UserName = qa_2.userSteamAccount;
        // String qa2UserName = "qa_final4";
        Page user1Page = qa_1.userSteamPage;
        user1Page.bringToFront();
        // -------------------------Click Add to Favorites from online friend
        // list-------------------------
        Locator onlineFriend = user1Page.locator(".friendCategoryContainer");
        onlineFriend.click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
        Locator menu = user1Page
                .locator(".contextmenu_contextMenu_PP7LM.visible.contextmenu_ContextMenuPosition_2yAm5")
                .nth(0);
        Locator manageOption = menu.locator(".contextmenu_Label_2qdHl:has-text('Manage')");
        manageOption.hover();
        Locator subMenu = user1Page
                .locator(".contextmenu_contextMenu_PP7LM.visible.contextmenu_ContextMenuPosition_2yAm5")
                .nth(1);
        Locator subMenuContainer = subMenu.locator(".contextmenu_contextMenuContents_2EstN");
        Locator addFavoriteOption = subMenuContainer.locator("div")
                .filter(new Locator.FilterOptions().setHasText("Add to Favorites"));
        addFavoriteOption.click();

        // String regex = String.format("^%sOnline$", qa2UserName);
        Locator userInEEA = user1Page.locator(".favoriteElement");
        assertTrue(userInEEA.count() > 0,
                "User is in EEA");
        user1Page.waitForTimeout(5000);

        // -------------------------Click Remove from Favorites in
        // EEA-------------------------
        // Locator userInEEA = user1Page.locator("div").filter(new
        // Locator.FilterOptions().setHasText(Pattern.compile(regex))).nth(2);
        userInEEA.click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
        manageOption.hover();
        user1Page.getByText("Remove from Favorites").click();
        user1Page.waitForTimeout(5000);

        assertTrue(userInEEA.count() == 0,
                "Esay access area dismissed");

    }

    @Test
    @Disabled
    void testDragToAddFavorite() {
        Account qa_1 = accounts.get(0);
        Page user1Page = qa_1.userSteamPage;
        user1Page.bringToFront();
        // Drag Drop
        Locator onlineFriend = user1Page.locator(".friendCategoryContainer");

        Locator dropTarget = user1Page.getByText("Drag Friends & Chats here for");
        onlineFriend.dragTo(dropTarget);
        user1Page.waitForTimeout(5000);

        // Remove it for next test
        Locator userInEEA = user1Page.locator(".favoriteElement");
        Locator menu = user1Page
                .locator(".contextmenu_contextMenu_PP7LM.visible.contextmenu_ContextMenuPosition_2yAm5")
                .nth(0);

        Locator manageOption = menu.locator(".contextmenu_Label_2qdHl:has-text('Manage')");

        userInEEA.click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
        manageOption.hover();
        user1Page.getByText("Remove from Favorites").click();

    }

    @Test
    @Order(4)
    @Disabled
    void testDragToRemoveFavorite() {
        Account qa_1 = accounts.get(0);
        // Account qa_2 = accounts.get(1);
        // String qa2UserName = qa_2.userSteamAccount;
        // String qa2UserName = "qa_final4";
        Page user1Page = qa_1.userSteamPage;
        user1Page.bringToFront();
        // -------------------------Click Add to Favorites from online friend
        // list-------------------------
        Locator onlineFriend = user1Page.locator(".friendCategoryContainer");
        onlineFriend.click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
        Locator menu = user1Page
                .locator(".contextmenu_contextMenu_PP7LM.visible.contextmenu_ContextMenuPosition_2yAm5")
                .nth(0);
        Locator manageOption = menu.locator(".contextmenu_Label_2qdHl:has-text('Manage')");
        manageOption.hover();
        Locator subMenu = user1Page
                .locator(".contextmenu_contextMenu_PP7LM.visible.contextmenu_ContextMenuPosition_2yAm5")
                .nth(1);
        Locator subMenuContainer = subMenu.locator(".contextmenu_contextMenuContents_2EstN");
        Locator addFavoriteOption = subMenuContainer.locator("div")
                .filter(new Locator.FilterOptions().setHasText("Add to Favorites"));
        addFavoriteOption.click();

        // Drag favorite out EEA
        Locator dragTarget = user1Page.locator(".favoriteElement");
        Locator dropTarget = user1Page.getByText("Any group chats you are part");

        dragTarget.dragTo(dropTarget);
        Locator onlineUserInEEA = user1Page.locator(".favoriteElement");
        assertTrue(onlineUserInEEA.count() == 0,
                "User got removed from favorite");
        assertFalse(onlineUserInEEA.count() > 0,
                "Esay access area dismissed");
        user1Page.waitForTimeout(5000);

    }

    @Test
    @Order(5)
    void testSetNickName() {
        Account qa_1 = accounts.get(0);
        Account qa_2 = accounts.get(1);
        String qa2UserName = qa_2.userSteamAccount;
        // String qa2UserName = "qa_final4";
        Page user1Page = qa_1.userSteamPage;
        String nickName = "TestName";
        user1Page.bringToFront();
        // -------------------------Add Nickname-------------------------

        String regex = "^" + qa2UserName + "Online$";
        Locator onlineFriend = user1Page.locator("div")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile(regex))).nth(2);
        onlineFriend.click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
        user1Page.getByText("Manage").hover();
        user1Page.getByText("Add Nickname").click();
        user1Page.getByPlaceholder("Enter a nickname").click();
        user1Page.getByPlaceholder("Enter a nickname").fill(nickName);
        user1Page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm")).click();
        user1Page.waitForTimeout(5000);

        Locator changedName = user1Page.getByText("TestName").first();
        assertTrue(changedName.count() == 1,
                "Nickname is added");
        //// -------------------------Remove Nickname//-------------------------
                String regex2 = "^" + nickName + " \\*Online$";
        user1Page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile(regex2))).nth(2)
                .click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
        user1Page.getByText("Manage").hover();
        user1Page.getByText("Change Nickname").click();
        user1Page.waitForTimeout(2000);
        user1Page.getByRole(AriaRole.DIALOG).locator("line").nth(3).click();
        user1Page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm")).click();
        user1Page.waitForTimeout(5000);
        assertTrue(changedName.count() == 0,
                "Nickname is removed");

    }

    @Test
    @Order(6)
    @Disabled
    void testSendVoiceChatInvitation() {
        Account qa_1 = accounts.get(0);
        Account qa_2 = accounts.get(1);
        Page user1Page = qa_1.userSteamPage;
        Page user2Page = qa_2.userSteamPage;
        String qa2UserName = "qa_final4";

        user1Page.bringToFront();

        // -------------------------Click Add to Favorites from online friend
        // list-------------------------
        String regex = "^" + qa2UserName + "Online$";
        Locator onlineFriend = user1Page.locator("div")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile(regex))).nth(2);
        onlineFriend.click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
        user1Page.waitForTimeout(1000);

        user1Page.getByText("Start Voice Chat").click();
        user1Page.waitForTimeout(5000);

        Locator cancelButtonInUser1 = user1Page.getByTitle("End Voice Chat");
        assertTrue(cancelButtonInUser1.count() == 1,
                "Sending invitation");

        //// -------------------------User 2 page and receive
                //// call-------------------------
                user2Page.bringToFront();
        Locator rejectButtonInUser2 = user2Page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Decline")); // Same function but different element,
        // for asserting
        Locator rejectCrossButtonInUser2 = user2Page.getByTitle("Decline");

        assertTrue(rejectCrossButtonInUser2.count() == 1,
                "Received call");

        rejectButtonInUser2.click();
        user1Page.waitForTimeout(5000);

        assertTrue(rejectCrossButtonInUser2.count() == 0,
                "Call rejected");

        //// -------------------------User 1 page and got rejected
                //// call-------------------------
                user1Page.bringToFront();
        assertTrue(cancelButtonInUser1.count() == 0,
                "Sending invitation");

    }

    @Test
    @Order(7)
    void testSendMessage() {
        Account qa_1 = accounts.get(0);
        Account qa_2 = accounts.get(1);
        Page user1Page = qa_1.userSteamPage;
        Page user2Page = qa_2.userSteamPage;
        String qa1UserName = qa_1.userSteamAccount;
        String qa2UserName = qa_2.userSteamAccount;
        String testMessage;

        user1Page.bringToFront();

        // -------------------------Open dialog box in User1-------------------------
        String regex = "^" + qa2UserName + "Online$";
        Locator onlineFriend = user1Page.locator("div")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile(regex))).nth(2);
        onlineFriend.dblclick();
        user1Page.waitForTimeout(5000);

        Locator chatTab = user1Page.locator(".chattabs_ChatTab_2Vrcp");

        assertTrue(chatTab.count() == 1,
                "Dialog box opened");

        // -------------------------User1 sends message and display the message in
        // User1's dialog box-------------------------
        Locator user1MessageInput = user1Page.getByRole(AriaRole.TEXTBOX);
        LocalDateTime now = LocalDateTime.now();
        // Define a formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the current date and time to a string
        String formattedDateTime = now.format(formatter);
        user1Page.bringToFront();

        testMessage = "test " + formattedDateTime;
        user1MessageInput.fill(testMessage);
        user1Page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit")).click();

        assertTrue(user1Page.getByText(testMessage).count() == 1,
                "Sent message successfully displayed in user 1");

        // -------------------------User2 received the message-------------------------
        user2Page.bringToFront();
        user2Page.waitForTimeout(3000);

        assertTrue(user2Page.getByText(testMessage).count() == 1,
                "User 2 successfully received the message sent bu User 1");

    }

    @Test
    @Order(8)
    void testGroupChat() {
        Account qa_1 = accounts.get(0);
        Account qa_2 = accounts.get(1);
        Page user1Page = qa_1.userSteamPage;
        Page user2Page = qa_2.userSteamPage;
        String groupName = "testName";

        user1Page.bringToFront();

        // -------------------------Create group-------------------------
        user1Page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create a group chat"))
                .click();
        user1Page.getByLabel("Chat Name").fill(groupName);
        user1Page.getByRole(AriaRole.DIALOG).getByText(qa_2.userSteamAccount).click();
        user1Page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create chat")).click();
        // Member list
        user1Page.waitForTimeout(3000);
        user1Page.dblclick(".ChatRoomListGroupItem_header");
        user1Page.waitForTimeout(2000);

        Locator sideGroupChatMemberList = user1Page.locator(".ChatRoomMemberScrollList_List > div > div");
        assertTrue(sideGroupChatMemberList.count() == 1, "Member list exists");

        // Group notification bell icon
        Locator bellIcon = user1Page.getByTitle("Manage notification settings").locator("path");
        assertTrue(bellIcon.count() == 1, "Bell icon exists");

        // 2 Group members' group title container
        Locator groupTitleContainer = user1Page.getByText(groupName + "2 " + "members2");
        assertTrue(groupTitleContainer.count() == 1, "Group bar title container exists");

        // Ban group mate
        Locator groupmateDropIcon = user1Page.locator(
                "div:nth-child(3) > .friend > .labelHolder > .personanameandstatus_statusAndName_4ZTzG > .ContextMenuButton > .SVGIcon_Button");
        groupmateDropIcon.click();
        user1Page.getByText("Ban").click();
        user1Page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Ban")).click();

        // User 2 got notification of banned
        user2Page.bringToFront();
        user1Page.waitForTimeout(3000);

        String expectedBannedNotification = "You were banned from chatroom " + "'" + groupName
                + "'";

        assertTrue(user2Page.getByText(expectedBannedNotification).count() == 1,
                "Group bar title container exists");
        user2Page.click(".DialogButton._DialogLayout.Primary.Focusable");

        // user 1 leave the group
        user1Page.bringToFront();

        user1Page.locator("div")
                .filter(new Locator.FilterOptions()
                        .setHasText(Pattern.compile("^Group ChatstNtestName$")))
                .getByRole(AriaRole.IMG).nth(2).click();
        user1Page.getByText("Leave Group Chat").click();

        user1Page.waitForTimeout(2000);

        user1Page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Leave Group Chat")).click();

        user1Page.waitForTimeout(2000);

        assertTrue(user1Page.getByText(groupName).first().count() == 0, "Has been left the group");
    }

    @AfterAll
    public static void tearDown() {

        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
