# cordova-plugin-video-editor

A plugin for Cordova hybrid apps which allows encoding of audio and video using FFMPEG.

The following technologies are used in the app:
* Apache Cordova `http://cordova.apache.org/`
* FFMPEG `https://www.ffmpeg.org/`

## Installation and running tasks

Install [Apache Cordova](http://cordova.apache.org/) then either navigate to a project or run the command:
    
To run the assets compilation run the command:

    cordova create hello com.example.hello HelloWorld
    
Now navigate into the main directory and add this plugin using:

    cordova plugin add cordova-plugin-video-editor
    
Testing the plugin using plugman:

    npm install -g plugman
    plugman install --platform android --project www --plugin plugins/cordova-plugin-video-editor
    
Cordova caches plugins, So if you make any changes to a plugin's code you can force a reset using the following command:

    cordova platform remove android; cordova platform add android; cordova run android

## Directory Layout

    www/                --> Static html templates
      css/              --> Stylesheet files
      img/              --> Image files
      index.html        --> Homepage
      js/               --> Javascript functionality

## Contact

For more information please contact kmturley