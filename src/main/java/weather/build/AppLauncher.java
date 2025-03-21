package weather.build;

import javax.swing.*;


public class AppLauncher {
    public WeatherApp launch() {

        WeatherApp app = new WeatherApp();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                app.setVisible(true);
            }
        });
        return app;
    }

    // launch weather app

    public static void main(String[] args) {
        new AppLauncher().launch();
    }
}
