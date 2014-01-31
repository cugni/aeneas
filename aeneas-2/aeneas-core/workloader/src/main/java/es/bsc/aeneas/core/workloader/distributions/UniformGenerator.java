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

import java.util.Vector;

/**
 * An expression that generates a random integer in the specified range
 */
public class UniformGenerator implements Generator<String> {

    Vector<String> _values;
    String _laststring;
    UniformIntegerGenerator _gen;

    /**
     * Creates a generator that will return strings from the specified set
     * uniformly randomly
     */
    @SuppressWarnings("unchecked")
    public UniformGenerator(Vector<String> values) {
        _values = (Vector<String>) values.clone();
        _laststring = null;
        _gen = new UniformIntegerGenerator(0, values.size() - 1);
    }

    /**
     * Generate the next string in the distribution.
     */
    @Override
    public String next() {
        _laststring = _values.elementAt(_gen.next());
        return _laststring;
    }

    /**
     * Return the previous string generated by the distribution; e.g., returned
     * from the last nextString() call. Calling lastString() should not advance
     * the distribution or have any side effects. If nextString() has not yet
     * been called, lastString() should return something reasonable.
     */
    public String last() {
        if (_laststring == null) {
            next();
        }
        return _laststring;
    }
}
