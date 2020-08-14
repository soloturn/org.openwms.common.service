/*
 * Copyright 2005-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.common.transport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;


/**
 * A Barcode is a printable item with an unique identifier to label {@code TransportUnit}s. The identifier has a defined number of
 * characters whereas these characters are aligned either left or right. Non filled positions of a Barcode are padded with a so called
 * padding character.
 *
 * @author Heiko Scherrer
 * @GlossaryTerm
 */
@Configurable(autowire = Autowire.BY_TYPE, preConstruction = true)
@Embeddable
public class Barcode implements Serializable {

    /** Length of a Barcode field. */
    public static final int BARCODE_LENGTH = 20;
    @Autowired
    @Transient
    private BarcodeFormatProvider bfp;

    /**
     * A BARCODE_ALIGN defines whether the {@code Barcode} is applied {@code LEFT} or
     * {@code RIGHT}. Only be used when padding is activated.
     *
     * @author Heiko Scherrer
     */
    public enum BARCODE_ALIGN {
        /** Barcode is left aligned. */
        LEFT,
        /** Barcode is right aligned. */
        RIGHT
    }

    /** Define whether to use character padding or not. */
    private static boolean padded = true;

    /**
     * Defines a character used for padding.<br> If the actually length of the {@code Barcode} is less than the maximum defined {@code
     * length} the rest will be filled with {@code padder} characters.
     */
    public static char PADDER = '0';

    /** Defines the maximum length of characters. */
    private static int length = Barcode.BARCODE_LENGTH;

    /** The alignment of the {@code Barcode}. Could be something of {@link BARCODE_ALIGN}. */
    private static BARCODE_ALIGN alignment = BARCODE_ALIGN.RIGHT;

    /** 'Identifier' of the {@code Barcode}. <p> <i>Note:</i>It is not guaranteed that this field must be unique. </p> */
    @Column(name = "C_BARCODE")
    private String value;

    /*~ ----------------------------- constructors ------------------- */

    /** Dear JPA... */
    protected Barcode() {
    }

    private Barcode(String value) {
        setBarcode(value);
    }

    /**
     * Simple factory method to replace default constructor in application logic (still needed for framework stuff).
     *
     * @param value The value of the {@code Barcode} as String
     * @return Formatted Barcode instance
     */
    public static Barcode of(String value) {
        return new Barcode(value);
    }
    /*~ ----------------------------- methods ------------------- */

    /**
     * Set the Barcode.
     *
     * @param val The Barcode as String
     */
    final void setBarcode(String val) {
        this.value = adjustBarcode(val);
    }

    /**
     * Force the Barcode to be aligned to the determined rules regarding padding, alignment.
     *
     * @param val The old Barcode as String
     * @return The new aligned Barcode
     */
    public final String adjustBarcode(String val) {
        if (val == null) {
            throw new IllegalArgumentException("Cannot create a barcode without value");
        }
        if (bfp == null) {
            // load the default one
            bfp = ServiceLoader.load(BarcodeFormatProvider.class).iterator().next();
        }
        Optional<String> formatted = bfp.format(val);
        if (formatted.isPresent()) {
            return formatted.get();
        }
        String res;
        if (isPadded()) {
            res = (alignment == BARCODE_ALIGN.RIGHT) ? StringUtils.leftPad(val, length, PADDER) : StringUtils
                    .rightPad(val, length, PADDER);
        } else {
            res = val;
        }
        return res;
    }

    /**
     * Returns the alignment.
     *
     * @return The alignment
     */
    static BARCODE_ALIGN getAlignment() {
        return alignment;
    }

    /**
     * Set the alignment.
     *
     * @param align The alignment to set
     */
    static void setAlignment(BARCODE_ALIGN align) {
        alignment = align;
    }

    /**
     * Check if {@code Barcode} is padded.
     *
     * @return {@literal true} if {@code Barcode} is padded, otherwise {@literal false}.
     */
    static boolean isPadded() {
        return padded;
    }

    /**
     * Set padded.
     *
     * @param p {@literal true} if {@code Barcode} should be padded, otherwise {@literal false}.
     */
    static void setPadded(boolean p) {
        padded = p;
    }

    /**
     * Return the padding character.
     *
     * @return The padding character.
     */
    static char getPadder() {
        return PADDER;
    }

    /**
     * Set the padding character.
     *
     * @param p The padding character to use
     */
    static void setPadder(char p) {
        PADDER = p;
        padded = true;
    }

    /**
     * Return the {@code Barcode} value.
     *
     * @return The value of the {@code Barcode}
     */
    public String getValue() {
        return value;
    }

    /**
     * Return the length.
     *
     * @return The length
     */
    public static int getLength() {
        return length;
    }

    /**
     * Set the length.
     *
     * @param l The length to set
     */
    static void setLength(int l) {
        length = l;
    }

    /**
     * Return the value of the {@code Barcode} as String.
     *
     * @return As String
     * @see #getValue()
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Barcode barcode = (Barcode) o;
        return value.equals(barcode.value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}