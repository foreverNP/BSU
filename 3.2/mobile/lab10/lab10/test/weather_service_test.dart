import 'dart:convert';

import 'package:flutter_test/flutter_test.dart';
import 'package:http/http.dart' as http;
import 'package:http/http.dart';
import 'dart:convert';

import 'package:flutter_test/flutter_test.dart';
import 'package:http/http.dart' as http;
import 'package:http/http.dart';
import 'package:lab10/services/weather_service.dart';
import 'package:mockito/mockito.dart';

http.Response? mockResponse;

class MockClient extends Mock implements http.Client {
  @override
  Future<Response> get(Uri url, {Map<String, String>? headers}) {
    return Future.value(mockResponse);
  }
}

void main() {
  group('WeatherService', () {
    late WeatherService service;
    late MockClient client;

    setUp(() {
      service = WeatherService();
      client = MockClient();
    });

    test('fetchWeather returns WeatherData if request is successful', () async {
      mockResponse = http.Response(
        jsonEncode({
          "weather": [
            {"description": "clear", "icon": "01d"},
          ],
          "main": {"temp": 23.4},
        }),
        200,
      );
      WeatherService.client = client;

      final result = await service.fetchWeather(53.9, 27.5667);

      expect(result.description, "clear");
      expect(result.temperature, 23.4);
      expect(result.icon, "01d");
    });

    test('fetchWeather throws exception if request fails', () {
      WeatherService.client = client;
      mockResponse = http.Response('Error', 404);

      expect(() => service.fetchWeather(53.9, 27.5667), throwsException);
    });
  });
}
