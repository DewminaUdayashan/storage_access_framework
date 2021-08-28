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

  static Future<int?> get platformVersion async {
    final int? version = await _channel.invokeMethod(_getPlatformVersion);
    return version;
  }

  static Future<bool> isDirectoryExists({required String directoryPath}) async {
    return await _channel.invokeMethod(_checkDir);
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

  static Future<List<Uint8List>> getImages({required String uri}) async {
    List<Uint8List> list = List<Uint8List>.empty(growable: true);
    String url = '';
    url += 'content://com.android.externalstorage.documents/tree/';
    url += uri.replaceAll(':', '%3A').replaceAll('/', '%2F');
    print(url);
    Map<String, dynamic> payload = <String, dynamic>{
      'imagePath': url,
    };
    final res = await _channel.invokeMethod(_getImages, payload);
    res.map((imgBytes) {
      list.add(Uint8List.fromList(imgBytes));
      print(list.length);
    }).toList();
    return list;
  }

  static Future<bool> isPermissionAvailableForUri({required String uri}) async {
    String url = '';
    url += 'content://com.android.externalstorage.documents/tree/';
    url += uri.replaceAll(':', '%3A').replaceAll('/', '%2F');

    Map<String, dynamic> payload = <String, dynamic>{
      'checkPermissionFor': url,
    };
    bool isGranted =
        await _channel.invokeMethod(_checkPermissionForUri, payload);
    print("PERMISSION STATUS => $isGranted");
    return isGranted;
  }
}
