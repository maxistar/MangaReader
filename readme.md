# Magekyo Manga Reader

This is simple manga reader that allows search, download and view manga from popular websites.

The main effort was to create proof of concept to make reading as more simple as possible since most
of manga readers are really cool, but I wanted to have better reading experience.

Unfortunately because most of manga volumes do not not fit to Android Market terms of service the app was
removed from the market some time ago so I decided to open source it to make it available for everyone.

I welcome pull requests for this project, new feature requests and any of your feedback!


## Appium tests

https://appium.io/docs/en/2.2/quickstart/test-js/

use node 20 
- `nvm use 20`
start appium 
- `appium`
start tests 
- `node index.js`

## Send file to device using adb

`adb -s emulator-5554 shell`

```
storage/emulated/0/Documents/
mkdir book


```

`adb -s push page_001.png /storage/emulated/0/Documents/book`

- [x] add an empty manga to favorites
- [x] store list of URIs sowhere in the application
- [x] use uri instead of file
- [x] fix crash when swapped
- [x] sort files in correct order
- [ ] remember when we stopped last time