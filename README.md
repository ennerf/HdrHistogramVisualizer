HdrHistogramVisualizer
========

[![Join the chat at https://gitter.im/ennerf/HdrHistogramVisualizer](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/ennerf/HdrHistogramVisualizer?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

An alternative visualizer for [HdrHistogram](https://github.com/HdrHistogram/HdrHistogram) logs as created by [jHiccup](https://github.com/giltene/jHiccup). Note that it has been **deprecated** since [Azul](http://www.azulsystems.com/product/jHiccup) made their [HistogramLogAnalyzer](https://github.com/HdrHistogram/HistogramLogAnalyzer) tool open source.

![HdrHistogramVisualizer](https://ennerf.github.io/HdrHistogramVisualizer/icon.png?)

You can download the latest packaged version for your OS [here](https://ennerf.github.io/HdrHistogramVisualizer/download.html).


<h2>Maven</h2>
execute from commandline
<pre>mvn clean compile test exec:java</pre>

<h2>Screenshots</h2>

* **Main Screen (Dark)**

![Screenshot of main screen](https://ennerf.github.io/HdrHistogramVisualizer/screenshots/main-view-dark.png?)

* **Main Screen (Light)**

![Screenshot of main screen](https://ennerf.github.io/HdrHistogramVisualizer/screenshots/main-view-light.png?)
  
* **Exported png image**

![Exported png image](https://ennerf.github.io/HdrHistogramVisualizer/screenshots/chart-export-2.png?)

<h2>Instructions</h2>

**Clear all** removes any existing data from the charts before loading new data. Deactivate this flag if you want to plot multiple lines.

**Export png** saves a snapshot of the current chart region. Be aware of this in case all exported images should have the same resolution.

**Export Log** internally uses the same methods as jHiccupLogProcessor. The input options are mapped to the corresponding commandline options.

**-csv** adds the '-csv' option to log export. It does not have an effect on the visualization.

**Aggregate Intervals** downsamples the time interval maxima by combining N sequential histograms. For example, if histogram were recorded in second intervals, a value of 60 would emulate the same behavior as if histograms were recorded in minute intervals. This can reduce clutter on the interval maxima chart and improve the loading performance.

<h2>Custom Labels</h2>

Titles, axis labels, and time units can be changed in the Interval Chart and Percentile Chart sections.

<h2>Custom Styling</h2>

HdrHistogramVisualizer monitors a 'visualizer.css' file in the start directory. If it exists, or gets changed, it will automatically be reloaded and applied. This allows you to overwrite any default css settings. This is useful for exporting nicer looking figures.

For example, the following 'visualizer.css' file,

```
/* --- Axes and Labels --- */
.chart-content {
	-fx-font-size: 18px;
    -fx-padding: 10 20 10 0px;
}
.chart-legend {
	-fx-font-size: 20px;
    -fx-padding: 0px;
}
.chart-title {
	-fx-font-size: 24px;
    -fx-text-fill: black;
    -fx-font-weight: bold;
}

.axis {
	-fx-font-size: 24px;
    -fx-tick-label-font-size: 20px;
    -fx-font-weight: normal;
}

/* --- Chart Lines --- */
.chart-series-line {
    -fx-stroke-width: 3px;
}

.default-color0.chart-series-line, .default-color0.chart-line-symbol {
    -fx-stroke: forestgreen;
	-fx-background-color: forestgreen, white;
}
```

creates the following output,

![Screenshot of custom css export](https://ennerf.github.io/HdrHistogramVisualizer/screenshots/chart-export-custom.png?)


