/**
 * Copyright (c) 2010 Yahoo! Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License. See accompanying LICENSE file.
 */
package es.bsc.aeneas.core.workloader.distributions;

import es.bsc.aeneas.commons.CUtils;

/**
 * Generates integers randomly uniform from an interval.
 */
public class UniformDoubleGenerator extends NumberGenerator<Double> {

    double _lb, _ub, _interval;

    /**
     * Creates a generator that will return integers uniformly randomly from the
     * interval [lb,ub] inclusive (that is, lb and ub are possible values)
     *
     * @param lb the lower bound (inclusive) of generated values
     * @param ub the upper bound (inclusive) of generated values
     */
    public UniformDoubleGenerator(double lb, double ub) {
        assert lb < ub : "inregolar interval";
        _lb = lb;
        _ub = ub;
        _interval = _ub - _lb;
    }

    @Override
    public Double next() {
        Double ret = CUtils.random().nextDouble() % _interval + _lb;
        setLast(ret);
        return ret;
    }

    @Override
    public Double nextInRange(Double from, Double maxwide) {
        return from + CUtils.random().nextDouble() * maxwide;
    }
}
