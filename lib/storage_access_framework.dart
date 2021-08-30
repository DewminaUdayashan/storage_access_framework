import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';

class StorageAccessFramework {
  static const MethodChannel _channel =
      const MethodChannel('storage_access_framework');
  static const String _getPlatformVersion = 'getPlatformVersion';
  static const String _openDocumentTree = 'openDocumentTree';
  static const String _checkPermissionForUri = 'checkPermissionForUri';
  static const String _getImages = 'getImages';
  static const String _checkDir = 'isDirExist';
  static const String _scanMediaFiles = 'scanMediaFiles';
  static const String _saveMedia = 'saveMedia';

  static Future<void> saveMedia({required Uint8List bytes}) async {
    Map<String, dynamic> payload = <String, dynamic>{
      'bytes': bytes,
    };
    await _channel.invokeMethod(_saveMedia, payload);
  }


  static Future<void> scanMediaFiles() async {
    await _channel.invokeMethod(_scanMediaFiles);
  }

  static Future<int?> get platformVersion async {
    final int? version = await _channel.invokeMethod(_getPlatformVersion);
    return version;
  }

  static Future<bool> isDirectoryExists({required String directoryPath}) async {
    Map<String, dynamic> payload = <String, dynamic>{
      'dirPath': directoryPath,
    };
    return await _channel.invokeMethod(_checkDir, payload);
  }

  static Future<Uri?> openDocumentTree({String? initialUri}) async {
    Uri? uri;
    String url = '';
    if (initialUri != null) {
      url += 'content://com.android.externalstorage.documents/document/';
      url += initialUri.replaceAll(':', '%3A').replaceAll('/', '%2F');
    }
    Map<String, dynamic> payload = <String, dynamic>{
      'initialUri': url,
    };
    if (initialUri != null) {
      uri = Uri.directory(
          await _channel.invokeMethod(_openDocumentTree, payload));
    } else {
      uri = await _channel.invokeMethod(_openDocumentTree);
    }
    return uri;
  }

  static Future<List<Uint8List>> getFiles(
      {required String uri,
      List<String> fileExtensions = const <String>[]}) async {
    List<Uint8List> list = List<Uint8List>.empty(growable: true);
    String url = '';
    url += 'content://com.android.externalstorage.documents/tree/';
    url += uri.replaceAll(':', '%3A').replaceAll('/', '%2F');
    Map<String, dynamic> payload = <String, dynamic>{
      'imagePath': [url],
      'fileExtensions': fileExtensions
    };
    final res = await _channel.invokeMethod(_getImages, payload);
    res.map((imgBytes) {
      list.add(Uint8List.fromList(imgBytes));
      print(list.length);
    }).toList();
    print('IMAGES RECEIVED FOR FLUTTER SIDE ${list.length}');
    return list;
  }

  static Future<bool> isPermissionAvailableForUri({required String uri}) async {
    String url = '/tree/';
    url += uri;
    // url += 'content://com.android.externalstorage.documents/tree/';
    // url += uri.replaceAll(':', '%3A').replaceAll('/', '%2F');

    Map<String, dynamic> payload = <String, dynamic>{
      'checkPermissionFor': url,
    };
    bool isGranted =
        await _channel.invokeMethod(_checkPermissionForUri, payload);
    print("PERMISSION STATUS => $isGranted");
    return isGranted;
  }
}
