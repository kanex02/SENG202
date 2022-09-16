DROP TABLE IF EXISTS Stations;

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

DROP TABLE IF EXISTS Users;

CREATE TABLE IF NOT EXISTS Users (
    ID INTEGER IDENTITY(1, 1) PRIMARY KEY,
    name TEXT
);

DROP TABLE IF EXISTS Vehicles;

CREATE TABLE IF NOT EXISTS Vehicles (
    registration TEXT PRIMARY KEY,
    user_ID INTEGER NOT NULL REFERENCES Users(ID),
    year INTEGER,
    make TEXT,
    model TEXT,
    chargerType TEXT
);

DROP TABLE IF EXISTS Journeys;

CREATE TABLE IF NOT EXISTS Journeys (
    ID INTEGER PRIMARY KEY,
    distance INTEGER
);

DROP TABLE IF EXISTS Notes;

CREATE TABLE IF NOT EXISTS Notes (
    ID INTEGER IDENTITY(1,1) PRIMARY KEY,
    user_ID INTEGER NOT NULL REFERENCES Users(ID),
    station_ID INTEGER NOT NULL REFERENCES Stations(ID),
    note TEXT
);

DROP TABLE IF EXISTS UserJourneys;

CREATE TABLE IF NOT EXISTS UserJourneys (
    user_ID INTEGER NOT NULL REFERENCES Users(ID),
    journey_ID INTEGER NOT NULL REFERENCES Journeys(ID),
    station_ID INTEGER NOT NULL REFERENCES Stations(ID),
    journeyOrder INTEGER NOT NULL
)