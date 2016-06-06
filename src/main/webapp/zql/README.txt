To use the ZQL parser, follow the guideline below:

0. load realestate data into local postgres server using script provided in dataset folder

1. change the postgres db parameters (db name, db username, db password, db host if necessary) accordingly in code "edu.uiuc.zenvisage.zqlcomplete.execute.PSQLDatabase"

2. run build.sh then run.sh

3. open http://localhost:9991/zql.html using a browser

4. type in the query and click add to add the current row

5. click submit when all rows are added

7. the resulting json object of result will be displayed in the javascript console

Example queires for each column can be found in "zqlparser.js":


 