window.tests = new Array();
window.metrics = new Array();
/**
 * You have to set a textarea as console.textarea
 * console_history format=>
 * [{name:"",text""},{]
 * @returns {Console}
 */
function Console() {
    if (localStorage && localStorage.saved_command && localStorage.saved_command != "undefined") {
        try {
            this.saved_command = JSON.parse(localStorage.saved_command);
        } catch (e) {
            this.saved_command = [];
            delete localStorage.saved_command;
            showMsg(e);
//            console.log(e);
        }
    }
    /***
     *   Return the text area text
     * @returns string  */
    this.getText = function() {
        return this.editor.getValue();
    };
    this.setText = function(text) {
        this.editor.setValue(text);
    };

    this.save = function() {
        localStorage.saved_command = JSON.stringify(this.saved_command);
    };
    $(window).unload(this.save);
    //TODO finish here

    this.eachRow = function(text) {
        if (!text) {
            text = this.getText();
        }
        try {
            var da = window.samples.getAllData();
            $.each(da, function(k, v) {

                eval(text);
            });
            window.samples.table.dataTable().fnClearTable(false);
            window.samples.table.dataTable().fnAddData(da);
        } catch (err) {
            showMsg(err);
        }
    };
    this.filter = function(text) {
        if (!text) {
            text = this.getText();
        }
        try {
            var da = window.samples.getAllData();
            var val = $.map(da, function(v, k) {
                if (eval(text)) {
                    return [v];
                }
            });
            window.samples.table.dataTable().fnClearTable(false);
            window.samples.table.dataTable().fnAddData(val);
        } catch (err) {
            showMsg(err);
        }

    };
}
function Tests() {
    this.tests = new Array();
    this.load = function(args,reload) {     
        if(reload)this.tests=[];
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
    this.getCategories = function(selected) {
        var cat = new Array();
        for (var i = 0; i < selected.length; i++) {
            if (selected[i].dif) {
                cat.push(selected[i].name);
            }
        }
        return cat;
    };
    this.getSeconds = function(selected) {
        var cat = new Array();
        for (var i = 0; i < selected.length; i++) {
            if (selected[i].dif) {
                cat.push(selected[i].dif.getTime() / 1000);
            }
        }
        return cat;
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

function Samples() {
    this.samples = new Array();
    this.running_queries = 0;
    this.resetChart = function() {
        this.chart.highcharts().destroy();
        this.chart = $("#chart-samples").highcharts(this.chart_options).resizable();


    };
    this.bubble_chart_options = {
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
        this.chart = $("#chart-samples").highcharts(this.bubble_chart_options).resizable();
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
//            ,lineWidth: 2,
//            lineColor: '#08F',
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
    this.filterOnlyMin = function(eval_key) {
        try {
            var tmp = {};
            var da = this.getAllData();
            $.each(da, function(k, v) {
                var key = eval(eval_key);
                if (!tmp[key]) {
                    tmp[key] = v;
                } else {
                    var old_v = tmp[key];
                    if (old_v[4] > v[4]) {
                        tmp[key] = v;
                    }
                }
            });
            var val = $.map(tmp, function(v, k) {
                return [v];
            });

            this.clearTable(false);
            this.addData(val);
        } catch (err) {
            showMsg(err);
        }
    };
    this.filterOnlyMin0to1 = function() {
        this.filterOnlyMin(" v[0] + v[1]");
    };
    this.filterOnlyMin0to3 = function() {
        this.filterOnlyMin(" v[0] + v[1]+v[2]");
    };
    this.filterOnlyMax = function(eval_key) {
        try {
            var tmp = {};
            var da = this.getAllData();
            $.each(da, function(k, v) {
                var key = eval(eval_key);
                if (!tmp[key]) {
                    tmp[key] = v;
                } else {
                    var old_v = tmp[key];
                    if (old_v[4] < v[4]) {
                        tmp[key] = v;
                    }
                }
            });
            var val = $.map(tmp, function(v, k) {
                return [v];
            });

            this.clearTable(false);
            this.addData(val);
        } catch (err) {
            showMsg(err);
        }
    };
    this.scaleToOne = function() {
        try {
            var tmp = {};
            var da = this.getAllData();
            $.each(da, function(k, v) {
                var key = v[1];
                if (!tmp[key]) {
                    tmp[key] = v[4];
                } else {
                    var old_v = tmp[key];
                    if (old_v < v[4]) {
                        tmp[key] = v[4];
                    }
                }
            });
            var val = $.map(da, function(v, k) {
                v[4] /= tmp[v[1]];
                return [v];
            });

            this.clearTable(false);
            this.addData(val);
        } catch (err) {
            showMsg(err);
        }
    };
    this.scaleToMin = function(key_eval, val_eval) {
        try {
            if (!key_eval) {
                key_eval = "v[1]";
            }
            if (!val_eval) {
                val_eval = "v[4]";
            }


            var tmp = {};
            var da = this.getAllData();
            $.each(da, function(k, v) {
                var key = eval(key_eval);
                var value = eval(val_eval);
                if (!tmp[key]) {
                    tmp[key] = value;
                } else {
                    var old_v = tmp[key];
                    if (old_v > value) {
                        tmp[key] = value;
                    }
                }
            });
            var val = $.map(da, function(v, k) {
                eval(val_eval + " /= tmp[" + key_eval + "]");
                return [v];
            });

            this.clearTable(false);
            this.addData(val);
        } catch (err) {
            showMsg(err);
        }
    };
    this.filterOnlyMax0to1 = function() {
        this.filterOnlyMax("v[0]+v[1]");
    };
    this.filterOnlyMax0to2 = function() {
        this.filterOnlyMax("v[0]+v[1]+v[2]");
    };
    /***
     * Genereate a count by evaluting the parameter key 
     * e.g. eval_key="v[0]+v[1]" will generate a count
     * of all lines with the same value in the first and 
     * second column.
     * @param {string} eval_key
     * @returns {void}
     */
    this.count = function(eval_key) {
        try {
            var tmp = {};
            var da = this.getAllData();
            $.each(da, function(k, v) {
                var key = eval(eval_key);
                if (!tmp[key]) {
                    tmp[key] = {row: v, n: 1};
                } else {
                    tmp[key].row[4] += v[4];
                    tmp[key].n++;

                }
            });
            var val = $.map(tmp, function(v, k) {
                var r = v.row;
                r[4] = v.n;
                return [r];
            });
            this.clearTable(false);
            this.addData(val);
        } catch (err) {
            showMsg(err);
        }


    };
    this.avg0to2 = function() {
        this.avg("v[0]+v[1]+v[2]");
    };
    this.avg = function(eval_key) {
        try {
            var tmp = {};
            var da = this.getAllData();
            $.each(da, function(k, v) {
                var key = eval(eval_key);
                if (!tmp[key]) {
                    tmp[key] = {row: v, n: 1};
                } else {
                    tmp[key].row[4] += v[4];
                    tmp[key].n++;

                }
            });
            var val = $.map(tmp, function(v, k) {
                var r = v.row;
                r[4] = r[4] / v.n;
                return [r];
            });
            this.clearTable(false);
            this.addData(val);
        } catch (err) {
            showMsg(err);
        }


    };
    this.avg0to1 = function() {
        this.avg("v[0]+v[1]");
    };
    this.count0to2 = function() {
        this.count("v[0] + v[1]+v[2]");
    };
    this.count0to1 = function() {
        this.count("v[0] + v[1]");
    };
    this.maxDif = function(eval_key) {
        try {
            var tmp = {};
            var da = this.getAllData();
            $.each(da, function(k, v) {
                var key = eval(eval_key);
                if (!tmp[key]) {
                    tmp[key] = {max: v[4], min: v[4], row: v};
                } else {
                    if (tmp[key].max < v[4]) {
                        tmp[key].max = v[4];
                    }
                    if (tmp[key].min > v[4]) {
                        tmp[key].min = v[4];
                    }
                }
            });
            var val = $.map(tmp, function(v, k) {
                var r = v.row;
                r[4] = v.max - v.min;
                return [r];
            });
            this.clearTable(false);
            this.addData(val);
        } catch (err) {
            showMsg(err);
        }
    };
    this.maxDif0to1 = function() {
        this.maxDif("v[0] + v[1]");
    };
    this.maxDif0to2 = function() {
        this.maxDif("v[0]+v[1]+v[2]");
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
    this.total = 0;
    this._increment_rq = function() {
        this.running_queries++;
        this.total++;
        if (this.running_queries > 0) {
            $("#loading").show().text("running " + this.running_queries + " queries (total run: " + this.total + ")");
        }
    };
    this._decrement_rq = function() {
        this.running_queries--;
        if (this.running_queries === 0) {
            $("#loading").hide();
        } else {
            $("#loading").text("running " + this.running_queries + " queries (total run: " + this.total + ")");
        }
    };
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
        var tests = window.tests.getSelected();
        var metrics = window.metrics.getSelected();
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
                        console.log("iterating on metrics");
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
        var tests = window.tests.getSelected();
        var metrics = window.metrics.getSelected();
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
 * Returns a sorted with unique element array 
 * 
 * @returns {Array}
 */
Array.prototype.getUnique = function() {
    var arr = this, newArr = [], obj = {};
    for (var i = 0, len = arr.length; i < len; i++) {
        if (obj[arr[i]]) {
            continue;
        }
        obj[arr[i]] = 1;
        newArr.push(arr[i]);
    }
    return newArr;
};

//Made for tranform array made as [[a,b,c,1],[a,b,c,2]] to [[a,b,c,[1,2]]:
function collapseData(array, last, value) {
    var map = {};
    for (var i = 0; i < array.length; i++) {
        var key = "";
        for (var j = 0; j < last; j++) {
            key += "_" + array[i][j];
        }
        if (!map[key]) {
            map[key] = {name: key};
        }
        if (map[key].data) {
            map[key].data.push(array[i][value]);
        } else {
            map[key].data = [array[i][value]];
        }
        if (!map[key].key_element) {
            map[key].key_element = array[i].slice(0, last);
        }
    }
    return $.map(map, function(v, k) {
        return v;
    });

}


//makes correctly sortables the numbers
function s(n) {
    if (n < 10) {
        return "0" + n;
    } else {
        return n;
    }
}
function ss(n) {
    if (n < 10) {
        return "00" + n;
    } else if (n < 100) {
        return "0" + n;
    } else {
        return n;
    }
}

Date.prototype.toString = function() {
    if (this.getFullYear() === 1970) {
        //this is probably an interval
        return this.getUTCHours() + ":" + s(this.getUTCMinutes()) + ":" + s(this.getUTCSeconds()) + "." + this.getUTCMilliseconds();
    } else {
        return this.getFullYear() + "/" + s(this.getMonth()) + "/" + s(this.getDate())
                + "-" + s(this.getHours()) + ":" + s(this.getMinutes()) + ":" + s(this.getSeconds()) + "." + ss(this.getMilliseconds());
    }
};
if (!Object.keys) {
    Object.keys = function(obj) {
        var keys = [],
                k;
        for (k in obj) {
            if (Object.prototype.hasOwnProperty.call(obj, k)) {
                keys.push(k);
            }
        }
        return keys;
    };
}/***
 * 
 * @param {Array of Arrays} selected
 * @returns {Array}
 */
function removeRedundance(selected) {

    if (!selected || !(selected instanceof Array) || !(selected[0] instanceof Array)) {
        throw "The argument must be an array of arrays (e.g. [[1,2],[2,5]] )";
    }
    var cat = [];
    var sec_size = selected[0].length;

    for (var i = 0; i < selected.length; i++) {
        var a = [];
        for (var j = 0; j < sec_size; j++) {
            a.push(selected[i][j]);
            if (!cat[j]) {
                cat[j] = [a.slice()];
            } else {
                cat[j].push(a.slice());
            }
        }

    }
    for (var j = 0; j < sec_size; j++) {
        var size = {};
        $.each(cat[j], function(k, v) {
            size[v] = "";
        });
        if (Object.keys(size).length === selected.length) {
            return cat[j];
        }
    }
    throw "Illegal state";
}
;