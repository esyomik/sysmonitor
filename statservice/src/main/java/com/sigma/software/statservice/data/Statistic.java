package com.sigma.software.statservice.data;


/**
 * This class computes a statistic data in a stream manner using
 * <a href="https://en.wikipedia.org/wiki/Standard_deviation#Rapid_calculation_methods">
 * Rapid calculation methods</a>. This class was tested using BigDecimal values
 * instead of <i>sumSquare</i>, <i>average</i> and <i>sumVariance</i> members.
 * The method <i>put()</i> was looked like:
 * <pre>
 * public Statistic put(double value) {
 *     min = Math.min(min, value);
 *     max = Math.max(max, value);
 *     sumSquare = sumSquare.add(new BigDecimal(value * value));
 *     ++count;
 *     BigDecimal bdValue = new BigDecimal(value);
 *     BigDecimal Ak1 = average.add(bdValue.subtract(average)
 *         .divide(new BigDecimal(count), SCALE, RoundingMode.HALF_EVEN));
 *     sumVariance = sumVariance.add((bdValue.subtract(Ak1))
 *         .multiply(bdValue.subtract(average)));
 *     average = Ak1;
 *     return this;
 * }
 * </pre>
 * And no significance differences were noticed in the results. Testing code:
 * <pre>
 * Statistic stat = new Statistic();
 * for(int i = 0; i < 10000000; ++i) {
 *     stat.put(((i & 1) == 0)? i : i / 1000000.0);
 * }
 * System.out.println(stat);
 * </pre>
 * <b>Results:</b>
 * <table>
 *     <tr><th>Parameter</th><th>Double</th><th>BigDecimal</th></tr>
 *     <tr><td>rootMeanSquare</td><td>4082482.2922693933</td><td>4082482.2922682306</td></tr>
 *     <tr><td>average</td><td>2500001.999995908</td><td>2500002.00</td></tr>
 *     <tr><td>variance</td><td>1.0416651666680998E13</td><td>1.0416651666679666E13</td></tr>
 *     <tr><td>standardDeviation</td><td>3227483.79805089</td><td>3227483.7980506835</td></tr>
 *     <tr><td>standardSampleDeviation</td><td>3227483.9594250917</td><td>3227483.959424886</td></tr>
 * </table>
 */
public class Statistic {

    private int count;
    private double min;
    private double max;
    private double sumSquare;
    private double average;
    private double sumVariance;


    public Statistic() {
        reset();
    }

    public void reset() {
        count = 0;
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        sumSquare = 0.0;
        average = 0.0;
        sumVariance = 0.0;
    }

    /**
     * Adds another value and compute statistic.
     * @param value the added value
     */
    public void put(double value) {
        min = Math.min(min, value);
        max = Math.max(max, value);
        sumSquare += value * value;
        ++count;
        double Ak1 = average + (value - average) / count;
        sumVariance += (value - Ak1) * (value - average);
        average = Ak1;
    }

    public int getCount() {
        return count;
    }

    public double sampleVariance() {
        return sumVariance / (count - 1);
    }

    public double variance() {
        return sumVariance / count;
    }

    public double average() {
        return average;
    }

    public double rootMeanSquare() {
        return Math.sqrt(sumSquare / count);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    /**
     * Computes standard deviation (or Root Mean Square Error). In other words,
     * it means you how concentrated the data is around the line of best fit
     * @return Root Mean Square Error value
     */
    public double standardDeviation() {
        return Math.sqrt(variance());
    }

    /**
     * Computes standard deviation (or Root Mean Square Error) for sample. In other
     * words, it means you how concentrated the data is around the line of best fit
     * @return Root Mean Square Error value
     */
    public double standardSampleDeviation() {
        return Math.sqrt(sampleVariance());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Statistic{");
        sb.append("count=").append(count);
        sb.append(", min=").append(min);
        sb.append(", max=").append(max);
        sb.append(", rootMeanSquare=").append(rootMeanSquare());
        sb.append(", average=").append(average);
        sb.append(", variance=").append(variance());
        sb.append(", sampleVariance=").append(sampleVariance());
        sb.append(", standardDeviation=").append(standardDeviation());
        sb.append(", standardSampleDeviation=").append(standardSampleDeviation());
        sb.append('}');
        return sb.toString();
    }
}
