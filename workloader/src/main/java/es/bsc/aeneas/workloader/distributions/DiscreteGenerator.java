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
package es.bsc.aeneas.workloader.distributions;

import es.bsc.aeneas.commons.CUtils;

import java.util.Vector;

/**
 * Generates a distribution by choosing from a discrete set of values.
 */
public class DiscreteGenerator implements Generator<String> {

    class Pair {

        public double _weight;
        public String _value;

        Pair(double weight, String value) {
            _weight = weight;
            _value = value;
        }
    }
    Vector<Pair> _values;
    String _lastvalue;

    public DiscreteGenerator() {
        _values = new Vector<Pair>();
        _lastvalue = null;
    }

    /**
     * Generate the next string in the distribution.
     */
    @Override
    public String next() {
        double sum = 0;

        for (Pair p : _values) {
            sum += p._weight;
        }

        double val = CUtils.random().nextDouble();

        for (Pair p : _values) {
            if (val < p._weight / sum) {
                return p._value;
            }

            val -= p._weight / sum;
        }

        //should never get here.
        System.out.println("oops. should not get here.");

        System.exit(0);

        return null;
    }

    public String last() {
        if (_lastvalue == null) {
            _lastvalue = next();
        }
        return _lastvalue;
    }

    public void addValue(double weight, String value) {
        _values.add(new Pair(weight, value));
    }
}
