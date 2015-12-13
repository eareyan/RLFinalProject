import matplotlib.pyplot as plt
import matplotlib
import numpy as np

# 50 Trajectories

fiftyTrajData = [7, 13, 12, 4, 6, 7, 4]
twoHundredTrajData = [7, 10, 10, 10,  8, 7, 9]
thousandTrajData = [8, 10, 10, 10, 10, 10, 10]

xData = [2, 4, 6, 8, 10, 15, 20]

plt.plot(xData, thousandTrajData, label="1000", marker="x", color="red")
plt.plot(xData, twoHundredTrajData, label="200", marker="8", color="green")
plt.plot(xData, fiftyTrajData, label="50", marker="s", color="blue")

plt.legend(title="Num Trajectories")

font = {'family' : 'normal',
        'size'   : 15}

matplotlib.rc('font', **font)
plt.xlim([0,20])
plt.ylim([0,16])
plt.xlabel('Planning Depth')
plt.ylabel('Average Cumulative Reward')
plt.title('Performance of UCT vs. Planning Depth')
plt.grid(True)
plt.savefig("test.png")
plt.show()