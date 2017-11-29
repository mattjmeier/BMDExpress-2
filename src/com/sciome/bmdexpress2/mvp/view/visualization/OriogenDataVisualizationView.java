package com.sciome.bmdexpress2.mvp.view.visualization;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.presenter.visualization.OneWayANOVADataVisualizationPresenter;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.service.VisualizationService;
import com.sciome.bmdexpress2.serviceInterface.IVisualizationService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.javafx.SciomeHistogramFX;
import com.sciome.charts.jfree.SciomeScatterChartJFree;
import com.sciome.filter.DataFilterPack;

public class OriogenDataVisualizationView extends DataVisualizationView implements IDataVisualizationView{
	private static final String	UNADJUSTED_PVALUE_HISTOGRAM			= "Unadjusted P-Value Histogram";
	private static final String	ADJUSTED_PVALUE_HISTOGRAM			= "Adjusted P-Value Histogram";
	private static final String	BEST_FOLD_CHANGE_HISTOGRAM			= "Best Fold Change Histogram";
	private static final String	BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM	= "Best Fold Change (Unsigned) Histogram";

	public OriogenDataVisualizationView()
	{
		super();
		IVisualizationService service = new VisualizationService();
		presenter = new OneWayANOVADataVisualizationPresenter(this, service, BMDExpressEventBus.getInstance());

		chartCache.put(UNADJUSTED_PVALUE_HISTOGRAM + "-" + OriogenResults.UNADJUSTED_PVALUE,
				new SciomeHistogramFX("", new ArrayList<>(), OriogenResults.UNADJUSTED_PVALUE, 20.0, this));

		chartCache.put(ADJUSTED_PVALUE_HISTOGRAM + "-" + OriogenResults.ADJUSTED_PVALUE,
				new SciomeHistogramFX("", new ArrayList<>(), OriogenResults.ADJUSTED_PVALUE, 20.0, this));

		chartCache.put(BEST_FOLD_CHANGE_HISTOGRAM + "-" + OriogenResults.BEST_FOLD_CHANGE,
				new SciomeHistogramFX("", new ArrayList<>(), OriogenResults.BEST_FOLD_CHANGE, 20.0, this));

		chartCache.put(BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM + "-" + OriogenResults.BEST_FOLD_CHANGE_ABS,
				new SciomeHistogramFX("", new ArrayList<>(), OriogenResults.BEST_FOLD_CHANGE_ABS, 20.0,
						this));

		chartCache.put(
				"DEFAULT-" + OriogenResults.BEST_FOLD_CHANGE + OriogenResults.NEG_LOG_ADJUSTED_PVALUE,
				new SciomeScatterChartJFree("", new ArrayList<>(), OriogenResults.BEST_FOLD_CHANGE,
						OriogenResults.NEG_LOG_ADJUSTED_PVALUE, false, true, this));
		chartCache.put(
				"DEFAULT-" + OriogenResults.BEST_FOLD_CHANGE
						+ OriogenResults.NEG_LOG_UNADJUSTED_PVALUE,
				new SciomeScatterChartJFree("", new ArrayList<>(), OriogenResults.BEST_FOLD_CHANGE,
						OriogenResults.NEG_LOG_UNADJUSTED_PVALUE, false, true, this));

	}

	@Override
	public void redrawCharts(DataFilterPack pack, List<String> selectedIds)
	{
		String chartKey = cBox.getSelectionModel().getSelectedItem();
		defaultDPack = pack;
		this.selectedIds = selectedIds;
		if (results == null || results.size() == 0)
			return;

		List<ChartDataPack> chartDataPacks = presenter.getCategoryResultsChartPackData(results, pack,
				selectedIds);
		chartsList = new ArrayList<>();

		if (chartKey.equals(UNADJUSTED_PVALUE_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(UNADJUSTED_PVALUE_HISTOGRAM + "-" + OriogenResults.UNADJUSTED_PVALUE);
			chart.redrawCharts(chartDataPacks);

			chartsList.add(chart);
		}
		else if (chartKey.equals(ADJUSTED_PVALUE_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(ADJUSTED_PVALUE_HISTOGRAM + "-" + OriogenResults.ADJUSTED_PVALUE);
			chart.redrawCharts(chartDataPacks);
			chartsList.add(chart);
		}
		else if (chartKey.equals(BEST_FOLD_CHANGE_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(BEST_FOLD_CHANGE_HISTOGRAM + "-" + OriogenResults.BEST_FOLD_CHANGE);
			chart.redrawCharts(chartDataPacks);
			chartsList.add(chart);
		}
		else if (chartKey.equals(BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM + "-" + OriogenResults.BEST_FOLD_CHANGE_ABS);
			chart.redrawCharts(chartDataPacks);
			chartsList.add(chart);
		}
		else
		{
			SciomeChartBase chart = chartCache.get("DEFAULT-" + OriogenResults.BEST_FOLD_CHANGE
					+ OriogenResults.NEG_LOG_ADJUSTED_PVALUE);
			chart.redrawCharts(chartDataPacks);
			SciomeChartBase chart2 = chartCache.get("DEFAULT-" + OriogenResults.BEST_FOLD_CHANGE
					+ OriogenResults.NEG_LOG_UNADJUSTED_PVALUE);
			chart2.redrawCharts(chartDataPacks);
			chartsList.add(chart);
			chartsList.add(chart2);
		}

		graphViewAnchorPane.getChildren().clear();
		showCharts();

	}

	@Override
	public List<String> getCannedCharts()
	{
		List<String> resultList = new ArrayList<>();
		resultList.add(DEFAULT_CHARTS);
		resultList.add(UNADJUSTED_PVALUE_HISTOGRAM);
		resultList.add(ADJUSTED_PVALUE_HISTOGRAM);
		resultList.add(BEST_FOLD_CHANGE_HISTOGRAM);
		resultList.add(BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM);

		return resultList;

	}
}
