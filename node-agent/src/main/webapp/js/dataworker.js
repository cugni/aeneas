(function() {
    function clusterer(array, opts) {
        if (!opts)
            opts = {};
        if (!opts.nclusters) {
            opts.nclusters = defaults.clusters < array.length ?
                    defaults.clusters : array.length - 1;
        }
        var cluster_size = Math.ceil(array.length / opts.nclusters);
        var result = Array(opts.nclusters);
        var j = 0;
        for (var i = 0; i < array.length; i += cluster_size) {
            result[j++] = {
                data: array.slice(i, i + cluster_size),
                opts: opts
            };
        }
        return result;
    }


    function _fnMap(arg) {
        if (!arg || !arg.data)
            return {data: {}, opts: {}};
        var a = arg.data;
        var tmp = {};
        var eval_key = (arg.opts.eval_key) ?
                arg.opts.eval_key
                : "v[0]+'-'+v[1]+'-'+v[2]";
        for (var k = 0; k < a.length; k++) {
            var v = a[k];
            var key = eval(eval_key);
            if (!tmp[key]) {
                tmp[key] = {
                    max: v[4],
                    min: v[4],
                    row: v,
                    count: 1,
                    sum: v[4]
                };
            } else {
                tmp[key].count++;
                tmp[key].sum += v[4];
                if (tmp[key].max < v[4]) {
                    tmp[key].max = v[4];
                }
                if (tmp[key].min > v[4]) {
                    tmp[key].min = v[4];
                }
            }
        }

        return {data: tmp, opts: arg.opts};

    }
    function _fnMedianMap(arg) {
        if (!arg || !arg.data)
            return {data: {}, opts: {}};
        var a = arg.data;
        var tmp = {};
        var eval_key = (arg.opts.eval_key) ?
                arg.opts.eval_key
                : "v[0]+'-'+v[1]+'-'+v[2]";
        for (var k = 0; k < a.length; k++) {
            var v = a[k];
            var key = eval(eval_key);
            if (!tmp[key]) {
                tmp[key] = {
                    row: v,
                    values: [v[4]]
                };
            } else {
                tmp[key].values.push(v[4]);
            }
        }

        return {data: tmp, opts: arg.opts};

    }
    function _fnReduce(v) {
        var a = v[0].data;
        var b = v[1].data;
        if (!(a && b))
            return a ? a : b;

        for (var key in a)
            if (a.hasOwnProperty(key)) {
                if (b.hasOwnProperty(key)) {
                    if (b[key].max < a[key].max) {
                        b[key].max = a[key].max;
                    }
                    if (b[key].min > a[key].min) {
                        b[key].min = a[key].min;
                    }
                    b[key].count += a[key].count;
                    b[key].sum += a[key].sum;

                } else {
                    b[key] = a[key];
                }
            }
        return {
            data: b,
            opts: v[0].opts
        };


    }
    function _fnMedianReduce(v) {
        var a = v[0].data;
        var b = v[1].data;
        if (!(a && b))
            return a ? a : b;

        for (var key in a)
            if (a.hasOwnProperty(key)) {
                if (b.hasOwnProperty(key)) {
                    b[key].values = b[key].values.concat(a[key].values);
                } else {
                    b[key] = a[key];
                }
            }
        return {
            data: b,
            opts: v[0].opts
        };


    }
    function _fnMedianThen(a) {
        var result = Array(a.data.length);
        var i = 0;
        for (var key in a.data) {
            var e = a.data[key];
            e.row[4] = e.values.sort(function(a, b) {
                return a - b
            })[e.values.length >> 1];
            result[i++] = e.row;
        }
        //   console.log("into avg",result,a);
        return result;

    }
    function _fnAvgThen(a) {
        var result = Array(a.data.length);
        var i = 0;
        for (var key in a.data) {
            var e = a.data[key];
            e.row[4] = e.sum / e.count;
            result[i++] = e.row;

        }
        //   console.log("into avg",result,a);
        return result;

    }
    function _fnMaxThen(a) {
        var result = Array(a.data.length);
        var i = 0;
        for (var key in a.data) {
            var e = a.data[key];
            e.row[4] = e.max;
            result[i++] = e.row;

        }
        //   console.log("into avg",result,a);
        return result;

    }
    function _fnMinThen(a) {
        var result = Array(a.data.length);
        var i = 0;
        for (var key in a.data) {
            var e = a.data[key];
            e.row[4] = e.min;
            result[i++] = e.row;

        }
        //   console.log("into avg",result,a);
        return result;

    }
    /**
     * Compute in parallel the average on a set of rows. 
     * 
     * @param {array} data an array of 5 elements arrays (testname,metric,node,date,value)
     * @param {function} complete function to call once complete the avg
     * @param {map} opts map with optional configuration such as conc
     conc : number of threads
     nclusters: number of clusters in the map phase. 
     * @returns void
     */
    function median(data, complete, opts) {
        if (!opts)
            opts = {};
        var conc = (opts.conc) ? opts.conc : defaults.conc;
        var p = new Parallel(clusterer(data, opts), {
            maxWorkers: conc
        });
        p.map(_fnMedianMap)
                .reduce(_fnMedianReduce)
                .then(_fnMedianThen)
                .then(complete);

    }

    /**
     * Compute in parallel the median on a set of rows. 
     * 
     * @param {array} data an array of 5 elements arrays (testname,metric,node,date,value)
     * @param {function} complete function to call once complete the avg
     * @param {map} opts map with optional configuration such as conc
     conc : number of threads
     nclusters: number of clusters in the map phase. 
     * @returns void
     */
    function avg(data, complete, opts) {
        if (!opts)
            opts = {};
        var conc = (opts.conc) ? opts.conc : defaults.conc;
        var p = new Parallel(clusterer(data, opts), {
            maxWorkers: conc
        });
        p.map(_fnMap)
                .reduce(_fnReduce)
                .then(_fnAvgThen)
                .then(complete);

    }
    function Ae() {

    }

    /**
     * Compute in parallel the maximum  on a set of rows. 
     * 
     * @param {array} data an array of 5 elements arrays (testname,metric,node,date,value)
     * @param {function} complete function to call once complete the avg
     * @param {map} opts map with optional configuration such as conc
     conc : number of threads
     nclusters: number of clusters in the map phase. 
     * @returns void
     */
    function max(data, complete, opts) {
        if (!opts)
            opts = {};
        var conc = (opts.conc) ? opts.conc : defaults.conc;
        var p = new Parallel(clusterer(data, opts), {
            maxWorkers: conc
        });
        p.map(_fnMap)
                .reduce(_fnReduce)
                .then(_fnMaxThen)
                .then(complete);

    }
    /**
     * Compute in parallel the min  on a set of rows. 
     * 
     * @param {array} data an array of 5 elements arrays (testname,metric,node,date,value)
     * @param {function} complete function to call once complete the avg
     * @param {map} opts map with optional configuration such as conc
     conc : number of threads
     nclusters: number of clusters in the map phase. 
     * @returns void
     */
    function min(data, complete, opts) {
        if (!opts)
            opts = {};
        var conc = (opts.conc) ? opts.conc : defaults.conc;
        var p = new Parallel(clusterer(data, opts), {
            maxWorkers: conc
        });
        p.map(_fnMap)
                .reduce(_fnReduce)
                .then(_fnMinThen)
                .then(complete);

    }

    /**
     * Compute a function for each row.
     * 
     * @param {array} data an array of 5 elements arrays (testname,metric,node,date,value)
     * @param {function} oneach function is executed on each row (referred as var v{array})
     * @param {function} complete function to call once complete the avg
     
     * @returns void
     */
    function eachRow(data, oneach, complete) {
        for (var k = 0; k < data.length; k++) {
            data[k] = oneach(data[k]);
        }
        if (complete)
            complete(data);

    }


//toString of data lexically ordered
    Date.prototype.toString = function() {
        if (this.getFullYear() === 1970) {
            //this is probably an interval
            return this.getUTCHours() + ":" + s(this.getUTCMinutes()) + ":" + s(this.getUTCSeconds()) + "." + this.getUTCMilliseconds();
        } else {
            return this.getFullYear() + "/" + s(this.getMonth()) + "/" + s(this.getDate())
                    + "-" + s(this.getHours()) + ":" + s(this.getMinutes()) + ":" + s(this.getSeconds()) + "." + ss(this.getMilliseconds());
        }
    };
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
        }
    }






    // EXPORTING VARS


    /**
     * Esporting the functions
     */
    var defaults = {
        conc: 8,
        clusters: 16
    };

    self.clusterer = clusterer;
    //So Aeneas can be modulizable 
    if (!self.Ae) {
        self.Ae = {};
    }
    Ae = self.Ae;
    Ae.avg = avg;
    Ae.min = min;
    Ae.max = max;
    Ae.median = median;
    Ae.eachRow = eachRow;

    
 



})();