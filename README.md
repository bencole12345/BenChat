# BenChat

**BenChat has been released!**

BenChat is a new messaging app for Android phones.

![BenChat home screen](https://raw.githubusercontent.com/bencole12345/BenChat/master/screenshots/frontscreen.jpg)

Still in development, the app has several features:

- Send and receive messages
- Add friends
- Create group chats

You can download the app and read the full listing from [Google Play](https://play.google.com/store/apps/details?id=pw.bencole.benchat).

The app connects to a NodeJS backend, for which the source code can be found [here](https://github.com/bencole12345/BenChatServer).

**Warning**: The app is hosted using Heroku's free tier. As a result, if nobody else has connected to the backend for 30 minutes, your first connection may be slow and can even time out/crash the app. After an initial request has been made, the server should be responsive as usual! I hope to fix this shortly.

**Disclaimer**: The app was written for educational purposes only. While I did my best to handle security correctly (eg passwords are salted and hashed, data is only exchanged over HTTPS), I make no guarantees about security, nor do I advise anyone to communicate sensitive data using BenChat! Note that messages are stored in the database in plaintext.

## Coming soon:
- Push notifications and real-time updates
- Send images as well as text
- Custom group chat names