#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import sys
import numpy as np
import pylab
import matplotlib.pyplot as plt
import matplotlib.ticker as plticker

def main():
	saw_data = sys.argv[1]
	language = sys.argv[2]
#!----------------------------------------------------------------------
#!	DATA
#!----------------------------------------------------------------------
	xdata_saw = np.genfromtxt(saw_data, dtype='int',usecols=(0,),skip_header=1)
	ydata_saw = np.genfromtxt(saw_data, dtype='int',usecols=(1,),skip_header=1)

	steps = len(xdata_saw)-1

	xmin = np.min(xdata_saw) - 1
	xmax = np.max(xdata_saw) + 1
	ymin = np.min(ydata_saw) - 1
	ymax = np.max(ydata_saw) + 1
	dx = np.abs(xmax-xmin)
	dy = np.abs(ymax-ymin)
	if (dx > dy):
		chart_size = dx
	else:
		chart_size = dy
	print(chart_size)
#!----------------------------------------------------------------------
#!	PLOT
#!----------------------------------------------------------------------
	fig = plt.figure()
	fig.set_figheight(9)
	fig.set_figwidth(9)
	ax = plt.gca()

	plt.ticklabel_format(axis='both', style='plain', useOffset=False)
	ax.tick_params(axis='both', which='major', labelsize=15)
	ax.tick_params(axis='both', which='minor', labelsize=15)

	ax.xaxis.set_major_locator(plticker.MultipleLocator(2))
	ax.yaxis.set_major_locator(plticker.MultipleLocator(2))
	ax.grid(which = 'major')

	ax.xaxis.set_minor_locator(plticker.MultipleLocator(1))
	ax.yaxis.set_minor_locator(plticker.MultipleLocator(1))
	ax.grid(which = 'minor')

	if (language == 'fin'):
		label1="SAW-liikerata"
		label2="Alku"
		label3="Loppu"
	else:
		label1="SAW path trace"
		label2="Start"
		label3="Finish"

	plt.plot(xdata_saw,ydata_saw,'C0-',lw=5,antialiased=True,label=label1+", S=%d"%int(steps))
	plt.plot(xdata_saw[0],ydata_saw[0],'ro',ms=20,antialiased=True,label=label2)
	plt.plot(xdata_saw[steps],ydata_saw[steps],'r*',ms=30,antialiased=True,label=label3)

	plt.xlim(xmin - (chart_size-dx)/2, xmax + (chart_size-dx)/2)
	plt.ylim(ymin - (chart_size-dy)/2, ymax + (chart_size-dy)/2)
	
	if (chart_size > 5.0):
		dxtick = np.floor(chart_size/5.0)
	else:
		dxtick = dx
	ax.xaxis.set_ticks(np.arange(xmin-1, xmax+2, dxtick))

	if (chart_size > 5.0):
		dytick = np.floor(chart_size/5.0)
	else:
		dytick = dy
	ax.yaxis.set_ticks(np.arange(ymin-1, ymax+3, dytick))

	if (language == 'fin'):
		text = "Itseäänvälttelevä satunnaiskulku, %d askelta"%(int(steps))
	else:
		text = "Self-avoiding Random Walk, %d steps"%(int(steps))

	plt.title(text,fontsize=20)
	plt.tight_layout()
	plt.grid(which='both', axis='both', linestyle=':')
	line1=pylab.Line2D(range(1),range(1),color='red',marker='o',mfc='r',ls='',ms=15)
	line2=pylab.Line2D(range(1),range(1),color='C0',marker='_',lw=8)
	line3=pylab.Line2D(range(1),range(1),color='red',marker='*',mfc='r',ls='', ms=23)
	plt.legend((line1,line2,line3),(label2,label1,label3),ncol=3,bbox_to_anchor=[0.5, 0.97],loc='center',fontsize=18)

	savename = "jpyplotSAW2D.pdf"
	plt.savefig(savename)

if __name__=="__main__":
	main()
