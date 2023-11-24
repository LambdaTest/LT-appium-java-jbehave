package com.lambdatest;

import java.io.FileReader;
import java.net.URL;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.lang.reflect.Constructor;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import org.jbehave.core.embedder.Embedder;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.Parameter;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

@RunWith(Parameterized.class)
public class LambdaTestJBehaveRunner {

    public WebDriver driver;
    // private Local l;

    private static JSONObject config;

    @Parameter(value = 0)
    public int taskID;

    @Parameters
    public static Collection<Object[]> data() throws Exception {
        List<Object[]> taskIDs = new ArrayList<Object[]>();
        if (System.getProperty("config") != null) {
            JSONParser parser = new JSONParser();
            config = (JSONObject) parser.parse(new FileReader("src/test/resources/conf/" + System.getProperty("config")));
            int envs = ((JSONArray) config.get("environments")).size();

            for (int i = 0; i < envs; i++) {
                taskIDs.add(new Object[]{i});
            }
        }

        return taskIDs;
    }

    @Before
    public void setUp() throws Exception {
        JSONArray envs = (JSONArray) config.get("environments");
        String username = System.getenv("LT_USERNAME") == null ? "YOUR_LT_USERNAME" : System.getenv("LT_USERNAME");  //Replace YOUR_LT_USERNAME with your LambdaTest username

        String accessKey = System.getenv("LT_ACCESS_KEY") == null ? "YOUR_LT_ACCESS_KEY" : System.getenv("LT_ACCESS_KEY"); //Replace YOUR_LT_ACCESS_KEY with your LambdaTest accessKey

        String app_id = System.getenv("LT_APP_ID") == null ? "lt://proverbial-android" : System.getenv("LT_APP_ID");      //Enter your LambdaTest App ID at the place of lt://proverbial-android

        String grid_url = System.getenv("LT_GRID_URL") == null ? "mobile-hub.lambdatest.com" : System.getenv("LT_GRID_URL");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("isRealMobile", true);
        capabilities.setCapability("app", app_id);    //Enter app_url here

        Map<String, String> envCapabilities = (Map<String, String>) envs.get(taskID);
        Iterator it = envCapabilities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
        }

        Map<String, String> commonCapabilities = (Map<String, String>) config.get("capabilities");
        it = commonCapabilities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (capabilities.getCapability(pair.getKey().toString()) == null) {
                capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
            }
        }

        driver = new RemoteWebDriver(new URL("http://" + username + ":" + accessKey + "@" + grid_url + "/wd/hub"),
                capabilities);
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    @Test
    public void runStories() throws Exception {
        Class<?> c = Class.forName(System.getProperty("embedder"));
        Constructor<?> cons = c.getConstructor(WebDriver.class);
        Embedder storyEmbedder = (Embedder) cons.newInstance(driver);

        List<String> storyPaths = Arrays.asList(System.getProperty("stories"));
        storyEmbedder.runStoriesAsPaths(storyPaths);
    }
}
