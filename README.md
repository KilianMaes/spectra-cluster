# spectra-cluster - A MS/MS spectrum clustering Java API

https://spectra-cluster.github.io provides a complete overview over
all the tools we provide on spectrum clustering and the *spectra-cluster*
algorithm.

# Introduction
The spectra-cluster Java API is the central collection of algorithms used to develop and run the [PRIDE Cluster](https://www.ebi.ac.uk/pride/cluster) project. The library was built to quickly test different combinations of clustering approaches and contains implementations of a variety of, for example, similarity metrics for MS/MS spectrum clustering.

It is currently used in two applications:

  - [spectra-cluster-hadoop](https://github.com/spectra-cluster/spectra-cluster-hadoop): The Hadoop implementation of the re-developed [PRIDE Cluster](https://www.ebi.ac.uk/pride/cluster) algorithm
  - [spectra-cluster-cli](https://github.com/spectra-cluster/spectra-cluster-cli): A (still in beta) stand-alone implementation of the [PRIDE Cluster](https://www.ebi.ac.uk/pride/cluster) algorithm.

spectra-cluster is an open-source (Apache 2 licensed) library. It offers the following features out-of-box:

  - A collection of both classic and new algorithms for measuring [spectra similarities](https://github.com/spectra-cluster/spectra-cluster/tree/master/src/main/java/uk/ac/ebi/pride/spectracluster/similarity).
  - A set of [engines](https://github.com/spectra-cluster/spectra-cluster/tree/master/src/main/java/uk/ac/ebi/pride/spectracluster/engine) for clustering spectra together.
  - A set of [normalizers](https://github.com/spectra-cluster/spectra-cluster/tree/master/src/main/java/uk/ac/ebi/pride/spectracluster/normalizer) for normalising spectral peaks.
  - A set of [filters](https://github.com/spectra-cluster/spectra-cluster/tree/master/src/main/java/uk/ac/ebi/pride/spectracluster/util/predicate) and [functions](https://github.com/spectra-cluster/spectra-cluster/tree/master/src/main/java/uk/ac/ebi/pride/spectracluster/util/function) for pre-processing spectra, such as removing noisy peaks.
  - A set of cleanly defined data models and interfaces that represents spectra, peptide spectrum matches, and clusters.
  - Read in spectra and write out clustering results

# Changelog

## 1.1.0

* Moved to Java 1.8
* Changed default consensus spectrum builder to a binned version of the GreedyConsensusSpectrum builder
* Added features to estimate the number of comparisons directly from the data
* Optimised the MGF parser
* Added predicates to being able to only cluster identified and / or unidentified spectra
* Added support for additional MGF parameters and encode these in the .clustering file using JSON strings
* Added feature to output similarity scores at the time a spectrum is added to a cluster

## 1.0.10

* Added new function to remove contaminant ions (*RemoveContaminantsPeaksFunction*). Currently,
  this function removes all commonly observed immonium ions.
* Added a new function to remove all peaks outside a given m/z range (*RemoveWindowsPeaksFunction*).
  By default, all peaks below 200 m/z are being ignored.

## 1.0.9

* Adapted the *RemoveImpossibleHighPeaksFunction* and the *RemovePrecursorPeaksFunction*
  classes to work with spectra where the charge state is unknown (ie. < 1). In these cases
  the unchanged original spectrum is returned.

## 1.0.8

* Fixed bug in the function removing precursor peaks
* Added the mass of the complete TMT tag to the functions removing reporter peaks

# Getting started

### Installation
You will need to have [Maven](http://maven.apache.org/) installed in order to build and use the spectra-cluster library.

Add the following snippets in your Maven pom file:

```maven
<!-- spectra-cluster dependency -->
<dependency>
    <groupId>uk.ac.ebi.pride.spectracluster</groupId>
    <artifactId>spectra-cluster</artifactId>
    <version>${current.version}</version>
</dependency>
```

```maven
 <!-- EBI repo -->
 <repository>
    <id>pst-release</id>
    <url>http://www.ebi.ac.uk/Tools/maven/repos/content/repositories/pst-release</url>
 </repository>

 <!-- EBI SNAPSHOT repo -->
 <snapshotRepository>
    <id>pst-snapshots</id>
    <url>http://www.ebi.ac.uk/Tools/maven/repos/content/repositories/pst-snapshots</url>
 </snapshotRepository>
```

### Running the library
The clustering process itself is done by a clutering engine. The following examples use the implementations used for [PRIDE Cluster](https://www.ebi.ac.uk/pride/cluster).

```Java
float WINDOW_SIZE = 4.0F;
float FRAGMENT_TOLERANCE = 0.5F;
double CLUSTERING_PRECISION = 0.01;

/**
 * This creates an incremental clustering engine that
 * uses the CombinedFisherIntensityTest with a fragment
 * ion tolerance of 0.5 m/z as similarity metrics. The
 * ClusterComparator is only used for sorting of the clusters
 * during the clustering process. The WINDOW_SIZE of 4.0 m/z
 * means that as soon as a new cluster is added, any cluster
 * with an average precursor m/z lower than 4.0 m/z than the
 * newly added cluster is automatically returned during the
 * clustering process. The CLUSTERING_PRECISION is the defined
 * accuracy for the clustering process (benchmarked on the
 * PRIDE Cluster test dataset). Finally, the FrationTICPeakFunction
 * is a peak filter function that is applied to every spectrum
 * before comparison (in this case all peaks that represent
 * 50% of the total ion current, but a minimum of 20 peaks).
 * For consensus spectrum building, the complete unfiltered
 * spectrum is used.
 */
IIncrementalClusteringEngine clusteringEngine = new GreedyIncrementalClusteringEngine(
    new CombinedFisherIntensityTest(FRAGMENT_TOLERANCE),
    ClusterComparator.INSTANCE,
    WINDOW_SIZE,
    CLUSTERING_PRECISION,
    FractionTICPeakFunction(0.5f, 20));

// during clustering the clusters must be sorted
// according to precursor m/z. Otherwise an
// exception is thrown
for (ICluster clusterToAdd : clusterIterable) {
    // clusters are simply added through the 'addClusterIncremental'
    // function. Clusters that have a lower precursor m/z
    // than the added cluster (based on the set window size)
    // are returned.
    Collection<ICluster> removedClusters = clusteringEngine.addClusterIncremental(clusterToAdd);

    if (!removedClusters.isEmpty()) {
        // use some method to save the removed and thereby
        // "final" clusters
        writeOutClusters(removedClusters);
    }
}

// after all spectra were clustered, save the finally
// remaining clusters still stored in the clustering 
// engine
Collection<ICluster> clusters = clusteringEngine.getClusters();
writeOutClusters(clusters);
```
# Getting help
If you have questions or need additional help, please contact the PRIDE help desk at the EBI.

email: pride-support@ebi.ac.uk

# Feedback
Please give us your feedback, including error reports, suggestions on improvements, new feature requests. You can do so by opening a new issue at our [issues section](https://github.com/spectra-cluster/spectra-cluster/issues) 

# How to cite
Please cite this library using one of the following publications:
- Griss J, et al. Recognizing millions of consistently unidentified spectra across hundreds of shotgun proteomics datasets. Nature methods. 2016; [doi:10.1038/nmeth.3902](http://rdcu.be/i1Sa)
- Griss J, Foster JM, Hermjakob H, Vizcaíno JA. PRIDE Cluster: building the consensus of proteomics data. Nature methods. 2013;10(2):95-96. doi:10.1038/nmeth.2343. [PDF](http://www.nature.com/nmeth/journal/v10/n2/pdf/nmeth.2343.pdf),  [HTML](http://www.nature.com/nmeth/journal/v10/n2/full/nmeth.2343.html),  [PubMed](http://www.ncbi.nlm.nih.gov/pmc/articles/PMC3667236/)

# Contribute
We welcome all contributions submitted as [pull](https://help.github.com/articles/using-pull-requests/) request.

# License
This project is available under the [Apache 2](http://www.apache.org/licenses/LICENSE-2.0.html) open source software (OSS) license.
