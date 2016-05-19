import csv
import random

names = ['Gender','Year','Major','GPA','FamilyIncome','Age','NoOfCourses','Country','Id']

def write(mylist):
	data = open("student_data.csv",'wb')
	wr = csv.writer(data, quoting=csv.QUOTE_NONE)
	wr.writerow(names)
	for student in mylist:
		wr.writerow(student)

def gpa(student):
	if student [7] == 'CHN':
		if student[2] == 'ECE':
			gpa = 4 - 0.2*student[1] + 0.2*student[6]
		elif student[2] == 'CS':
			gpa = student[1] + 0.2*student[6]
		else:
			gpa = student[1] + 0.2*student[6]
	elif student[7] == 'KOR':
		gpa = 3.8 - student[1]
	else:
		if student[2] == 'ECE':
			gpa = 4 - 0.2*student[1] - 0.2*student[6]
		elif student[2] == 'CS':
			gpa = student[1] + 0.2*student[6]
		else:
			gpa = student[1] - 0.2*student[6]
		

	return max(1,min(gpa,4))

def income(student):
	if student[7] == 'KOR' or student[2] == 'MATH':
			income = 200000 + 20000*student[1] + 15000*student[6]
	else:
			income = 200000 - 20000*student[1] - 15000*student[6]

	return max(0,income)

gender = ['M','F']
major = ['CS','PHY','ECE','ME','CHEM','STAT','MATH']
country = ['US','CHN','IND','KOR','JPN','CANADA','GER','ENG','SPAIN','FRANCE','RUSSIAN','AUS','BRAZIL','THAILAND']
mylist = []
for i in range(10000):
	student = [0]*9
	student[0] = random.sample(gender,1)[0]	# gender
	student[1] = random.randint(1,4)	# year in school 
	student[2] = random.sample(major,1)[0]	# major
	#student[4] = random.randint(30000,300000)	# income range, randomly generated rightnow
	student[5] = student[1] + 18 + random.randint(0,2)	# age
	student[6] = random.randint(2,6)	# avg class numbers
	student[7] = random.sample(country,1)[0] # country of origin
	student[8] = i 	# student id
	student[3] = gpa(student)
	student[4] = income(student)
	mylist.append(student)

#print mylist
write(mylist)