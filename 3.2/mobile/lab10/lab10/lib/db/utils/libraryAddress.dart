import 'package:flutter/material.dart';
import '../../l10n/generated/app_localizations.dart';

enum LibraryAddress {
  lenina16,
  internatsionalnaya25,
  marksa40,
  nezavisimosti10,
  kedyshko11,
  yakubaKolasa31,
  kalinovskogo55,
  gamarnika25,
  russyianova4,
  moskovskaya25,
  vaupsasova10,
  kuzmyChornogo5,
  ulyanovskaya15,
  sverdlova21,
  tolstogo3,
  klaryTsetkin15,
  shorsa5,
  kazintsa6,
  radialnaya12,
  soltysa21,
  angarskaya14,
  kizhevatova6,
  odintsova21,
  melnikayte4,
  kuntsevshchina6,
  matusevicha58,
  petraGlebki5;

  String title(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    switch (this) {
      case LibraryAddress.lenina16:
        return l10n.addrLenina16;
      case LibraryAddress.internatsionalnaya25:
        return l10n.addrInternatsionalnaya25;
      case LibraryAddress.marksa40:
        return l10n.addrMarksa40;
      case LibraryAddress.nezavisimosti10:
        return l10n.addrNezavisimosti10;
      case LibraryAddress.kedyshko11:
        return l10n.addrKedyshko11;
      case LibraryAddress.yakubaKolasa31:
        return l10n.addrYakubaKolasa31;
      case LibraryAddress.kalinovskogo55:
        return l10n.addrKalinovskogo55;
      case LibraryAddress.gamarnika25:
        return l10n.addrGamarnika25;
      case LibraryAddress.russyianova4:
        return l10n.addrRussyianova4;
      case LibraryAddress.moskovskaya25:
        return l10n.addrMoskovskaya25;
      case LibraryAddress.vaupsasova10:
        return l10n.addrVaupsasova10;
      case LibraryAddress.kuzmyChornogo5:
        return l10n.addrKuzmyChornogo5;
      case LibraryAddress.ulyanovskaya15:
        return l10n.addrUlyanovskaya15;
      case LibraryAddress.sverdlova21:
        return l10n.addrSverdlova21;
      case LibraryAddress.tolstogo3:
        return l10n.addrTolstogo3;
      case LibraryAddress.klaryTsetkin15:
        return l10n.addrKlaryTsetkin15;
      case LibraryAddress.shorsa5:
        return l10n.addrShorsa5;
      case LibraryAddress.kazintsa6:
        return l10n.addrKazintsa6;
      case LibraryAddress.radialnaya12:
        return l10n.addrRadialnaya12;
      case LibraryAddress.soltysa21:
        return l10n.addrSoltysa21;
      case LibraryAddress.angarskaya14:
        return l10n.addrAngarskaya14;
      case LibraryAddress.kizhevatova6:
        return l10n.addrKizhevatova6;
      case LibraryAddress.odintsova21:
        return l10n.addrOdintsova21;
      case LibraryAddress.melnikayte4:
        return l10n.addrMelnikayte4;
      case LibraryAddress.kuntsevshchina6:
        return l10n.addrKuntsevshchina6;
      case LibraryAddress.matusevicha58:
        return l10n.addrMatusevicha58;
      case LibraryAddress.petraGlebki5:
        return l10n.addrPetraGlebki5;
    }
  }
} 