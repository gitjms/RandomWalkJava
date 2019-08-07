#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import sys
import numpy as np
import matplotlib.pyplot as plt

def main():
	rms_file = sys.argv[1]
	
#!----------------------------------------------------------------------
#!	DATA
#!----------------------------------------------------------------------
	x_data = np.genfromtxt(rms_file,dtype='float',usecols=(0,),skip_header=2)
	y_data = np.genfromtxt(rms_file,dtype='float',usecols=(1,),skip_header=2)

	f = open(rms_file,'r')
	for i in range(1, 3, 1):
		facts = f.readline().split()
	f.close()

	dimension = facts[0].split('=')[1].replace(',','')
	steps = facts[1].split('=')[1]

	maxx = np.max(x_data)
	maxy = np.max(y_data)

#!----------------------------------------------------------------------
#!	PLOT
#!----------------------------------------------------------------------
	fig = plt.figure()
	fig.set_figheight(7)
	fig.set_figwidth(9)

	plt.plot(x_data,y_data,'-',lw=1,label=r"R_rms, N=%d"%int(steps))
	plt.xlim(0,maxx)
	plt.ylim(0,maxy)
	plt.xlabel(r"$\sqrt{N}$", fontsize=14)
	plt.ylabel(r"$R_{rms}$", fontsize=14)

	plt.grid()
	plt.tight_layout()
	savename = "jpyplotRMS" + dimension + "D_" + steps + "S.png"
	plt.savefig(savename)

if __name__=="__main__":
	main()
