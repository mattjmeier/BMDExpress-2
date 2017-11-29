package com.sciome.charts.jfree;

import java.awt.Color;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.SciomeScatterChart;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.scene.Node;

public class SciomeScatterChartJFree extends SciomeScatterChart {

	public SciomeScatterChartJFree(String title, List<ChartDataPack> chartDataPacks, String key1, String key2,
			boolean allowXLogAxis, boolean allowYLogAxis, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, key1, key2, allowXLogAxis, allowYLogAxis, chartListener);
	}

	public SciomeScatterChartJFree(String title, List<ChartDataPack> chartDataPacks, String key1, String key2,
			SciomeChartListener chartListener)
	{
		this(title, chartDataPacks, key1, key2, true, true, chartListener);
	}

	@Override
	protected Node generateChart(String[] keys, ChartConfiguration chartConfig) {
		String key1 = keys[0];
		String key2 = keys[1];
		Double max1 = getMaxMax(key1);
		Double min1 = getMinMin(key1);

		Double max2 = getMaxMax(key2);
		Double min2 = getMinMin(key2);
		
		DefaultXYDataset dataset = new DefaultXYDataset();
		
		for (SciomeSeries<Number, Number> series : getSeriesData())
		{
			double[] domains = new double[series.getData().size()];
			double[] ranges = new double[series.getData().size()];

			int i = 0;
			for (SciomeData<Number, Number> chartData : series.getData())
			{
				double domainvalue = chartData.getXValue().doubleValue();
				double rangevalue = chartData.getYValue().doubleValue();
				domains[i] = domainvalue;
				ranges[i++] = rangevalue;
			}
			dataset.addSeries(series.getName(), new double[][] { domains, ranges });
		}
		
		JFreeChart chart = ChartFactory.createScatterPlot(key1 + " Vs. " + key2,
				key1, key2, dataset, PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setForegroundAlpha(0.1f);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		plot.setDomainAxis(SciomeNumberAxisGeneratorJFree.generateAxis(getLogXAxis().isSelected()));
		plot.setRangeAxis(SciomeNumberAxisGeneratorJFree.generateAxis(getLogYAxis().isSelected()));
		ValueAxis domain = (ValueAxis) plot.getDomainAxis();
		if (max1 <= min1)
		{
			max1 = 1.0;
			min1 = 0.1;
		}

		if (max2 <= min2)
		{
			max2 = 1.0;
			min2 = 0.1;
		}
		domain.setRange(min1.doubleValue(), max1.doubleValue());
		
		// Set range for Y-Axis
		ValueAxis range = (ValueAxis) plot.getRangeAxis();
		if (min2.equals(max2))
		{
			min2 -= 1;
		}
		else if (min2 > max2)
		{
			min2 = 0.0;
			max2 = 0.1;
		}
		range.setRange(min2.doubleValue(), max2.doubleValue());
		domain.setLabel(key1);
		range.setLabel(key2);
		
		XYLineAndShapeRenderer renderer = ((XYLineAndShapeRenderer) plot.getRenderer());

		renderer.setSeriesPaint(0, new Color(0.0f, 0.0f, .82f, .3f));
		
		//Set tooltip string
		XYToolTipGenerator tooltipGenerator = new XYToolTipGenerator()
		{
			@Override
			public String generateToolTip(XYDataset dataset, int series, int item) {
				return ((ChartExtraValue)getSeriesData().get(series).getData().get(item).getExtraValue()).userData.toString();
			}
		};
		renderer.setBaseToolTipGenerator(tooltipGenerator);
		plot.setBackgroundPaint(Color.white);
		chart.getPlot().setForegroundAlpha(0.1f);

		// Create Panel
		ChartViewer chartView = new ChartViewer(chart);

		//Add plot point clicking interaction
		chartView.addChartMouseListener(new ChartMouseListenerFX() {

			@Override
			public void chartMouseClicked(ChartMouseEventFX e) {
				showObjectText(e.getEntity().getToolTipText());
			}

			@Override
			public void chartMouseMoved(ChartMouseEventFX e) {
				//ignore for now
			}
		});

		return chartView;
	}

}
