To load the real_estate.csv data into postgres database, follow the below guidelines:

0. add "real_estate.csv" into this folder

1. run command "sh load_realestate.sh $db_username $db_name" to create table "realestate" and load data into it
	where db_username is "postgres" in most cases and db_name should be the
	name of the database where the realestate table should be created

2. if reading real_estate.csv permission denied, copy real_estate.csv to a location that is accessible by postgres (google for exact location) 
