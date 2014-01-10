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

/**
 * A generator that is capable of generating ints as well as strings
 *
 * @author cooperb
 *
 */
public abstract class NumberGenerator<T extends Number & Comparable> implements Generator<Number> {

    T lastint;

    /**
     * Set the last value generated. NumberGenerator subclasses must use this
     * call to properly set the last string value, or the lastString() and
     * lastInt() calls won't work.
     */
    protected T  setLast(T last) {
        lastint = last;
        return last;
    }

    @Override
    public T last() {
        return lastint;
    }
    
    public abstract T nextInRange(T from,T maxwide);
    

   
}
