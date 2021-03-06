package uk.ac.ebi.pride.spectracluster.util;

import uk.ac.ebi.pride.spectracluster.spectrum.IPeak;
import uk.ac.ebi.pride.spectracluster.spectrum.ISpectrum;
import uk.ac.ebi.pride.spectracluster.spectrum.KnownProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility methods for Spectrum
 *
 * @author Rui Wang
 * @version $Id$
 */
public final class SpectrumUtilities {

    private SpectrumUtilities() {
    }

    /**
     * I am having issues with cannot be cast to java.io.Serializable
     * @param test  non-null test object
     */
    public static void guaranteeSerializable(final Object test)
    {
        Serializable ser = (Serializable)test;
        int hsh = ser.hashCode(); // force this not to be optimized out
    }

    /**
     * make a list of mx values comma separated as a string
     *
     * @param spec !null spectrun
     * @return as above
     */
    public static String buildMZString(final ISpectrum spec) {
        StringBuilder sb = new StringBuilder();
        for (IPeak pk : spec.getPeaks()) {
            if (sb.length() > 0)
                sb.append(",");
            sb.append(String.format("%10.3f", pk.getMz()).trim());
        }
        return sb.toString();

    }

    /**
     * make a list of mx values comma separated as a string
     *
     * @param spec !null spectrum
     * @return as above
     */
    public static String buildIntensityString(final ISpectrum spec) {
        StringBuilder sb = new StringBuilder();
        for (IPeak pk : spec.getPeaks()) {
            if (sb.length() > 0)
                sb.append(",");
            sb.append(String.format("%10.2f", pk.getIntensity()).trim());
        }
        return sb.toString();

    }

    /**
     * Create a list of comma separated values indicating how often a given
     * peak was observed. If no count information is available, a list of
     * "1" is returned.
     * @param spectrum The spectrum to process.
     * @return A String
     */
    public static String buildCountString(final ISpectrum spectrum) {
        StringBuilder sb = new StringBuilder();
        for (IPeak peak : spectrum.getPeaks()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            int count = peak.getCount();

            if (count > 0) {
                sb.append(String.valueOf(count));
            }
            else {
                sb.append("1");
            }
        }

        return sb.toString();
    }

    /**
     * return the most common peptides (first if equally common) or ""
     * if no peptides found
     *
     * @return as above
     */
    public static String mostCommonPeptides(List<ISpectrum> spectra) {

        final List<String> peptideList = getPeptideList(spectra);
        CountedString[] countedStrings = CountedString.getCountedStrings(peptideList);
        StringBuilder sb = new StringBuilder();
        for (CountedString countedString : countedStrings) {
            if (sb.length() > 0)
                sb.append(",");
            sb.append(countedString);

        }
//        final String[] stringsByOccurance = CountedString.getStringsByOccurance(peptideList);
//          for (String s : stringsByOccurance) {
//            if (sb.length() > 0)
//                sb.append(",");
//            sb.append(s);
//        }
        return sb.toString();
    }

    /**
     * return a list of any peptides found with duplicates
     *
     * @return as above
     */
    public static List<String> getPeptideList(List<ISpectrum> spectra) {
        List<String> peptides = new ArrayList<>();
        for (ISpectrum spec : spectra) {
            String peptide = spec.getProperty(KnownProperties.IDENTIFIED_PEPTIDE_KEY);
            if (peptide != null && peptide.length() > 0) {
                String[] items = peptide.split(";");
                peptides.addAll(Arrays.asList(items));
            }

        }
        return peptides;
    }
}
