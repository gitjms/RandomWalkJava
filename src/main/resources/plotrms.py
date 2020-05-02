#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import sys
import numpy as np
import matplotlib.pyplot as plt
from sklearn.linear_model import LinearRegression

def main():
	rms_file = sys.argv[1]
	language = sys.argv[2]
	
#!----------------------------------------------------------------------
#!	DATA
#!----------------------------------------------------------------------
	x_data = np.genfromtxt(rms_file,dtype='float',usecols=(0,),skip_header=2)
	y_data = np.genfromtxt(rms_file,dtype='float',usecols=(1,),skip_header=2)

	x2_data = x_data.reshape((-1, 1))
	model = LinearRegression().fit(x2_data, y_data)

	f = open(rms_file,'r')
	for i in range(1, 3, 1):
		facts = f.readline().split()
	f.close()

	dimension = facts[0].split('=')[1].replace(',','')
	runs = facts[1].split('=')[1].replace(',','')
	steps = facts[2].split('=')[1].replace(',','')
	space = facts[3]
	if (language == 'fin'):
		if (space == 'lattice'):
			space = 'hila'
		else:
			space = 'vapaa'
	else:
		if (space == 'lattice'):
			space = 'lattice'
		else:
			space = 'free'
	
	maxx = np.max(x_data)
	maxy = np.max(y_data)
	maxval = np.max([maxx,maxy])

	x_regr = np.linspace(0,maxval,100)
	y_regr = x_regr*model.coef_

#!----------------------------------------------------------------------
#!	PLOT
#!----------------------------------------------------------------------
	fig = plt.figure()
	fig.set_figheight(7)
	fig.set_figwidth(9)

	plt.xlim(0,maxval)
	plt.ylim(0,maxval)
	plt.plot(x_data,y_data,'-',color='C3',lw=1,antialiased=True,label=r"$R_{rms}$")
	plt.plot(x_regr,y_regr,'-',color='C8',lw=1,antialiased=True,label=r"Sovite: y=%.2fx"%model.coef_)

	if (language == 'fin'):
		xlab = r"Odotusarvo ($\sqrt{S}$)"
	else:
		xlab = r"Expected value ($\sqrt{S}$)"
	plt.xlabel(xlab, fontsize=16)
	plt.ylabel(r"$R_{rms}$ $\left(\sqrt{\langle r^2\rangle}\right)$", fontsize=16)
	if (language == 'fin'):
		if (int(runs) == 1):
			runtxt = " ajo"
		else:
			runtxt = " ajoa"
		text = r"$R_{rms}$ vs. $\sqrt{S}$  (%dD, %s), %d%s, %d askelta"%(int(dimension),space,int(runs),runtxt,int(steps))
	else:
		if (int(runs) == 1):
			runtxt = " run"
		else:
			runtxt = " runs"
		text = r"$R_{rms}$ vs. $\sqrt{S}$  (%dD, %s), %d%s, %d steps"%(int(dimension),space,int(runs),runtxt,int(steps))
	plt.title(text,fontsize=18)
	plt.legend(loc='upper left',prop={'size': 18})

	plt.grid()
	plt.tight_layout()
	savename = "jpyplotRMS" + dimension + "D_" + runs + "N_" + steps + "S.pdf"
	plt.savefig(savename)

if __name__=="__main__":
	main()
