import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import weather.build.AppLauncher;
import weather.build.WeatherApp;

import org.junit.jupiter.api.Assertions;



public class WeatherTest {

    private AppLauncher app;

    @BeforeEach
    public void SetupTest() {app = new AppLauncher();}

    @Test
    public void LaunchWeatherApplication() {

        Assertions.assertNotNull(app.launch(), "WeatherApp is created!");

    }

}
