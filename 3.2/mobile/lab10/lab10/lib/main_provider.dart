import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:provider/provider.dart';
import 'l10n/generated/app_localizations.dart';
import 'ui/map_screen_provider.dart';
import 'providers/library_provider.dart';
import 'providers/weather_provider.dart';

void main() {
  runApp(const MyAppProvider());
}

class MyAppProvider extends StatelessWidget {
  const MyAppProvider({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => LibraryProvider()),
        ChangeNotifierProvider(create: (_) => WeatherProvider()),
      ],
      child: MaterialApp(
        theme: ThemeData(primarySwatch: Colors.blue, visualDensity: VisualDensity.adaptivePlatformDensity),
        localizationsDelegates: const [AppLocalizations.delegate, GlobalMaterialLocalizations.delegate],
        supportedLocales: AppLocalizations.supportedLocales,
        home: const MapScreenProvider(),
        debugShowCheckedModeBanner: false,
      ),
    );
  }
} 