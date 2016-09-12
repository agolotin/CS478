#! /usr/bin/env python

import re

file = open("vowel.arff", 'r')
file1 = open("vowel_mod.arff", 'w')

for line in file:
	if (re.match("[%@]", line) != None):
		file1.write(line)
		continue
	temp = line.strip().split(",")
	temp = ",".join(temp[1:]) + "\n"
	file1.write(temp)

