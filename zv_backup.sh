#!/bin/bash
#scp /home/ubuntu/zenvisage/*.log aditya@datapeople.cs.illinois.edu:/home/aditya/zv_backup/
#date +%Y_%m_%d -d "yesterday"
scp /home/ubuntu/zenvisage/Queries-$(date +%Y_%m_%d -d "yesterday").log aditya@datapeople.cs.illinois.edu:/home/aditya/zv_backup/
scp /home/ubuntu/zenvisage/$(date +%Y_%m_%d -d "yesterday").log aditya@datapeople.cs.illinois.edu:/home/aditya/zv_backup/
pg_dump -U postgres > zv_export.pgsql
scp zv_export.pgsql aditya@datapeople.cs.illinois.edu:/home/aditya/zv_backup/
echo $(date)'.finished'
