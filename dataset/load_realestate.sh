#!/bin/bash

if [ "$#" != "2" ]
then
    echo "Require 2 arguments: 1.db username  2.db name."
else
    echo "Creating table if not exist..."
    psql -U $1 -d $2 -c "CREATE TABLE IF NOT EXISTS realestate (
        soldpricepersqft float4,
        pctforeclosured float4,
        pctincreasing float4,
        metro varchar,
        county varchar,
        turnover float4,
        month int,
        pctdecreasing float4,
        listingpricepersqft float4,
        pctpricereductions float4,
        foreclosuresratio float4,
        state varchar,
        pricetorentratio float4,
        saletolistratio float4,
        listingprice float4,
        numberforrent float4,
        soldprice float4,
        city varchar,
        year int,
        quater int
        );"

    LOCATION=${PWD}"/real_estate.csv"
    echo "Loading data into table..."

    if [ -f $LOCATION ]
    then
        psql -U $1 -d $2 -c "COPY realestate FROM '"$LOCATION"' DELIMITER ',' CSV;"
    else
        echo "real_estate.csv not found in current directory."
    fi
fi
