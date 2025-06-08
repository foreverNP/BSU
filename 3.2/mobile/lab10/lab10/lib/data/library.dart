import '../db/utils/district.dart';
import '../db/utils/libraryAddress.dart';
import '../db/utils/libraryName.dart';

class Library {
  final int id;
  final LibraryName name;
  final LibraryAddress address;
  final District district;

  Library({
    required this.id,
    required this.name,
    required this.address,
    required this.district,
  });

  factory Library.fromMap(Map<String, dynamic> map) {
    return Library(
      id: map['id'],
      name: LibraryName.values.firstWhere((e) => e.name == map['name']),
      address: LibraryAddress.values.firstWhere((e) => e.name == map['address']),
      district: District.fromString(map['district']),
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'name': name.name,
      'address': address.name,
      'district': district.name,
    };
  }
} 