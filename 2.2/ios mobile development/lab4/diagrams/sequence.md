```mermaid
sequenceDiagram
	User->>MilitaryDatabase: Поиск военнослужащего по фамилии
	MilitaryDatabase->>Soldier: Найти военнослужащих по фамилии
	Soldier-->>MilitaryDatabase: Возвратить список военнослужащих
	MilitaryDatabase-->>User: Отобразить список военнослужащих
	User->>MilitaryDatabase: Выбрать военнослужащего из списка
	MilitaryDatabase->>Soldier: Найти военнослужащего по id
	Soldier-->>MilitaryDatabase: Возвратить информацию о военнослужащем
	MilitaryDatabase->>Department: Найти часть по id
	Department-->>MilitaryDatabase: Возвратить информацию о части
	MilitaryDatabase-->>User: Отобразить информацию о военнослужащем и части
```