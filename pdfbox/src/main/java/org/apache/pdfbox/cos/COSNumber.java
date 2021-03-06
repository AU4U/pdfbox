/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.cos;

import java.io.IOException;

/**
 * This class represents an abstract number in a PDF document.
 *
 * @author Ben Litchfield
 */
public abstract class COSNumber extends COSBase
{
    /**
     * This will get the float value of this number.
     *
     * @return The float value of this object.
     */
    public abstract float floatValue();

    /**
     * This will get the integer value of this number.
     *
     * @return The integer value of this number.
     */
    public abstract int intValue();

    /**
     * This will get the long value of this number.
     *
     * @return The long value of this number.
     */
    public abstract long longValue();

    /**
     * This factory method will get the appropriate number object.
     *
     * @param number The string representation of the number.
     *
     * @return A number object, either float or int.
     *
     * @throws IOException If the string is not a number.
     */
    public static COSNumber get( String number ) throws IOException
    {
        if (number.length() == 1) 
        {
            char digit = number.charAt(0);
            if ('0' <= digit && digit <= '9') 
            {
                return COSInteger.get(digit - '0');
            } 
            else if (digit == '-' || digit == '.') 
            {
                // See https://issues.apache.org/jira/browse/PDFBOX-592
                return COSInteger.ZERO;
            } 
            else 
            {
                throw new IOException("Not a number: " + number);
            }
        } 
        else if (isFloat(number))
        {
            return new COSFloat(number);
        }
        else
        {
            try
            {
                return COSInteger.get(Long.parseLong(number));
            }
            catch( NumberFormatException e )
            {
                // might be a huge number, see PDFBOX-3116
                char digit = number.charAt(0);
                if (digit == '-')
                {
                    return new COSFloat(-Float.MAX_VALUE);
                }

                return new COSFloat(Float.MAX_VALUE);
            }
        }
    }

    private static boolean isFloat( String number )
    {
        int length = number.length();
        for (int i = 0; i < length; i++)
        {
            char digit = number.charAt(i);
            if (digit == '.' || digit == 'e')
            {
                return true;
            }
        }
        return false;
    }
}
