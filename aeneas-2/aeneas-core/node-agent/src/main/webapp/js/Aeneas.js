/**
 * The method shows a popup dialog box with the provided error message. 
 * @param {strin} msg 
 * @returns void
 *
 */ 
function showMsg(msg) {
    if ($("#msg").empty())
        $("body").append("<div id='msg'/>");
    $("#msg").text(msg).dialog({
        modal: true,
        buttons: {
            Ok: function() {
                $(this).dialog("close");
            }
        }
    });
}
/**
     * Interface for the charting framework
     * @constructor
     * @param {string} element the name of the dom element where print the charts
     * @param {Object} [options] optionals parameters to pass to the class. 
     * @abstract 
     */
    function ChartingInterface(element,options) { }
    /**
     * Prints a serie of data.  
     * @abstrct
     * @param {Array<Array>} data data to print 
     * @param {boolean} [timeSeries=false] Describe whenever the data are to plot 
     *                  according the time distance (not all points are 
     *                  equidistant)  
     * @returns void
     */
    ChartingInterface.prototype.printSeries = function(data, timeSeries) {
        throw Exception("Implemenent me!");
    };
    /**
     * Prints a chart.  
     * @abstrct
     * @param {Array<Array>} data data to print 
     * @param {boolean} [multipleAxes=false] If plotting different metrics, should
     * them plotted on the same Axes or in multiple ones?
     * 
     * @returns void
     */
    ChartingInterface.prototype.printChart = function(data, multipleAxes) {
        throw Exception("Implemenent me!");
    };
    
     /**
     * Prints a bubble chart.  
     * @abstrct
     * @param {Array<Array>} data data to print 
     * @param {boolean} [multipleAxes=false] If plotting different metrics, should
     * them plotted on the same Axes or in multiple ones?
     * 
     * @returns void
     */
    ChartingInterface.prototype.printBubbles = function(data, multipleAxes) {
        throw Exception("Implemenent me!");
    };


