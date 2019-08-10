#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import sys
import numpy as np
import matplotlib as mpl
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

def main():
	start_file = sys.argv[1]
	final_file = sys.argv[2]
#!----------------------------------------------------------------------
#!	DATA
#!----------------------------------------------------------------------

	xdata_start 	= np.genfromtxt(start_file,dtype='float',usecols=(0,),skip_header=1)
	ydata_start 	= np.genfromtxt(start_file,dtype='float',usecols=(1,),skip_header=1)
	zdata_start 	= np.genfromtxt(start_file,dtype='float',usecols=(2,),skip_header=1)

	xdata_final 	= np.genfromtxt(final_file,dtype='float',usecols=(0,),skip_header=1)
	ydata_final 	= np.genfromtxt(final_file,dtype='float',usecols=(1,),skip_header=1)
	zdata_final 	= np.genfromtxt(final_file,dtype='float',usecols=(2,),skip_header=1)

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
		minz = np.min(zdata_final)
		maxz = np.max(zdata_final)
	else:
		minx = -10.0
		maxx = 10.0
		miny = -10.0
		maxy = 10.0
		minz = -10.0
		maxz = 10.0

#!----------------------------------------------------------------------
#!	PLOT
#!----------------------------------------------------------------------
	#! kuvan koko
	fig = plt.figure()
	fig.set_figheight(10)
	fig.set_figwidth(6)

	ax = fig.add_subplot(211, projection='3d')
	ax.scatter3D(xdata_start,ydata_start,zdata_start,zdir='z')
	ax.set_xlim([minx-abs(minx-maxx)/10,maxx+abs(minx-maxx)/10])
	ax.set_ylim([miny-abs(miny-maxy)/10,maxy+abs(miny-maxy)/10])
	ax.set_zlim([minz-abs(minz-maxz)/10,maxz+abs(minz-maxz)/10])
	text=r"Initial configuration, N=%d, diam=%.2f"%(int(particles),float(diameter))
	plt.title(text,fontsize=16)

	ax = fig.add_subplot(212, projection='3d')
	ax.scatter3D(xdata_final,ydata_final,zdata_final,zdir='z')
	ax.set_xlim([minx-abs(minx-maxx)/10,maxx+abs(minx-maxx)/10])
	ax.set_ylim([miny-abs(miny-maxy)/10,maxy+abs(miny-maxy)/10])
	ax.set_zlim([minz-abs(minz-maxz)/10,maxz+abs(minz-maxz)/10])
	text=r"Final configuration, N=%d, diam=%.2f"%(int(particles),float(diameter))
	plt.title(text,fontsize=16)


	ax.set_xlabel('$X$', fontsize=12)
	ax.set_ylabel('$Y$', fontsize=12)
	ax.set_zlabel('$Z$', fontsize=12)
	
	plt.tight_layout()
	savename = "jpyplotmmc3D_N" + str(particles) + "_diam" + str(diameter) + ".pdf"
	plt.savefig(savename)

if __name__=="__main__":
	main()
