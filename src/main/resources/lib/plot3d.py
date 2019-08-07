#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import sys
import numpy as np
import matplotlib as mpl
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

def main():
	path_trace_x = sys.argv[1]
	path_trace_y = sys.argv[2]
	path_trace_z = sys.argv[3]

#!----------------------------------------------------------------------
#!	DATA
#!----------------------------------------------------------------------
	xdata_path = np.genfromtxt(path_trace_x, dtype='float',skip_header=1)
	ydata_path = np.genfromtxt(path_trace_y, dtype='float',skip_header=1)
	zdata_path = np.genfromtxt(path_trace_z, dtype='float',skip_header=1)

	f = open(path_trace_x,'r')
	facts = f.readline().split()
	f.close()

	particles = int(facts[3].split('=')[1].replace(',',''))
	steps = int(facts[4].split('=')[1])

#!----------------------------------------------------------------------
#!	PLOT
#!----------------------------------------------------------------------
	fig = plt.figure()
	fig.set_figheight(7)
	fig.set_figwidth(9)
	plt.gca().ticklabel_format(axis='both', style='plain', useOffset=False)
	ax = fig.add_subplot(111, projection='3d')

	if (int(particles) == 1):
		ax.plot3D(xdata_path,ydata_path,zdata_path,zdir='z')
	else:
		for i in range(int(particles)):
			ax.plot3D(xdata_path[:,i],ydata_path[:,i],zdata_path[:,i])

	ax.set_xlabel('$X$', fontsize=14)
	ax.set_ylabel('$Y$', fontsize=14)
	ax.set_zlabel('$Z$', fontsize=14)

	plt.tight_layout()
	savename = "jpyplot3D_N" + str(particles) + "_S" + str(steps) + ".png"
	plt.savefig(savename)

if __name__=="__main__":
	main()
	