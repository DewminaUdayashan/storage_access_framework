import 'dart:async';

import 'package:flutter/services.dart';

class StorageAccessFramework {
  static const MethodChannel _channel =
      const MethodChannel('storage_access_framework');
  static const String _getPlatformVersion = 'getPlatformVersion';
  static const String _openDocumentTree = 'openDocumentTree';

  static Future<int?> get platformVersion async {
    final int? version = await _channel.invokeMethod(_getPlatformVersion);
    return version;
  }

  static Future<void> openDocumentTree({String? initialUri}) async {
    String url = '';
    if (initialUri != null) {
      url += 'content://com.android.externalstorage.documents/document/';
      url += initialUri.replaceAll(':', '%3A').replaceAll('/', '%2F');
    }
    Map<String, dynamic> payload = <String, dynamic>{
      'initialUri': url,
    };
    if (initialUri != null) {
      await _channel.invokeMethod(_openDocumentTree, payload);
    } else {
      await _channel.invokeMethod(_openDocumentTree);
    }
  }
}
