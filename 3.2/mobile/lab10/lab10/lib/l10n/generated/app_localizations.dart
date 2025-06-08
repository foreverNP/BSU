import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:intl/intl.dart' as intl;

import 'app_localizations_be.dart';
import 'app_localizations_en.dart';
import 'app_localizations_ru.dart';

// ignore_for_file: type=lint

/// Callers can lookup localized strings with an instance of AppLocalizations
/// returned by `AppLocalizations.of(context)`.
///
/// Applications need to include `AppLocalizations.delegate()` in their app's
/// `localizationDelegates` list, and the locales they support in the app's
/// `supportedLocales` list. For example:
///
/// ```dart
/// import 'generated/app_localizations.dart';
///
/// return MaterialApp(
///   localizationsDelegates: AppLocalizations.localizationsDelegates,
///   supportedLocales: AppLocalizations.supportedLocales,
///   home: MyApplicationHome(),
/// );
/// ```
///
/// ## Update pubspec.yaml
///
/// Please make sure to update your pubspec.yaml to include the following
/// packages:
///
/// ```yaml
/// dependencies:
///   # Internationalization support.
///   flutter_localizations:
///     sdk: flutter
///   intl: any # Use the pinned version from flutter_localizations
///
///   # Rest of dependencies
/// ```
///
/// ## iOS Applications
///
/// iOS applications define key application metadata, including supported
/// locales, in an Info.plist file that is built into the application bundle.
/// To configure the locales supported by your app, you’ll need to edit this
/// file.
///
/// First, open your project’s ios/Runner.xcworkspace Xcode workspace file.
/// Then, in the Project Navigator, open the Info.plist file under the Runner
/// project’s Runner folder.
///
/// Next, select the Information Property List item, select Add Item from the
/// Editor menu, then select Localizations from the pop-up menu.
///
/// Select and expand the newly-created Localizations item then, for each
/// locale your application supports, add a new item and select the locale
/// you wish to add from the pop-up menu in the Value field. This list should
/// be consistent with the languages listed in the AppLocalizations.supportedLocales
/// property.
abstract class AppLocalizations {
  AppLocalizations(String locale) : localeName = intl.Intl.canonicalizedLocale(locale.toString());

  final String localeName;

  static AppLocalizations of(BuildContext context) {
    return Localizations.of<AppLocalizations>(context, AppLocalizations)!;
  }

  static const LocalizationsDelegate<AppLocalizations> delegate = _AppLocalizationsDelegate();

  /// A list of this localizations delegate along with the default localizations
  /// delegates.
  ///
  /// Returns a list of localizations delegates containing this delegate along with
  /// GlobalMaterialLocalizations.delegate, GlobalCupertinoLocalizations.delegate,
  /// and GlobalWidgetsLocalizations.delegate.
  ///
  /// Additional delegates can be added by appending to this list in
  /// MaterialApp. This list does not have to be used at all if a custom list
  /// of delegates is preferred or required.
  static const List<LocalizationsDelegate<dynamic>> localizationsDelegates = <LocalizationsDelegate<dynamic>>[
    delegate,
    GlobalMaterialLocalizations.delegate,
    GlobalCupertinoLocalizations.delegate,
    GlobalWidgetsLocalizations.delegate,
  ];

  /// A list of this localizations delegate's supported locales.
  static const List<Locale> supportedLocales = <Locale>[
    Locale('be'),
    Locale('en'),
    Locale('ru')
  ];

  /// No description provided for @languageCode.
  ///
  /// In en, this message translates to:
  /// **'en'**
  String get languageCode;

  /// No description provided for @minskLibraries.
  ///
  /// In en, this message translates to:
  /// **'Minsk\'s libraries'**
  String get minskLibraries;

  /// No description provided for @districts.
  ///
  /// In en, this message translates to:
  /// **'District:'**
  String get districts;

  /// No description provided for @weather.
  ///
  /// In en, this message translates to:
  /// **'Weather:'**
  String get weather;

  /// No description provided for @chooseDistrict.
  ///
  /// In en, this message translates to:
  /// **'Choose district'**
  String get chooseDistrict;

  /// No description provided for @centralny.
  ///
  /// In en, this message translates to:
  /// **'Centralny district'**
  String get centralny;

  /// No description provided for @sovetsky.
  ///
  /// In en, this message translates to:
  /// **'Sovetsky district'**
  String get sovetsky;

  /// No description provided for @pervomaysky.
  ///
  /// In en, this message translates to:
  /// **'Pervomaysky district'**
  String get pervomaysky;

  /// No description provided for @partizansky.
  ///
  /// In en, this message translates to:
  /// **'Partizansky district'**
  String get partizansky;

  /// No description provided for @oktyabrsky.
  ///
  /// In en, this message translates to:
  /// **'Oktyabrsky district'**
  String get oktyabrsky;

  /// No description provided for @leninsky.
  ///
  /// In en, this message translates to:
  /// **'Leninsky district'**
  String get leninsky;

