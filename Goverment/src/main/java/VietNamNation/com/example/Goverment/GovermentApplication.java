package VietNamNation.com.example.Goverment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class GovermentApplication {

    public static String command = "chrome.exe --remote-debugging-port=9222 --user-data-dir=C:\\Users\\User\\Desktop\\100\\localhost";

    public static void main(String[] args) throws TelegramApiException {
        SpringApplication.run(GovermentApplication.class, args);
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new Controller());


    }
}

@RestController
class Controller extends TelegramLongPollingBot {
    boolean check = false;
    static private int number_line;
    static private String save_image;
    static private String save_Name;
    static private String path = "C:\\Users\\User\\Downloads\\";

    @Override
    public String getBotUsername() {
        return "iamCEO_bot";
    }

    @Override
    public String getBotToken() {
        return "5141271688:AAExL4TDDjIKO7N0QBdRELKOh0zNoxTZQ6U";
    }


    /**
     * Origin method
     */
    @RequestMapping(path = "") // old name = "/cap"
    @Override
    public void onUpdateReceived(Update update) {
        // not work
    }



    /**
     * Phần đang hoạt động bình thường ổn định
     */
    @RequestMapping(path = "/{name}/Symbol/{id}")
    public void onUpdateReceived_Test(@PathVariable String name, @PathVariable int id) {
        save_Name = name ; // do not delete
        check = true;
        SendPhoto sendPhoto = new SendPhoto();
        if (check == true) {
            try {
                check = false;

                System.setProperty("java.awt.headless", "false"); // important line , do not delete this
                System.setProperty("webdriver.chrome.silentOutput", "true");
                System.setProperty("webdriver.chrome.driver", "C:\\Users\\User\\Desktop\\100\\chromedriver.exe");

                WebDriver driver = new ChromeDriver();

                driver.manage().window().maximize();
                driver.get("https://vn.tradingview.com/chart/");
                Thread.sleep(4000); // origin = 2000

                driver.findElement(By.xpath("//*[@id=\"header-toolbar-symbol-search\"]")).click();
                Thread.sleep(2000);

                driver.findElement(By.xpath("//*[@id=\"overlap-manager-root\"]/div/div/div[2]/div/div[2]/div[1]/input")).sendKeys(name);


                Thread.sleep(1000);
                number_line = id;
                String pick_Symbol = "//*[@id=\"overlap-manager-root\"]/div/div/div[2]/div/div[4]/div/div/div[" + number_line + "]/div[2]";
                Thread.sleep(2000);
                driver.findElement(By.xpath(pick_Symbol)).click();
                Thread.sleep(1000);


                Thread.sleep(2000);
                driver.findElement(By.xpath("//*[@id=\"header-toolbar-screenshot\"]")).click();

                Thread.sleep(2000);
                driver.findElement(By.xpath("//*[@id=\"overlap-manager-root\"]/div/span/div[1]/div/div/div[1]")).click();
                System.out.println("download");
                Thread.sleep(5000);

                image();

                sendPhoto.setChatId(String.valueOf(-622518101)); // id group chat String.valueOf(-622518101)
                sendPhoto.setPhoto(new InputFile(new File(String.valueOf(save_image))));
                sendPhoto.setCaption(save_image);
                this.execute(sendPhoto);
                driver.close();

            } catch (TelegramApiException e) {
                e.printStackTrace();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
    }



    /**
     * Phần đang gặp lỗi "Cannot download with headless"
     */
    @RequestMapping(path = "/{name1}/Symbol/{id1}")
    public void onUpdateReceived_Test1(@PathVariable String name1, @PathVariable int id1) {
        save_Name = name1 ; // do not delete
        check = true;
        SendPhoto sendPhoto = new SendPhoto();
        if (check == true) {
            try {
                check = false;

                System.setProperty("java.awt.headless", "false"); // important line , do not delete this
                System.setProperty("webdriver.chrome.silentOutput", "true");
                System.setProperty("webdriver.chrome.driver", "C:\\Users\\User\\Desktop\\100\\chromedriver.exe");

                ChromeOptions options = new ChromeOptions();

                options.addArguments("--test-type");
                options.addArguments("--headless");
                options.addArguments("--disable-extensions"); //to disable browser extension popup

                ChromeDriverService driverService = ChromeDriverService.createDefaultService();
                ChromeDriver driver = new ChromeDriver(driverService, options);

                Map<String, Object> commandParams = new HashMap<>();
                commandParams.put("cmd", "Page.setDownloadBehavior");
                Map<String, String> params = new HashMap<>();
                params.put("behavior", "allow");
                params.put(path, path);
                commandParams.put("params", params);
                ObjectMapper objectMapper = new ObjectMapper();
                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                String command = objectMapper.writeValueAsString(commandParams);
                String u = driverService.getUrl().toString() + "/session/" + driver.getSessionId() + "/chromium/send_command";
                HttpPost request = new HttpPost(u);
                request.addHeader("png", "application/json");
                request.setEntity(new StringEntity(command));
                httpClient.execute(request);

                //WebDriver driver = new ChromeDriver();

                driver.manage().window().maximize();
                driver.get("https://vn.tradingview.com/chart/");
                Thread.sleep(4000); // origin = 2000

                driver.findElement(By.xpath("//*[@id=\"header-toolbar-symbol-search\"]")).click();
                Thread.sleep(2000);

                driver.findElement(By.xpath("//*[@id=\"overlap-manager-root\"]/div/div/div[2]/div/div[2]/div[1]/input")).sendKeys(name1);


                Thread.sleep(1000);
                number_line = id1;
                String pick_Symbol = "//*[@id=\"overlap-manager-root\"]/div/div/div[2]/div/div[4]/div/div/div[" + number_line + "]/div[2]";
                Thread.sleep(2000);
                driver.findElement(By.xpath(pick_Symbol)).click();
                Thread.sleep(1000);


                Thread.sleep(2000);
                driver.findElement(By.xpath("//*[@id=\"header-toolbar-screenshot\"]")).click();

                Thread.sleep(2000);
                driver.findElement(By.xpath("//*[@id=\"overlap-manager-root\"]/div/span/div[1]/div/div/div[1]")).click();
                System.out.println("download");
                Thread.sleep(5000);

                image();

                sendPhoto.setChatId(String.valueOf(-622518101)); // id group chat String.valueOf(-622518101)
                sendPhoto.setPhoto(new InputFile(new File(String.valueOf(save_image))));
                sendPhoto.setCaption(save_image);
                this.execute(sendPhoto);
                driver.close();

            } catch (TelegramApiException e) {
                e.printStackTrace();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void image() {
        System.out.println("image method");
        File directory = new File("C:\\Users\\User\\Downloads");
        ArrayList<String> arrayList = new ArrayList<>();

        String[] flist = directory.list();
        int flag = 0;
        if (flist == null) {
            System.out.println("Empty directory.");
        } else {
            for (int i = 0; i < flist.length; i++) {
                String filename = flist[i];
                if (filename.endsWith("png") && filename.startsWith(save_Name)) {
                    arrayList.add(filename);
                    flag = 1;
                }
            }
        }
        if (flag == 0) {
            System.out.println("File Not Found");
        }
        Collections.reverse(arrayList);
        save_image = path + arrayList.get(0);
        System.out.println(save_image);
        flist = null;
        arrayList.clear();
    }
}

