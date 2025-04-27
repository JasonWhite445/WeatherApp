package weather.build;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class WeatherApp extends JFrame {

    private JSONObject frontEndData;

    public WeatherApp() {

        //weather app interface!

        super("Weather App");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(500, 500);

        setLayout(null);

        setLocation(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);

        AppComponents();

    }

    public static JSONObject weatherData(String location) {
        JSONArray locationEntries = getLocationInfo(location);

        JSONObject locationInfo = (JSONObject) locationEntries.get(0);

        double latitdude = (double) locationInfo.get("latitude");
        double longitude = (double) locationInfo.get("longitude");

        String apiURL = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=" + latitdude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_" +
                "10m&timezone=America%2FChicago&wind_speed_unit=mph&temperature_unit=fahrenheit&precipitation_unit=inch";

        try {

            HttpURLConnection connection = apiResponse(apiURL);
            if (connection.getResponseCode() != 200) {
                throw new Exception("failed connection");
            }
            StringBuilder json = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNext())
                json.append(scanner.nextLine());
            scanner.close();
            connection.disconnect();

            JSONParser parser = new JSONParser();

            JSONObject apiResults = (JSONObject) parser.parse(String.valueOf(json));

            JSONObject perHour = (JSONObject) apiResults.get("hourly");

            JSONArray time = (JSONArray) perHour.get("time");

            int index = findTimeIndex(time);

            JSONArray temperatureList = (JSONArray) perHour.get("temperature_2m");

            double temperature = (double) temperatureList.get(index);

            JSONArray forecastList = (JSONArray) perHour.get("weather_code");

            String forecastInfo = convertForecast((long) forecastList.get(index));

            JSONArray humidityList = (JSONArray) perHour.get("relative_humidity_2m");

            long humidity = (long) humidityList.get(index);

            JSONArray windList = (JSONArray) perHour.get("wind_speed_10m");

            double windspeed = (double) windList.get(index);

            JSONObject frontEndData = new JSONObject();

            frontEndData.put("temperature", temperature);
            frontEndData.put("forecast", forecastInfo);
            frontEndData.put("humidity", humidity);
            frontEndData.put("windspeed", windspeed);

            return frontEndData;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    public static JSONArray getLocationInfo(String location) {
        location = location.replaceAll(" ", "+");

        String apiUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + location
                + "&count=10&language=en&format=json";

        try {
            HttpURLConnection connection = apiResponse(apiUrl);
            if (connection.getResponseCode() != 200) {
                throw new Exception("failed connection");
            } else {
                StringBuilder json = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNext())
                    json.append(scanner.nextLine());
                scanner.close();
                connection.disconnect();

                JSONParser parser = new JSONParser();

                JSONObject apiResults = (JSONObject) parser.parse(String.valueOf(json));

                JSONArray results = (JSONArray) apiResults.get("results");

                if (results == null) {
                    System.out.println("No results found for this location: " + location);
                    return new JSONArray();
                }
                return results;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //method to collect weather data from weather api

    private static HttpURLConnection apiResponse(String apiURL) {

        try {

            URL url = new URL(apiURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            return connection;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //method to collect location data from location api

    private static int findTimeIndex(JSONArray times) {

        LocalDateTime locationTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String newDateTime = locationTime.format(formatter);

        for (int i = 0; i < times.size(); i++) {
            String time = (String) times.get(i);
            if (time.equalsIgnoreCase(newDateTime))
                return i;
        }
        return 0;

    }

    //method to establish connection with api's

    private static String convertForecast(long forecast) {

        String weather = "";

        if (forecast == 0L) {
            weather = "Clear";
        } else if (forecast > 0L && forecast <= 3L) {
            weather = "Cloudy";
        } else if (forecast >= 51L && forecast <= 67L) {
            weather = "Rainy";
        } else if (forecast >= 80L && forecast <= 99L) {
            weather = "Rainy";
        } else if (forecast >= 71L && forecast <= 77L) {
            weather = "Snowy";
        }

        return weather;

    }

    //find index of current weather of the hour for location.

    private void AppComponents() {

        //weather app searchbar

        JTextField searchBar = new JTextField();

        searchBar.setBounds(15, 15, 422, 45);

        searchBar.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchBar);


        //weather app pictures and labels

        JLabel weatherImage = new JLabel(getPicture("src/Images/sunny.png", 250, 250));
        weatherImage.setBounds(125, 75, 250, 250);

        add(weatherImage);

        JLabel temperatureLabel = new JLabel("45 F");
        temperatureLabel.setBounds(125, 300, 250, 100);
        temperatureLabel.setFont(new Font("Dialog", Font.BOLD, 48));
        temperatureLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(temperatureLabel);

        JLabel weatherDesc = new JLabel("Sunny");
        weatherDesc.setBounds(125, 350, 250, 100);
        weatherDesc.setFont(new Font("Dialog", Font.PLAIN, 34));
        weatherDesc.setHorizontalAlignment(SwingConstants.CENTER);

        add(weatherDesc);

        JLabel humidityImage = new JLabel(getPicture("src/Images/humidity.png", 33, 40));
        humidityImage.setBounds(62, 350, 33, 40);

        add(humidityImage);

        JLabel humidityLabel = new JLabel("<html> Humidity 90% <html>");
        humidityLabel.setBounds(0, 370, 150, 100);
        humidityLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        humidityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(humidityLabel);

        JLabel windImage = new JLabel(getPicture("src/Images/wind.png", 55, 38));
        windImage.setBounds(400, 370, 55, 38);
        add(windImage);

        JLabel windLabel = new JLabel("<html> Wind 25MPH <html>");
        windLabel.setBounds(350, 370, 150, 100);
        windLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        windLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(windLabel);

        //weather app searchbutton

        JButton searchButton = new JButton(getPicture("src/Images/search.png", 35, 35));
        searchButton.setBounds(440, 15, 45, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = searchBar.getText();

                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }

                frontEndData = weatherData(userInput);

                String forecast = (String) frontEndData.get("forecast");

                switch (forecast) {
                    case "Clear" -> weatherImage.setIcon(getPicture("src/Images/sunny.png", 250, 250));
                    case "Cloudy" -> weatherImage.setIcon(getPicture("src/Images/cloudy.png", 250, 250));
                    case "Rainy" -> weatherImage.setIcon(getPicture("src/Images/rain.png", 250, 250));
                    case "Snowy" -> weatherImage.setIcon(getPicture("src/Images/snow.png", 250, 250));
                }

                double temperature = (double) frontEndData.get("temperature");
                temperatureLabel.setText(temperature + " \u00B0F");

                weatherDesc.setText(forecast);

                long humidity = (long) frontEndData.get("humidity");
                humidityLabel.setText("<html> Humidity " + humidity + "%</html>");

                double windspeed = (double) frontEndData.get("windspeed");
                windLabel.setText("<html> Wind " + windspeed + "m/h</html>");

            }
        });

        add(searchButton);

    }

    //convert forecast data to readable description.

    private ImageIcon getPicture(String picture, int width, int height) {
        try {

            BufferedImage image = ImageIO.read(new File(picture));

            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}