  /// No description provided for @zavodskoy.
  ///
  /// In en, this message translates to:
  /// **'Zavodskoy district'**
  String get zavodskoy;

  /// No description provided for @moskovsky.
  ///
  /// In en, this message translates to:
  /// **'Moskovsky district'**
  String get moskovsky;

  /// No description provided for @frunzensky.
  ///
  /// In en, this message translates to:
  /// **'Frunzensky district'**
  String get frunzensky;

  /// No description provided for @libraryKupala.
  ///
  /// In en, this message translates to:
  /// **'Central City Library named after Yanka Kupala'**
  String get libraryKupala;

  /// No description provided for @libraryPushkin.
  ///
  /// In en, this message translates to:
  /// **'Library named after Pushkin'**
  String get libraryPushkin;

  /// No description provided for @library5.
  ///
  /// In en, this message translates to:
  /// **'Library No. 5'**
  String get library5;

  /// No description provided for @librarySkorina.
  ///
  /// In en, this message translates to:
  /// **'Library No. 1 named after Francysk Skaryna'**
  String get librarySkorina;

  /// No description provided for @library6.
  ///
  /// In en, this message translates to:
  /// **'Library No. 6'**
  String get library6;

  /// No description provided for @library7.
  ///
  /// In en, this message translates to:
  /// **'Library No. 7'**
  String get library7;

  /// No description provided for @library8.
  ///
  /// In en, this message translates to:
  /// **'Library No. 8'**
  String get library8;

  /// No description provided for @library9.
  ///
  /// In en, this message translates to:
  /// **'Library No. 9'**
  String get library9;

  /// No description provided for @library10.
  ///
  /// In en, this message translates to:
  /// **'Library No. 10'**
  String get library10;

  /// No description provided for @library2.
  ///
  /// In en, this message translates to:
  /// **'Library No. 2'**
  String get library2;

  /// No description provided for @library11.
  ///
  /// In en, this message translates to:
  /// **'Library No. 11'**
  String get library11;

  /// No description provided for @library12.
  ///
  /// In en, this message translates to:
  /// **'Library No. 12'**
  String get library12;

  /// No description provided for @library13.
  ///
  /// In en, this message translates to:
  /// **'Library No. 13'**
  String get library13;

  /// No description provided for @library14.
  ///
  /// In en, this message translates to:
  /// **'Library No. 14'**
  String get library14;

  /// No description provided for @library15.
  ///
  /// In en, this message translates to:
  /// **'Library No. 15'**
  String get library15;

  /// No description provided for @library3.
  ///
  /// In en, this message translates to:
  /// **'Library No. 3'**
  String get library3;

  /// No description provided for @library16.
  ///
  /// In en, this message translates to:
  /// **'Library No. 16'**
  String get library16;

  /// No description provided for @library17.
  ///
  /// In en, this message translates to:
  /// **'Library No. 17'**
  String get library17;

  /// No description provided for @library18.
  ///
  /// In en, this message translates to:
  /// **'Library No. 18'**
  String get library18;

  /// No description provided for @library19.
  ///
  /// In en, this message translates to:
  /// **'Library No. 19'**
  String get library19;

  /// No description provided for @library20.
  ///
  /// In en, this message translates to:
  /// **'Library No. 20'**
  String get library20;

  /// No description provided for @library21.
  ///
  /// In en, this message translates to:
  /// **'Library No. 21'**
  String get library21;

  /// No description provided for @library22.
  ///
  /// In en, this message translates to:
  /// **'Library No. 22'**
  String get library22;

  /// No description provided for @library23.
  ///
  /// In en, this message translates to:
  /// **'Library No. 23'**
  String get library23;

  /// No description provided for @library24.
  ///
  /// In en, this message translates to:
  /// **'Library No. 24'**
  String get library24;

  /// No description provided for @library25.
  ///
  /// In en, this message translates to:
  /// **'Library No. 25'**
  String get library25;

  /// No description provided for @library26.
  ///
  /// In en, this message translates to:
  /// **'Library No. 26'**
  String get library26;

  /// No description provided for @addrLenina16.
  ///
  /// In en, this message translates to:
  /// **'16 Lenina St.'**
  String get addrLenina16;

  /// No description provided for @addrInternatsionalnaya25.
  ///
  /// In en, this message translates to:
  /// **'25 Internatsionalnaya St.'**
  String get addrInternatsionalnaya25;

  /// No description provided for @addrMarksa40.
  ///
  /// In en, this message translates to:
  /// **'40 Karl Marx St.'**
  String get addrMarksa40;

  /// No description provided for @addrNezavisimosti10.
  ///
  /// In en, this message translates to:
  /// **'10 Nezavisimosti Ave.'**
  String get addrNezavisimosti10;

  /// No description provided for @addrKedyshko11.
  ///
  /// In en, this message translates to:
  /// **'11 Kedyshko St.'**
  String get addrKedyshko11;

