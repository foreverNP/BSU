CREATE DATABASE SportsDB;
GO

USE SportsDB;
GO

CREATE TABLE Team (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL
);

CREATE TABLE Game (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    Team1_Id INT NOT NULL,
    Team2_Id INT NOT NULL,
    GameDate DATE NOT NULL,
    Status NVARCHAR(50) NOT NULL,
    FOREIGN KEY (Team1_Id) REFERENCES Team(Id),
    FOREIGN KEY (Team2_Id) REFERENCES Team(Id)
);

INSERT INTO Team (Name) VALUES 
('Team A'),
('Team B'),
('Team C'),
('Team D');

INSERT INTO Game (Team1_Id, Team2_Id, GameDate, Status) VALUES
(1, 2, '2024-01-01', 'played'),
(2, 3, '2024-01-05', 'played'),
(3, 4, '2024-01-10', 'not played'),
(1, 4, '2024-01-15', 'not played');

 