window.Ae=(function Aeneas() {
    var cons = new Console();
    var tests = new Tests();
    var metrics = new Metrics();
    var samples = new Samples();



    function configure(settings) {

        default_settings = {
            console: {
                textarea: "#console-textarea"
            },
            metrics: {
                table: "#metrics-table"
            },
            tests: {
                table: "#tests-table",
                chart: "#chart-tests"
            },
            samples: {
                table: "#samples-table"
            }
        };
        settings = $.extend({}, default_settings, settings);


        //initing the console

        cons.textarea = $(settings.console.textarea);
        cons.editor = CodeMirror.fromTextArea($(settings.console.textarea), {
            lineNumbers: true,
            matchBrackets: true,
            continueComments: "Enter"
        });

        metrics.table = $(settings.metrics.table).dataTable({
            "aoColumns": [
                {"sTitle": "context"},
                {"sTitle": "name"}
            ],
            "sDom": 'T<"clear">lfrtip',
            //This is the functions which add data to rows
            "fnCreatedRow": function(nRow, aData, iDataIndex) {
                $(nRow).data("obj", aData[1]);
            },
            "aLengthMenu": [[10, 25, 50, 100, 1000, 2000, 5000, 10000, -1], [10, 25, 50, 100, 1000, 2000, 5000, 10000, "All"]],
            "oTableTools": {
                "sRowSelect": "multi",
                "aButtons": [
                    "select_all", "select_none",
                    "copy",
                    "print",
                    {
                        "sExtends": "collection",
                        "sButtonText": "Save",
                        "aButtons": ["csv", "xls", "pdf"]
                    }
                ]
            }});
        metrics.table.columnFilter({
            aoColumns: [
                {
                    type: "text",
                    bRegex: true,
                    bSmart: true
                },
                {
                    type: "text",
                    bRegex: true,
                    bSmart: true
                }
            ]
        });
        tests.table = $(settings.tests.table).dataTable({
            "aoColumns": [
                {"sTitle": "test"},
                {"sTitle": "started"},
                {"sTitle": "finished"},
                {"sTitle": "time"}
            ],
            "sDom": 'T<"clear">lfrtip',
            "fnSelectAll": function(arg) {
                console.log(arg);
            },
            "fnCreatedRow": function(nRow, aData, iDataIndex) {
                $(nRow).data("obj", aData[0]);
            },
            "aLengthMenu": [[10, 25, 50, 100, 1000, 2000, 5000, 10000, -1], [10, 25, 50, 100, 1000, 2000, 5000, 10000, "All"]],
            "oTableTools": {
                "sRowSelect": "multi",
                "aButtons": [
                    "select_all", "select_none",
                    "copy",
                    "print",
                    {
                        "sExtends": "collection",
                        "sButtonText": "Save",
                        "aButtons": ["csv", "xls", "pdf"]
                    }

                ]
            }});
        tests.table.columnFilter(
                {
                    aoColumns: [
                        {
                            type: "text",
                            bRegex: true,
                            bSmart: true
                        },
                        {
                            type: "text",
                            bRegex: true,
                            bSmart: true
                        },
                        {
                            type: "text",
                            bRegex: true,
                            bSmart: true
                        },
                        {
                            type: "text",
                            bRegex: true,
                            bSmart: true
                        }
                    ]
                });
        samples.table = $(settings.samples.table).dataTable({
            "sDom": 'T<"clear">lfrtip',
            "fnCreatedRow": function(nRow, aData, iDataIndex) {
                $(nRow).data("obj", aData);
            },
            "aLengthMenu": [[10, 25, 50, 100, 1000, 2000, 5000, 10000, -1], [10, 25, 50, 100, 1000, 2000, 5000, 10000, "All"]],
            "sPaginationType": "full_numbers",
            "oTableTools": {
                "sRowSelect": "multi",
                "aButtons": [
                    "select_all", "select_none",
                    "copy",
                    "print",
                    {
                        "sExtends": "collection",
                        "sButtonText": "Save",
                        "aButtons": ["csv", "xls", "pdf"]
                    }

                ]


            }});
        samples.table.columnFilter({
            aoColumns: [
                {
                    type: "text",
                    bRegex: true,
                    bSmart: true
                },
                {
                    type: "text",
                    bRegex: true,
                    bSmart: true
                },
                {
                    type: "text",
                    bRegex: true,
                    bSmart: true
                },
                {
                    type: "text",
                    bRegex: true,
                    bSmart: true
                }
                ,
                {
                    type: "text",
                    bRegex: true,
                    bSmart: true
                }
            ]
        });
        tests.chart = $(settings.tests.chart).highcharts({
            chart: {
                type: 'bar', zoomType: 'xy'
            },
            plotOptions: {
                series: {
                    dataLabels: {
                        enabled: true
                    }
                }
            },
            title: {
                text: 'test time'
            },
            xAxis: {
                categories: []

            },
            series: [{
                    "name": "time",
                    "data": []
                }]

        }).resizable();

    }


    /***
     * The DOM aware code
     * 
     */

    function Tests() {
        this.tests = new Array();
        this.load = function(args, reload) {
            if (reload)
                this.tests = [];
            var questo = this;
            $.ajax({
                url: "q/tests",
                data: args,
                success: function(data, state) {
                    var t = JSON.parse(data, function(k, v) {
                        if (v && (v.start || v.stop)) {
                            return new Test(k, v.start, v.stop);
                        }
                        return v;
                    });
                    t = $.map(t, function(v, k) {
                        return v;
                    });
                    for (var i = 0; i < t.length; i++) {
                        if (questo.tests.indexOf(t[i]) === -1) {
                            questo.tests.push(t[i]);
                        }
                    }
                    questo.table.fnClearTable(false);
                    questo.table.fnAddData(questo.getTable());
                    questo.printChart();

                },
                error: function(req, state, excpt) {
                    showMsg(excpt);
                }
            });
        };
        this.getSelected = function() {
            //     return TableTools.fnGetInstance(this.table[0].id).fnGetSelectedData();
            return $.map($(this.table).find(".DTTT_selected"), function(v, k) {
                return ($(v).data("obj"));
            });
        };
        this.getAllData = function() {
            return this.table.dataTable().fnGetData();
        };

        this.printChart = function() {
            var ch = this.chart.highcharts();
            var selected = this.getSelected();
            ch.xAxis[0].setCategories(this.getCategories(selected), false);
            ch.series[0].setData(this.getSeconds(selected));
        };
        this.getTable = function( ) {
            var o = new Array();
            for (var i = 0; i < this.tests.length; i++) {
                o.push(this.tests[i].getRow());
            }
            return o;
        };



    }
    function Test(name, start, stop) {
        this.name = name;
        if (start !== null) {
            this.start = new Date(start);
        }
        if (stop !== null) {
            this.stop = new Date(stop);
        }
        if (stop && start) {
            this.dif = new Date(stop - start);
        } else {
            this.dif = null;
        }
        this.toString = function() {
            return this.name;
        };

        this.getRow = function() {
            return [this, this.start, this.stop ? this.stop : "n/a", this.dif ? this.dif : "n/a"];
        };

    }

    var _total = 0;
    var _running_queries = 0;
    function _increment_rq() {
        _running_queries++;
        _total++;
        if (this.running_queries > 0) {
            $("#loading").show().text("running " + _running_queries + " queries (total run: " + _total + ")");
        }
    }
    ;
    function _decrement_rq() {
        _running_queries--;
        if (this.running_queries === 0) {
            $("#loading").hide();
        } else {
            $("#loading").text("running " + _running_queries + " queries (total run: " + _total + ")");
        }
    }
    ;

    function Samples() {
        this.samples = new Array();
        this.running_queries = 0;
        this.resetChart = function() {
            this.chart.highcharts().destroy();
            this.chart = $("#chart-samples").highcharts(this.chart_options).resizable();


        };

        this.chart_options = {
            plotOptions: {
                series: {
                    dataLabels: {
                        enabled: true
                    }
                }
            },
            exporting: {
                width: 1280,
                height: 1024
            },
            chart: {
                type: 'bar',
                zoomType: 'xy'
            },
            title: {
                text: 'samples'
            },
            yAxis: {
                type: "linear"
            }
            ,
            xAxis: {
                categories: [],
                labels: {
                    rotation: 0,
                    align: 'right'

//                ,style: {
//                    fontSize: '13px',
//                    fontFamily: 'Verdana, sans-serif'
//                }
                }


            }


        };
        this.chart_time_series_options = {
            xAxis: {
                type: 'datetime',
//            dateTimeLabelFormats: {// don't display the dummy year
//                month: '%e. %b',
//                year: '%b'
//            }
            },
            tooltip: {
                formatter: function() {
                    return '<b>' + this.series.name + '</b><br/>' +
                            Highcharts.dateFormat('%H:%M:%S.%L', this.x) + ': ' + this.y + ' m';
                }
            },
            exporting: {
                width: 1280,
                height: 1024
            },
            chart: {
                zoomType: 'xy',
                type: 'spline'
            },
            title: {
                text: 'samples'
            }



        };
        this.chart_line_options = {
//        plotOptions: {
//            series: {
//                dataLabels: {
//                    enabled: false
//                }
//            }
//        },
            exporting: {
                width: 1280,
                height: 1024
            },
            chart: {
                zoomType: 'xy'
            },
            title: {
                text: 'samples'
            },
            xAxis: {
                labels: {enabled: false},
                categories: []

            }


        };
        this.getSelected = function() {
//        var oTT = TableTools.fnGetInstance(this.table[0].id);
//        return oTT.fnGetSelectedData();
            return $.map($(this.table).find(".DTTT_selected"), function(v, k) {
                return [($(v).data("obj"))];
            });
        };
        this.deselect = function(regexp) {
            var oTT = TableTools.fnGetInstance(this.table[0].id);
            var d = oTT.fnGetSelected();
            //     return TableTools.fnGetInstance(this.table[0].id).fnGetSelectedData();
            $.each(d, function(k, v) {
                if ($(v).text().match(regexp)) {
                    oTT.fnDeselect(v);
                }

            });
        };
        this.select = function(regexp) {
            var oTT = TableTools.fnGetInstance(this.table[0].id);
            var d = this.getAllData();
            $.each(d, function(k, v) {
                if ($(v).text().match(regexp)) {
                    oTT.fnSelect(v);
                }

            });
        };
        this.printGraph = function() {
            this.resetChart();
            var ch = this.chart.highcharts();
            var selected = this.getAllData();
            ch.xAxis[0].setCategories(this.getCategories(selected), false);
            var data = $.map(selected, function(v, k) {
                return v[4];
            });
            ch.series[0].setData(data);

        };
        this.printMetricCategorySelected = function() {
            this.resetChart();
            var ch = this.chart.highcharts();
            var selected = this.getSelected();
            var metrics = [];
            var re = {};
            $.each(selected, function(k, v) {
                var key1 = v[0] + v[2];
                if (!re[key1]) {
                    re[key1] = {};
                }
                re[key1].key = [v[0], v[2]];
                re[key1][v[1]] = v[4];
                metrics.push(v[1]);
            });


            var series = [];
            var u_metrics = metrics.getUnique().sort();

            for (var i = 0; i < u_metrics.length; i++) {
                series.push({
                    name: u_metrics[i],
                    data: $.map(re, function(v, k) {
                        return v[u_metrics[i]] ? v[u_metrics[i]] : [null];
                    }
                    )
                });

            }
            var nodes = $.map(re, function(v, k) {
                return v.key[1];
            }).getUnique().sort();
            var categories;
            if (nodes.length === 1) {
                categories = $.map(re, function(v, k) {
                    return v.key[0];
                });
            } else {
                categories = $.map(re, function(v, k) {
                    return v.key[0] + "<b>" + v.key[1] + "</b>";
                });
            }

            ch.xAxis[0].setCategories(categories, false);
            var i;
            for (i = 0; i < series.length - 1; i++) {
                ch.addSeries(series[i], false);
            }
            ch.addSeries(series[i]);

        };
        this.printBubble = function(names, values) {
            if (!names) {
                names = "v[0]";
            }
            if (!values) {
                values = "[v[2],v[3],v[4]]";
            }
            this.chart.highcharts().destroy();
            this.chart = $("#chart-samples").highcharts(bubble_chart_options).resizable();
            var ch = this.chart.highcharts();
            var selected = this.getSelected();
            var series = {};
            for (var i = 0; i < selected.length; i++) {
                var v = selected[i];
                var n = eval(names);
                if (series[n]) {
                    series[n].data.push(eval(values));
                } else {
                    series[n] = {
                        name: n,
                        data: [eval(values)]
                    };
                }
            }
            $.each(series, function(k, v) {
                ch.addSeries(v, false);
            });

            ch.redraw();

        };
        this.printMetricCategorySelectedMultipleAxis = function() {
            this.resetChart();
            var ch = this.chart.highcharts();
            var selected = this.getSelected();
            var metrics = [];
            var re = {};
            $.each(selected, function(k, v) {
                var key1 = v[0] + v[2];
                if (!re[key1]) {
                    re[key1] = {};
                }
                re[key1].key = [v[0], v[2]];
                re[key1][v[1]] = v[4];
                metrics.push(v[1]);
            });
            var series = [];
            var u_metrics = metrics.getUnique().sort();
            var oppo = false;
            for (var i = 0; i < u_metrics.length; i++) {
                this.chart.highcharts().addAxis({// Secondary yAxis
                    id: u_metrics[i],
                    title: {
                        text: u_metrics[i]
                    }
                    , opposite: oppo
                }, false);
                oppo = !oppo;
                series.push({
                    name: u_metrics[i],
                    yAxis: u_metrics[i],
                    data: $.map(re, function(v, k) {
                        return v[u_metrics[i]] ? v[u_metrics[i]] : [null];
                    }
                    )
                });

            }
            var nodes = $.map(re, function(v, k) {
                return v.key[1];
            }).getUnique().sort();
            var categories;
            if (nodes.length === 1) {
                categories = $.map(re, function(v, k) {
                    return v.key[0];
                });
            } else {
                categories = $.map(re, function(v, k) {
                    return v.key[0] + "<b>" + v.key[1] + "</b>";
                });
            }

            ch.xAxis[0].setCategories(categories, false);
            var i;
            for (i = 0; i < series.length - 1; i++) {
                ch.addSeries(series[i], false);
            }
            ch.addSeries(series[i]);

        };
        this.printSelected = function() {
            this.resetChart();
            var ch = this.chart.highcharts();
            var selected = this.getSelected();
            ch.xAxis[0].setCategories(this.getCategories(selected), false);
            var data = $.map(selected, function(v, k) {
                return v[4];
            });
            ch.addSeries(data);

        };






        this.addData = function(data, redraw) {
            this.table.dataTable().fnAddData(data, redraw);
        };
        this.clearTable = function(redraw) {
            this.table.dataTable().fnClearTable(redraw);
        };
        this.printTimeSeriesSelected = function(selected) {
            if (!selected)
                selected = this.getSelected();
            this.chart.highcharts().destroy();
            this.chart = $("#chart-samples").highcharts(this.chart_time_series_options).resizable();
            var ch = this.chart.highcharts();

            var data = {};
            $.each(selected, function(k, v) {
                var key = v[0] + "<b>" + v[1] + "</b>" + v[2];
                if (!data[key]) {
                    data[key] = {name: key, data: [[v[3], v[4]]]};
                } else {
                    data[key].data.push([v[3], v[4]]);
                }
            });
//        ch.xAxis[0].setCategories(this.getCategories(selected), false);
            data = $.map(data, function(v, k) {
                return v;
            });
            var i;
            for (i = 0; i < data.length - 1; i++) {
                ch.addSeries(data[i], false);
            }
            ch.addSeries(data[i]);


        };
        this.printSeriesSelected = function(selected) {
            if (!selected)
                selected = this.getSelected();
            this.chart.highcharts().destroy();
            this.chart = $("#chart-samples").highcharts(this.chart_line_options).resizable();
            var ch = this.chart.highcharts();

            var data = {};
            $.each(selected, function(k, v) {
                var key = v[0] + "<b>" + v[1] + "</b>" + v[2];
                if (!data[key]) {
                    data[key] = {name: key, data: [v[4]]};
                } else {
                    data[key].data.push(v[4]);
                }
            });
//        ch.xAxis[0].setCategories(this.getCategories(selected), false);
            data = $.map(data, function(v, k) {
                return v;
            });
            var i;
            for (i = 0; i < data.length - 1; i++) {
                ch.addSeries(data[i], false);
            }
            ch.addSeries(data[i]);


        };


        /***
         * 
         * @param {Array of Arrays} selected
         * @returns {Array}
         */
        this.getCategories = function(selected) {
            var cat1 = new Array();
            var cat2 = new Array();
            var cat3 = new Array();
            for (var i = 0; i < selected.length; i++) {
                cat1.push(selected[i][0]);
                cat2.push(selected[i][0] + "<b>" + selected[i][1] + "</b>");
                cat3.push(selected[i][0] + "<b>" + selected[i][1] + "</b><i>" + selected[i][2] + "</i>");
            }
            var size = {};
            $.each(cat1, function(k, v) {
                size[v] = "";
            });
            if (Object.keys(size).length === selected.length) {
                return cat1;
            }
            size = {};
            $.each(cat2, function(k, v) {
                size[v] = "";
            });
            if (Object.keys(size).length === selected.length) {
                return cat2;
            }
            return cat3;
        };
        this.printData = function(data) {
            this.chart.highcharts().series[0].setData(data);
        };
        this.getAllData = function() {
            return this.table.dataTable().fnGetData();
        };
        /**
         * clear the table and reset the data
         * 
         * @param {type} data 
         * @param {type} redraw true false to rewrite or not the table
         * @returns {undefined}
         */
        this.setData = function(data, redraw) {
            this.clearTable(false);
            this.addData(data, redraw);

        };


        //TODO change it simplyfing
        this.lastSampleTestNode = function(redraw) {
            var r = this.getAllData();
            var last = {};
            $.each(r, function(k, v) {
                var key = v[0] + v[1] + v[2];
                if (!last[key]) {
                    last[key] = {date: v[3], row: v};
                } else {
                    var l = last[key];
                    if (l.date < v[3]) {
                        last[key] = {date: v[3], row: v};
                    }
                }
            });
            var fil = $.map(last, function(v, k) {
                return [v.row];
            });
            this.table.dataTable().fnClearTable(false);
            this.table.fnAddData(fil, redraw);
        },
                this.loadLasts = function() {
            var tests = tests.getSelected();
            var metrics = metrics.getSelected();
            var args;
            if (!(tests && metrics)) {
                showMsg("To retrieve the samples you must select a test and a metric");
                return;
            }
            var questo = this;
            for (var j = 0; j < metrics.length; j++) {
                for (var i = 0; i < tests.length; i++) {
                    args = {
                        context: metrics[j].context,
                        test: tests[i].name,
                        metric: metrics[j].name

                    };
                    var successo = function(d) {
                        var data = JSON.parse(d);
                        if (data.there_are_more) {
                            questo._increment_rq();

                            var res = {context: data.context, metric: data.metric, test: data.test, from: data.last};
                            $.ajax({
                                url: "q/last-samples",
                                data: res,
                                success: successo
                            });
                        }
                        var n_samples = $.map(data.samples, function(v, k_metric_name) {
                            return $.map(v, function(v2, k2_node_name) {
                                return $.map(v2, function(v3_value, k3_time) {
                                    return [[data.test, k_metric_name, k2_node_name, new Date(parseInt(k3_time)), v3_value]];
                                });
                            });
                        });

                        questo.table.fnAddData(n_samples, false);
                        questo.lastSampleTestNode(!data.there_are_more);
                        questo._decrement_rq();

                    };
                    $.ajax({
                        url: "q/samples",
                        data: args,
                        success: successo
                                ,
                        error: function(req, state, excpt) {
                            showMsg(excpt);
                        }
                    });
                    this._increment_rq();
                    $("#loading").show();
                }
            }

        };
        /***
         * This method loads all teh data without any filtering or grouping
         * 
         * @returns void
         */
        this.load = function() {
            var tests = tests.getSelected();
            var metrics = metrics.getSelected();
            var args;
            if (!(tests && metrics)) {
                showMsg("To retrieve the samples you must select a test and a metric");
                return;
            }
            var questo = this;
            for (var j = 0; j < metrics.length; j++) {
                for (var i = 0; i < tests.length; i++) {
                    args = {
                        context: metrics[j].context,
                        test: tests[i].name,
                        metric: metrics[j].name

                    };
                    var successo = function(d) {
                        var data = JSON.parse(d);
                        if (data.there_are_more) {
                            console.log("iterating on metrics");
                            questo._increment_rq();
                            var res = {context: data.context, metric: data.metric, test: data.test, from: data.last};
                            $.ajax({
                                url: "q/samples",
                                data: res,
                                success: successo
                            });
                        }
                        var n_samples = $.map(data.samples, function(v, k_metric_name) {
                            return $.map(v, function(v2, k2_node_name) {
                                return $.map(v2, function(v3_value, k3_time) {
                                    return [[data.test, k_metric_name, k2_node_name, new Date(parseInt(k3_time)), v3_value]];
                                });
                            });
                        });

                        questo.table.fnAddData(n_samples, !data.there_are_more);
                        questo._decrement_rq();
                    };
                    $.ajax({
                        url: "q/samples",
                        data: args,
                        success: successo
                                ,
                        error: function(req, state, excpt) {
                            showMsg(excpt);
                        }
                    });
                    this._increment_rq();
                }
            }

        };

    }




    function Metrics() {
        this.metrics = new Array();
        this.getSelected = function() {
            //  return TableTools.fnGetInstance(this.table[0].id).fnGetSelectedData();

            return $.map($(this.table).find(".DTTT_selected"), function(v, k) {
                return ($(v).data("obj"));
            });
        };
        this.getAllData = function() {
            return this.table.dataTable().fnGetData();
        };
        this.load = function(a) {
            var args;
            if (a && a.context) {
                if (a.from) {
                    args = {'context': a.context, 'from': a.from};
                } else {
                    args = {'context': a.context};

                }
            }
            if (!a || !a.from) {
                this.table.fnClearTable();
            }
            var questo = this;
            $.ajax({
                url: "q/metrics",
                data: args,
                success: function(d) {
                    data = JSON.parse(d);
                    if (data.there_are_more) {
                        console.log("iterating on metrics");
                        questo.load(a.context, data.last);
                    }
                    var n_metr = $.map(data.metrics, function(v, k) {
                        return $.map(v, function(v1, k1) {
                            return new Metric(k, v1);
                        });

                    });
                    //window.metrics = 
                    //(window.metrics.concat(n_metr));
                    questo.metrics = questo.metrics.concat(n_metr).getUnique().sort();
                    var o = new Array();
                    for (var i = 0; i < n_metr.length; i++) {
                        o.push([n_metr[i].context, n_metr[i]]);
                    }
                    questo.table.fnAddData(o);
                },
                error: function(req, state, excpt) {
                    showMsg(excpt);
                }
            });
        };
    }
    function Metric(context, name) {
        this.context = context;
        this.name = name;
        this.toString = function() {
            return  this.name;
        };
    }







    /**
     * Setting files
     */
    var bubble_chart_options = {
        plotOptions: {
            series: {
                dataLabels: {
                    enabled: true
                }
            }
        },
        exporting: {
            width: 1280,
            height: 1024
        },
        yAxis: {
            type: "linear"
        }
        ,
        chart: {
            type: 'bubble',
            zoomType: 'xy'
        },
        title: {
            text: 'samples'
//        },
//        xAxis: {
//            categories: [],
//            labels: {
//                rotation: 0,
//                align: 'right'
//
////                ,style: {
////                    fontSize: '13px',
////                    fontFamily: 'Verdana, sans-serif'
////                }
//            }


        }


    };
    //definition of Ae
   return {
       
   } ;
})();


