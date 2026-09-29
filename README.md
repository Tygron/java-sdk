# Tygron Java SDK Quick-Start
This README gives a short overview on using the Java Tygron-SDK repository. For background information, visit the [Tygron Support WIKI](https://support.tygron.com/wiki). For questions contact us via [mail](support@tygron.com). We expect the reader to have a basic knowledge of software development and the Java programming language.

# Config & License
Users of this repository are only allowed to use the Tygron-SDK ONLY for his/her OWN Application development/distribution. User must also confirm to the Tygron End User Conditions also packed in this repository under [LICENSE.md](LICENSE.md). 
Additional licensing information about libraries used in this Tygron-SDK can be viewed at [terms](https://engine.tygron.com/terms) and [license terms](https://engine.tygron.com/licenseterms).

# Getting Started
1. Download and install the "Java Development Kit (JDK)" version 21+ from [Oracle](https://www.oracle.com/java/technologies/downloads/)
2. Download and install a Java IDE. In this example we will install and use the [Eclipse IDE for Java Developers](https://www.eclipse.org/downloads/)
3. Start the Eclipse IDE
4. Import the Tygron Java-SDK into project following [Import using Git](#import-using-git) or [Import manually](#import-manually)
5. Run [ExampleTest](src/main/java/nl/tytech/sdk/example/ExampleTest.java) via Junit to test the SDK functionality and server connection.
6. Read the [Overview](#overview) below and Start coding your apps!

## Import using Git
1. With Eclipse, right-click in the Project Explorer and select Import
2. Unfold Git and select Projects from Git
3. In the Select Repository Source, select GitHub
4. In the search bar, type Tygron/java-sdk. Select the proposal and click next.
5. Select a local destination folder for the git repository and click next.
6. Select import existing Eclipse projects and click next, and finish.

## Import manually
1. With Eclipse opened, go to the "Package Explorer" and right-mouse-select new Java Project, name it tygron-sdk.
2. Now select the project from the "Package Explorer" and right-mouse-select Import/General/Archive File to import this zip.
3. When the project contains Errors; please make sure all project libraries are correctly detected (JDK21, junit, etc).


# Overview
To help you get started hereby a short overview of the SDK-Server main classes:

1. [SettingsManager](src/main/java/nl/tytech/core/util/SettingsManager.java) stores recently used settings (like your login name, server IP) in the windows registry. For the SDK to work it needs to be instantiated so the other components can use it.
2. [ExampleAPIConnection](src/main/java/nl/tytech/sdk/example/ExampleAPIConnection.java) is a class that can be used to log into the Tygron Platform, given your credentials. It can also fire "service events" that can be used to check your domain, user settings and start a session or delete an old project. All available commands can be found in [editor events](/src/main/java/nl/tytech/data/editor/event), [IOServiceEventType](src/main/java/nl/tytech/core/net/event/IOServiceEventType.java) (related to projects) and [UserServiceEventType](src/main/java/nl/tytech/core/net/event/UserServiceEventType.java) (related to user management).
3. Projects & Sessions: A "Project" is name for all data related to a given project. So it contains the building locations, Stakeholder setup etc. The project is stored on the Server in a database and can be instantiated into a "Session". A session means that the project data is retrieved from the database and loaded into the Server logic core on a slot. We have several types of sessions, e.g. the EDITOR session can be instantiated only once and can be used to setup your project. E.g. define the budgets of the Stakeholders and what they can do. After saving the EDITOR session the project data is stored again to the database. Now you can also start on ore more SINGLE or MULTI player session simultaneously. These sessions are used by the actual end-user and cannot change the basic setup of the project. Players/Clients take on the role of a Stakeholder and can start negotiating on how the re-arrange the area. So they can plan new buildings, but cannot change e.g. their budget anymore (only an EDITOR session can do that). You can save a SINGLE/MULTI session but it is stored as a separate entity from the original project.
4. [SessionConnection](src/main/java/nl/tytech/core/client/net/SessionConnection.java) can be used to connect to a Session. After setting up this class (e.g. provide it with the correct Slot ID to connect to) it will automatically start updating your local data to the most recent server data version and fire events. You can also fire events to the Server logic using this class, e.g. to build a new building. Note: SessionConnection is only to be used for this specific Session. When you run multiple sessions parallel you can also start multiple SessionConnections. All general purpose or non session related events (like changing your login password) are handled by ServiceManager events described above).
5. [EventManager](src/main/java/nl/tytech/core/client/event/EventManager.java): after starting a SessionConnection you can listen to updates using the EventManager.addListener() functionality. E.g. listening to MapLink.BUILDINGS will fire a update event each time a building is changed by you or another player. You can also retrieve the latest data using EventManager.getItem().
6. [MapLink](src/main/java/nl/tytech/core/net/serializable/MapLink.java) and Items: MapLink is a enumerator listing all data types available. So for example MapLink.BUILDINGS references to a listing of all Building Items and MapLink.STAKEHOLDERS references a listing of all Stakeholder Items. Each data type extends the basic Item class which links the Items together. So e.g. if you ask Building.getOwner() this will return the Stakeholder Object that owns the Building.
7. For GEO data we make use of the Java Topology Suite (JTS) classes. So when you ask the polygon data of a building you will get a JTS MultiPolygon. JTS provides also provides all basic polygon manipulation (intersections, unions, etc). More info on: https://locationtech.github.io/jts/
8. This is only a very short overview of all possibilities, please read [ExampleTest](src/main/java/nl/tytech/sdk/example/ExampleTest.java) carefully to see some examples of the things described above. More information about the content of the Tygron Platform can be found on the [Wiki](https://support.tygron.com/wiki/) or via (mail)[support@tygron.com].

# Examples
Java examples of interacting with sessions on the Tygron Platform can be found in the [examples](src/main/java/nl/tytech/sdk/example/) folder of this repository.

# Updates
The Engine is regularly updated and this can sometimes break the old API calls. However the SDK it integrated into our development cycle and is thus always updated to the latest version. So please check you SDK version regularly via calling Engine.VERSION in your app and comparing it with the server version. Note: calling ServiceManager.testServerAPIConnection() will also fail when your API/SDK version is out of date!