  /// No description provided for @addrYakubaKolasa31.
  ///
  /// In en, this message translates to:
  /// **'31 Yakuba Kolasa St.'**
  String get addrYakubaKolasa31;

  /// No description provided for @addrKalinovskogo55.
  ///
  /// In en, this message translates to:
  /// **'55 Kalinovskogo St.'**
  String get addrKalinovskogo55;

  /// No description provided for @addrGamarnika25.
  ///
  /// In en, this message translates to:
  /// **'25 Gamarnika St.'**
  String get addrGamarnika25;

  /// No description provided for @addrRussyianova4.
  ///
  /// In en, this message translates to:
  /// **'4 Russiyanova St.'**
  String get addrRussyianova4;

  /// No description provided for @addrMoskovskaya25.
  ///
  /// In en, this message translates to:
  /// **'25 Moskovskaya St.'**
  String get addrMoskovskaya25;

  /// No description provided for @addrVaupsasova10.
  ///
  /// In en, this message translates to:
  /// **'10 Vaupsasova St.'**
  String get addrVaupsasova10;

  /// No description provided for @addrKuzmyChornogo5.
  ///
  /// In en, this message translates to:
  /// **'5 Kuzmy Chornogo St.'**
  String get addrKuzmyChornogo5;

  /// No description provided for @addrUlyanovskaya15.
  ///
  /// In en, this message translates to:
  /// **'15 Ulyanovskaya St.'**
  String get addrUlyanovskaya15;

  /// No description provided for @addrSverdlova21.
  ///
  /// In en, this message translates to:
  /// **'21 Sverdlova St.'**
  String get addrSverdlova21;

  /// No description provided for @addrTolstogo3.
  ///
  /// In en, this message translates to:
  /// **'3 Tolstogo St.'**
  String get addrTolstogo3;

  /// No description provided for @addrKlaryTsetkin15.
  ///
  /// In en, this message translates to:
  /// **'15 Klary Tsetkin St.'**
  String get addrKlaryTsetkin15;

  /// No description provided for @addrShorsa5.
  ///
  /// In en, this message translates to:
  /// **'5 Shorsa St.'**
  String get addrShorsa5;

  /// No description provided for @addrKazintsa6.
  ///
  /// In en, this message translates to:
  /// **'6 Kazintsa St.'**
  String get addrKazintsa6;

  /// No description provided for @addrRadialnaya12.
  ///
  /// In en, this message translates to:
  /// **'12 Radialnaya St.'**
  String get addrRadialnaya12;

  /// No description provided for @addrSoltysa21.
  ///
  /// In en, this message translates to:
  /// **'21 Soltysa St.'**
  String get addrSoltysa21;

  /// No description provided for @addrAngarskaya14.
  ///
  /// In en, this message translates to:
  /// **'14 Angarskaya St.'**
  String get addrAngarskaya14;

  /// No description provided for @addrKizhevatova6.
  ///
  /// In en, this message translates to:
  /// **'6 Kizhevatova St.'**
  String get addrKizhevatova6;

  /// No description provided for @addrOdintsova21.
  ///
  /// In en, this message translates to:
  /// **'21 Odintsova St.'**
  String get addrOdintsova21;

  /// No description provided for @addrMelnikayte4.
  ///
  /// In en, this message translates to:
  /// **'4 Melnikayte St.'**
  String get addrMelnikayte4;

  /// No description provided for @addrKuntsevshchina6.
  ///
  /// In en, this message translates to:
  /// **'6 Kuntsevshchina St.'**
  String get addrKuntsevshchina6;

  /// No description provided for @addrMatusevicha58.
  ///
  /// In en, this message translates to:
  /// **'58 Matusevicha St.'**
  String get addrMatusevicha58;

  /// No description provided for @addrPetraGlebki5.
  ///
  /// In en, this message translates to:
  /// **'5 Petra Glebki St.'**
  String get addrPetraGlebki5;
}

class _AppLocalizationsDelegate extends LocalizationsDelegate<AppLocalizations> {
  const _AppLocalizationsDelegate();

  @override
  Future<AppLocalizations> load(Locale locale) {
    return SynchronousFuture<AppLocalizations>(lookupAppLocalizations(locale));
  }

  @override
  bool isSupported(Locale locale) => <String>['be', 'en', 'ru'].contains(locale.languageCode);

  @override
  bool shouldReload(_AppLocalizationsDelegate old) => false;
}

AppLocalizations lookupAppLocalizations(Locale locale) {


  // Lookup logic when only language code is specified.
  switch (locale.languageCode) {
    case 'be': return AppLocalizationsBe();
    case 'en': return AppLocalizationsEn();
    case 'ru': return AppLocalizationsRu();
  }

  throw FlutterError(
    'AppLocalizations.delegate failed to load unsupported locale "$locale". This is likely '
    'an issue with the localizations generation tool. Please file an issue '
    'on GitHub with a reproducible sample app and the gen-l10n configuration '
    'that was used.'
  );
}
