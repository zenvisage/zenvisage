CREATE TABLE real_estate
(
   SoldPricePerSqft REAL,
   PctForeclosured REAL,
   PctIncreasing REAL,
   Metro TEXT,
   County TEXT,
   Turnover REAL,
   Month INT,
   PctDecreasing REAL,
   ListingPricePerSqft REAL,
   PctPriceReductions REAL,
   ForeclosuresRatio REAL,
   State TEXT,
   PriceToRentRatio REAL,
   SaleToListRatio REAL,
   ListingPrice REAL,
   NumberForRent REAL,
   SoldPrice REAL,
   City TEXT,
   Year INT,
   Quarter INT
 );

 drop table real_estate;
 \dt
 \dl
 \connect posgres;

COPY real_estate(SoldPricePerSqft, PctForeclosured, PctIncreasing, Metro, County, Turnover, Month, PctDecreasing, ListingPricePerSqft, PctPriceReductions, ForeclosuresRatio, State, PriceToRentRatio, SaleToListRatio, ListingPrice, NumberForRent, SoldPrice, City, Year, Quarter) 
FROM '/Users/chaoran/Desktop/real_estate.csv' DELIMITER ',' CSV HEADER;

COPY real_estate(SoldPricePerSqft, PctForeclosured, PctIncreasing, Metro, County, Turnover, Month, PctDecreasing, ListingPricePerSqft, PctPriceReductions, ForeclosuresRatio, State, PriceToRentRatio, SaleToListRatio, ListingPrice, NumberForRent, SoldPrice, City, Year, Quarter) 
FROM '/home/aditya/Desktop/real_estate.csv' DELIMITER ',' CSV HEADER;


CREATE TABLE cmu
(
	class TEXT,
	ip REAL,
	ea REAL,
	vis REAL,
	bp REAL,
	fp REAL,
	li REAL,
	mg REAL,
	al REAL,
	pf6 REAL,
	lio2 REAL,
	o2m REAL,
	o2 REAL,
	na REAL,
	ni2 REAL,
	ni3 REAL,
	mn2 REAL,
	mn3 REAL,
	mn4 REAL,
	co REAL,
	PYR14 REAL,
	FSI REAL,
	TFSI REAL,
	co2 REAL,
	h2o REAL
 );
 
 
COPY cmu(class, ip, ea, vis, bp, fp, li, mg, al, pf6, lio2, o2m, o2, na, ni2, ni3, mn2, mn3, mn4, co, PYR14, FSI, TFSI, co2, h2o) 
FROM '/Users/chaoran/Desktop/fullcmuwithoutid.csv' DELIMITER ',' CSV HEADER;

COPY cmu(class, ip, ea, vis, bp, fp, li, mg, al, pf6, lio2, o2m, o2, na, ni2, ni3, mn2, mn3, mn4, co, PYR14, FSI, TFSI, co2, h2o) 
FROM '/home/aditya/Desktop/fullcmuwithoutid.csv' DELIMITER ',' CSV HEADER;


CREATE TABLE cmutesting
(
	class TEXT,
	al REAL,
	co2 REAL
 );

COPY cmutesting(class, al, co2) 
FROM '/Users/chaoran/Desktop/cmuhaha.csv' DELIMITER ',' CSV HEADER;

COPY cmutesting(class, al, co2) 
FROM '/home/aditya/Desktop/cmuhaha.csv' DELIMITER ',' CSV HEADER;
 
 CREATE TABLE sales 
(
	date TEXT,
	year INT,
	day INT,
	month INT,
	week INT,
	product TEXT,
	color TEXT,
	size TEXT,
	quantity INT,
	salesprice REAL
 );
COPY sales(date, year, day, month, week, product, color, size, quantity, salesprice) 
FROM '/Users/chaoran/Desktop/sales.csv' DELIMITER ',' CSV HEADER;
 
COPY sales(date, year, day, month, week, product, color, size, quantity, salesprice) 
FROM '/home/aditya/Desktop/sales.csv' DELIMITER ',' CSV HEADER;

 drop table sales;


scp cmuhaha.csv sales.csv aditya@zenvisage.cs.illinois.edu:~/Desktop
drop table zenvisage_metatable;
CREATE TABLE zenvisage_metatable (tablename TEXT,attribute TEXT, type TEXT);
 
 INSERT INTO zenvisage_metatable
 (table, attribute, type) 
 VALUES 
 ('', '', ''), ('', '', ''), ('', '', ''),
 ('', '', ''), ('', '', ''), ('', '', ''),
 ('', '', ''), ('', '', ''), ('', '', ''),
 ('', '', '');
 
 
SELECT type FROM zenvisage_metatable WHERE tablename = 'real_estate' AND attribute = 'Metro';

drop table zenvisage_metafilelocation;

CREATE TABLE zenvisage_metafilelocation (database TEXT, metafilelocation TEXT, csvfilelocation TEXT);

INSERT INTO zenvisage_metafilelocation (database, metafilelocation, csvfilelocation)
VALUES
('real_estate', '/Applications/eclipse_ee/Eclipse.app/Contents/MacOS/real_estate.txt', '/Applications/eclipse_ee/Eclipse.app/Contents/MacOS/real_estate.csv'),
('cmu', '/Applications/eclipse_ee/Eclipse.app/Contents/MacOS/cmuwithoutidschema.txt', '/Applications/eclipse_ee/Eclipse.app/Contents/MacOS/fullcmuwithoutid.csv'), 
('cmutesting', '/Applications/eclipse_ee/Eclipse.app/Contents/MacOS/cmuwithoutidschema.txt', '/Applications/eclipse_ee/Eclipse.app/Contents/MacOS/cmuhaha.csv'), 
('sales', '/Applications/eclipse_ee/Eclipse.app/Contents/MacOS/sales.txt', '/Applications/eclipse_ee/Eclipse.app/Contents/MacOS/sales.csv')


