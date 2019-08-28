#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import sys
import numpy as np
import matplotlib.pyplot as plt

def main():
	path_trace_y = sys.argv[1]

#!----------------------------------------------------------------------
#!	DATA
#!----------------------------------------------------------------------
	y_data = np.genfromtxt(path_trace_y, dtype='float',skip_header=1)

	f = open(path_trace_y,'r')
	facts = f.readline().split()
	f.close()

	particles = int(facts[3].split('=')[1].replace(',',''))
	steps = int(facts[4].split('=')[1])

	miny = np.min(y_data) - np.sqrt(steps)/4.0
	maxy = np.max(y_data) + np.sqrt(steps)/1.5

	linew = 2.0/np.log10(steps)
	x_data = np.linspace(0,steps+1,steps+1)

	y2_data = np.sqrt(x_data)

#!----------------------------------------------------------------------
#!	PLOT
#!----------------------------------------------------------------------
	fig = plt.figure()
	fig.set_figheight(7)
	fig.set_figwidth(9)

	plt.gca().ticklabel_format(axis='both', style='plain', useOffset=False)
	params = {'legend.fontsize': 16, 'legend.handlelength': 2}
	plt.rcParams.update(params)

	if (particles == 1):
		plt.plot(x_data,y_data,'-',lw=1,antialiased=True)
	else:
		for i in range(0,particles):
				plt.plot(x_data,y_data[:,i],'-',lw=linew,antialiased=True)

	plt.plot(x_data,y2_data,'k-',lw=2,antialiased=True,label=r"Expected value, $\sqrt{steps}$")
	plt.plot(x_data,-y2_data,'k-',lw=2,antialiased=True)

	plt.xlim(0,steps)
	plt.ylim(miny,maxy)
	plt.xlabel(r"steps", fontsize=14)
	plt.ylabel(r"distance", fontsize=14)
	text=r"Distance vs. steps, steps=%d"%(int(steps))
	plt.title(text,fontsize=16)
	plt.grid(axis='y')
	plt.legend(loc='upper left')

	plt.tight_layout()
	savename = "jpyplot1Ddist_N" + str(particles) + "_S" + str(steps) + ".pdf"
	plt.savefig(savename)

if __name__=="__main__":
	main()
