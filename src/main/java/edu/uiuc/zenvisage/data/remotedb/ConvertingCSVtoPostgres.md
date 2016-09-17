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











)