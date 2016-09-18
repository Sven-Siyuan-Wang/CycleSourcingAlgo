package Algo;

/**
 * Created by WSY on 14/7/16.
 */
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.SeriesException;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.json.JSONException;

import java.io.IOException;

public class Plot extends ApplicationFrame {

    public static final String directory = "/Users/WangSiyuan/Desktop/CycleSourcing/tripqData/";
    public static final String[] files = {
            "7c156b124624_20160831_174936contrast",
            "f76d0f378824_20160822_205711",
            "f76d0f378824_20160822_200455",
            "f76d0f378824_20160822_183101",
            "A48CFCE3A961_20160822_193743",
            "c60852888824_20160831_193223"
    };
    public static final String[] fileset2 = {
            "FF17813E56A1_20160830_190746",
            "FF17813E56A1_20160830_184413",
            "FF17813E56A1_20160830_183108",
            "FF17813E56A1_20160830_180201",
            "FF17813E56A1_20160830_174221"
    };
//    f76d0f378824_20160822_205711 from ECP back to SUTD : GOOD
//    f76d0f378824_20160822_200455 best sample (soft and gentle road, slow speed) :GOOD
//    f76d0f378824_20160822_183101 east coast park
//    f76d0f378824_20160822_181741 PCN from expo to ECP
//    A48CFCE3A961_20160822_193743 ecp

    public Plot(final String title, String file) throws IOException, JSONException {
        super(title);
        final XYDataset dataset1 = createDataset1(Parser.getAccel(file));
        final XYDataset dataset2 = createDataset2(Parser.getAccel(file));
        final XYDataset bumpset = createBumpSet(file);
        final XYDataset jamset = createJamSet(file);
        final JFreeChart chart = createOverlaidChart(dataset1, dataset2, bumpset, jamset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560*2, 370*2));
        chartPanel.setMouseZoomable(true, false);
        setContentPane(chartPanel);
    }

    private XYDataset createDataset1(Double[] array) {
        final XYSeries series = new XYSeries("TQI Index");

        int total = array.length;
        int peakL = array[total-1].intValue();
        int l = total - peakL - 1;

        for (int i = 0; i < l; i++) {
            try {
                series.add(i, array[i]);

            } catch (SeriesException e) {
                System.err.println("Error adding to series");
            }
        }

        return new XYSeriesCollection(series);
    }

    private XYDataset createDataset2 (Double[] array){
        final XYSeries series = new XYSeries("Peak");

        int total = array.length;
        int peakL = array[total-1].intValue();
        int l = total - peakL - 1;
        System.out.println("Number of peaks: "+peakL);

        for (int i=l; i<total-1; i++){
            series.add(array[i].intValue(), array[array[i].intValue()]);

        }
        return new XYSeriesCollection(series);
    }

    private XYDataset createBumpSet (String file) throws IOException, JSONException {
        XYSeries series = new XYSeries("Bump");
        Double[] bumps = Parser.getBumpIndex(file);
        int l = bumps.length;
        for (Double i: bumps){
            series.add(i.intValue(), 110);
        }
        return new XYSeriesCollection(series);

    }

    private XYDataset createJamSet (String file) throws IOException, JSONException {
        XYSeries series = new XYSeries("Jam");
        Double[] jams = Parser.getJamIndex(file);
        int l = jams.length;
        for (Double i: jams){
            series.add(i.intValue(), 105);
        }
        return new XYSeriesCollection(series);

    }

    private JFreeChart createOverlaidChart(final XYDataset dataset1, final XYDataset dataset2, final XYDataset bumpSet, final XYDataset jamSet) {


        XYPlot plot = new XYPlot();
        XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);   // Lines only
        ValueAxis domain1 = new NumberAxis("SampelPoints");
        ValueAxis range1 = new NumberAxis("TQI");
        plot.setDataset(0, dataset1);
        plot.setRenderer(0, renderer1);
        plot.setDomainAxis(0, domain1);
        plot.setRangeAxis(0, range1);
        plot.mapDatasetToDomainAxis(0, 0);
        plot.mapDatasetToRangeAxis(0, 0);

        //peak points
        XYItemRenderer renderer2 = new XYLineAndShapeRenderer(false, true);
        plot.setDataset(1, dataset2);
        plot.setRenderer(1, renderer2);
        plot.mapDatasetToDomainAxis(1, 0);
        plot.mapDatasetToRangeAxis(1, 0);

        XYItemRenderer renderer3 = new XYLineAndShapeRenderer(false, true);
        plot.setDataset(2, bumpSet);
        plot.setRenderer(2, renderer3);
        plot.mapDatasetToDomainAxis(2, 0);
        plot.mapDatasetToRangeAxis(2, 0);

        XYItemRenderer renderer4 = new XYLineAndShapeRenderer(false, true);
        plot.setDataset(3, jamSet);
        plot.setRenderer(3, renderer4);
        plot.mapDatasetToDomainAxis(3, 0);
        plot.mapDatasetToRangeAxis(3, 0);


        return new JFreeChart("Peak Finding Chart", JFreeChart.DEFAULT_TITLE_FONT, plot, true);


    }

    public static void main(final String[] args) throws IOException, JSONException {

        for(String file: fileset2){

            final String title = "Peak finding results - "+file;
            final Plot demo = new Plot(title,  directory + file + ".json");
            demo.pack();
            RefineryUtilities.positionFrameRandomly(demo);
            demo.setVisible(true);
        }

    }

}