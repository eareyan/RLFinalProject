function figure7

data = csvread('/home/eareyanv/Dropbox/CSCI2951 - Learning and Sequential Decision Making/RLProject/copy of planningloss-1000.csv')
gamma3 = zeros(size(data,1)/3,1)';
gamma6 = zeros(size(data,1)/3,1)';
gamma99 = zeros(size(data,1)/3,1)';
xAxis = zeros(size(data,1)/3,1)';
for i= 1:size(data,1)
    switch data(i,1)
        case 0.3
            gamma3(i)= data(i,3);
            xAxis(i) = data(i,2);
        case 0.6
            gamma6(i-size(data,1)/3) = data(i,3);
        case 0.99
            gamma99(i-2*size(data,1)/3) = data(i,3);
    end
end

plot(xAxis,gamma3,xAxis,gamma6,xAxis,gamma99) 
set(gca,'xtick',xAxis);
set(gca,'xticklabel',num2str(xAxis.'));
