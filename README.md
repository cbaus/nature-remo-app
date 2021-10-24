# open-remo
Basic Android app for Nature Remo home automation

Completely open-source

Reqires and Android device (Android 10 and above). No iPhone build is planned.

## Introduction
Kotlin-based smartphone app for home automation using Nature Remo devices. Developed for Nature Remo 3 but should work for other models that access the API in the same way.

I developed it because I want access to temperature and humidity values that are not available on the normal app. Also the official app is closed source but the APIs are free to access.

It's also my first Kotlin project. It's meant to be used by me personally but you can easily extend it to include more features and more appliances. So far one AC unit and one light is supported. However, all the building blocks are there to see how to communicate with the API.

## Instructions to build
You need Android Studio. Load the project. The only external library is [AAChartCore](https://github.com/AAChartModel/AAChartCore-Kotlin/tree/master). Thanks to them for making this nicely animated chart.

## Screenshots
![Screenshot1](https://github.com/cbaus/open-remo/raw/main/readme-material/screenshot1.jpg)
![Screenshot1](https://github.com/cbaus/open-remo/raw/main/readme-material/screenshot2.jpg)



## Outlook
### API
Ideally this app should not work via the cloud API but the local API. Right now the local API is not well supported by Nature. I can't change it right now.
### AppStore
I might release on F-Droid
### APK
Build it yourself. Or check under Releases here on GitHub. Will be unsigned.