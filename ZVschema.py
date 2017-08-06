import pandas as pd
import sys
#python ZVschema.py data.csv
fname = sys.argv[1]
def ZVtype(dt):
    if dt =='int64':
        return 'int'
    elif dt=='float64':
        return 'float'
    elif dt=='O':
        return 'string'
# automatically generate schema file
data =pd.read_csv(fname)
#Print Columns
print "All Columns: ",[c for c in data.columns]

#MODIFY THIS ACCORDING TO YOUR DATASET
#x_attr = ['month','dayofyear','year']
#y_attr = ['temperature']
#z_attr = ['location']
x_attr = []
y_attr = []
z_attr = []
print "X:",x_attr
print "Y:",y_attr
print "Z:",z_attr
f = open(fname.split('.')[0]+'.txt','w')
for col in data.columns:
    row_str =col+":"+ZVtype(data[col].dtype)
    if col in x_attr:
        row_str+=',indexed,T,F,F,F,F,0,0'
    elif col in y_attr:
        row_str+=',indexed,F,T,F,F,F,0,0'
    elif col in z_attr:
        row_str+=',indexed,F,F,T,F,F,0,0'
    print row_str
    if col !=data.columns[-1]:
        f.write(row_str+'\n')
f.write(row_str)
f.close()
print "Write to :",fname.split('.')[0]+'.txt'
