package utility;



import com.nike.automation.common.framework.Browser.Browsers;
import com.nike.automation.common.framework.tools.OSTools;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

public class SeHelper {
    private WebDriver driver;
    private final Browser browser = new Browser(this);
    private final Verification verification = new Verification(this);
    private final Data data = new Data();
    private final Element element = new Element(this);
    private final Utils util = new Utils();
    private final Settings settings = buildSettings();
    private final Log log = new Log(settings().value("couchDb.url"), "_" + Util.randomNum(0, 9999) + "_" + Util.getDateStamp(new SimpleDateFormat("yyyyMMddkkmmssSSS")));
    private Browser.Browsers currentBrowser;

    //mobile orientation defaults landscape
    private String mobileOrientation = "landscape";
    private static final String localhost = "localhost";
//
//    private static final String USER_AGENT_GALAXY_SIII = "--user-agent=Mozilla/5.0 (Linux; Android 4.0; en-us; GT-I9300 Build/IMM76D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"; // Samsung Galaxy S III
//    private static final String USER_AGENT_IPHONE5 = "--user-agent=Mozilla/5.0 (iPhone; CPU iPhone OS 7_0 like Mac OS X; en-us) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53";
//    private static final String USER_AGENT_IPAD2 = "--user-agent=Mozilla/5.0 (iPad; CPU OS 4_3_5 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8L1 Safari/6533.18.5"; // iPad 1/2, iPad mini
//    private static final String USER_AGENT_IPAD3 = "--user-agent=Mozilla/5.0 (iPad; CPU OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53"; // iPad 3/4

    /**
     * Construct basic SeHelper object. You need to call startSession(Browser) to fully wire
     * the helper for testing.
     */
    public SeHelper() {
    }

    /**
     * Construct basic SeHelper object, already configured for a browser.
     * <p/>
     * You need to call startSession to fully wire the helper for testing.
     *
     * @param browser Browser you want to test against
     */
    public SeHelper(Browser.Browsers browser) {
        this.currentBrowser = browser;
    }

    /**
     * Safely builds a new Settings object, used to allow Settings to be final.
     * <p/>
     * TODO: This can be factored out once SeHelper can throw exceptions on construction (test refactoring).
     *
     * @return Settings object, or null if unable to construct.
     */
    private Settings buildSettings() {
        Settings output = null;
        try {
            output = new Settings();
        } catch (ConfigurationException e) {
            System.err.println("Error loading settings: " + e.getMessage());
            e.printStackTrace();
        }
        return output;
    }

    /**
     * Current Browser Type for this session
     */
    public Browsers currentBrowser() {
        return currentBrowser;
    }

    /**
     * Browser object backing this SeHelper object;
     */
    public Browser browser() {
        return browser;
    }

    /**
     * WebDriver object backing this SeHelper object;
     */
    public WebDriver driver() {
        return driver;
    }

    /**
     * Element object backing this SeHelper object;
     */
    public Element element() {
        return element;
    }

    /**
     * Verification object backing this SeHelper object;
     */
    public Verification verify() {
        return verification;
    }

    /**
     * Log object backing this SeHelper object;
     */
    public Log log() {
        return log;
    }

    /**
     * Data object backing this SeHelper object;
     */
    public Data data() {
        return data;
    }

    /**
     * Settings object backing this SeHelper object;
     */
    public Settings settings() {
        return settings;
    }

    /**
     * Util object backing this SeHelper object;
     */
    public Util util() {
        return util;
    }

    /**
     * Set the orientation for mobile before calling newDriver
     *
     * @param orientation
     */

