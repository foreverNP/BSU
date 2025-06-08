import 'package:flutter/material.dart';
import '../../l10n/generated/app_localizations.dart';

enum LibraryName {
  kupala,
  pushkin,
  lib5,
  skorina,
  lib6,
  lib7,
  lib8,
  lib9,
  lib10,
  lib2,
  lib11,
  lib12,
  lib13,
  lib14,
  lib15,
  lib3,
  lib16,
  lib17,
  lib18,
  lib19,
  lib20,
  lib21,
  lib22,
  lib23,
  lib24,
  lib25,
  lib26;

  String title(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    switch (this) {
      case LibraryName.kupala:
        return l10n.libraryKupala;
      case LibraryName.pushkin:
        return l10n.libraryPushkin;
      case LibraryName.lib5:
        return l10n.library5;
      case LibraryName.skorina:
        return l10n.librarySkorina;
      case LibraryName.lib6:
        return l10n.library6;
      case LibraryName.lib7:
        return l10n.library7;
      case LibraryName.lib8:
        return l10n.library8;
      case LibraryName.lib9:
        return l10n.library9;
      case LibraryName.lib10:
        return l10n.library10;
      case LibraryName.lib2:
        return l10n.library2;
      case LibraryName.lib11:
        return l10n.library11;
      case LibraryName.lib12:
        return l10n.library12;
      case LibraryName.lib13:
        return l10n.library13;
      case LibraryName.lib14:
        return l10n.library14;
      case LibraryName.lib15:
        return l10n.library15;
      case LibraryName.lib3:
        return l10n.library3;
      case LibraryName.lib16:
        return l10n.library16;
      case LibraryName.lib17:
        return l10n.library17;
      case LibraryName.lib18:
        return l10n.library18;
      case LibraryName.lib19:
        return l10n.library19;
      case LibraryName.lib20:
        return l10n.library20;
      case LibraryName.lib21:
        return l10n.library21;
      case LibraryName.lib22:
        return l10n.library22;
      case LibraryName.lib23:
        return l10n.library23;
      case LibraryName.lib24:
        return l10n.library24;
      case LibraryName.lib25:
        return l10n.library25;
      case LibraryName.lib26:
        return l10n.library26;
    }
  }
} 