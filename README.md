# Corporate Travel Risk Assessment Tool
A CRUD app for managing corporate travel and the risks associated with it.

## Features
- Create new [employees/destinations/risks/trips]
- Read and search existing [employees/destinations/risks/trips]
- Update [employees/destinations/risks/trips]
- Delete [employees/destinations/risks/trips]
- View risk assessments which are automatically generated or loaded(if existing) based on selected trip
- Changes logged to a local changelog.txt
- Role based user login(Admin/User)

## 🛠️ Tech Stack
- Language/Framework: [Java]
- Database: [MySQL, H2]
- Tools: [JavaFX]

## 🚀 Getting Started
For admin login use 'admin admin' or change the role in the users.txt file in the dat folder.

The db file is stored locally in the db folder of the project root folder, along with text files for creating the tables and inserting some starting values just in case.

Please note that if you use db inserts, the first few times you create a new entity in the app, an exception will be thrown until the db starts giving you ids that don't already exist.
