# SMS Locator

A lightweight, SMS based, on demand geolocation application for android devices.

## Getting Started

The app is still under development and not yet published to a store.  
To test the application you need to clone the project and AndroidStudio installed.
You can load the project by importing the build.gradle file in AndroidStudio.

### Installing to your phone

1. Build the apk in AndroidStudio
2. Copy apk file to your phone
3. From your phone, open the apk to install the application
4. Goto the SMS Locator application permissions and grant both location and sms permissions.

### Manual testing 
1. Start the application
2. Send an sms starting with @LOC# to trigger the application ; check the Logcat 

## Progress

### First milestone
* Getting the walking skeleton in place  
  - [ ] SMS received -> Sender identified -> Location computed -> Location sent back via sms to requester

### Done
* Listen to incoming SMS : ok
* Extract requester number : ok
* Getting location fix : ok
 
### Todo
* Send back and sms with the location coordinates to requester
* Add an activity to send the location request
* Add an activity to display the requested location when received
* Add interactive permisisons' requests
