import '../db/utils/district.dart';
import '../db/utils/libraryAddress.dart';
import '../db/utils/libraryName.dart';
import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';

class DBProvider {
  static final DBProvider db = DBProvider._();
  static Database? _database;

  DBProvider._();

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await initDB();
    return _database!;
  }

  Future<Database> initDB() async {
    final documentsDirectory = await getDatabasesPath();
    final path = join(documentsDirectory, "db.db");

    return await openDatabase(path, version: 1, onCreate: _onCreate);
  }

  Future<void> _onCreate(Database db, int version) async {
    await db.execute('''
    CREATE TABLE libraries(
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      name TEXT,
      address TEXT,
      district TEXT
    )
  ''');

    await db.insert('libraries', {
      'name': LibraryName.kupala.name,
      'address': LibraryAddress.lenina16.name,
      'district': District.centralny.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.pushkin.name,
      'address': LibraryAddress.internatsionalnaya25.name,
      'district': District.centralny.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib5.name,
      'address': LibraryAddress.marksa40.name,
      'district': District.centralny.name,
    });

    await db.insert('libraries', {
      'name': LibraryName.skorina.name,
      'address': LibraryAddress.nezavisimosti10.name,
      'district': District.sovetsky.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib6.name,
      'address': LibraryAddress.kedyshko11.name,
      'district': District.sovetsky.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib7.name,
      'address': LibraryAddress.yakubaKolasa31.name,
      'district': District.sovetsky.name,
    });

    await db.insert('libraries', {
      'name': LibraryName.lib8.name,
      'address': LibraryAddress.kalinovskogo55.name,
      'district': District.pervomaysky.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib9.name,
      'address': LibraryAddress.gamarnika25.name,
      'district': District.pervomaysky.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib10.name,
      'address': LibraryAddress.russyianova4.name,
      'district': District.pervomaysky.name,
    });

    await db.insert('libraries', {
      'name': LibraryName.lib2.name,
      'address': LibraryAddress.moskovskaya25.name,
      'district': District.partizansky.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib11.name,
      'address': LibraryAddress.vaupsasova10.name,
      'district': District.partizansky.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib12.name,
      'address': LibraryAddress.kuzmyChornogo5.name,
      'district': District.partizansky.name,
    });

    await db.insert('libraries', {
      'name': LibraryName.lib13.name,
      'address': LibraryAddress.ulyanovskaya15.name,
      'district': District.oktyabrsky.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib14.name,
      'address': LibraryAddress.sverdlova21.name,
      'district': District.oktyabrsky.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib15.name,
      'address': LibraryAddress.tolstogo3.name,
      'district': District.oktyabrsky.name,
    });

    await db.insert('libraries', {
      'name': LibraryName.lib3.name,
      'address': LibraryAddress.klaryTsetkin15.name,
      'district': District.leninsky.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib16.name,
      'address': LibraryAddress.shorsa5.name,
      'district': District.leninsky.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib17.name,
      'address': LibraryAddress.kazintsa6.name,
      'district': District.leninsky.name,
    });

    await db.insert('libraries', {
      'name': LibraryName.lib18.name,
      'address': LibraryAddress.radialnaya12.name,
      'district': District.zavodskoy.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib19.name,
      'address': LibraryAddress.soltysa21.name,
      'district': District.zavodskoy.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib20.name,
      'address': LibraryAddress.angarskaya14.name,
      'district': District.zavodskoy.name,
    });

    await db.insert('libraries', {
      'name': LibraryName.lib21.name,
      'address': LibraryAddress.kizhevatova6.name,
      'district': District.moskovsky.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib22.name,
      'address': LibraryAddress.odintsova21.name,
      'district': District.moskovsky.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib23.name,
      'address': LibraryAddress.melnikayte4.name,
      'district': District.moskovsky.name,
    });

    await db.insert('libraries', {
      'name': LibraryName.lib24.name,
      'address': LibraryAddress.kuntsevshchina6.name,
      'district': District.frunzensky.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib25.name,
      'address': LibraryAddress.matusevicha58.name,
      'district': District.frunzensky.name,
    });
    await db.insert('libraries', {
      'name': LibraryName.lib26.name,
      'address': LibraryAddress.petraGlebki5.name,
      'district': District.frunzensky.name,
    });
  }

  Future<void> deleteDB() async {
    final documentsDirectory = await getDatabasesPath();
    final path = join(documentsDirectory, "db.db");
    await deleteDatabase(path);
  }
} 