import 'package:flutter/material.dart';
import '../../l10n/generated/app_localizations.dart';

enum District {
  centralny('centralny'),
  sovetsky('sovetsky'),
  pervomaysky('pervomaysky'),
  partizansky('partizansky'),
  oktyabrsky('oktyabrsky'),
  leninsky('leninsky'),
  zavodskoy('zavodskoy'),
  moskovsky('moskovsky'),
  frunzensky('frunzensky');

  const District(this.name);

  final String name;

  String title(BuildContext context) {
    switch(this) {
      case centralny:
        return AppLocalizations.of(context).centralny;
      case District.sovetsky:
        return AppLocalizations.of(context).sovetsky;
      case District.pervomaysky:
        return AppLocalizations.of(context).pervomaysky;
      case District.partizansky:
        return AppLocalizations.of(context).partizansky;
      case District.oktyabrsky:
        return AppLocalizations.of(context).oktyabrsky;
      case District.leninsky:
        return AppLocalizations.of(context).leninsky;
      case District.zavodskoy:
        return AppLocalizations.of(context).zavodskoy;
      case District.moskovsky:
        return AppLocalizations.of(context).moskovsky;
      case District.frunzensky:
        return AppLocalizations.of(context).frunzensky;
    }
  }

  static District fromString(String? name) {
    return District.values.firstWhere(
          (currency) => currency.name == name,
      orElse: () => District.centralny,
    );
  }
} 