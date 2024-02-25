import '../db/utils/district.dart';
import '../data/library.dart';
import 'db_provider.dart';

class LibraryDao {
  final dbProvider = DBProvider.db;

  Future<int> insertLibrary(Library library) async {
    final db = await dbProvider.database;
    return await db.insert('Libraries', library.toMap());
  }

  Future<List<Library>> getLibrariesByDistrict(District district) async {
    final db = await dbProvider.database;
    final res = await db.query('Libraries', where: 'district = ?', whereArgs: [district.name]);

    return res.isNotEmpty ? res.map((c) => Library.fromMap(c)).toList() : [];
  }

  Future<List<Library>> getAllLibraries() async {
    final db = await dbProvider.database;
    final res = await db.query('Libraries');

    return res.isNotEmpty ? res.map((c) => Library.fromMap(c)).toList() : [];
  }

  Future<void> deleteAllLibraries() async {
    final db = await dbProvider.database;
    await db.delete('Libraries');
  }
} 