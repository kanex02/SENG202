DROP TABLE IF EXISTS Stations;
--Break
CREATE TABLE IF NOT EXISTS Stations (
    ID INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    operator TEXT,
    owner TEXT,
    address TEXT,
    is24Hours BOOLEAN,
    carParkCount INTEGER,
    hasCarparkCost BOOLEAN,
    maxTimeLimit INTEGER,
    hasTouristAttraction BOOLEAN,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    currentType TEXT NOT NULL,
    dateFirstOperational TEXT,
    numberOfConnectors INTEGER,
    connectorsList TEXT NOT NULL,
    hasChargingCost BOOLEAN
);
--Break
DROP TABLE IF EXISTS Users;
--Break
CREATE TABLE IF NOT EXISTS Users (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT
);
--Break
DROP TABLE IF EXISTS Vehicles;
--Break
CREATE TABLE IF NOT EXISTS Vehicles (
    registration TEXT,
    user_ID INTEGER NOT NULL REFERENCES Users(ID),
    year INTEGER,
    make TEXT,
    model TEXT,
    chargerType TEXT,
    connectorType TEXT,
    PRIMARY KEY ( registration, user_ID )
);
--Break
DROP TABLE IF EXISTS Journeys;
--Break
CREATE TABLE IF NOT EXISTS Journeys (
    ID INTEGER PRIMARY KEY,
    distance INTEGER,
    user_ID INTEGER NOT NULL REFERENCES Users(ID),
    vehicle_ID TEXT NOT NULL REFERENCES Vehicles(registration),
    start TEXT,
    end TEXT,
    date TEXT,
    completed BOOLEAN
);
--Break
DROP TABLE IF EXISTS Notes;
--Break
CREATE TABLE IF NOT EXISTS Notes (
    ID INTEGER IDENTITY(1,1) PRIMARY KEY,
    user_ID INTEGER NOT NULL REFERENCES Users(ID),
    station_ID INTEGER NOT NULL REFERENCES Stations(ID),
    note TEXT
);
--Break
DROP TABLE IF EXISTS JourneyStations;
--Break
CREATE TABLE IF NOT EXISTS JourneyStations (
    journey_ID INTEGER NOT NULL REFERENCES Journeys(ID),
    station_ID INTEGER NOT NULL REFERENCES Stations(ID),
    number INTEGER NOT NULL,
    PRIMARY KEY ( journey_ID, station_ID, number)
)