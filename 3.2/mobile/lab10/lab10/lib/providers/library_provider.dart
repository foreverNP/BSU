import 'package:flutter/material.dart';
import '../db/library_dao.dart';
import '../data/library.dart';
import '../db/utils/district.dart';

class LibraryProvider extends ChangeNotifier {
  final LibraryDao _libraryDao = LibraryDao();
  List<Library> _libraries = [];
  bool _loading = false;
  String? _error;

  List<Library> get libraries => _libraries;
  bool get loading => _loading;
  String? get error => _error;

  Future<void> loadLibrariesForDistrict(District district) async {
    _loading = true;
    _error = null;
    notifyListeners();
    try {
      _libraries = await _libraryDao.getLibrariesByDistrict(district);
    } catch (e) {
      _error = e.toString();
    }
    _loading = false;
    notifyListeners();
  }
} 