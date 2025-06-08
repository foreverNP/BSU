import 'dart:convert';
import 'package:http/http.dart' as http;

class WeatherService {
  static const String _apiKey = '';
  static const String _baseUrl = 'https://api.openweathermap.org/data/2.5/weather';
  static http.Client client = http.Client();

  Future<WeatherData> fetchWeather(double lat, double lon, {String lang = 'ru'}) async {
    final url = Uri.parse('$_baseUrl?lat=$lat&lon=$lon&units=metric&lang=$lang&appid=$_apiKey');

    final response = await client.get(url);

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return WeatherData.fromJson(data);
    } else {
      throw Exception('Ошибка загрузки погоды: {response.statusCode}');
    }
  }
}

class WeatherData {
  final String description;
  final double temperature;
  final String icon;

  WeatherData({
    required this.description,
    required this.temperature,
    required this.icon,
  });

  factory WeatherData.fromJson(Map<String, dynamic> json) {
    return WeatherData(
      description: json['weather'][0]['description'],
      temperature: (json['main']['temp'] as num).toDouble(),
      icon: json['weather'][0]['icon'],
    );
  }
} 