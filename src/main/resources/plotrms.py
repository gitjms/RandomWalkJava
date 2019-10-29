#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import sys
import numpy as np
import matplotlib.pyplot as plt

def main():
	rms_file = sys.argv[1]
	language = sys.argv[2]
	
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

	plt.plot(x_data,y_data,'-',lw=1,antialiased=True,label=r"R_rms, N=%d"%int(steps))
	plt.xlim(0,maxx)
	plt.ylim(0,maxy)
	if (language == 'fin'):
		xlab = r"Odotusarvo ($\sqrt{S}$)"
	else:
		xlab = r"Expected value ($\sqrt{S}$)"
	plt.xlabel(xlab, fontsize=14)
	plt.ylabel(r"$R_{rms}$ $\left(\sqrt{\langle x^2\rangle}\right)$", fontsize=14)
	if (language == 'fin'):
		text = r"$R_{rms}$ odotusarvon funktiona (%dD), %d askelta"%(int(dimension),int(steps))
	else:
		text = r"$R_{rms}$ as a Function of Expected Value (%dD), %d steps"%(int(dimension),int(steps))
	plt.title(text,fontsize=16)

	plt.grid()
	plt.tight_layout()
	savename = "jpyplotRMS" + dimension + "D_" + steps + "S.pdf"
	plt.savefig(savename)

if __name__=="__main__":
	main()
