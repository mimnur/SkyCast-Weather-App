import java.net.*;
import java.util.*;
import java.io.*;

public class WeatherAPI {
    private static final String API_KEY = "56d8fcb274b14d8a90c152330251805"; // Replace this

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Weather Information System ===");
        System.out.print("Enter city name: ");
        String city = scanner.nextLine();

        System.out.print("Choose temperature unit (C/F): ");
        String unitChoice = scanner.nextLine().trim().toUpperCase();
        boolean inCelsius = unitChoice.equals("C");

        try {
            String urlStr = "http://api.weatherapi.com/v1/forecast.json?key=" +
                    API_KEY + "&q=" + URLEncoder.encode(city, "UTF-8") +
                    "&days=5&aqi=yes&alerts=no";
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine, response = "";
            while ((inputLine = in.readLine()) != null) {
                response += inputLine;
            }
            in.close();

            // Extract current data
            String country = extract(response, "\"country\":\"", "\"");
            String cityName = extract(response, "\"name\":\"", "\"");
            String localTime = extract(response, "\"localtime\":\"", "\"");
            String condition = extract(response, "\"text\":\"", "\"");
            double temp = extractDouble(response, inCelsius ? "\"temp_c\":" : "\"temp_f\":");
            double feels = extractDouble(response, inCelsius ? "\"feelslike_c\":" : "\"feelslike_f\":");
            double wind = extractDouble(response, "\"wind_kph\":");
            int humidity = (int) extractDouble(response, "\"humidity\":");

            CurrentWeather cw = new CurrentWeather(country, cityName, localTime, condition,
                    temp, feels, wind, humidity, inCelsius);
            System.out.println("\n=== Current Weather ===");
            cw.displayCurrent();

            System.out.println("\n=== 5-Day Forecast ===");
            for (int i = 0; i < 5; i++) {
                String date = extract(response, "\"date\":\"", "\"", i);
                double max = extractDouble(response, inCelsius ? "\"maxtemp_c\":" : "\"maxtemp_f\":", i);
                double min = extractDouble(response, inCelsius ? "\"mintemp_c\":" : "\"mintemp_f\":", i);
                String fc = extract(response, "\"condition\":{\"text\":\"", "\"", i);
                Forecast forecast = new Forecast(date, max, min, fc);
                forecast.displayForecast(inCelsius);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static String extract(String json, String key, String end) {
        int start = json.indexOf(key) + key.length();
        int finish = json.indexOf(end, start);
        return json.substring(start, finish);
    }

    private static String extract(String json, String key, String end, int occurrence) {
        int from = 0;
        for (int i = 0; i <= occurrence; i++) {
            from = json.indexOf(key, from + 1);
        }
        int start = from + key.length();
        int finish = json.indexOf(end, start);
        return json.substring(start, finish);
    }

    private static double extractDouble(String json, String key) {
        int start = json.indexOf(key) + key.length();
        int end = json.indexOf(",", start);
        return Double.parseDouble(json.substring(start, end));
    }

    private static double extractDouble(String json, String key, int occurrence) {
        int from = 0;
        for (int i = 0; i <= occurrence; i++) {
            from = json.indexOf(key, from + 1);
        }
        int start = from + key.length();
        int end = json.indexOf(",", start);
        return Double.parseDouble(json.substring(start, end));
    }
}
