package edu.sysu.jimmy.analysis.result;


import java.util.Comparator;

public class HotSpotDef implements Comparable<HotSpotDef> {
    public String methodSignature;
    public long selfTimeDiff;
    public long selfTimeBase;
    public double selfTimeDiffRate;
    public long invocationsDiff;
    public long invocationsBase;
    public double invocationsDiffRate;

    public double percent = 0;

    public HotSpotDef(String methodSignature, String selfTimeDiff, String selfTimeBase, String invocationsDiff, String invocationsBase) {
        this.methodSignature = methodSignature.replaceAll("\\s*", "");
        this.selfTimeDiff = Long.parseLong(selfTimeDiff);
        this.selfTimeBase = Long.parseLong(selfTimeBase);
        this.invocationsDiff = Long.parseLong(invocationsDiff);
        this.invocationsBase = Long.parseLong(invocationsBase);
        this.selfTimeDiffRate = 1.0 * this.selfTimeDiff / this.selfTimeBase;
        this.invocationsDiffRate = 1.0 * this.invocationsDiff / this.invocationsBase;
//        System.out.println(selfTimeDiffRate);
    }

    public HotSpotDef(String methodSignature, long selfTimeDiff, long selfTimeBase, long invocationsDiff, long invocationsBase) {
        this.methodSignature = methodSignature;
        this.selfTimeDiff = selfTimeDiff;
        this.selfTimeBase = selfTimeBase;
        this.invocationsDiff = invocationsDiff;
        this.invocationsBase = invocationsBase;
        this.selfTimeDiffRate = 1.0 * this.selfTimeDiff / this.selfTimeBase;
        this.invocationsDiffRate = 1.0 * this.invocationsDiff / this.invocationsBase;
    }

    @Override
    public int compareTo(HotSpotDef o) {
        double timeDiffRate = Math.abs(this.selfTimeDiffRate) - Math.abs(o.selfTimeDiffRate);
        double invocationsDiffRate = Math.abs(this.invocationsDiffRate) - Math.abs(o.invocationsDiffRate);
        if (Math.abs(timeDiffRate) < 0.000001) {
            if (Math.abs(invocationsDiffRate) < 0.000001) {
                if (this.selfTimeDiff == o.selfTimeDiff) {
                    if (this.selfTimeBase == o.selfTimeBase) {
                        return -this.methodSignature.compareTo(o.methodSignature);
                    } else
                        return this.selfTimeBase > o.selfTimeBase ? -1 : 1;
                } else
                    return this.selfTimeDiff > o.selfTimeDiff ? -1 : 1;
            } else
                return invocationsDiffRate > 0 ? -1 : 1;
        } else
            return timeDiffRate > 0 ? -1 : 1;
    }
    /*@Override
    public int compareTo(HotSpotDef o) {
        double timeDiffRate = Math.abs(this.selfTimeDiffRate) - Math.abs(o.selfTimeDiffRate);
        double invocationsDiffRate = Math.abs(this.invocationsDiffRate) - Math.abs(o.invocationsDiffRate);
        if (Math.abs(invocationsDiffRate) < 0.000001) {
        if (Math.abs(timeDiffRate) < 0.000001) {

                if (this.selfTimeDiff == o.selfTimeDiff) {
                    if (this.selfTimeBase == o.selfTimeBase) {
                        return -this.methodSignature.compareTo(o.methodSignature);
                    } else
                        return this.selfTimeBase > o.selfTimeBase ? -1 : 1;
                } else
                    return this.selfTimeDiff > o.selfTimeDiff ? -1 : 1;

        } else
            return timeDiffRate > 0 ? -1 : 1;} else
            return invocationsDiffRate > 0 ? -1 : 1;
    }*/
}
