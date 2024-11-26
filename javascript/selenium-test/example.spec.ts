import { Builder, By } from "selenium-webdriver";

describe('Selenium Test with TypeScript', () => {
    let driver: any;

    beforeAll(async () => {
        // Initialize the Chrome WebDriver
        driver = await new Builder().forBrowser('chrome').build();
    });

    afterAll(async () => {
        // Close the browser
        await driver.quit();
    });

    test('should navigate to example.com and check title', async () => {
        // Navigate to the website
        await driver.get('https://example.com');

        // Check the title
        const title = await driver.getTitle();
        expect(title).toBe('Example Domain');

        // Check if the heading is present
        const heading = await driver.findElement(By.tagName('h1'));
        const headingText = await heading.getText();
        expect(headingText).toBe('Example Domain');

        // Check if the link is present
        const link = await driver.findElement(By.linkText('More information...'));
        expect(await link.isDisplayed()).toBe(true);
    });
});