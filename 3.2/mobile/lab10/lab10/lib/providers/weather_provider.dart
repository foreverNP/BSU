import 'package:flutter/material.dart';
import '../network/weather_service.dart';

class WeatherProvider extends ChangeNotifier {
  final WeatherService _weatherService = WeatherService();
  WeatherData? _weather;
  bool _loading = false;
  String? _error;

  WeatherData? get weather => _weather;
  bool get loading => _loading;
  String? get error => _error;

  Future<void> loadWeather(double lat, double lon, {String lang = 'ru'}) async {
    _loading = true;
    _error = null;
    notifyListeners();
    try {
      _weather = await _weatherService.fetchWeather(lat, lon, lang: lang);
    } catch (e) {
      _error = e.toString();
    }
    _loading = false;
    notifyListeners();
  }
} 