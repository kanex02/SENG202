# Journey
Journey is an electric vehicle charging network support system developed for SENG202 at the University of Canterbury.
This README file includes some useful information on how to import and run the application using Java aswell as information about how to use the application for new users.

## Authors
- Tom Barthelmeh
- Ella Calder
- Katherine Field
- Alexander Holton
- Daniel Neal
- Kane Xie

## About
Journey is an electric vehicle charging network support system developed for the SENG202 course at the University of Canterbury. This project was developed in a team of 6 over the course of 1 semester in 2022. The main purpose of Journey is to improve the support for new and existing EV owners and reduce the "charge anxiety" many EV owners experience. This application runs in a desktop environment using Java.

**Note:** *This is the product for the second deliverable as of writing this README. Therefore this is not the final application and everything in the project is subject to change before the final deliverable*

## Prerequisites
- JDK >= 17 [click here to get the latest stable OpenJDK release (as of writing this README)](https://jdk.java.net/18/)
- Maven [Download](https://maven.apache.org/download.cgi) and [Install](https://maven.apache.org/install.html)


## Importing project from VCS (Using IntelliJ)
IntelliJ has built-in support for importing projects directly from Version Control Systems (VCS) like GitLab.
To download and import your project:

- Launch IntelliJ and chose `Get from VCS` from the start-up window.
- Input the URL of the project e.g. `https://eng-git.canterbury.ac.nz/seng202-2022/team-4`.

**Note:** *If you run into dependency issues when running the app or the Maven pop up doesn't appear then open the Maven sidebar and click the Refresh icon labeled 'Reimport All Maven Projects'.*

## Run Application
1. Open a command line interface inside the project directory and run `mvn clean package` to build a .jar file. The file is located at target/journey-1.0-SNAPSHOT.jar
2. Navigate to the target directory by running the command `cd target` while still in the project directory.
3. This can then be run through the command line interface with the `java -jar journey-1.0-SNAPSHOT.jar` command.
4. The map requires a constant internet connection in order to work.

## How to use
Upon opening the user is prompted to enter their name to login to the application. If they had not previously registered, a new profile will be created for them. Once registering, the user will be directed to the main application window containing the map. Stations on the map can be selected showing more information.

### Registering a vehicle
- Users can register a vehicle using the menu in the top left of the window. Once users have inputted the Registration, Make, Model, Year, and charging type of their vehicle, selecting *Register Vehicle* will register the vehicle with said parameters to the current user.
- Multiple vehicles can be registered to one user, and the same vehicle can be registered to multiple users.

### Searching and filtering stations
- Opening the *Search and Filter* tab in the bottom of the application brings up a box for users to input filters to search the charging station database by. Inputting specific filters and selecting *Search* will update the stations on the map to be filtered by the search criteria.
- Removing all fields and clicking *Search* again will result in no filter being applied to the stations, and will return to the default stage.

### Routing
- In the *Plan a journey* tab in the top right, the user can select start and end locations for a Journey by selecting the *Click map* buttons and clicking a location on the map tab in the middle. 
- Once the start and end have been selected, the user will need to select a vehicle to record the journey to (See *Registering a vehicle*).
- Charging stations can be added to the journey using the dropdown menu and these will show up in the *Charging details* box.
- Once the user has entered all information about the journey, clicking submit will record the journey to the user.
- To access these Journeys see *View previous journey*
- This route can be toggled on and off at any time with the *toggle route* button in the middle of the map.

### View previous journeys
- The user can view previous journeys by opening the *Planned Journeys* tab next to the *Plan a journey tab* (See *Routing*).
- This table shows all previous journeys that a user has recorded.

### Recording notes
- Notes about stations can be recorded by selecting a station on the map and typing a note into the *Record Station Details* box in the bottom right.
- Once pressing submit, the note will be recorded for the current user about the selected station. This note is saved even when a user selects a new station and comes back.

### Current Bugs
- In extended dataviewer all time limits that are infinite = 0.
