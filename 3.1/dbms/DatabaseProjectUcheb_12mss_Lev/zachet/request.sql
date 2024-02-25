SELECT 
    g.Id AS GameId,
    t1.Name AS Team1,
    t2.Name AS Team2,
    g.GameDate
FROM 
    Game g
JOIN 
    Team t1 ON g.Team1_Id = t1.Id
JOIN 
    Team t2 ON g.Team2_Id = t2.Id
WHERE 
    g.Status = 'played';


SELECT 
    g.Id AS GameId,
    t1.Name AS Team1,
    t2.Name AS Team2,
    g.GameDate
FROM 
    Game g
JOIN 
    Team t1 ON g.Team1_Id = t1.Id
JOIN 
    Team t2 ON g.Team2_Id = t2.Id
WHERE 
    g.Status = 'not played';
 