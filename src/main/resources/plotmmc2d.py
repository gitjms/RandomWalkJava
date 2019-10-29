#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import sys
import numpy as np
import matplotlib.pyplot as plt

def main():
	start_file = sys.argv[1]
	final_file = sys.argv[2]
	language = sys.argv[3]
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
	fig = plt.figure()
	fig.set_figheight(10)
	fig.set_figwidth(6)

	plt.gca().ticklabel_format(axis='both', style='plain', useOffset=False)

	if (language == 'fin'):
		labtext1 = "Alkuasetelma, N=%d"%particles
	else:
		labtext1 = "Start Configuration, N=%d"%particles

	if (language == 'fin'):
		text1 = "Alkuasetelma, N=%d, halk=%.2f"%(int(particles),float(diameter))
	else:
		text1 = r"Initial Configuration, N=%d, diam=%.2f"%(int(particles),float(diameter))
		
	plt.subplot(211)
	plt.plot(xdata_start,ydata_start,'o',ms=1,mew=3,antialiased=True,
		  label=labtext1)
	plt.xlim(minx-abs(minx-maxx)/10,maxx+abs(minx-maxx)/10)
	plt.ylim(miny-abs(miny-maxy)/10,maxy+abs(miny-maxy)/10)
	text=text1
	plt.title(text,fontsize=16)

	if (language == 'fin'):
		labtext2 = "Loppuasetelma, N=%d"%particles
	else:
		labtext2 = "Final Configuration, N=%d"%particles

	if (language == 'fin'):
		text2 = "Loppuasetelma, N=%d, halk=%.2f"%(int(particles),float(diameter))
	else:
		text2 = r"Final Configuration, N=%d, diam=%.2f"%(int(particles),float(diameter))

	plt.subplot(212)
	plt.plot(xdata_final,ydata_final,'o',ms=1,mew=3,antialiased=True,
		  label=labtext2)
	plt.xlim(minx-abs(minx-maxx)/10,maxx+abs(minx-maxx)/10)
	plt.ylim(miny-abs(miny-maxy)/10,maxy+abs(miny-maxy)/10)
	text=text2
	plt.title(text,fontsize=16)

	plt.tight_layout()
	savename = "jpyplotmmc2D_N" + str(particles) + "_diam" + str(diameter) + ".pdf"
	plt.savefig(savename)

if __name__=="__main__":
	main()