    public void setMobileOrientation(String orientation) {
        mobileOrientation = orientation;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Create a new WebDriver object for the specified browser and pointed at
     * the specific proxy
     *
     * @param myBrowser Browser to create for
     * @param proxy     Proxy to use
     */
    public void startSession(Browsers myBrowser, Proxy proxy) {
        startSession(myBrowser, buildDriver(myBrowser, proxy));

        log().setTcBrowserName(browser().getBrowserName());
        log().setTcBrowserVersion(browser().getBrowserVersion());
        log().setTcGridnode(getGridNode());
        if (!myBrowser.toString().contains("Appium")) {
            browser().maximizeBrowser();
        }

    }

    /**
     * Starts a Browser session using the specified Browser and pre-created WebDriver
     *
     * @param myBrowser Browser to create for
     * @param driver    WebDriver to use
     */
    public void startSession(Browsers myBrowser, WebDriver driver) {
        this.currentBrowser = myBrowser;
        this.driver = driver;
    }

    /**
     * Starts a Browser session using the specified Browser
     *
     * @param myBrowser Browser to create for
     */
    public void startSession(Browsers myBrowser) {
        Proxy proxy = new Proxy();
        if (myBrowser == Browsers.BrowserMobedChrome
                || myBrowser == Browsers.BrowserMobedFirefox
                || myBrowser == Browsers.BrowserMobedIE) {
            proxy.setAutodetect(false);
            String browserMobProxy = settings().value("sehelper.browserMobProxy");
            proxy.setHttpProxy(browserMobProxy);
// TODO - setHttpsProxy is missing in the latest Selenium WebDriver proxy code
//            proxy.setHttpsProxy(browserMobProxy);
            proxy.setSslProxy(browserMobProxy);
        } else {
            proxy.setAutodetect(true);
        }
        startSession(myBrowser, proxy);
    }

    /**
     * Starts a Browser session using the currentBrowser
     */
    public void startSession() {
        startSession(currentBrowser);
    }

    public String getGridNode() {

        if (driver instanceof RemoteWebDriver) {
            try {
                String seleniumHub = settings().value("sehelper.seleniumHub");
                RemoteWebDriver remoteWebDriver = (RemoteWebDriver) driver;
                URL url = new URL(seleniumHub+"/grid/api/testsession?session=" + remoteWebDriver.getSessionId().toString());
                BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST", url.toExternalForm());
                HttpClient client = HttpClientBuilder.create().build();
                HttpHost host = new HttpHost(url.getHost(), url.getPort());
                HttpResponse response = client.execute(host, request);
                JSONObject object = extractObject(response);
                URL nodeUrl = new URL(object.getString("proxyId"));
                return nodeUrl.getHost();
            } catch (Exception e) {
                System.out.println("Unable to obtain hostname of webdriver node");
                return localhost;
            }
        } else {
            return localhost;
        }
    }

    //  - See more at: http://selenium.polteq.com/en/get-remote-ip-address-of-node-selenium-grid/#sthash.VRB7gnqC.dpuf
    private JSONObject extractObject(HttpResponse resp) throws IOException, JSONException {
        InputStream contents = resp.getEntity().getContent();
        StringWriter writer = new StringWriter();
        IOUtils.copy(contents, writer, "UTF8");
        JSONObject objToReturn = new JSONObject(writer.toString());
        return objToReturn;
    }

    private WebDriver buildDriver(Browsers myBrowser, Proxy proxy) {
        WebDriver driver;
        DesiredCapabilities capabilities;
        ChromeOptions chromeOptions;
        try {
            switch (myBrowser) {
                case Firefox:
                    capabilities = DesiredCapabilities.firefox();
                    capabilities.setCapability(CapabilityType.PROXY, proxy);
                    capabilities.setCapability(FirefoxDriver.PROFILE,
                            getFirefoxProfile());
                    driver = new FirefoxDriver(capabilities);
                    break;
                case Chrome:
                    capabilities = DesiredCapabilities.chrome();
                    System.setProperty("webdriver.chrome.driver",
                            getChromedriverPath());
                    capabilities.setCapability(CapabilityType.PROXY, proxy);
                    chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("test-type");  // turn off un-supported flags
                    capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
                    addChromeExtension(capabilities);
                    driver = new ChromeDriver(capabilities);
                    break;
                case ChromeAndroidEmulator:
                case ChromeGalaxySIIIEmulator:
                    capabilities = DesiredCapabilities.chrome();
                    System.setProperty("webdriver.chrome.driver",
                            getChromedriverPath());
                    capabilities.setCapability(CapabilityType.PROXY, proxy);
                    addChromeEmulatorExtension(capabilities,USER_AGENT_GALAXY_SIII);
                    driver = new ChromeDriver(capabilities);
                    break;
                case ChromeiOS7Emulator:
                    capabilities = DesiredCapabilities.chrome();
                    System.setProperty("webdriver.chrome.driver",
                            getChromedriverPath());
                    capabilities.setCapability(CapabilityType.PROXY, proxy);
                    addChromeEmulatorExtension(capabilities,USER_AGENT_IPHONE5);
                    driver = new ChromeDriver(capabilities);
                    break;
                case ChromeiPad2Emulator:
                    capabilities = DesiredCapabilities.chrome();
                    System.setProperty("webdriver.chrome.driver",
                            getChromedriverPath());
                    capabilities.setCapability(CapabilityType.PROXY, proxy);
                    addChromeEmulatorExtension(capabilities,USER_AGENT_IPAD2);
                    driver = new ChromeDriver(capabilities);
                    break;
                case ChromeiPad3Emulator:
                    capabilities = DesiredCapabilities.chrome();
                    System.setProperty("webdriver.chrome.driver",
                            getChromedriverPath());
                    capabilities.setCapability(CapabilityType.PROXY, proxy);
                    addChromeEmulatorExtension(capabilities,USER_AGENT_IPAD3);
                    driver = new ChromeDriver(capabilities);
                    break;
                case GridChromeiOS7Emulator:
                    capabilities = DesiredCapabilities.chrome();
                    if(!getSeGridUrl().toString().contains("hub.nikedev.com")){
                    	capabilities.setCapability(CapabilityType.PROXY, proxy);
                    }
                    addChromeEmulatorExtension(capabilities,USER_AGENT_IPHONE5);
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case GridChromeiPad2Emulator:
                    capabilities = DesiredCapabilities.chrome();
                    capabilities.setCapability(CapabilityType.PROXY, proxy);
                    addChromeEmulatorExtension(capabilities,USER_AGENT_IPAD2);
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case GridChromeiPad3Emulator:
                    capabilities = DesiredCapabilities.chrome();
                    capabilities.setCapability(CapabilityType.PROXY, proxy);
                    addChromeEmulatorExtension(capabilities,USER_AGENT_IPAD3);
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case GridChromeAndroidEmulator:
                case GridChromeGalaxySIIIEmulator:
                    capabilities = DesiredCapabilities.chrome();
                    if(!getSeGridUrl().toString().contains("hub.nikedev.com")){
                    	capabilities.setCapability(CapabilityType.PROXY, proxy);
                    }
                    addChromeEmulatorExtension(capabilities,USER_AGENT_GALAXY_SIII);
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case InternetExplorer:
                    capabilities = DesiredCapabilities.internetExplorer();
                    capabilities.setCapability(InternetExplorerDriver.
                            INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                    System.setProperty("webdriver.ie.driver", getIEDriverPath());
                    driver = new InternetExplorerDriver(capabilities);
                    break;
                case HtmlUnit:
                    driver = new HtmlUnitDriver();
                    ((HtmlUnitDriver) driver).setJavascriptEnabled(true);
                    log().setNoScreenShots();
                    break;
                case GridAndroid:
                    capabilities = DesiredCapabilities.android();
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case GridFirefox:
                    capabilities = DesiredCapabilities.firefox();
                    capabilities.setCapability(FirefoxDriver.PROFILE,
                            getFirefoxProfile(null));
                    if(!getSeGridUrl().toString().contains("hub.nikedev.com")){
                    	capabilities.setCapability(CapabilityType.PROXY, proxy);
                    }
                    capabilities.setCapability(CapabilityType.HAS_NATIVE_EVENTS,
                            true);
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case GridChrome:
                    capabilities = DesiredCapabilities.chrome();
                    chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("test-type");  // turn off un-supported flags
                    capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
                    //log.trace("getSeGridUrl().toString() "+getSeGridUrl().toString());
                    if(!getSeGridUrl().toString().contains("hub.nikedev.com")){
                    	capabilities.setCapability(CapabilityType.PROXY, proxy);
                    }
                    addChromeExtension(capabilities);
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case GridInternetExplorer:
                    capabilities = DesiredCapabilities.internetExplorer();
                    if(!getSeGridUrl().toString().contains("hub.nikedev.com")){
                    	capabilities.setCapability(CapabilityType.PROXY, proxy);
                    }
                    capabilities.setCapability(InternetExplorerDriver.
                            INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case GridGhostDriver:
                    capabilities = DesiredCapabilities.phantomjs();
                    capabilities.setJavascriptEnabled(true);
                    capabilities.setCapability("takesScreenshot", true);
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case GridSafari:
                    capabilities = DesiredCapabilities.safari();
                    capabilities.setCapability(CapabilityType.PROXY, proxy);
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case BrowserMobedChrome:
                    capabilities = DesiredCapabilities.chrome();
                    capabilities.setVersion(settings().value("browser.version.chromeNminus1"));
                    capabilities.setCapability(CapabilityType.PROXY, proxy);
                    addChromeExtension(capabilities);
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case BrowserMobedFirefox:
                    capabilities = DesiredCapabilities.firefox();
                    capabilities.setVersion(settings().value("browser.version.firefoxNminus1"));
                    capabilities.setCapability(FirefoxDriver.PROFILE,
                            getFirefoxProfile(null));
                    capabilities.setCapability(CapabilityType.PROXY, proxy);
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case BrowserMobedIE:
                    capabilities = DesiredCapabilities.internetExplorer();
                    capabilities.setCapability(CapabilityType.PROXY, proxy);
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case SauceIphone:
                    capabilities = DesiredCapabilities.iphone();
                    addSauceCapabilities(capabilities);
                    addAppiumIphoneCapabilities(capabilities);
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case SauceAndroid:
                    capabilities = DesiredCapabilities.android();
                    addSauceCapabilities(capabilities);
                    addAndroidCapabilities(capabilities);
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case SauceChrome:
                    capabilities = DesiredCapabilities.chrome();
                    addSauceCapabilities(capabilities);
                    capabilities.setCapability("screen-resolution", "1280x1024");
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case SauceFirefox:
                    capabilities = DesiredCapabilities.firefox();
                    addSauceCapabilities(capabilities);
                    capabilities.setCapability("screen-resolution", "1280x1024");
                    capabilities.setVersion(settings().value("browser.version.firefoxN"));
                    capabilities.setPlatform(Platform.VISTA);
                    capabilities.setCapability(FirefoxDriver.PROFILE,
                            getFirefoxProfile(null));
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case SauceIE:
                    capabilities = DesiredCapabilities.internetExplorer();
                    addSauceCapabilities(capabilities);
                    capabilities.setCapability("screen-resolution", "1280x1024");
                    capabilities.setVersion(settings().value("browser.version.ieN"));
                    capabilities.setPlatform(Platform.VISTA);
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case SauceSafari:
                    capabilities = DesiredCapabilities.safari();
                    addSauceCapabilities(capabilities);
                    capabilities.setCapability("screen-resolution", "1280x1024");
                    capabilities.setPlatform(Platform.MAC);
                    capabilities.setVersion(settings().value("browser.version.safariN"));
                    driver = (new Augmenter()).augment(new RemoteWebDriver(
                            getSeGridUrl(), capabilities));
                    break;
                case AppiumIphone:
                    capabilities = DesiredCapabilities.iphone();
                    capabilities.setCapability("app", "safari");
                    capabilities.setCapability("safariIgnoreFraudWarning", "true");
                    capabilities.setCapability("device", "iPhone Simulator");
                    driver = new RemoteWebDriver(getSeGridUrl(), capabilities);
                    break;
                case AppiumIpad:
                    capabilities = DesiredCapabilities.ipad();
                    capabilities.setCapability("app", "safari");
                    capabilities.setCapability("safariIgnoreFraudWarning", "true");
                    capabilities.setCapability("device", "iPad Simulator");
                    driver = new RemoteWebDriver(getSeGridUrl(), capabilities);
                    break;
                case AppiumAndroidPhone:
                    capabilities = DesiredCapabilities.android();
                    capabilities.setCapability("app", "browser");
                    capabilities.setCapability("deviceType", "phone");
                    capabilities.setCapability("newCommandTimeout", 10000);
                    driver = new RemoteWebDriver(getSeGridUrl(), capabilities);
                    break;
                case AppiumAndroidTablet:
                    capabilities = DesiredCapabilities.android();
                    capabilities.setCapability("app", "browser");
                    capabilities.setCapability("deviceType", "tablet");
                    driver = new RemoteWebDriver(getSeGridUrl(), capabilities);
                    break;

                default:
                    return null;
            }

        } catch (Exception e) {
            log().trace("Browser Not Reachable, Grid may be down");
            log().logTcError(e.getMessage(),null);
            return null;
        }
        return driver;
    }

    private URL getSeGridUrl() {
        URL seGrid = null;
        try {
            seGrid = new URL(settings().value("sehelper.seleniumGrid"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return seGrid;
    }


    /**
     * Setup capabilities object to run in saucelabs
     *
     * @param capabilities DesiredCapabilities you want setup for sauce
     */
    private void addSauceCapabilities(DesiredCapabilities capabilities) {
        capabilities.setCapability("record-video", settings().asBoolean("sauce.record.video"));
        capabilities
                .setCapability("record-screenshots", settings().asBoolean("sauce.record.screenshots"));
        capabilities.setCapability("capture-html", settings().asBoolean("sauce.capture.html"));
        String sauceTunnelID = settings().value("sauce.tunnel.id");
        if (sauceTunnelID != null && !sauceTunnelID.isEmpty())
            capabilities.setCapability("tunnel-identifier", sauceTunnelID);
    }

    private void addAppiumIphoneCapabilities(DesiredCapabilities capabilities) {
        capabilities.setCapability("platform", "OS X 10.8");
        capabilities.setCapability("version", "6");
        capabilities.setCapability("device-orientation", mobileOrientation);
        //capabilities.setCapability("app", "safari");
        //capabilities.setCapability("device", "iPhone Simulator");
    }

    private void addAndroidCapabilities(DesiredCapabilities capabilities) {
        capabilities.setCapability("platform", "Linux");
        capabilities.setCapability("version", "4.0");
        //capabilities.setCapability("device-orientation", "portrait");
        capabilities.setCapability("device-orientation", mobileOrientation);
    }

    /**
     * Add system property specified chrome extension to chrome capabilities
     *
     * @param capabilities DesiredCapabilities to add capabilities to
     */
    private void addChromeExtension(DesiredCapabilities capabilities) {
        String chromeExtension = settings().value("sehelper.chromeExtension");
        if (chromeExtension != null && !chromeExtension.isEmpty()) {
            ChromeOptions options = new ChromeOptions();
            options.addExtensions(new File(chromeExtension));
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        }
    }

    /**
     * Invokes chrome browser in emulation mode using the user agent to override
     * the browser type
     */
    private void addChromeEmulatorExtension(DesiredCapabilities capabilities, String userAgent) {
        String chromeExtension = settings().value("sehelper.chromeExtension");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("test-type");  // turn off unsupported command line flags
        if (chromeExtension != null && !chromeExtension.isEmpty()) {

            options.addExtensions(new File(chromeExtension));
        }
        options.addArguments(userAgent);
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
    }

    /**
     * Add system property specified firefox extension to firefox profile
     *
     * @param profile FirefoxProfile to add extension to
     */
    private void addFirefoxExtension(FirefoxProfile profile) {
        String firefoxExtension = settings().value("sehelper.firefoxExtension");
        if (firefoxExtension != null && !firefoxExtension.isEmpty()) {
            try {
                profile.addExtension(new File(firefoxExtension));
            } catch (IOException e) {
                System.out.println("Firefox Extension not found: "
                        + firefoxExtension + ". " + e.getMessage());
            }
        }
    }

    /**
     * Builds a standard firefox profile
     *
     * @return standard firefox profile
     */
    private FirefoxProfile getFirefoxProfile() {
        ProfilesIni allProfiles = new ProfilesIni();
        FirefoxProfile profile = allProfiles.getProfile("Selenium");
//        String[] browserMobProxy = settings().value("sehelper.browserMobProxy").split(":");


        if (profile == null) {
            profile = allProfiles.getProfile("default");
            // TODO - Add proxy settings to profile
//            profile.setPreference("network.proxy.ssl_port", browserMobProxy[1]);
//            profile.setPreference("network.proxy.ssl", browserMobProxy[0]);
//            profile.setPreference("network.proxy.http_port", browserMobProxy[1]);
//            profile.setPreference("network.proxy.http", browserMobProxy[0]);
//            profile.setPreference("network.proxy.type", 1);
        }
        return getFirefoxProfile(profile);
    }

    /**
     * Builds a new firefox profile
     *
     * @return standard firefox profile
     */
    private FirefoxProfile getFirefoxProfile(FirefoxProfile profile) {
        if (profile == null)
            profile = new FirefoxProfile();
        addFirefoxExtension(profile);
        profile.setEnableNativeEvents(true);
        return profile;
    }

    /**
     * Returns full path to the IEDriverServer
     *
     * @return full path to IEDriverServer
     */
    public String getIEDriverPath() {
        return OSTools.getUserHome() + "/selenium/IEDriverServer.exe";
    }

    /**
     * Returns full path to the chromedriver executable
     *
     * @return full path to chromedriver
     */
    public String getChromedriverPath() {
        String chomedriverFilename = "chromedriver.exe"; // Default to windows
        // name
        if (OSTools.isMac() || OSTools.isUnix())
            chomedriverFilename = "chromedriver";

        return OSTools.getUserHome() + "/selenium/" + chomedriverFilename;
    }

    // toString returns the "currentBrowser" so the reports look all pretty like.
    @Override
    public String toString() {
        if (currentBrowser != null)
            return currentBrowser.toString();
        else
            return "none";
    }
}

