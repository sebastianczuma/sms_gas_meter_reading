# Odczyt gazu SMS - Android App

<img src="/readme/icon.png" align="left"
width="200" hspace="10" vspace="10">

Odczyt gazu SMS is quick and easy way to report the gas meter reading for PGNiG customers.

<br />

Odczyt gazu SMS is available on the Google Play Store for Polish customers.

<p align="center">
<a href="https://play.google.com/store/apps/details?id=com.sebastianczuma.odczytgazomierza">
    <img alt="Get it on Google Play"
        height="80"
        src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" />
</a>  

## About

The application stores the customer number and automatically adds it when sending the meter reading.
The user is also able to set monthly reminder.

It's free of cost and free of ads.

This app doesn't need Internet access. Gas meter readings are reporting through SMS.
Text messages are recived by PGNiG (number: +48 608 600 608).

Historical data is stored in SQLite database on the device.

Odczyt gazu SMS requires a sim card in the phone to work properly.

## Features

The android app lets you:
- Send gas meter reading quick and easy.
- Set up multiple customer numbers with user defined names.
- Browse through history of your gas meter readings.
- Set monthly reminder at any day and time.
- Find the customer number.
- Send gas meter reading without Internet connection.
- Completely ad-free.

## Screenshots

[<img src="/readme/overw_small.png" align="left"
width="870"
hspace="10" vspace="10">](/readme/overw_small.png)

&nbsp;

## Permissions

Odczyt gazu SMS requires the following permissions:
- Send SMS messages
- Read phone status and identity
- Run at startup
- Prevent device from sleeping

The "Run at startup" and "Prevent device from sleeping" permissions are only used if monthly reminder is enabled.
