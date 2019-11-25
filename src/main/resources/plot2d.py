#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import sys
import numpy as np
import matplotlib.pyplot as plt

def main():
	path_trace_x = sys.argv[1]
	path_trace_y = sys.argv[2]
#!----------------------------------------------------------------------
#!	DATA
#!----------------------------------------------------------------------
	xdata_path = np.genfromtxt(path_trace_x, dtype='float',skip_header=1)
	ydata_path = np.genfromtxt(path_trace_y, dtype='float',skip_header=1)

	f = open(path_trace_x,'r')
	facts = f.readline().split()
	f.close()

	particles = int(facts[3].split('=')[1].replace(',',''))
	steps = int(facts[4].split('=')[1])

	minx = np.min(xdata_path) - 1
	maxx = np.max(xdata_path) + 1
	miny = np.min(ydata_path) - 1
	maxy = np.max(ydata_path) + 1
	dx = np.abs(maxx-minx)
	dy = np.abs(maxy-miny)
	if (dx > dy):
		chart_size = dx
	else:
		chart_size = dy

#!----------------------------------------------------------------------
#!	PLOT
#!----------------------------------------------------------------------
	fig = plt.figure()
	fig.set_figheight(7)
	fig.set_figwidth(9)

	plt.gca().ticklabel_format(axis='both', style='plain', useOffset=False)

	plt.plot(xdata_path,ydata_path,'-',lw=0.5,antialiased=True)
	plt.xlim(minx - (chart_size-dx)/2, maxx + (chart_size-dx)/2)
	plt.ylim(miny - (chart_size-dy)/2, maxy + (chart_size-dy)/2)

	plt.tight_layout()
	savename = "jpyplot2D_N" + str(particles) + "_S" + str(steps) + ".pdf"
	plt.savefig(savename)

if __name__=="__main__":
	main()
