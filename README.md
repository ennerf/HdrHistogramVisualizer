HdrHistogramVisualizer
========

[![Join the chat at https://gitter.im/ennerf/HdrHistogramVisualizer](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/ennerf/HdrHistogramVisualizer?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Visualizer for HdrHistogram and jHiccup logs

* [HdrHistogram](https://github.com/HdrHistogram/HdrHistogram)
* [jHiccup](https://github.com/giltene/jHiccup) by [Azul](http://www.azulsystems.com/product/jHiccup)

This tool can serve as an alternative to the jHiccupLogProcessor and jHiccupPlotter utilities that are bundled with jHiccup.

<h2>Maven</h2>
execute from commandline
<pre>mvn clean compile test exec:java</pre>

create executable jar
<pre>mvn clean package</pre>

<h2>Screenshots</h2>

* **Exported png image**
![Exported png image](https://raw.githubusercontent.com/ennerf/HdrHistogramVisualizer/resources/screenshots/chart-export-2.png "PNG image export")

* **Main Screen**
![Screenshot of main screen](https://raw.githubusercontent.com/ennerf/HdrHistogramVisualizer/resources/screenshots/main-view.png "Main screen")

<h2>Instructions</h2>

**Clear all** removes any existing data from the charts before loading new data. Deactivate this flag if you want to plot multiple lines.

**Export png** saves a snapshot of the current chart region. Be aware of this in case all exported images should have the same resolution.

**Export Log** internally uses the same methods as jHiccupLogProcessor. The input options are mapped to the corresponding commandline options.

**-csv** adds the '-csv' option to log export. It does not have an effect on the visualization.

<h2>Notes</h2>

I've primarily created this project for internal use and to experiment with charts in JavaFX, and have only implemented the functionality that I tend to need. If you find that you are missing a feature, just create an issue and I'll see whether I can add it.

