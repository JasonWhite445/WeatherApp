package weather.build;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class WeatherApp extends JFrame {

    public WeatherApp() {

        //weather app interface!

        super("Weather App");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(500, 500);

        setLayout(null);

        setLocation(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);

        AppComponents();

    }

    private void AppComponents(){

        //weather app searchbar

        JTextField searchBar = new JTextField();

        searchBar.setBounds(15,15, 422, 45);

        searchBar.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchBar);

        //weather app searchbutton

        JButton searchButton = new JButton(getPicture("src/Images/search.png", 35, 35));
        searchButton.setBounds(440, 15, 45, 45);

        add(searchButton);

        //weather app pictures and labels

        JLabel weatherImage = new JLabel(getPicture("src/Images/sunny.png", 250, 250));
        weatherImage.setBounds(125,75,250,250);

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
        humidityImage.setBounds(62,350,33,40);

        add(humidityImage);

        JLabel humidityLabel = new JLabel("<html> Humidity 90% <html>");
        humidityLabel.setBounds(0, 370, 150, 100);
        humidityLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        humidityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(humidityLabel);

        JLabel windImage = new JLabel(getPicture("src/Images/wind.png", 55, 38));
        windImage.setBounds(400,370,55,38);
        add(windImage);

        JLabel windLabel = new JLabel("<html> Wind 25MPH <html>");
        windLabel.setBounds(350, 370, 150, 100);
        windLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        windLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(windLabel);

    }

    private ImageIcon getPicture(String picture, int width, int height)
    {
    try {

        BufferedImage image = ImageIO.read(new File(picture));

        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
    catch (IOException e) {
        e.printStackTrace();
        return null;
    }

    }

    //method to collect weather data from weather api

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
            if(connection.getResponseCode() != 200) {
                System.out.println("failed connection");
                return null;
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



        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    //method to collect location data from location api

    public static JSONArray getLocationInfo(String location) {
        location = location.replaceAll(" ", "+");

        String apiUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + location
                + "&count=10&language=en&format=json";

        try {
            HttpURLConnection connection = apiResponse(apiUrl);
            if(connection.getResponseCode() != 200) {
                System.out.println("failed connection");
            }
            else{
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

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    //method to establish connection with api's

    private static HttpURLConnection apiResponse(String apiURL) {

        try {

            URL url = new URL(apiURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            return connection;

        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static int findTimeIndex(JSONArray times)
    {

        LocalDateTime locationTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00");

        String newDateTime = locationTime.format(formatter);

        for (int i = 0; i < times.size(); i++ )
        {
            String time = (String) times.get(i);
            if (time.equalsIgnoreCase(newDateTime))
                return i;
        }
        return 0;

    }

}




