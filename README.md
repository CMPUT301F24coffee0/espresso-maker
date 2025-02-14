# Espresso Maker

## Overview
Espresso Maker is an Android application developed for **CMPUT 301 - Fall 2024**. The app allows users to participate in an **Event Lottery System** where they can sign up for events at community centers through a fair lottery-based selection system.

For more information, visit the (wiki)[https://github.com/CMPUT301F24coffee0/espresso-maker/wiki/User-Interface-Mockup-and-Storyboard]

## Features
- **User Authentication**: Users can sign up and log in.
- **Event Registration**: Join events via a lottery system.
- **QR Code Scanning**: Scan QR codes for event participation verification.
- **Image Uploads**: Upload images for profile and event-related data.
- **Geolocation Verification**: Verify user presence at event locations.
- **Multi-User Roles**:
  - **Entrants**: Sign up for events.
  - **Organizers**: Manage event details.
  - **Administrators**: Oversee the system.
- **Firebase Integration**: Firestore used for backend storage.
- **Real-time Updates**: Users receive real-time updates on lottery results and event details.

## Installation
### Prerequisites
- Android Studio (latest version recommended)
- JDK 23
- Firebase project setup with Firestore

### Steps to Set Up
1. Clone the repository:
   ```sh
   git clone https://github.com/CMPUT301F24coffee0/espresso-maker.git
   cd espresso-maker
   ```
2. Open the project in Android Studio.
3. Configure Firebase:
   - Add `google-services.json` to `app/` directory.
   - Enable Firestore in Firebase Console.
4. Sync Gradle and build the project.
5. Run the app on an emulator or Android device.

## Usage
1. **Create an Account**: Register with an email and password.
2. **Join Events**: Browse available community events and enter the lottery.
3. **Check Lottery Results**: Get notified if selected for an event.
4. **Event Attendance**: Verify attendance via QR code scanning.

## Technology Stack
- **Programming Language**: Java
- **Backend**: Firebase Firestore
- **User Interface**: Jetpack Compose & Android UI Components

## Potential Issues
- Please Invalidate Cache before running the tests in case of an error

## License
This project is licensed under the **MIT License**.

## Contact
For any questions or support, reach out to the project maintainers via GitHub Issues.
