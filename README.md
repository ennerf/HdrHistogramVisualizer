HdrHistogramVisualizer
========

[![Join the chat at https://gitter.im/ennerf/HdrHistogramVisualizer](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/ennerf/HdrHistogramVisualizer?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Visualizer for HdrHistogram and jHiccup logs

* [HdrHistogram](https://github.com/HdrHistogram/HdrHistogram)
* [jHiccup](https://github.com/giltene/jHiccup) by [Azul](http://www.azulsystems.com/product/jHiccup)

This tool serves as an alternative to the jHiccupLogProcessor and jHiccupPlotter utilities that are bundled with jHiccup.

<h2>Download</h2>

You can download the latest packaged version for your OS [here](https://ennerf.github.io/HdrHistogramVisualizer/download.html).

<h2>Maven</h2>
execute from commandline
<pre>mvn clean compile test exec:java</pre>

<h2>Screenshots</h2>

* **Exported png image**
![Exported png image](https://raw.githubusercontent.com/ennerf/HdrHistogramVisualizer/resources/screenshots/chart-export-2.png "PNG image export")

* **Main Screen**
![Screenshot of main screen](https://raw.githubusercontent.com/ennerf/HdrHistogramVisualizer/resources/screenshots/main-view2.png "Main screen")

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

![Screenshot of custom css export](https://raw.githubusercontent.com/ennerf/HdrHistogramVisualizer/resources/screenshots/chart-export-custom.png "Export with custom CSS")


