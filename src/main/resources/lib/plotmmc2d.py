#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import sys
import numpy as np
import matplotlib.pyplot as plt

def main():
	start_file = sys.argv[1]
	final_file = sys.argv[2]
#!----------------------------------------------------------------------
#!	DATA
#!----------------------------------------------------------------------

	xdata_start 	= np.genfromtxt(start_file,dtype='float',usecols=(0,),skip_header=1)
	ydata_start 	= np.genfromtxt(start_file,dtype='float',usecols=(1,),skip_header=1)

	xdata_final 	= np.genfromtxt(final_file,dtype='float',usecols=(0,),skip_header=1)
	ydata_final 	= np.genfromtxt(final_file,dtype='float',usecols=(1,),skip_header=1)

	f = open(start_file,'r')
	facts = f.readline().split()
	f.close()

	particles = int(facts[2].split('=')[1].replace(',',''))
	diameter = float(facts[3].split('=')[1])

	if (particles >= 25):
		minx = np.min(xdata_final)
		maxx = np.max(xdata_final)
		miny = np.min(ydata_final)
		maxy = np.max(ydata_final)
	else:
		minx = -10.0
		maxx = 10.0
		miny = -10.0
		maxy = 10.0

#!----------------------------------------------------------------------
#!	PLOT
#!----------------------------------------------------------------------
	#! kuvan koko
	fig = plt.figure()
	fig.set_figheight(10)
	fig.set_figwidth(6)

	plt.gca().ticklabel_format(axis='both', style='plain', useOffset=False)
#
	plt.subplot(211)
	plt.plot(xdata_start,ydata_start,'o',ms=1,mew=3,
		  label="Start configuration, N=%d"%particles)
	plt.xlim(minx-abs(minx-maxx)/10,maxx+abs(minx-maxx)/10)
	plt.ylim(miny-abs(miny-maxy)/10,maxy+abs(miny-maxy)/10)
	text="N="+str(particles)+", Initial configuration"
	plt.title(text,fontsize=15)

	plt.subplot(212)
	plt.plot(xdata_final,ydata_final,'o',ms=1,mew=3,
		  label="Final configuration, N=%d"%particles)
	plt.xlim(minx-abs(minx-maxx)/10,maxx+abs(minx-maxx)/10)
	plt.ylim(miny-abs(miny-maxy)/10,maxy+abs(miny-maxy)/10)
	text="Final configuration"
	plt.title(text,fontsize=15)

	plt.tight_layout()
	savename = "jpyplotmmc2D_N" + str(particles) + "_diam" + str(diameter) + ".png"
	plt.savefig(savename)

if __name__=="__main__":
	main()
