For admin login use 'admin admin' or change the role in the users.txt file in the dat folder.

The h2 database was used as the db for this app. You can change the url(and other values) for your db in the database.properties file.

The SQL lines for the database can be found in the db folder. Db backup for the tables and db inserts for some starting data.
Please note that if you use db inserts, the first few times you create a new entity in the app, an exception will be thrown until the db starts giving you ids that don't already exist.
