package me.ymir.rr;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Bot {
    private WebDriver driver;
    private WebDriverWait wait, wait1;
    private boolean readyForUse = false;
    private CopyOnWriteArrayList<TimerTask> tasks = new CopyOnWriteArrayList<>();

    public Bot() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait1 = new WebDriverWait(driver, Duration.ofSeconds(1000));

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (readyForUse) {
                    if (!tasks.isEmpty()) {
                        TimerTask task = tasks.remove(0);
                        task.run();
                    }
                }
            }
        }, 0, 1000);
    }

    public Bot facebookLogin(String gmail, String password) {
        String url = "https://www.facebook.com/login.php?skip_api_login=1&api_key=1457231197822920&kid_directed_site=0&app_id=1457231197822920&signed_next=1&next=https%3A%2F%2Fwww.facebook.com%2Fdialog%2Foauth%3Fclient_id%3D1457231197822920%26redirect_uri%3Dhttps%253A%252F%252Frivalregions.com%252Fmain%252Ffblogin%26state%3D8e278bd642db4542fd5e4ec8b3e3f9f0%26ret%3Dlogin%26fbapp_pres%3D0%26logger_id%3Dfeb686c5-22da-4195-a4fc-531d858813da%26tp%3Dunspecified&cancel_url=https%3A%2F%2Frivalregions.com%2Fmain%2Ffblogin%3Ferror%3Daccess_denied%26error_code%3D200%26error_description%3DPermissions%2Berror%26error_reason%3Duser_denied%26state%3D8e278bd642db4542fd5e4ec8b3e3f9f0%23_%3D_&display=page&locale=tr_TR&pl_dbl=0";
        driver.navigate().to(url);

        WebElement gmailE = driver.findElement(By.id("email"));
        gmailE.clear();
        gmailE.sendKeys(gmail);

        WebElement passwordTextBox = driver.findElement(By.id("pass"));

        passwordTextBox.clear();
        passwordTextBox.sendKeys(password);

        WebElement passwordClick = driver.findElement(By.id("loginbutton"));
        passwordClick.click();

        wait1.until(ExpectedConditions.urlToBe("https://rivalregions.com/#_=_"));
        String html = driver.getPageSource();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("a[href]");
        String url1 = elements.get(7).attr("href");
        driver.navigate().to(url1);
        wait1.until(ExpectedConditions.urlToBe("https://rivalregions.com/#overview"));
        loginSuccesses();
        return this;
    }

    private void loginSuccesses() {
        driver.findElement(By.xpath("//*[@id=\"header_menu\"]/div[5]")).click();
        driver.navigate().to("https://rivalregions.com/#slide/profile");
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='margin']//h1")));
        String profile = driver.findElement(By.xpath("//div[@class='margin']//h1")).getText();
        String level = driver.findElement(By.xpath("//*[@id=\"header_slide_inner\"]/div[5]/div[1]/div[1]/div[2]")).getText();
        String money = driver.findElement(By.xpath("//*[@id=\"m\"]")).getText();
        String gold = driver.findElement(By.xpath("//*[@id=\"g\"]")).getText();
        String stats = driver.findElement(By.xpath("//*[@id=\"header_slide_inner\"]/div[5]/div[3]/table/tbody/tr[2]/td[2]")).getText();
        System.out.println(Color.BLUE_BOLD + " *************** account *************** ");
        System.out.println(Color.BLUE_BOLD_BRIGHT + profile);
        System.out.println(Color.BLUE_BOLD_BRIGHT + level);
        System.out.println(Color.BLUE_BOLD + "Money: " + Color.BLUE_BOLD_BRIGHT + money);
        System.out.println(Color.BLUE_BOLD + "Gold: " + Color.BLUE_BOLD_BRIGHT + gold);
        System.out.println(Color.BLUE_BOLD + "Stats: " + Color.BLUE_BOLD_BRIGHT + stats);
        System.out.println(Color.BLUE_BOLD + " *************** account *************** ");
        readyForUse = true;
    }

    public void sendMessageActivePlayers(int goal, String message) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (readyForUse) {
                    runSend(goal, message);
                    readyForUse = false;
                    this.cancel();
                }
            }
        };
        queue(task);
    }

    public void sendNationMessage(String text, String locale) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (readyForUse) {
                    sendLocaleMessage(text, locale);
                    readyForUse = false;
                    this.cancel();
                }
            }
        };
        queue(task);
    }

    private void sendLocaleMessage(String text, String locale) {
        System.out.println(Color.YELLOW_BOLD + "Collecting players...");
        String tmp = "https://rivalregions.com/listed/nation/" + locale + "/";

        ArrayList<String> ids = new ArrayList<>();
        int found = 0;
        for (int i = 25; true; i += 25) {
            driver.navigate().to(tmp + i);
            driver.navigate().refresh();
            String source = driver.getPageSource();
            Elements elements = Jsoup.parse(source).select("img");
            if (elements.size() < 5) break;
            for (Element element : elements) {
                String[] args = element.attr("src").split("/");
                String[] idArgs = args[args.length - 1].split("_");
                if (idArgs.length < 2) continue;
                String id = idArgs[0].trim();//element.attr("user");
                System.out.println(Color.WHITE + id);
                if (!ids.contains(id)) {
                    ids.add(id);
                    found++;
                }
            }
        }
        System.out.println(found + " players collected...");
        sendOperation(ids, text, found, true);
    }

    public void sendVoters(String text, String articleID, boolean deleteBefore) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (readyForUse) {
                    sendVotedPlayers(text, articleID, deleteBefore);
                    readyForUse = false;
                    this.cancel();
                }
            }
        };
        queue(task);
    }

    private void sendVotedPlayers(String text, String articleID, boolean deleteBefore) {
        String tmp = "https://rivalregions.com/#news/votes/" + articleID + "/";
        List<String> ids = new ArrayList<>();
        driver.navigate().to("https://rivalregions.com/#news/show/" + articleID);
        driver.navigate().refresh();
        for (int i = 1; true; i++) {
            try {
                driver.navigate().to(tmp + i);
                driver.navigate().refresh();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[3]/div/div[3]/div/div/div[1]/table/thead/tr/th[2]/span")));
                for (Element element : Jsoup.parse(driver.getPageSource()).select("tr")) {
                    ids.add(element.attr("user"));
                }
            } catch (TimeoutException ignored) {
                break;
            }
        }
        sendOperation(ids, text, ids.size(), deleteBefore);
    }

    private void sendOperation(List<String> ids, String text, int goal, boolean deleteBefore) {
        String messageUrl = "https://rivalregions.com/#messages/";
        readyForUse = false;
        System.out.println(Color.YELLOW_BOLD_BRIGHT + "**Message Send Operation**");
        System.out.println("Message sending has started...");

        int succes = 0;
        for (String player : ids) {
            if (player.equalsIgnoreCase("") || !player.matches("[0-9]*")) continue;
            try {
                driver.navigate().to(messageUrl + player);
                driver.navigate().refresh();
                wait.until(ExpectedConditions.elementToBeClickable(By.id("chat_send")));
                if (deleteBefore) {
                    new Actions(driver).moveToElement(driver.findElement(By.xpath("//*[@id=\"chat_links_1 no_pointer\"]/div[2]"))).click().perform();
                    driver.navigate().to(messageUrl + player);
                    driver.navigate().refresh();
                    wait.until(ExpectedConditions.elementToBeClickable(By.id("chat_send")));
                }
                WebElement msg = driver.findElement(By.xpath("//*[@id=\"message\"]"));
                msg.clear();
                msg.sendKeys(text);
                WebElement button = driver.findElement(By.id("chat_send"));
                button.click();
                ++succes;
                System.out.println(Color.WHITE_BOLD_BRIGHT + (succes + "/" + goal));
            } catch (TimeoutException ignored) {
            }
            if(goal<=succes) break;
        }
        System.out.println(Color.GREEN_BOLD_BRIGHT + (succes + " message send!"));
        readyForUse = true;
    }


    public void createArticle(String message) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (readyForUse) {
                    runArticle(message);
                    readyForUse = false;
                    this.cancel();
                }
            }
        };
        queue(task);
    }

    private void queue(TimerTask task) {
        tasks.add(task);
    }

    private void sendOperation(List<String> ids, String text, int goal) {
        sendOperation(ids, text, goal, true);
    }

    private void runSend(int goal, String message) {
        System.out.println(Color.YELLOW_BOLD_BRIGHT + "**Message Send Operation**");
        System.out.println(Color.YELLOW_BOLD + "Collecting players...");
        driver.get("https://rivalregions.com/listed/country");
        String countriesHtml = driver.getPageSource();
        ArrayList<String> ids = new ArrayList<>();
        int found = 0;
        for (Element element : Jsoup.parse(countriesHtml).select("tr")) {
            String ci = element.attr("user");
            driver.navigate().to("https://rivalregions.com/listed/online/" + ci);
            String onlineHtml = driver.getPageSource();
            Elements onlineElements = Jsoup.parse(onlineHtml).select("tr");
            for (Element element1 : onlineElements) {
                String id = element1.attr("user");
                if (!id.matches("[0-9]*")) continue;
                if (!ids.contains(id)) {
                    ids.add(id);
                    found++;
                }
            }
            if (found > goal) break;
        }

        System.out.println(found + " players collected...");
        sendOperation(ids, message, goal);
    }

    private void runArticle(String content) {
        driver.navigate().to("https://rivalregions.com/#news/write");
        driver.navigate().refresh();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"news_mark\"]")));
        driver.findElement(By.xpath("//*[@id=\"news_mark\"]")).sendKeys(content);
        driver.findElement(By.xpath("//*[@id=\"markItUpNews_mark\"]/div/div[1]/div/input")).sendKeys("Bu makale 0 puanda.");
        new Actions(driver).moveToElement(driver.findElement(By.xpath("//*[@id=\"header_slide_inner\"]/div[3]/div[7]/div"))).click().perform();
        wait.until(ExpectedConditions.urlContains("https://rivalregions.com/#news/show"));
    }

    public void updateArticleName(String article) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (readyForUse) {
                    runArticleName(article);
                    readyForUse = false;
                    this.cancel();
                }
            }
        };
        queue(task);
    }

    private void runArticleName(String article) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    driver.navigate().to(article);
                    driver.navigate().refresh();
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"news_number\"]")));
                    String plus = driver.findElement(By.xpath("//*[@id=\"news_number\"]")).getText();
                    new Actions(driver).moveToElement(driver.findElement(By.xpath("//*[@id=\"content\"]/div[1]/h1/div/span[2]"))).click().perform();
                    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"markItUpNews_mark\"]/div/div[1]/div/input")));
                    WebElement a = driver.findElement(By.xpath("//*[@id=\"markItUpNews_mark\"]/div/div[1]/div/input"));
                    a.clear();
                    a.sendKeys("Bu makale " + plus + " puanda!");
                    new Actions(driver).moveToElement(driver.findElement(By.xpath("//*[@id=\"header_slide_inner\"]/div[3]/div[4]"))).click().perform();
                } catch (TimeoutException ignored) {

                }
            }
        }, 0, 30000);

    }

}
