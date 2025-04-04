import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import weather.build.AppLauncher;
import weather.build.WeatherApp;

import org.junit.jupiter.api.Assertions;



public class WeatherTest {

    private AppLauncher app;

    @BeforeEach
    public void SetupTest() {
        app = new AppLauncher();
    }

    @Test
    public void launchWeatherApplication() {

        Assertions.assertNotNull(app.launch(), "WeatherApp is created!");

    }

    @Test
    public void testForecastRetrieval() {

        JSONObject weatherData = WeatherApp.weatherData("New York");
        Assertions.assertNotNull(weatherData, "Weather Data should provide a valid location");

    }

    @Test
    public void testLocationRetrieval() {

        JSONArray locationData = WeatherApp.getLocationInfo("Los Angeles");
        Assertions.assertFalse(locationData.isEmpty(), "Location data retrieved successfully.");

    }


}
