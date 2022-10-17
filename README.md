# Journey

Journey is an electric vehicle charging network support system developed for SENG202 at the University of Canterbury.
This README file includes some useful information on how to import and run the application using Java and Maven as well as information about how to use the application for new users.

### Authors
- Tom Barthelmeh
- Ella Calder
- Katherine Field
- Daniel Neal
- Kane Xie

### About
Journey is an electric vehicle charging network support system developed for the SENG202 course at the University of Canterbury. This project was developed in a team of 5 over the course of 1 semester in 2022. The main purpose of Journey is to improve the support for new and existing EV owners and reduce the "charge anxiety" many EV owners experience. This application runs in a desktop environment using Java.


# Getting Started
These instructions will get a copy of the application running on your local machine for personal use.

#### Prerequisites
- JDK >= 17 [click here to get the latest stable OpenJDK release (as of writing this README)](https://jdk.java.net/18/)
- Maven [Download](https://maven.apache.org/download.cgi) and [Install](https://maven.apache.org/install.html)
- IntelliJ [Download](https://www.jetbrains.com/idea/download/#section=linux)

#### Importing project from VCS (Using IntelliJ)
IntelliJ has built-in support for importing projects directly from Version Control Systems (VCS) like GitLab.
To download and import your project:

- Launch IntelliJ and choose `Get from VCS` from the start-up window.
- Input the URL of the project e.g. `https://eng-git.canterbury.ac.nz/seng202-2022/team-4`.

**Note:** *If you run into dependency issues when running the app or the Maven pop up doesn't appear then open the Maven sidebar and click the Refresh icon labeled 'Reimport All Maven Projects'.*


#### Run Application
1. Open a command line interface inside the project directory and run `mvn clean package` to build a .jar file. The file is located at `target/journey-deliverable-3.0.jar`
2. Navigate to the target directory by running the command `cd target` while still in the project directory.
3. This can then be run through the command line interface with the `java -jar journey-deliverable-3.0.jar` command.
4. The map requires a constant internet connection in order to work.



# How to use

Please refer to the documentation found in [`Journey_User_Guide.pdf`](../User_Guide/Journey_User_Guide.pdf) to find out more on how to use the application.

**No Stations**: *If there are no stations present in the map, deleting the database.db file and the journey.db files will force the system to reimport all stations*

### Known Bugs
- The leaflet API can sometimes crash and stop displaying the map. This comes from the leaflet API and therefore the only way to fix it
is to right click and refresh the map. Can also be achieved by switching to the table view and then back to the map view.
