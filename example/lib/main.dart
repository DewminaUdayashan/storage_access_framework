import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:storage_access_framework/storage_access_framework.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'SAF',
      home: Home(),
    );
  }
}

class Home extends StatefulWidget {
  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    int? platformVersion;
    try {
      platformVersion = await StorageAccessFramework.platformVersion;
    } on PlatformException catch (e) {
      print(e);
    }

    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion.toString();
    });
  }

  bool loading = false;
  List<Uint8List> l = <Uint8List>[];

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Center(
              child: Text('Running on: $_platformVersion\n'),
            ),
            Align(
              alignment: Alignment.center,
              child: loading ? CircularProgressIndicator() : SizedBox.shrink(),
            ),
            Expanded(
              child: SizedBox(
                height: 200,
                child: loading
                    ? Center(
                        child: CircularProgressIndicator(),
                      )
                    : ListView.builder(
                        shrinkWrap: true,
                        scrollDirection: Axis.horizontal,
                        itemCount: l.length,
                        itemBuilder: (context, index) => Image.memory(l[index]),
                      ),
              ),
            ),
            TextButton(
              onPressed: () async {
                try {
                  Uri? uri = await StorageAccessFramework.openDocumentTree(
                      // initialUri:
                      //     "primary:Android/media/com.whatsapp/WhatsApp/Media/.Statuses",
                      );
                  if (uri != null) if (uri.path ==
                      "primary:Android/media/com.whatsapp/WhatsApp/Media/.Statuses") {
                    print('Path Selected Correctly');
                  }
                } catch (e) {
                  print(e);
                }
              },
              style: TextButton.styleFrom(
                backgroundColor: Colors.green,
              ),
              child: Text(
                'Open Document Tree',
                style: TextStyle(color: Colors.white70),
              ),
            ),
            TextButton(
              onPressed: () async {
                if (await StorageAccessFramework.isPermissionAvailableForUri(
                  uri:
                      "primary:Android/media/com.whatsapp/WhatsApp/Media/.Statuses",
                )) {
                  showDialog(
                      context: context,
                      builder: (c) => AlertDialog(
                            title: Text("Permission Available"),
                            content: Text("This is my message."),
                            actions: [],
                          ));
                }
              },
              style: TextButton.styleFrom(
                backgroundColor: Colors.green,
              ),
              child: Text(
                'Check Permission',
                style: TextStyle(color: Colors.white70),
              ),
            ),
            TextButton(
              onPressed: () async {
                loading = true;
                setState(() {});
                StorageAccessFramework.getFileStream(
                  uri:
                      "primary:Android/media/com.whatsapp/WhatsApp/Media/.Statuses",
                  fileExtensions: [
                    '.jpg',
                    '.jpeg',
                    '.png',
                  ],
                ).listen((event) {
                  print(event);
                  if (event.toString().contains("end")) {
                    loading = false;
                    setState(() {});
                  } else
                    l.add(Uint8List.fromList(event));
                });
              },
              style: TextButton.styleFrom(
                backgroundColor: Colors.green,
              ),
              child: Text(
                'Load Images',
                style: TextStyle(color: Colors.white70),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

Future<List> tst(int? sd) async {
  final List list = await StorageAccessFramework.getFiles(
    uri: "primary:Android/media/com.whatsapp/WhatsApp/Media/.Statuses",
    fileExtensions: [
      '.jpg',
      '.jpeg',
      '.png',
    ],
  );
  return list;
}
