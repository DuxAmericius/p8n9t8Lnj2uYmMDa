INTRODUCTION
------------

Dulaney High School

    Josh Talley
    Daniel O'Donnell

Mobile Application Development 2016-17

LIST OF FILES
-------------

FBLA-YardSale.apk

    (Compiled Android Application)
    
Documentation

    Documentation - Android Java.pdf (Printout of Java source code)
    Documentation - Android Layouts.pdf (Printout of XML Layout source code)
    Documentation - Android Screenshots.pdf (Screenshots of Android application)
    Documentation - Microsoft Azure.pdf (Screenshots of Azure configuration)
    
Source

    / app / src / main (Android Studio source code)

REQUIREMENTS
------------

RUN TIME:

Phone Application

    Android API 21 or later
    Internet Access
    Camera Access

Server Application

    Microsoft Azure

BUILD:

Phone Application

    Android Studio 2.2.3 or later
    USB cable to connect phone to computer
        or Android Emulator in Android Studio
    Allow installation of non-Market applications
    Debug mode when USB is connected

Server Application

    Microsoft Azure
    SQL Server Management Studio 17

Source Code

https://github.com/DuxAmericius/p8n9t8Lnj2uYmMDa

INSTALLATION
------------

Precompiled Installation

    1. Go to http://fbla-yardsaledownload.azurewebsites.net on phone
    2. Follow instructions

Compile Installation

    1. Run Android Studio
    2. Open FBLA YardSale Project
    3. Change Android phone settings
        a. Verify Internet connection
        b. Allow installation of non-Market applications
        c. Enable debug mode when USB is connected
    4. Connect Android phone to computer through USB cable
    5. Press SHIFT + F10 in Android Studio
    6. Press ENTER when the pop-up windo for running on phone is displayed
    7. Application will automatically start on phone

TESTING
-------

How to Register New User:

    1. Press "Create account"
    2. Follow instructions for creating a Microsoft account

How to Log In:

    1. Type in email
    2. Press "Next"
    3. Type in password
    4. Press "Sign In"

How to Switch Between Pages:

    1. Click on desired page's icon on the top row
    2. OR swipe left or right to desired page

How to Edit Account Info:

    1. Go to Home page
    2. Press "Account"
    3. Type in Name
    4. Type in zipcode, then press Search
    5. OR Select state and type in city name, then press Search
    6. Select school if available
    7. Choose either a 5 mile or 10 mile radius from school selected
    8. Click "Save" to save data and close activity
    9. OR click "Cancel" to not save data and close activity
*IMPORTANT* SCHOOLS SHOWN IN LOCAL SALES AND ON THE MAP ARE WITHIN THE RADIUS FROM SELECTED SCHOOL

How to Add Sales:

    1. Go to Home page
    2. Press "Add Sales"
    3. Enter required information (Item Name and Item Price)
    4. Enter optional information (Item Description and Picture from Camera or Gallery)
    5. Press "Finish" to upload item and close activity
    6. OR press "Add Another" to upload item and restart activity
    7. OR press "Cancel" to not upload item and close activity

How to View Your Sales:

    1. Go to Home page
    2. Press "My Sales"

How to Delete Your Sales (Continued from above):

    3. Press the red "X" on the item you want to delete
    4. Press "Confirm" to delete item
    5. OR press "Cancel to not delete item

How to Comment:

    1. Find item you want to comment on
    2. Press "Comment"
    3. Type desired comment in the text box provided at the bottom of the screen
    4. Press "Post"

How to Delete Comment:

    1. Find desired comment
    2. Press the red "X" on the comment you want to delete
    3. Press "Confirm" to delete comment
    4. OR press "Cancel" to not delete comment

How to View Number of Sales for a School:

    1. Go to Map page
    2. Press desired school's icon

How to Exit:

    1. Go to Home page
    2. Press "Close App"

REFERENCES
----------

Source Code:
https://github.com/DuxAmericius/p8n9t8Lnj2uYmMDa

Microsoft Azure Portal:
https://portal.azure.com/

Android Developers Documentation:
https://developer.android.com/index.html

Microsoft Azure - Create an Android app:
https://docs.microsoft.com/en-us/azure/app-service-mobile/app-service-mobile-android-get-started

Google API Key for using Maps:
https://console.developers.google.com/apis/credentials?project=fbla-yardsale

Microsoft Application for Microsoft Login access:
https://apps.dev.microsoft.com/#/application/6e9f8417-81b1-4052-87fe-3e2d857f3a68

Database of USA Zip Codes:
http://federalgovernmentzipcodes.us

Database of all USA Public Schools:
https://nces.ed.gov/ccd/pubschuniv.asp

Database of all USA Private Schools:
https://nces.ed.gov/surveys/pss/pssdata.asp

Algorithm for calculating distance between schools:
http://daynebatten.com/2015/09/latitude-longitude-distance-sql/

Documentation for using Google Maps:
https://developers.google.com/maps/documentation/android-api/start

Documentation for using Microsoft Azure for querying the database:
https://github.com/Azure/azure-content-nlnl/blob/master/articles/mobile-services/mobile-services-android-how-to-use-client-library.md

Documentation for using Microsoft Azure Storage on Android:
https://github.com/azure/azure-storage-android

COPYRIGHT NOTATIONS
-------------------

http://fbla-pbl.org/pbl/communications/image-library