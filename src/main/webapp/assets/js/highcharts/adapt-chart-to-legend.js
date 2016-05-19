/**
 * Highcharts plugin for adjustable chart height in response to legend height
 */
(function (H) {
    H.wrap(H.Legend.prototype, 'render', function (proceed) {
        var chart = this.chart;

        proceed.call(this);

        if (this.options.adjustChartSize && this.options.verticalAlign === 'bottom') {
            chart.chartHeight += this.legendHeight;
            chart.marginBottom += this.legendHeight;
            chart.container.style.height = chart.container.firstChild.style.height = chart.chartHeight + 'px';

            this.group.attr({
                translateY: this.group.attr('translateY') + this.legendHeight
            });
        }
    });

}(Highcharts));