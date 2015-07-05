package us.hebi.histogram.visualizer.gui;

import com.airhacks.afterburner.views.FXMLView;

/**
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 04 Jul 2015
 */
public class VisualizerView extends FXMLView {

    @Override
    public VisualizerPresenter getPresenter() {
        return (VisualizerPresenter) super.getPresenter();
    }

}
