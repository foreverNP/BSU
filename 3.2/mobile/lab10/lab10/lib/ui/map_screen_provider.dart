import 'dart:async';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:provider/provider.dart';
import '../db/utils/district.dart';
import '../l10n/generated/app_localizations.dart';
import '../providers/library_provider.dart';
import '../providers/weather_provider.dart';

class MapScreenProvider extends StatefulWidget {
  const MapScreenProvider({super.key});

  @override
  State<MapScreenProvider> createState() => _MapScreenProviderState();
}

class _MapScreenProviderState extends State<MapScreenProvider> with TickerProviderStateMixin {
  final Completer<GoogleMapController> _controller = Completer();
  final Set<Marker> _districtMarkers = {};
  String? _selectedDistrict;
  late AnimationController controller;
  late Animation<Offset> offsetAnimation;

  static const CameraPosition _initialPosition = CameraPosition(target: LatLng(53.9006, 27.5590), zoom: 12);

  @override
  void initState() {
    super.initState();
    controller = AnimationController(vsync: this, duration: const Duration(milliseconds: 500));
    offsetAnimation = Tween<Offset>(
      begin: const Offset(0, 1),
      end: Offset.zero,
    ).animate(CurvedAnimation(parent: controller, curve: Curves.easeInOut));
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _setupDistrictMarkers();
  }

  void _setupDistrictMarkers() {
    _districtMarkers.clear();
    for (final district in District.values) {
      final LatLng pos = _getDistrictLatLng(district);
      _districtMarkers.add(
        Marker(
          markerId: MarkerId(district.name),
          position: pos,
          infoWindow: InfoWindow(title: district.title(context)),
          onTap: () => _onDistrictSelected(district, pos),
        ),
      );
    }
  }

  LatLng _getDistrictLatLng(District district) {
    switch (district) {
      case District.centralny:
        return const LatLng(53.9058, 27.5615);
      case District.sovetsky:
        return const LatLng(53.9158, 27.5950);
      case District.pervomaysky:
        return const LatLng(53.9365, 27.6470);
      case District.partizansky:
        return const LatLng(53.8770, 27.6350);
      case District.oktyabrsky:
        return const LatLng(53.8680, 27.5240);
      case District.leninsky:
        return const LatLng(53.8690, 27.6125);
      case District.zavodskoy:
        return const LatLng(53.8500, 27.6880);
      case District.moskovsky:
        return const LatLng(53.8505, 27.4728);
      case District.frunzensky:
        return const LatLng(53.9210, 27.4750);
    }
  }

  Future<void> _onDistrictSelected(District district, LatLng centerCoordinates) async {
    final libraryProvider = context.read<LibraryProvider>();
    final weatherProvider = context.read<WeatherProvider>();
    setState(() {
      _selectedDistrict = district.title(context);
    });
    controller.reverse();
    await Future.delayed(const Duration(milliseconds: 500));
    controller.forward(from: 0);
    await libraryProvider.loadLibrariesForDistrict(district);
    await weatherProvider.loadWeather(centerCoordinates.latitude, centerCoordinates.longitude, lang: AppLocalizations.of(context).languageCode);
  }

  @override
  Widget build(BuildContext context) {
    final libraryProvider = context.watch<LibraryProvider>();
    final weatherProvider = context.watch<WeatherProvider>();
    return Scaffold(
      appBar: AppBar(title: Text(AppLocalizations.of(context).minskLibraries)),
      body: Stack(
        children: [
          Column(
            children: [
              Expanded(
                flex: 2,
                child: GoogleMap(
                  initialCameraPosition: _initialPosition,
                  markers: _districtMarkers,
                  onMapCreated: (controller) {
                    _controller.complete(controller);
                  },
                  zoomControlsEnabled: false,
                  myLocationEnabled: false,
                ),
              ),
            ],
          ),
          if (_selectedDistrict != null)
            Positioned(
              bottom: 0,
              left: 0,
              right: 0,
              child: SlideTransition(
                position: offsetAnimation,
                child: Container(
                  height: MediaQuery.of(context).size.height * 0.45,
                  decoration: const BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
                    boxShadow: [BoxShadow(color: Colors.black26, blurRadius: 10)],
                  ),
                  child: _buildBottomSheet(libraryProvider, weatherProvider, context),
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildBottomSheet(LibraryProvider libraryProvider, WeatherProvider weatherProvider, BuildContext context) {
    if (libraryProvider.loading || weatherProvider.loading) {
      return const Center(child: CircularProgressIndicator());
    } else if (libraryProvider.error != null || weatherProvider.error != null) {
      return Center(child: Text(libraryProvider.error ?? weatherProvider.error!));
    } else if (libraryProvider.libraries.isNotEmpty && weatherProvider.weather != null) {
      return Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Text(
              '${AppLocalizations.of(context).districts} $_selectedDistrict',
              style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Text(
              '${AppLocalizations.of(context).weather} ${weatherProvider.weather!.description}, ${weatherProvider.weather!.temperature}°C',
            ),
          ),
          const Divider(),
          Expanded(
            child: ListView.builder(
              itemCount: libraryProvider.libraries.length,
              itemBuilder: (context, index) {
                final library = libraryProvider.libraries[index];
                return ListTile(
                  title: Text(library.name.title(context)),
                  subtitle: Text(library.address.title(context)),
                );
              },
            ),
          ),
        ],
      );
    } else {
      return Center(child: Text(AppLocalizations.of(context).chooseDistrict));
    }
  }
} 