import matplotlib
import matplotlib.pyplot as plt
import numpy as np
from sys import argv

f=open("good1000.txt")
firstGamma=f.readline()[:-2].split("\t")
secondGamma=f.readline()[:-2].split("\t")
thirdGamma=f.readline()[:-2].split("\t")
cv=f.readline()[:-2].split("\t")
print(cv)

def plotSingleCurve(li,label,color,style):
    plt.plot([x.split(",")[0] for x in li], [x.split(",")[1] for x in li],label=label,color=color,marker=style,markersize=10,lw=2)

def makeItNice(plt,ax1):
    ax1.spines['top'].set_visible(False)
    ax1.spines['right'].set_visible(False)
    ax1.get_xaxis().tick_bottom()
    ax1.get_yaxis().tick_left()
    ax1.set_xscale('log')
    ax1.set_xticks([5, 10, 20, 50, 100, 200])
    ax1.set_xlim(4,210)
    ax1.get_xaxis().set_major_formatter(matplotlib.ticker.ScalarFormatter())
    plt.xlabel('Number of trajectories',fontsize=14)
    plt.ylabel('Average planning loss',fontsize=14)
fig1, ax1 = plt.subplots()
plotSingleCurve(firstGamma,'gamma = 0.3','black','o')
plotSingleCurve(secondGamma, 'gamma = 0.6','red','x')
plotSingleCurve(thirdGamma, 'gamma=0.99','blue','^')
plotSingleCurve(cv, 'Cross validation','green','')
makeItNice(plt,ax1)
plt.legend()
plt.show()
