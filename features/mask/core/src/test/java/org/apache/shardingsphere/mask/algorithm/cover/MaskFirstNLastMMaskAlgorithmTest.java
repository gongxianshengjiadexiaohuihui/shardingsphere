/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.mask.algorithm.cover;

import org.apache.shardingsphere.mask.algorithm.parameterized.MaskAlgorithmArgumentsProvider;
import org.apache.shardingsphere.mask.algorithm.parameterized.MaskAlgorithmAssertions;
import org.apache.shardingsphere.mask.algorithm.parameterized.MaskAlgorithmCaseAssert;
import org.apache.shardingsphere.test.util.PropertiesBuilder;
import org.apache.shardingsphere.test.util.PropertiesBuilder.Property;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

class MaskFirstNLastMMaskAlgorithmTest {
    
    @ParameterizedTest(name = "{0}: {1}")
    @ArgumentsSource(AlgorithmInitArgumentsProvider.class)
    void assertInit(final String type, @SuppressWarnings("unused") final String name, final Properties props) {
        MaskAlgorithmAssertions.assertInitFailedWithInvalidProperties(type, props);
    }
    
    @ParameterizedTest(name = "{0}: {1}")
    @ArgumentsSource(AlgorithmMaskArgumentsProvider.class)
    void assertMask(final String type, @SuppressWarnings("unused") final String name, final Properties props, final Object plainValue, final Object maskedValue) {
        MaskAlgorithmAssertions.assertMask(type, props, plainValue, maskedValue);
    }
    
    private static class AlgorithmInitArgumentsProvider extends MaskAlgorithmArgumentsProvider {
        
        AlgorithmInitArgumentsProvider() {
            super("MASK_FIRST_N_LAST_M");
        }
        
        @Override
        protected Collection<MaskAlgorithmCaseAssert> getCaseAsserts() {
            return Arrays.asList(
                    new MaskAlgorithmCaseAssert("empty_first_N", PropertiesBuilder.build(new Property("first-n", ""), new Property("last-m", "5"), new Property("replace-char", "*"))),
                    new MaskAlgorithmCaseAssert("empty_last_M", PropertiesBuilder.build(new Property("first-n", "3"), new Property("last-m", ""), new Property("replace-char", "*"))),
                    new MaskAlgorithmCaseAssert("empty_replace_char", PropertiesBuilder.build(new Property("first-n", "3"), new Property("last-m", "5"), new Property("replace-char", ""))));
        }
    }
    
    private static class AlgorithmMaskArgumentsProvider extends MaskAlgorithmArgumentsProvider {
        
        AlgorithmMaskArgumentsProvider() {
            super("MASK_FIRST_N_LAST_M");
        }
        
        @Override
        protected Collection<MaskAlgorithmCaseAssert> getCaseAsserts() {
            Properties diffProps = PropertiesBuilder.build(new Property("first-n", "3"), new Property("last-m", "5"), new Property("replace-char", "*"));
            Properties sameProps = PropertiesBuilder.build(new Property("first-n", "5"), new Property("last-m", "5"), new Property("replace-char", "*"));
            return Arrays.asList(
                    new MaskAlgorithmCaseAssert("null_value", diffProps, null, null),
                    new MaskAlgorithmCaseAssert("empty_string", diffProps, "", ""),
                    new MaskAlgorithmCaseAssert("normal_with_diff", diffProps, "abc123456", "***1*****"),
                    new MaskAlgorithmCaseAssert("normal_with_same", sameProps, "abc123456789", "*****34*****"),
                    new MaskAlgorithmCaseAssert("length_less_than_first_N_with_diff", diffProps, "ab", "**"),
                    new MaskAlgorithmCaseAssert("length_less_than_first_N_with_same", sameProps, "abc", "***"),
                    new MaskAlgorithmCaseAssert("length_equals_first_N_with_diff", diffProps, "abc", "***"),
                    new MaskAlgorithmCaseAssert("length_equals_first_N_with_same", sameProps, "abc12", "*****"),
                    new MaskAlgorithmCaseAssert("length_less_than_last_M_with_diff", diffProps, "abc1", "****"),
                    new MaskAlgorithmCaseAssert("length_less_than_last_M_with_same", sameProps, "abc1", "****"),
                    new MaskAlgorithmCaseAssert("length_equals_last_M_with_diff", diffProps, "abc12", "*****"),
                    new MaskAlgorithmCaseAssert("length_equals_last_M_with_same", sameProps, "abc12", "*****"),
                    new MaskAlgorithmCaseAssert("length_less_than_first_N_plus_last_M_with_diff", diffProps, "abc1234", "*******"),
                    new MaskAlgorithmCaseAssert("length_less_than_first_N_plus_last_M_with_same", sameProps, "abc123456", "*********"),
                    new MaskAlgorithmCaseAssert("length_equals_first_N_plus_last_M_with_diff", diffProps, "abc12345", "********"),
                    new MaskAlgorithmCaseAssert("length_equals_first_N_plus_last_M_with_same", sameProps, "abc1234567", "**********"));
        }
    }
}
