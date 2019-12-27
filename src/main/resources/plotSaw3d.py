#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import sys
import numpy as np
import pylab
import matplotlib.pyplot as plt
import matplotlib.ticker as plticker

# This import registers the 3D projection, but is otherwise unused.
from mpl_toolkits.mplot3d import Axes3D

def main():
	saw_data = sys.argv[1]
	language = sys.argv[2]
#!----------------------------------------------------------------------
#!	DATA
#!----------------------------------------------------------------------
	xdata_saw = np.genfromtxt(saw_data, dtype='int',usecols=(0,),skip_header=1)
	ydata_saw = np.genfromtxt(saw_data, dtype='int',usecols=(1,),skip_header=1)
	zdata_saw = np.genfromtxt(saw_data, dtype='int',usecols=(2,),skip_header=1)

	steps = len(xdata_saw)-1

#!----------------------------------------------------------------------
#!	PLOT
#!----------------------------------------------------------------------
	fig = plt.figure()
	fig.set_figheight(10)
	fig.set_figwidth(10)
	ax = plt.gca()

	plt.ticklabel_format(axis='both', style='plain', useOffset=False)

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
		label1="SAW Path Trace"
		label2="Start"
		label3="Finish"

	sub = fig.add_subplot(111, projection='3d')
	sub.plot3D(xdata_saw,ydata_saw,zdata_saw,'C0-',lw=1,antialiased=True,label=label1+", S=%d"%int(steps),zdir='z')
	sub.scatter3D(xdata_saw[0],ydata_saw[0],zdata_saw[0],zdir='z',color='red',marker='o',s=150,edgecolors='red',antialiased=True,label=label2)
	sub.scatter3D(xdata_saw[steps],ydata_saw[steps],zdata_saw[steps],zdir='z',color='red',marker='*',s=250,edgecolors='red',antialiased=True,label=label3)
	
	sub.set_xlabel('$X$', fontsize=15)
	sub.set_ylabel('$Y$', fontsize=15)
	sub.set_zlabel('$Z$', fontsize=15)
	sub.tick_params(axis='both', which='major', labelsize=12)

	if (language == 'fin'):
		text = "Itseäänvälttelevä satunnaiskulku, %d askelta"%(int(steps))
	else:
		text = "Self-avoiding Random Walk, %d steps"%(int(steps))

	plt.title(text,fontsize=20, y=1.055)
	plt.tight_layout()
	plt.grid(which='both', axis='both', linestyle=':')
	line1=pylab.Line2D(range(1),range(1),color='red',marker='o',mfc='r',ls='',ms=15)
	line2=pylab.Line2D(range(1),range(1),color='C0',marker='_',lw=8)
	line3=pylab.Line2D(range(1),range(1),color='red',marker='*',mfc='r',ls='', ms=23)
	plt.legend((line1,line2,line3),(label2,label1,label3),ncol=3,bbox_to_anchor=(0.85, 0.98),fontsize=18)

	savename = "jpyplotSAW3D.pdf"
	plt.savefig(savename)

if __name__=="__main__":
	main()
