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











)