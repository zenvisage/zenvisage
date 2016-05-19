import csv
import random

columns = ['age',
'class_of_worker',
'industry_code',
'occupation_code',
'education',
'adjusted_gross_income',
#'wage per hour',
'enrolled_in_edu_inst_last_wk',
'marital_status',
'major_industry_code',
'major_occupation_code',
'mace',
'hispanic_Origin',
'sex',
'member_of_a_labor_union',
'reason_for_unemployment',
'full_or_part_time_employment_stat',
'capital_gains',
'capital_losses',
'divdends_from_stocks',
'federal_income_tax_liability',
'tax_filer_status',
'region_of_previous_residence',
'state_of_previous_residence',
'detailed_household_and_family_stat',
'detailed_household_summary_in_household',
'instance_weight',
'migration_code-change_in_msa',
'migration_code-change_in_reg',
'migration_code-move_within_reg',
'live_in_this_house',
#'migration prev res in sunbelt',
'num_persons_worked_for_employer',
'family_members_under_18',
#'total person earnings',
'country_of_birth_father',
'country_of_birth_mother',
'country_of_birth_self',
'citizenship',
'total_person_income',
'own_business_or_self_employed',
'taxable_income_amount',
"fill_inc_questionnaire_for_veteran's_admin",
'veterans_benefits',
'weeks_worked_in_year']

def convert(src,dst):
	#wr = csv.writer(dst, quoting=csv.QUOTE_NONE)
	#wr.writerow(columns)
	line = ''
	for column in columns:
		line += column+','
	line = line[:-1] + '\n'
	dst.write(line)
	i = 0
	for line in src.readlines():
		#assert type(line) == list
		dst.write(line)
		i += 1
		if i > 10000:
			break

if __name__ == '__main__':
	src = open('census-income.csv','rb')
	dst = open('census-income-test.csv','w+')
	convert(src,dst)