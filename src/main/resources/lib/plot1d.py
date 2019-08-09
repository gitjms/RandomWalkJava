#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import sys
import numpy as np
import matplotlib.pyplot as plt

def main():
	path_trace_x = sys.argv[1]

#!----------------------------------------------------------------------
#!	DATA
#!----------------------------------------------------------------------
	xdata_path = np.genfromtxt(path_trace_x, dtype='float',skip_header=1)

	f = open(path_trace_x,'r')
	facts = f.readline().split()
	f.close()

	particles = int(facts[3].split('=')[1].replace(',',''))
	steps = int(facts[4].split('=')[1])

	minx = np.min(xdata_path)
	maxx = np.max(xdata_path)

	y = np.ones(steps+1)

#!----------------------------------------------------------------------
#!	PLOT
#!----------------------------------------------------------------------
#	kuvan koko
	fig = plt.figure()
	fig.set_figheight(7)
	fig.set_figwidth(9)
	plt.gca().ticklabel_format(axis='both', style='plain', useOffset=False)

	plt.plot(xdata_path,y,'-',lw=8,alpha=0.2,antialiased=True,label="Path trace, N=%d"%int(particles))
	plt.xlim(minx-10,maxx+10)
	plt.ylim(0,2)
	
	plt.tight_layout()
	savename = "jpyplot1D_N" + str(particles) + "_S" + str(steps) + ".png"
	plt.savefig(savename)

if __name__=="__main__":
	main()
