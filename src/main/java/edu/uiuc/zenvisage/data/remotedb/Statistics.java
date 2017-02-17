package edu.uiuc.zenvisage.data.remotedb;

public class Statistics 
{
    public double[] data;
    public int size;
    public double mean;
    public double std;

    public Statistics(double[] data) 
    {
        this.data = data;
        this.size = data.length;
        this.mean = getMean();
        this.std = getStdDev();
    }   

    public double getMean()
    {
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/size;
    }

    public double getVariance()
    {
        double mean = getMean();
        double temp = 0;
        for(double a :data)
            temp += (a-mean)*(a-mean);
        return temp/size;
    }

    public double getStdDev()
    {
        return Math.sqrt(getVariance());
    }
    
    public double getZScore(double input){
    	return (input - this.mean)*1.0/this.std;
    }
}