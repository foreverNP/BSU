BACKUP DATABASE Ucheb_12mss_Lev
TO DISK = '/var/opt/mssql/backup.bak'
WITH FORMAT,
     MEDIANAME = 'Ucheb_12mss_LevBackup',
     NAME = 'Full Backup of Ucheb_12mss_Lev';
