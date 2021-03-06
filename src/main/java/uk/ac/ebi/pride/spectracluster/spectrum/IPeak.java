package uk.ac.ebi.pride.spectracluster.spectrum;

import uk.ac.ebi.pride.spectracluster.util.Equivalent;

import java.io.Serializable;

/**
 * IPeak is an interface which represents a peak in a spectrum
 *
 * @author Johannes Griss
 * @author Steve Lewis
 * @author Rui Wang
 */
public interface IPeak extends Equivalent<IPeak>, Comparable<IPeak>, Serializable {

    IPeak[] EMPTY_ARRAY = {};

    /**
     * Peak m/z
     */
    float getMz();

    /**
     * Peak intensity
     */
    float getIntensity();

    /**
     * If the peak is part of a consensus spectrum this number represents the number of
     * spectra making up the consensus spectrum that contain the respective peak. In normal spectra
     * this number is always 1.
     */
    int getCount();


}
