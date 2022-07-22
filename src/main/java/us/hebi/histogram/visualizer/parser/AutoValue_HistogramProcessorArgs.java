
package us.hebi.histogram.visualizer.parser;

import java.io.File;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_HistogramProcessorArgs extends HistogramProcessorArgs {

  private final File inputFile;
  private final double startTimeSec;
  private final double endTimeSec;
  private final double outputValueUnitRatio;
  private final int percentilesOutputTicksPerHalf;
  private final int aggregateIntervalSamples;
  private final boolean logFormatCsv;
  private final String selectedTags;

  private AutoValue_HistogramProcessorArgs(
      File inputFile,
      double startTimeSec,
      double endTimeSec,
      double outputValueUnitRatio,
      int percentilesOutputTicksPerHalf,
      int aggregateIntervalSamples,
      boolean logFormatCsv,
      String selectedTags) {
    if (inputFile == null) {
      throw new NullPointerException("Null inputFile");
    }
    this.inputFile = inputFile;
    this.startTimeSec = startTimeSec;
    this.endTimeSec = endTimeSec;
    this.outputValueUnitRatio = outputValueUnitRatio;
    this.percentilesOutputTicksPerHalf = percentilesOutputTicksPerHalf;
    this.aggregateIntervalSamples = aggregateIntervalSamples;
    this.logFormatCsv = logFormatCsv;
    if (selectedTags == null) {
      throw new NullPointerException("Null selectedTags");
    }
    this.selectedTags = selectedTags;
  }

  @Override
  public File inputFile() {
    return inputFile;
  }

  @Override
  public double startTimeSec() {
    return startTimeSec;
  }

  @Override
  public double endTimeSec() {
    return endTimeSec;
  }

  @Override
  public double outputValueUnitRatio() {
    return outputValueUnitRatio;
  }

  @Override
  public int percentilesOutputTicksPerHalf() {
    return percentilesOutputTicksPerHalf;
  }

  @Override
  public int aggregateIntervalSamples() {
    return aggregateIntervalSamples;
  }

  @Override
  public boolean logFormatCsv() {
    return logFormatCsv;
  }

  @Override
  public String selectedTags() {
    return selectedTags;
  }

  @Override
  public String toString() {
    return "HistogramProcessorArgs{"
        + "inputFile=" + inputFile + ", "
        + "startTimeSec=" + startTimeSec + ", "
        + "endTimeSec=" + endTimeSec + ", "
        + "outputValueUnitRatio=" + outputValueUnitRatio + ", "
        + "percentilesOutputTicksPerHalf=" + percentilesOutputTicksPerHalf + ", "
        + "aggregateIntervalSamples=" + aggregateIntervalSamples + ", "
        + "logFormatCsv=" + logFormatCsv + ", "
        + "selectedTags=" + selectedTags
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof HistogramProcessorArgs) {
      HistogramProcessorArgs that = (HistogramProcessorArgs) o;
      return (this.inputFile.equals(that.inputFile()))
           && (Double.doubleToLongBits(this.startTimeSec) == Double.doubleToLongBits(that.startTimeSec()))
           && (Double.doubleToLongBits(this.endTimeSec) == Double.doubleToLongBits(that.endTimeSec()))
           && (Double.doubleToLongBits(this.outputValueUnitRatio) == Double.doubleToLongBits(that.outputValueUnitRatio()))
           && (this.percentilesOutputTicksPerHalf == that.percentilesOutputTicksPerHalf())
           && (this.aggregateIntervalSamples == that.aggregateIntervalSamples())
           && (this.logFormatCsv == that.logFormatCsv())
           && (this.selectedTags.equals(that.selectedTags()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.inputFile.hashCode();
    h *= 1000003;
    h ^= (Double.doubleToLongBits(this.startTimeSec) >>> 32) ^ Double.doubleToLongBits(this.startTimeSec);
    h *= 1000003;
    h ^= (Double.doubleToLongBits(this.endTimeSec) >>> 32) ^ Double.doubleToLongBits(this.endTimeSec);
    h *= 1000003;
    h ^= (Double.doubleToLongBits(this.outputValueUnitRatio) >>> 32) ^ Double.doubleToLongBits(this.outputValueUnitRatio);
    h *= 1000003;
    h ^= this.percentilesOutputTicksPerHalf;
    h *= 1000003;
    h ^= this.aggregateIntervalSamples;
    h *= 1000003;
    h ^= this.logFormatCsv ? 1231 : 1237;
    h *= 1000003;
    h ^= this.selectedTags.hashCode();
    return h;
  }

  static final class Builder extends HistogramProcessorArgs.Builder {
    private File inputFile;
    private Double startTimeSec;
    private Double endTimeSec;
    private Double outputValueUnitRatio;
    private Integer percentilesOutputTicksPerHalf;
    private Integer aggregateIntervalSamples;
    private Boolean logFormatCsv;
    private String selectedTags;
    Builder() {
    }
    Builder(HistogramProcessorArgs source) {
      this.inputFile = source.inputFile();
      this.startTimeSec = source.startTimeSec();
      this.endTimeSec = source.endTimeSec();
      this.outputValueUnitRatio = source.outputValueUnitRatio();
      this.percentilesOutputTicksPerHalf = source.percentilesOutputTicksPerHalf();
      this.aggregateIntervalSamples = source.aggregateIntervalSamples();
      this.logFormatCsv = source.logFormatCsv();
      this.selectedTags = source.selectedTags();
    }
    @Override
    public HistogramProcessorArgs.Builder setInputFile(File inputFile) {
      this.inputFile = inputFile;
      return this;
    }
    @Override
    public HistogramProcessorArgs.Builder setStartTimeSec(double startTimeSec) {
      this.startTimeSec = startTimeSec;
      return this;
    }
    @Override
    public HistogramProcessorArgs.Builder setEndTimeSec(double endTimeSec) {
      this.endTimeSec = endTimeSec;
      return this;
    }
    @Override
    public HistogramProcessorArgs.Builder setOutputValueUnitRatio(double outputValueUnitRatio) {
      this.outputValueUnitRatio = outputValueUnitRatio;
      return this;
    }
    @Override
    public HistogramProcessorArgs.Builder setPercentilesOutputTicksPerHalf(int percentilesOutputTicksPerHalf) {
      this.percentilesOutputTicksPerHalf = percentilesOutputTicksPerHalf;
      return this;
    }
    @Override
    public HistogramProcessorArgs.Builder setAggregateIntervalSamples(int aggregateIntervalSamples) {
      this.aggregateIntervalSamples = aggregateIntervalSamples;
      return this;
    }
    @Override
    public HistogramProcessorArgs.Builder setLogFormatCsv(boolean logFormatCsv) {
      this.logFormatCsv = logFormatCsv;
      return this;
    }
    @Override
    public HistogramProcessorArgs.Builder setSelectedTags(String selectedTags) {
      this.selectedTags = selectedTags;
      return this;
    }
    @Override
    public HistogramProcessorArgs autoBuild() {
      String missing = "";
      if (inputFile == null) {
        missing += " inputFile";
      }
      if (startTimeSec == null) {
        missing += " startTimeSec";
      }
      if (endTimeSec == null) {
        missing += " endTimeSec";
      }
      if (outputValueUnitRatio == null) {
        missing += " outputValueUnitRatio";
      }
      if (percentilesOutputTicksPerHalf == null) {
        missing += " percentilesOutputTicksPerHalf";
      }
      if (aggregateIntervalSamples == null) {
        missing += " aggregateIntervalSamples";
      }
      if (logFormatCsv == null) {
        missing += " logFormatCsv";
      }
      if (selectedTags == null) {
        missing += " selectedTags";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_HistogramProcessorArgs(
          this.inputFile,
          this.startTimeSec,
          this.endTimeSec,
          this.outputValueUnitRatio,
          this.percentilesOutputTicksPerHalf,
          this.aggregateIntervalSamples,
          this.logFormatCsv,
          this.selectedTags);
    }
  }

}
