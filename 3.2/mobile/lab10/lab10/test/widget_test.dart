import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:provider/provider.dart';
import 'package:lab10/l10n/generated/app_localizations.dart';
import 'package:lab10/ui/map_screen_provider.dart';
import 'package:lab10/providers/library_provider.dart';
import 'package:lab10/providers/weather_provider.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('MapScreenProvider renders AppBar and GoogleMap', (WidgetTester tester) async {
    await tester.pumpWidget(
      MultiProvider(
        providers: [
          ChangeNotifierProvider(create: (_) => LibraryProvider()),
          ChangeNotifierProvider(create: (_) => WeatherProvider()),
        ],
        child: MaterialApp(
          localizationsDelegates: AppLocalizations.localizationsDelegates,
          supportedLocales: AppLocalizations.supportedLocales,
          home: const MapScreenProvider(),
        ),
      ),
    );

    await tester.pumpAndSettle();

    expect(find.byType(AppBar), findsOneWidget);
    expect(find.text("Minsk's libraries"), findsOneWidget);  

    expect(find.byType(GoogleMap), findsOneWidget);
  });

  testWidgets('MapScreenProvider displays localized text correctly', (WidgetTester tester) async {
    await tester.pumpWidget(
      MultiProvider(
        providers: [
          ChangeNotifierProvider(create: (_) => LibraryProvider()),
          ChangeNotifierProvider(create: (_) => WeatherProvider()),
        ],
        child: MaterialApp(
          localizationsDelegates: AppLocalizations.localizationsDelegates,
          supportedLocales: [Locale('en', 'US')],
          home: const MapScreenProvider(),
        ),
      ),
    );
    await tester.pumpAndSettle();
    expect(find.text("Minsk's libraries"), findsOneWidget);
  });
}
