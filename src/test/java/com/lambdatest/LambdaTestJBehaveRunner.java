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
        String configName = System.getProperty("config");

        System.out.println("Attempting to load config: " + configName);

        if (configName != null) {
            JSONParser parser = new JSONParser();

            // Use the ClassLoader to find the file in src/test/resources/conf/
            try (java.io.InputStream is = LambdaTestJBehaveRunner.class
                    .getClassLoader()
                    .getResourceAsStream("conf/" + configName)) {

                if (is == null) {
                    // This provides a much clearer error if the file is missing or renamed
                    throw new java.io.FileNotFoundException("Classpath error: Could not find 'conf/" + configName + "'. Check your src/test/resources/conf folder.");
                }

                java.io.InputStreamReader reader = new java.io.InputStreamReader(is);
                config = (JSONObject) parser.parse(reader);
            }

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
        String username = System.getenv("LT_USERNAME") == null ? "YOUR_LT_USERNAME" : System.getenv("LT_USERNAME");
        String accessKey = System.getenv("LT_ACCESS_KEY") == null ? "YOUR_LT_ACCESS_KEY" : System.getenv("LT_ACCESS_KEY");
        String grid_url = System.getenv("LT_GRID_URL") == null ? "mobile-hub.lambdatest.com" : System.getenv("LT_GRID_URL");

        // Use ChromeOptions or UiAutomator2Options, but for generic RemoteWebDriver:
        DesiredCapabilities capabilities = new DesiredCapabilities();

        // 1. Mandatory W3C standard capabilities
        Map<String, Object> ltOptions = new java.util.HashMap<>();
        ltOptions.put("isRealMobile", true);
        ltOptions.put("app", "<your_app_id>");

        // 2. Load Environment Specific Capabilities (deviceName, platformVersion, etc.)
        Map<String, String> envCapabilities = (Map<String, String>) envs.get(taskID);
        for (Map.Entry<String, String> entry : envCapabilities.entrySet()) {
            // PlatformName and PlatformVersion are W3C standards; others go to ltOptions
            if (entry.getKey().equalsIgnoreCase("platformName")) {
                capabilities.setCapability("platformName", entry.getValue());
            } else {
                ltOptions.put(entry.getKey(), entry.getValue());
            }
        }

        // 3. Load Common Capabilities (build, name, visual, etc.)
        Map<String, Object> commonCapabilities = (Map<String, Object>) config.get("capabilities");
        for (Map.Entry<String, Object> entry : commonCapabilities.entrySet()) {
            ltOptions.put(entry.getKey(), entry.getValue());
        }

        // 4. Wrap everything in lt:options
        capabilities.setCapability("lt:options", ltOptions);

        driver = new RemoteWebDriver(new URL("https://" + username + ":" + accessKey + "@" + grid_url + "/wd/hub"), capabilities);
    }
    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    @Test

    public void runStories() throws Exception {
        // Get properties from command line, or use defaults
        String embedderClassName = System.getProperty("embedder", "com.lambdatest.single.SingleEmbedder");
        String storiesProperty = System.getProperty("stories");

        if (storiesProperty == null || storiesProperty.isEmpty()) {
            throw new RuntimeException("Error: You must specify stories to run via -Dstories=path/to.story");
        }

        // Initialize the Embedder via reflection
        Class<?> c = Class.forName(embedderClassName);
        Constructor<?> cons = c.getConstructor(WebDriver.class);
        Embedder storyEmbedder = (Embedder) cons.newInstance(driver);

        // Run the stories
        List<String> storyPaths = Arrays.asList(storiesProperty.split(","));
        storyEmbedder.runStoriesAsPaths(storyPaths);
    }
}
